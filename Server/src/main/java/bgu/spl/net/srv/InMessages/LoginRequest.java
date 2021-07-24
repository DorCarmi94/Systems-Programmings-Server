package bgu.spl.net.srv.InMessages;

import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Ack;
import bgu.spl.net.srv.Outmessage.Error;
import java.util.LinkedList;
@SuppressWarnings("unchecked")
public class LoginRequest extends Message {
    private int Opcode=2;

    public LoginRequest(byte[] bytesFromClient){
        super(bytesFromClient);
        int indexOfNext=decodeUserName(0);
        decodePassword(indexOfNext);
    }


    public void process() {
        if (dataBase.isUserExsits(userName)&&
                dataBase.getUser(userName).getPassword().equals(password)&&
                !dataBase.isUserLoggedIn(userName)&&
                dataBase.login(connectionId, userName)) //do the login and add the connectionId to the data base.
        {
            Ack ack=new Ack(Opcode);
            connections.send(connectionId,ack);
            //now that the user login, if he got messages waiting to get for him, we will now send them.
            LinkedList<Message>tosend=dataBase.getUser(userName).getMessageToSeng();
            for (Message m :tosend)
                connections.send(connectionId,m);
            dataBase.getUser(userName).clearList();
        }
        else {
            Error error=new Error(Opcode);
            connections.send(connectionId,error);
        }
    }
}
