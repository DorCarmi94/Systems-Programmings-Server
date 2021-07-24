package bgu.spl.net.srv.Outmessage;

import bgu.spl.net.srv.Message;

public class Ack extends Message {
    private int opCode=10;
    private int messageOpcode;
    public Ack (int messageOpcode){
        this.messageOpcode=messageOpcode;
    }

    public byte[] getBytes (){
        return shortToBytes(intToShort(opCode),intToShort(messageOpcode));
    }

    public byte[] shortToBytes(short opCode,short messageOpcode ) {
        byte[] bytesArr = new byte[4];
        fill4CellsInArray(opCode,messageOpcode,bytesArr);
        return bytesArr;
    }
    public int getMessageOpcode(){
        return messageOpcode;
    }
}