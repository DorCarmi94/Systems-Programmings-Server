package bgu.spl.net.srv.Outmessage;

import bgu.spl.net.srv.Message;

public class StatAck extends Message {
    private int Opcode=10;
    private int messageOp;
    private int numOfPost;
    private int numOfFollowers;
    private int numOfFollowing;

    public StatAck(int messageOp,int numOfPost,int numOfFollowers,int numOfFollowing){
        this.messageOp=messageOp;
        this.numOfPost=numOfPost;
        this.numOfFollowers=numOfFollowers;
        this.numOfFollowing=numOfFollowing;
    }

    public byte[] getBytes (){
        return shortToBytes(intToShort(Opcode),
                intToShort(messageOp),
                intToShort(numOfPost),
                intToShort(numOfFollowers),
                intToShort(numOfFollowing));
    }

    public byte[]shortToBytes (short Opcode,short messageOp,short numOfPost,short numOfFollowers,short numOfFollowing){
        byte[] bytesArr = new byte[10];
        bytesArr[0] = (byte)((Opcode >> 8) & 0xFF);
        bytesArr[1] = (byte)(Opcode & 0xFF);
        bytesArr[2] = (byte)((messageOp >> 8) & 0xFF);
        bytesArr[3] = (byte)(messageOp & 0xFF);
        bytesArr[4] = (byte)((numOfPost >> 8) & 0xFF);
        bytesArr[5] = (byte)(numOfPost & 0xFF);
        bytesArr[6] = (byte)((numOfFollowers >> 8) & 0xFF);
        bytesArr[7] = (byte)(numOfFollowers & 0xFF);
        bytesArr[8] = (byte)((numOfFollowing >> 8) & 0xFF);
        bytesArr[9] = (byte)(numOfFollowing & 0xFF);

        return bytesArr;
    }
}