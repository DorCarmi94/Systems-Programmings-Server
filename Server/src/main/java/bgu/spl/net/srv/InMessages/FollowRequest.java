package bgu.spl.net.srv.InMessages;
import bgu.spl.net.api.User;
import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.Error;
import bgu.spl.net.srv.Outmessage.FollowAck;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

@SuppressWarnings("unchecked")

public class FollowRequest extends Message {
    private int opcode = 4;
    private int numToFollow;
    private boolean follow;
    private LinkedList<String> userNameList;
    private LinkedList<String> successfulFollows;

    public FollowRequest(short numToFollow, byte[] bytesFromClient) {
        super(bytesFromClient);
        this.numToFollow = numToFollow;
        successfulFollows = new LinkedList<>();
        userNameList =new LinkedList<>();
        findArgs();


        //the first byte is follow/unfollow and next all useres
    }

    private void findArgs() {
        if (bytesFromClient[0] == '\0')
            follow = true;
        else
            follow = false;
        int j=3; //we know the number to follow
        for (int i = numToFollow; i>0; i--) {
            int endOfUserName = findZero(bytesFromClient, j);
            if (endOfUserName != -1) {
                int length = endOfUserName - j;//calculate the name length
                String name = new String(bytesFromClient, j, length, StandardCharsets.UTF_8);
                j = endOfUserName + 1;
                userNameList.add(name);
            }
        }
    }

    public void process() {
        if (dataBase.isConnectionIdLoogedIn(connectionId)) {
            User me = dataBase.getLoginUser(connectionId);
            if (follow) {
                for (String s : userNameList) { //run of the list we want to follow of
                    if (dataBase.isUserExsits(s)&&!dataBase.getUser(s).isUserFollowMe(me)) {
                        User toFollow = dataBase.getUser(s);
                        toFollow.addFollow(me);
                        me.addFollowing(dataBase.getUser(s));
                        successfulFollows.add(toFollow.getName());
                    }
                }
            }
            //unFollow
            else {
                for (String s : userNameList) {//run of the list we want to unfollow of
                    if (dataBase.isUserExsits(s)&&(dataBase.getUser(s).isUserFollowMe(me))) {
                        User toUnFollow = dataBase.getUser(s);
                        toUnFollow.removeFollower(me);
                        me.removeFollowing(dataBase.getUser(s));
                        successfulFollows.add(toUnFollow.getName());
                    }
                }
            }
            if (successfulFollows.size() == 0) {//if no one succeeded then return Error message
                Error error = new Error(opcode);
                connections.send(connectionId, error);
            } else {//if some succeeded,return Ack message.
                FollowAck followAck = new FollowAck(opcode, successfulFollows);
                connections.send(connectionId, followAck);
            }

        } else {
            Error error = new Error(opcode);
            connections.send(connectionId, error);
        }

    }
}