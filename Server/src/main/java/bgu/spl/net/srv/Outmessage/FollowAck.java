package bgu.spl.net.srv.Outmessage;
import bgu.spl.net.srv.Message;
import java.util.LinkedList;
@SuppressWarnings("unchecked")
public class FollowAck extends Message {
    private int opcode=10;
    private int messageOpcode;
    private LinkedList<String> successfulFollows;

    public FollowAck(int messageOpCode,LinkedList names){
        this.messageOpcode=messageOpCode;
        this.successfulFollows=names;
    }

    public byte[] getBytes (){
        return shortToBytes(intToShort(opcode),intToShort(messageOpcode),successfulFollows);
    }

    public byte[] shortToBytes(short opcode,short messageOpcode,LinkedList<String> successfulFollows) {
        int counter =0;
        for (String u : successfulFollows) {
            counter=counter+u.getBytes().length;
        }
        short num = intToShort(successfulFollows.size());
        byte[] bytesArr = new byte[6 +counter+successfulFollows.size()];
        fill4CellsInArray(opcode,messageOpcode,bytesArr);
        bytesArr[4] = (byte) ((num >> 8) & 0xFF);//successful Follows
        bytesArr[5] = (byte) (num & 0xFF);//successful Follows
        int j = 6;
        for (String u : successfulFollows) { //successful Follows names
            byte[] name = u.getBytes();
            for (int i = 0; i < name.length; i++) {
                bytesArr[j] = name[i];
                j++;
            }
            bytesArr[j] = '\0';
            j++;
        }
        return bytesArr;
    }

}