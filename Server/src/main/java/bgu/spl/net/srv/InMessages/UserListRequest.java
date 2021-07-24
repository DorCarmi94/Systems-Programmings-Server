package bgu.spl.net.srv.InMessages;

import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Error;
import bgu.spl.net.srv.Outmessage.UserListAck;

@SuppressWarnings("unchecked")

public class UserListRequest extends Message {
    private int Opcode=7;

    public UserListRequest(byte[] bytesFromClient) {
        super(bytesFromClient);
    }

    public void process() {
        if (dataBase.isConnectionIdLoogedIn(connectionId)){
            UserListAck userList=new UserListAck(Opcode,dataBase.getUserList());
            connections.send(connectionId,userList);
        }
        else {
            Error error=new Error(Opcode);
            connections.send(connectionId,error);
        }
    }
}
