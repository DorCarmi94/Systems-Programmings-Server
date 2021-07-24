package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        DataBase dataBase=new DataBase();
        int port=Integer.parseInt(args[0]);
        Server.threadPerClient(
                port, //port
                () -> new BidiMessagingProtocolImpl(dataBase), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }
}
