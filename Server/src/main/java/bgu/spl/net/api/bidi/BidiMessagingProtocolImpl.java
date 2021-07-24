package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Ack;
@SuppressWarnings("unchecked")

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private int connectionId;
    private Connections connections;
    private DataBase DB;
    boolean shouldTerminate;

    public BidiMessagingProtocolImpl(DataBase dataBase){
        this.DB=dataBase;
        this.shouldTerminate=false;
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
            this.connectionId=connectionId;
            this.connections=connections;
    }

    @Override
    public void process(Message message ) {
             message.set(connectionId,connections,DB);
                     message.process();
                     if (message instanceof Ack&&((Ack) message).getMessageOpcode()==3)
                         shouldTerminate=true;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}