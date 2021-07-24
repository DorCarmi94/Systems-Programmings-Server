package bgu.spl.net.srv.InMessages;

import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Ack;
import bgu.spl.net.srv.Outmessage.Error;
@SuppressWarnings("unchecked")
public class LogoutRequest extends Message {
    private int Opcode=3;

    public LogoutRequest(byte[] bytesFromClient) {
        super(bytesFromClient);
    }

    public void process() {
        if (dataBase.isConnectionIdLoogedIn(connectionId)) {
            dataBase.logout(connectionId);
            Ack ack=new Ack(Opcode);
            connections.send(connectionId,ack);
            connections.disconnect(connectionId);

        }
        else {
            Error error=new Error(Opcode);
            connections.send(connectionId,error);
        }
    }
}