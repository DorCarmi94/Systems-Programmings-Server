package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import java.nio.charset.StandardCharsets;
@SuppressWarnings("unchecked")
public abstract class Message {
    protected byte[] bytesFromClient;
    protected String userName;
    protected String password;
    protected String content;
    protected Connections connections;
    protected int connectionId;
    protected DataBase dataBase;

    public Message(){}

    public Message(byte[] bytesFromClient ){
        this.bytesFromClient=bytesFromClient;
    }


    public void set(int connectionId, Connections connections,DataBase dataBase) {
        this.connectionId=connectionId;
        this.connections=connections;
        this.dataBase=dataBase;

    }

    public void process() {
    }

    public short intToShort (int num){
        Integer integer=num;
        return integer.shortValue();
    }

    protected void decodeContent(int from){
        int endOfContent=findZero(bytesFromClient,from);
        this.content = new String(bytesFromClient, from, endOfContent-from, StandardCharsets.UTF_8);
    }

    protected int decodeUserName(int from){
        int endOfUserName=findZero(bytesFromClient,from);
        this.userName = new String(bytesFromClient, from, endOfUserName-from, StandardCharsets.UTF_8);
        return endOfUserName+1;
    }
    protected void decodePassword(int from){
        int endOfUserPassword=findZero(bytesFromClient,from);
        this.password = new String(bytesFromClient, from, endOfUserPassword-from, StandardCharsets.UTF_8);
    }


    public int findZero(byte[] bytes,int start){
        for (int i=start; i<bytes.length; i++){
            if (bytes[i]=='\0')
                return i;
        }
        return -1;
    }

    protected  void fill4CellsInArray(short opCode,short messageOpcode,byte[] bytesArr ){
        bytesArr[0] = (byte)((opCode >> 8) & 0xFF);
        bytesArr[1] = (byte)(opCode & 0xFF);
        bytesArr[2] = (byte)((messageOpcode >> 8) & 0xFF);
        bytesArr[3] = (byte)(messageOpcode & 0xFF);
    }
}