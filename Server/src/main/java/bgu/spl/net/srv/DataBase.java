package bgu.spl.net.srv;

import bgu.spl.net.api.User;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    private ConcurrentHashMap<String,User> data; //map of register users
    private ConcurrentHashMap<Integer,User> loggedin; //map of logged in users

    // Private constructor suppresses generation of a (public) default constructor
    public DataBase() {
        data=new ConcurrentHashMap<>();
        loggedin=new ConcurrentHashMap<>();
    }


    public boolean insertNewUser (String name, String password){
        User u=new User(name,password);
        return data.putIfAbsent(u.getName(), u)==null;
    }

    public boolean login (int connectionId, String name){
        User u=getUser(name);
        return loggedin.putIfAbsent(connectionId,u)==null;
    }

    public User getUser(String name){
        return data.get(name);
    }

    public User getLoginUser (int connectionId){
        return loggedin.get(connectionId);
    }

    public int getConnectionId (String name){
        Set<Integer> connectionsId=loggedin.keySet();
        for (Integer i:connectionsId)
            if (loggedin.get(i).getName().equals(name))
                return i;
            return -1;
    }
    public LinkedList getUserList (){
        LinkedList<User> list=new LinkedList<>();
        for (Map.Entry<String, User> entry : data.entrySet())
                list.add(entry.getValue());
        return list;
    }

    public boolean isConnectionIdLoogedIn (int connectionId){
        return loggedin.containsKey(connectionId);
    }

    public boolean isUserLoggedIn (String name){
        User u=getUser(name);
        return loggedin.containsValue(u);
    }

    public boolean isUserExsits(String name){
        return data.containsKey(name);
    }

    public void logout (int conectionId){
        synchronized (loggedin.get(conectionId)) {
            loggedin.remove(conectionId);
        }
    }

}
