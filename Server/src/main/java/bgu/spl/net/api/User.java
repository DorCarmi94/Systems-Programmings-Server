package bgu.spl.net.api;

import bgu.spl.net.srv.Message;
import java.util.LinkedList;
@SuppressWarnings("unchecked")

public class User {
    private String name;
    private String password;
    private LinkedList<User> followers;
    private LinkedList<User> following;
    private LinkedList<Message> messagesToSend;
    private LinkedList<String> posts;

    public User (String name, String password){
        this.name=name;
        this.password=password;
        followers=new LinkedList<>();
        following=new LinkedList<>();
        messagesToSend=new LinkedList<>();
        posts=new LinkedList<>();
    }

    public String getPassword(){
        return password;
    }

    public String getName(){
        return name;
    }

    public int getnumOfFollowers(){return followers.size();}

    public int getnumOfFollowing(){return following.size();}

    public int getnumOfPosts (){return posts.size();}

    public LinkedList<User> getFollowersList(){return followers;}

    public LinkedList<Message> getMessageToSeng (){
        return messagesToSend;
    }

    public void addFollow (User user){
        synchronized (followers) {
            if (!followers.contains(user))
                followers.add(user);
        }
    }
    public void addFollowing (User user){
        synchronized (following) {
            if (!following.contains(user))
                following.add(user);
        }
    }
    public void addMessageToSend (Message m){
        messagesToSend.add(m);
    }
    public void addPost(String post){
        posts.add(post);
    }
    public void removeFollower (User user){
        synchronized (followers) {
            if (followers.contains(user))
                followers.remove(user);
        }
    }
    public void removeFollowing (User user){
        synchronized (following) {
            if (following.contains(user))
                following.remove(user);
        }
    }

    public boolean isUserFollowMe (User name){
        return followers.contains(name);
    }
    public void clearList (){
        messagesToSend.clear();
    }
}