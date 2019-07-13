FROM ubuntu:18.04
COPY vmd-1.9.3.bin.LINUXAMD64.text.tar /root
WORKDIR /root
RUN tar -xvf vmd-1.9.3.bin.LINUXAMD64.text.tar
RUN apt update && apt install make
WORKDIR /root/vmd-1.9.3
RUN ./configure
WORKDIR /root/vmd-1.9.3/src
RUN make install && \
    vmd
