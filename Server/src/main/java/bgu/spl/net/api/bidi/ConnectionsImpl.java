package bgu.spl.net.api.bidi;


import bgu.spl.net.srv.bidi.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;
@SuppressWarnings("unchecked")

public class ConnectionsImpl<T> implements Connections<T> {
    //hashmap of all active clients
    private ConcurrentHashMap<Integer, ConnectionHandler> activeConnectionHandlers;

    public ConnectionsImpl() {
        this.activeConnectionHandlers=new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(!activeConnectionHandlers.containsKey(connectionId))
            return false;
      activeConnectionHandlers.get(connectionId).send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        for (Integer i:activeConnectionHandlers.keySet()) {
            activeConnectionHandlers.get(i).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        activeConnectionHandlers.remove(connectionId);
    }

    public void addConnectionHandler (int id, ConnectionHandler connectionHandler){
        activeConnectionHandlers.put(id,connectionHandler);
    }
}