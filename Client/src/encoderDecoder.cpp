#include <string>
#include <cstring>
#include <iostream>
#include "../include/encoderDecoder.h"
#include "../include/connectionHandler.h"
using namespace std;

int encoderDecoder::encodeLineFromUser(string line,char* clientRequest) {
    //find opcode;
    short opcode=findOpcode(line);
    char opcodeArray[2];
    shortToBytes(opcode,opcodeArray);

    switch (opcode){
        //REGISTER
        case 1: return twoArgsRequest(line,opcodeArray,clientRequest);
        //LOGIN
        case 2: return twoArgsRequest(line,opcodeArray,clientRequest);
        //LOGOUT
        case 3: return zeroArgsRequest(opcodeArray,clientRequest);
        //FOLLOW/UNFOLLOW
        case 4:{
            char follow=line.at(0);
            line = line.substr(2);
            short users=(short)stoi(line.substr(0,line.find(' ')));
            char numOfUsers[users];
            shortToBytes(users,numOfUsers);
            if (users>0){
                line=line.substr(line.find(' ')+1);
                //looking for users
                int currSpace=line.find(" ",0);
                while (currSpace!=-1) {
                    line[currSpace] = '\0';
                    currSpace = line.find(" ", 0);
                }
                int counter=0; //counting chars inserted to array
                counter=insertToCharArray(opcodeArray,2,clientRequest,counter);
                clientRequest[2]=follow-'0';
                counter++;
                counter=insertToCharArray(numOfUsers,2,clientRequest,counter);
                //insert the user list that the client wish to follow/unfollow into output char array
                for (unsigned int i=0;i<line.length(); i++){
                    clientRequest[counter]=line.at(i);
                    counter++;
                }
                clientRequest[counter]='\0';
                counter++;
                return counter;
            }
            return 0;
        }
        //POST
        case 5: return oneArgsRequest(line,opcodeArray,clientRequest);
        //PM
        case 6: return twoArgsRequest(line,opcodeArray,clientRequest);
        //USERLIST
        case 7: return zeroArgsRequest(opcodeArray,clientRequest);
        //STAT
        case 8: return oneArgsRequest(line, opcodeArray,clientRequest);
     }
    return 0;
}
short encoderDecoder::bytesToShort(char* bytesArr){
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
void encoderDecoder::shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
int encoderDecoder::zeroArgsRequest(char *opcodeArray, char *clientRequest) {
    for (int i=0;i<2; i++)
        clientRequest[i]=opcodeArray[i];
    return 2;
    }
int encoderDecoder::oneArgsRequest (string line,char opcodeArray[],char* clientRequest) {
    int counter = 0;
    counter = insertToCharArray(opcodeArray, 2, clientRequest, counter);
    for (unsigned int i=0;i<line.length(); i++){
        clientRequest[counter]=line.at(i);
        counter++;
    }
    clientRequest[counter]='\0';
    counter++;
    return counter;
}
//insert the line into client request and return the lenth of the message
int encoderDecoder::twoArgsRequest (string line,char opcodeArray[],char* clientRequest){
    int endOfUsername = line.find(" ", 0);
    line[endOfUsername] = '\0'; //change space into '/0'
    int counter=0; //counting the bytes
    counter=insertToCharArray(opcodeArray,2,clientRequest,counter);
    for (unsigned int i=0;i<line.length(); i++){
        clientRequest[counter]=line.at(i);
        counter++;
    }
    clientRequest[counter]='\0';
    counter++;//insert '\0' in the end of the line
    return counter;
}
int encoderDecoder::insertToCharArray(char toCopy[],int toCopyLength,char output[],int counter){
    for (unsigned int i=0;i<(unsigned)toCopyLength; i++) {
        output[counter] = toCopy[i];
        counter++;
    }
    return counter;
}
int encoderDecoder::findOpcode(string &line) {
    short opcode;
    int endOfCommand= line.find(" ",0);
    if (endOfCommand==-1) {
        if (line.length() == 6) // commandFromUser=="LOGOUT"
            opcode = 3;
        else  // commandFromUser=="USERLIST"
            opcode = 7;
    }
    else{
        string commandFromUser=line.substr(0,endOfCommand);
        if (commandFromUser=="REGISTER")
            opcode=1;
        else if (commandFromUser=="LOGIN")
            opcode=2;
        else if (commandFromUser=="FOLLOW")
            opcode=4;
        else if (commandFromUser=="POST")
            opcode=5;
        else if (commandFromUser=="PM")
            opcode=6;
        else // (commandFromUser=="STAT")
            opcode=8;
        line=line.substr(endOfCommand+1);
    }
    return opcode;
}
//will print to screen at the end
void encoderDecoder::decodeLineFromServer(ConnectionHandler& connectionHandler,bool* logoutBool,bool* terminated) {
    char opcodeArray1[2];
    connectionHandler.getBytes(opcodeArray1,2);
    short opcodeOfResponse=bytesToShort(opcodeArray1);
    switch(opcodeOfResponse) {
        //NOTIFICATION
        case 9: {
            char requestArray[1];
            connectionHandler.getBytes(requestArray, 1);
            string request;
            if (requestArray[0]=='\0')
                request = "PM";
            else
                request = "Public";
            string postingUser;
            connectionHandler.getFrameAscii(postingUser,'\0');
            postingUser.resize(postingUser.size()-1);
            string content;
            connectionHandler.getFrameAscii(content,'\0');
            content.resize(content.size()-1);
            cout << "NOTIFICATION " << request <<" "<<postingUser<<" "<<content<< endl;
            break;
    }
        //ACK
        case 10:{
            char opcodeArray2[2];
            connectionHandler.getBytes(opcodeArray2,2);
            short opcodeOfRequest=bytesToShort(opcodeArray2);
            bool output=printACK(opcodeOfRequest,connectionHandler);
            if (output){
                *terminated=true;
                *logoutBool=true; //output of terminate has changed
            } //should terminate
            break;
        }
        //ERROR
        case 11:{
            char opcodeArray2[2];
            connectionHandler.getBytes(opcodeArray2,2);
            short opcodeOfRequest=bytesToShort(opcodeArray2);
            if (opcodeOfRequest==3){
                *logoutBool=true;
            }
            cout<<"ERROR "+to_string(opcodeOfRequest)<<endl;
            break;
        }
    }
}
bool encoderDecoder::printACK(short opcodeOfRequest,ConnectionHandler& connectionHandler) {
    //FOLLOW ACK or USERLIST ACK
    if (opcodeOfRequest==4 || opcodeOfRequest==7){
        short numOfUsers=getBytesAndConvertToShort(connectionHandler);
        string userNameList="";
        for (int i=0;i<numOfUsers; i++){
            string username;
            connectionHandler.getFrameAscii(username,'\0');
            username.resize(username.size()-1);
            userNameList=userNameList+username+" ";
        }
        cout<<"ACK "<<to_string(opcodeOfRequest)<<" "<<to_string(numOfUsers)<<" "<<userNameList<<endl;
    }
    //LOGOUT ACK
    else if(opcodeOfRequest==3){
        cout<<"ACK "<<to_string(opcodeOfRequest)<<endl;
        return true; //should terminate
    }
    //STAT ACK
    else if (opcodeOfRequest==8){
       short numOfPosts=getBytesAndConvertToShort(connectionHandler);
       short numOfFollowers=getBytesAndConvertToShort(connectionHandler);
       short numOfFollowing=getBytesAndConvertToShort(connectionHandler);
       cout<<"ACK "<<to_string(opcodeOfRequest)<<" "<<to_string(numOfPosts)<<" "<<
       to_string(numOfFollowers)<<" "<<to_string(numOfFollowing)<<endl;
    }
    else //opcodeOfRequest==1/2/5/6
        cout<<"ACK "<<to_string(opcodeOfRequest)<<endl;
    return false; //not terminated
}
short encoderDecoder::getBytesAndConvertToShort(ConnectionHandler &connectionHandler) {
    char somethingArray[2];
    connectionHandler.getBytes(somethingArray,2);
    short something=bytesToShort(somethingArray);
    return something;
}