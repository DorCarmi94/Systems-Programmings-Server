package bgu.spl.net.srv.Outmessage;

import bgu.spl.net.api.User;
import bgu.spl.net.srv.Message;

public class Notification extends Message {
    private int opCode=9;
    private byte notificationType;
    private User postingUser;
    private String content;

    public Notification (byte type,User postingUser,String content){
        this.notificationType=type;
        this.postingUser=postingUser;
        this.content=content;
    }

    public byte[] getBytes (){
        return shortToBytes(intToShort(opCode),notificationType,postingUser.getName(),content);
    }

    public byte[] shortToBytes(short opcode,byte notificationType,String name,String content)
    {
        byte[] postingUser=name.getBytes();
        byte[] con=content.getBytes();

        byte[] bytesArr = new byte[postingUser.length+con.length+5];
        bytesArr[0] = (byte)((opcode >> 8) & 0xFF);
        bytesArr[1] = (byte)(opcode & 0xFF);
        bytesArr[2] = notificationType;
        int j=3;
        for (int i=0;i<postingUser.length;i++){
            bytesArr[j]=postingUser[i];
            j++;
        }
        bytesArr[j] = '\0';
        j++;
        for (int t=0;t<con.length;t++){
            bytesArr[j]=con[t];
            j++;
        }
        bytesArr[j] ='\0';

        return bytesArr;
    }
}