
package bgu.spl.net.api;

import bgu.spl.net.srv.InMessages.*;
import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.Outmessage.*;
import bgu.spl.net.srv.Outmessage.Error;
import java.nio.ByteBuffer;
import java.util.Arrays;
@SuppressWarnings("unchecked")

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    short opCode;
    private ByteBuffer buffer=ByteBuffer.allocate(2);
    private int zerosCounter;
    private int bytesCounterForFollow=3; //for follow
    private byte[] bytes ; //start with 1k
    private int len = 0;
    private short numOfUsersToFollow;

    public MessageEncoderDecoderImpl(){
        opCode=0;
        zerosCounter=-1;
    }

    @Override
    public Message decodeNextByte(byte nextByte) {

        switch (opCode) {
            //get opCode
            case 0: return findOpCode(nextByte);
            //Register
            case 1: return decodeTwoArgs(nextByte);
            //Login
            case 2: return decodeTwoArgs(nextByte);
            //case 3 is taken care in case 0 when the opcode is known, that's because there might not be next bite for that case.

            //Follow
            case 4:
                //zero counter represent bytes that has been read since opcode
                if (zerosCounter==-1 && bytesCounterForFollow==3)
                    bytesCounterForFollow--;

                else if (zerosCounter==-1 && bytesCounterForFollow==2 || bytesCounterForFollow==1){
                    if (buffer.hasRemaining()){
                        buffer.put(nextByte);
                        bytesCounterForFollow--;
                    }
                    if(!buffer.hasRemaining()){
                        buffer.flip();
                        numOfUsersToFollow = buffer.getShort();
                        buffer.clear(); //gets buffer ready for next numOfUsersToFollow
                        bytesCounterForFollow=3;
                        zerosCounter=numOfUsersToFollow;
                        if (zerosCounter==0)
                            return new FollowRequest(numOfUsersToFollow,bytes);
                    }
                }
                else if (zerosCounter>0){
                    if (nextByte == '\0' && zerosCounter>0)
                        zerosCounter--;
                    if (zerosCounter==0) {
                        zerosCounter = -1;
                        opCode=0;
                        return new FollowRequest(numOfUsersToFollow,bytes);
                    }
                }
                pushByte(nextByte);

                return null;
            //Post
            case 5: return decodeOneArgs(nextByte);
            //PM
            case 6: return decodeTwoArgs(nextByte);
            //case 7 is taken care in case 0 when the opcode is known, that's because there might not be next bite for that case.
            //Stat
            case 8: return decodeOneArgs(nextByte);
        }
        //switch case for opCode
        return null;
    }

    @Override
    //check the instance of the message and return its bytes.
    public byte[] encode(Message message) {
        if (message instanceof Ack)
            return ((Ack) message).getBytes();
        if (message instanceof Error)
            return ((Error) message).getBytes();
        if (message instanceof Notification)
            return ((Notification) message).getBytes();
        if (message instanceof UserListAck)
            return ((UserListAck) message).getBytes();
        if (message instanceof StatAck)
            return ((StatAck) message).getBytes();
        if (message instanceof FollowAck)
            return ((FollowAck) message).getBytes();
        return null;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private Message findOpCode(byte nextByte){
        if (buffer.hasRemaining())
            buffer.put(nextByte);
        if (!buffer.hasRemaining()){
            buffer.flip();
            opCode = buffer.getShort();
            bytes = new byte[1 << 10];
            len=0;
            buffer.clear(); //gets buffer ready for next opcode
        }
        if (opCode == 3) {
            opCode=0;
            return new LogoutRequest(bytes);
        }
        if (opCode == 7){
            opCode=0;
            return new UserListRequest(bytes);
        }
        return null;
    }

    private Message decodeTwoArgs(byte nextByte){
        if (zerosCounter==-1)
            zerosCounter=2; //number of arguments to read
        else {
            if (nextByte == '\0' )
                zerosCounter--;
            if (zerosCounter == 0){
                zerosCounter=-1;
                if (opCode==1) {
                    opCode = 0;
                    return new RegisterRequest(bytes);
                }
                else if (opCode==2){
                    opCode=0;
                    return new LoginRequest(bytes);
                }
                else if (opCode==6){
                    opCode=0;
                    return new PMRequest(bytes);
                }
            }
        }
        pushByte(nextByte);
        return null;
    }

    private Message decodeOneArgs(byte nextByte){
        if (zerosCounter==-1)
            zerosCounter=1; //number of arguments to read
        else {
            if (nextByte =='\0' && zerosCounter == 1)
                zerosCounter--;
            if (zerosCounter == 0){
                zerosCounter=-1;
                if (opCode==5){
                    opCode=0;
                    return new PostRequest(bytes);
                } //POST
                else {// (opCode==8) STAT
                    opCode=0;
                    return new StatRequest(bytes);
                }
            }
        }
        pushByte(nextByte);
        return null;
    }
}