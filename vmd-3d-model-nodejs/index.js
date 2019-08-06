var admin = require("firebase-admin");

var localConfig = require("./local-config.json");

var serviceAccount = require(localConfig["admin-json"]);
var http = require('http');
var fs = require('fs');
const { renderString, renderTemplateFile } = require('template-file')

const { exec } = require('child_process');
const obj2gltf = require('obj2gltf');

var download = function(url, dest, cb) {
    var file = fs.createWriteStream(dest);
    var request = http.get(url, function(response) {
        response.pipe(file);
        file.on('finish', function() {
            file.close(cb);  // close() is async, call cb after close completes.
        });
    }).on('error', function(err) { // Handle errors
        fs.unlink(dest); // Delete the file async. (But we don't check the result)
        if (cb) cb(err.message);
    });
};

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    storageBucket: "arbiostruct.appspot.com"
});

const bucket = admin.storage().bucket();
const db = admin.firestore();

let proteinQuery = db.collection('proteins');
let modelQuery = db.collection('models');

let proteinObserver = proteinQuery.onSnapshot(querySnapshot => {
    console.log(`Received query snapshot of size ${querySnapshot.size}`);
    querySnapshot.docChanges().forEach(change => {
        if (change.type === 'added') {
            let pdbID = change.doc.data()['pdbID']
            modelQuery.doc(pdbID).get().then(documentSnapshot => {
                if (documentSnapshot.exists) {
                    //console.log('Document retrieved successfully.');
                } else {
                    modelQuery.doc(pdbID).create({status:'new', modelUrl:'', updateTime:admin.firestore.Timestamp.now()})
                }
            })
        }
    })
})
function upload(doc, cb){
    modelQuery.doc(doc.id).update({status:'uploading'})
    bucket.upload("workdir/"+doc.id+".gltf", function(err, file, apiResponse) {
        if(err){
            throw err;
        }
        // `file` is an instance of a File object that refers to your new file.
        file.getSignedUrl({
            action: 'read',
            expires: '03-09-2491'
        }).then(signedUrls => {
            modelQuery.doc(doc.id).update({status:"ready", modelUrl:signedUrls[0]})
            console("PDB ID:", doc.id, "ready")
            // signedUrls[0] contains the file's public URL
        });
    });
}

function convertglTF(doc, cb){
    obj2gltf('workdir/'+doc.id+'.obj')
        .then(function(gltf) {
            const data = Buffer.from(JSON.stringify(gltf));
            fs.writeFileSync('workdir/'+doc.id+'.gltf', data);
            console.log("PDB ID:", doc.id, "finish rendering")
            upload(doc, cb)
        });
}

function renderPDB(doc, cb){
    renderTemplateFile('vmdcommand.tpl', {input_filename: "workdir/"+doc.id+".pdb", output_filename: "workdir/" + doc.id+".obj"})
        .then(renderedString => {
            fs.writeFile("workdir/"+doc.id + ".vmd", renderedString, function(err) {
                if(err) throw err
                console.log("Vmd command script for PDB ID ", doc.id, "saved");
                exec('vmd -e workdir/' + doc.id + ".vmd", (err, stdout, stderr) => {
                    if (err) {
                        return;
                    }
                    convertglTF(doc, cb)
                    console.log('stdout:', stdout);
                    console.log('stderr:', stderr);
                });
            });
        })
}

function processStructure(doc){
    //console.log('start process' ,doc.id, doc.data())
    modelQuery.doc(doc.id).update({status:'rendering'})
    pdb_url = "http://files.rcsb.org/download/" + doc.id + ".pdb"
    pdb_file_path = "workdir/" + doc.id + ".pdb"
    download(pdb_url, pdb_file_path, function(msg){
        if(msg){
            console.log(msg);
        }else{
            renderPDB(doc);
        }
    })
}

let modelObserver = modelQuery.onSnapshot(querySnapshot => {
    querySnapshot.docChanges().forEach(change => {
        if (change.type === 'added'){
            console.log('model added:' , change.doc.data())
            if(change.doc.data()['status'] == 'new' || change.doc.data()['status'] == 'rendering'){
                processStructure(change.doc)
            }
            else if(change.doc.data()['status'] == 'uploading'){
                upload(change.doc)
            }
        }
    })
})

