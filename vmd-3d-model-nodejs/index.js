var admin = require("firebase-admin");

var localConfig = require("./local-config.json");

var serviceAccount = require(localConfig["admin-json"]);

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    storageBucket: "arbiostruct.appspot.com"
});

const bucket = admin.storage().bucket();
const db = admin.firestore();

let query = db.collection('protein-render');

let observer = query.onSnapshot(querySnapshot => {
    console.log(`Received query snapshot of size ${querySnapshot.size}`);
    querySnapshot.docChanges().forEach(change => {
        if (change.type === 'added') {
            console.log('model-entry added:', change.doc.data());
        }
        if (change.type === 'modified') {
            console.log('model-entry modified: ', change.doc.data());
        }
        if (change.type === 'removed') {
            console.log('model-entry removed: ', change.doc.data());
        }
    });
}, err => {
    console.log(`Encountered error: ${err}`);
});
