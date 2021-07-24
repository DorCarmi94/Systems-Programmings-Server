package bgu.spl.net.srv.InMessages;

import bgu.spl.net.api.User;
import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Ack;
import bgu.spl.net.srv.Outmessage.Error;
import bgu.spl.net.srv.Outmessage.Notification;
import java.util.LinkedList;

@SuppressWarnings("unchecked")
public class PostRequest extends Message {
    private int Opcode=5;


    public PostRequest(byte[] bytesFromClient){
        super(bytesFromClient);
        decodeContent(0);
    }


    public void process() {
        if (dataBase.isConnectionIdLoogedIn(connectionId)) {
            dataBase.getLoginUser(connectionId).addPost(content); //add the user's num of posts.
            LinkedList<String> toSend = new LinkedList<>();
            int i = 0;
            while (i < content.length()) { //search if there's any tags in the message.
                String name = null;
                int first = content.indexOf("@", i);
                if (first != -1){
                int last = content.indexOf(" ",first);
                    if (last != -1)
                        name = content.substring(first + 1, last);
                    else name = content.substring(first+1);
                    if (name != null) {
                        if (!toSend.contains(name)&&dataBase.isUserExsits(name))
                            toSend.add(name);
                        if (last!=-1)
                            i=last;
                        else i=content.length();
                }
                else i=content.length();
                }
                else i=content.length();
            }
            Notification n = new Notification( (byte)1, dataBase.getLoginUser(connectionId), content);
            for (String s : toSend) { //sends to all tags the post.
                synchronized (dataBase.getUser(s)) {
                    int connectionIdToSend = dataBase.getConnectionId(s);
                    if (connectionIdToSend != -1)
                        connections.send(connectionIdToSend, n);
                    else
                        dataBase.getUser(s).addMessageToSend(n);
                }
            }
            LinkedList<User> followers = dataBase.getLoginUser(connectionId).getFollowersList();
            for (User f : followers) {//sends to all the user followers the post
                if (!toSend.contains(f.getName())) {
                    synchronized (f) {
                        int connectionIdToSend = dataBase.getConnectionId(f.getName());
                        if (connectionIdToSend != -1) {
                            connections.send(connectionIdToSend, n);
                        } else f.addMessageToSend(n);
                    }
                }
            }
            Ack ack =new Ack(Opcode);
            connections.send(connectionId,ack);
        }

        else {
            Error error=new Error(Opcode);
            connections.send(connectionId,error);
        }

    }
}
