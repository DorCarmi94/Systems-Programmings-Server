package bgu.spl.net.srv.Outmessage;

import bgu.spl.net.api.User;
import bgu.spl.net.srv.Message;

import java.util.LinkedList;
@SuppressWarnings("unchecked")
public class UserListAck extends Message {
    private int Opcode=10;
    private int messageOpcode;
    private LinkedList<User> userList;

    public UserListAck(int messageOpCode ,LinkedList userList){
        this.userList=userList;
        this.messageOpcode=messageOpCode;
    }

    public byte[] getBytes (){
        return shortToBytes(intToShort(Opcode),intToShort(messageOpcode));
    }

    public byte[] shortToBytes(short opCode,short messageOpcode ){
        short num=intToShort(userList.size());
        int counter=0;
        for (User u:userList) {
            counter = counter + u.getName().getBytes().length;
        }

        byte[] bytesArr = new byte[6+counter+userList.size()];
        bytesArr[0] = (byte)((opCode >> 8) & 0xFF);
        bytesArr[1] = (byte)(opCode & 0xFF);
        bytesArr[2] = (byte)((messageOpcode >> 8) & 0xFF);
        bytesArr[3] = (byte)(messageOpcode & 0xFF);
        bytesArr[4] = (byte)((num >> 8) & 0xFF);
        bytesArr[5] = (byte)(num & 0xFF);
        int j=6;
        for (User u:userList){ //insert the names of the users into bytes array.
            byte[] name=u.getName().getBytes();
            for (int i=0; i<name.length; i++){
                bytesArr[j] = name[i];
                j++;
            }
            bytesArr[j] = '\0';
            j++;
        }
        return bytesArr;
    }

}