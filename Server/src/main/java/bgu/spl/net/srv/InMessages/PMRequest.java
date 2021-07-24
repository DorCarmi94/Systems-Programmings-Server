package bgu.spl.net.srv.InMessages;

import bgu.spl.net.api.User;
import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Ack;
import bgu.spl.net.srv.Outmessage.Error;
import bgu.spl.net.srv.Outmessage.Notification;
@SuppressWarnings("unchecked")
public class PMRequest extends Message{
    private int Opcode=6;



    public PMRequest(byte[] bytesFromClient) {
        super(bytesFromClient);
        int indexOfNext=decodeUserName(0);
        decodeContent(indexOfNext);
    }


    public void process() {
        if (dataBase.isConnectionIdLoogedIn(connectionId)&&dataBase.isUserExsits(userName)){
            User sender=dataBase.getLoginUser(connectionId);
            synchronized (dataBase.getUser(userName)) {// the user in the data base is licked until the message will send.
                Notification pm = new Notification((byte) 0, sender, content);
                int connectionIdToSend = dataBase.getConnectionId(userName);
                if (connectionIdToSend != -1) {//the user logged in
                    connections.send(connectionIdToSend, pm);
                } else dataBase.getUser(userName).addMessageToSend(pm); //the message will be added to his message list.
            }
                Ack ack = new Ack(Opcode);
                connections.send(connectionId, ack);
        }
        else {
            Error error=new Error(Opcode);
            connections.send(connectionId,error);

        }
    }
}