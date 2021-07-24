package bgu.spl.net.srv.InMessages;

import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Ack;
import bgu.spl.net.srv.Outmessage.Error;
@SuppressWarnings("unchecked")
public class RegisterRequest extends Message {
    private int Opcode=1;

    public RegisterRequest (byte[] bytesFromClient){
       super(bytesFromClient);
       int indexOfNext=decodeUserName(0);
       decodePassword(indexOfNext);
    }

    public void process() {
        if (!dataBase.isUserExsits(userName)&&dataBase.insertNewUser(userName,password)) {
            Ack ack=new Ack(Opcode);
            connections.send(connectionId,ack);
        }
        else {
            Error error=new Error(Opcode);
            connections.send(connectionId,error);
        }
    }
}
