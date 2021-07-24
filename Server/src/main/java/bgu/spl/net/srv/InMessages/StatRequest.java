package bgu.spl.net.srv.InMessages;

import bgu.spl.net.api.User;
import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Error;
import bgu.spl.net.srv.Outmessage.StatAck;

@SuppressWarnings("unchecked")


public class StatRequest extends Message {
    private int opcode=8;

    public StatRequest(byte[] bytesFromClient){
        super(bytesFromClient);
        decodeUserName(0);
    }

    public void process() {
        if (dataBase.isConnectionIdLoogedIn(connectionId)&&dataBase.isUserExsits(userName)) {
            User user = dataBase.getUser(userName);
            StatAck ack = new StatAck(opcode, user.getnumOfPosts(), user.getnumOfFollowers(), user.getnumOfFollowing());
            connections.send(connectionId,ack);
        }
        else {
            Error error=new Error(opcode);
            connections.send(connectionId,error);
        }
    }
}
