#ifndef ASSIGNMENT3_ENCODERDECODER_H
#define ASSIGNMENT3_ENCODERDECODER_H
#include <string>
#include "connectionHandler.h"

using namespace std;
class encoderDecoder{
public:
    int encodeLineFromUser(string line,char* c);
    void decodeLineFromServer(ConnectionHandler& connectionHandler,bool* logoutBool,bool* terminated);

private:
    short bytesToShort(char* bytesArr);
    void shortToBytes(short num, char* bytesArr);
    int zeroArgsRequest(char opcodeArray[],char* clientRequest);
    int oneArgsRequest (string line,char opcodeArray[],char* clientRequest);
    int twoArgsRequest (string line,char opcodeArray[],char* clientRequest);
    int insertToCharArray(char toCopy[],int toCopyLength,char output[],int counter);
    int findOpcode (string& line);
    bool printACK(short opcodeOfRequest,ConnectionHandler& connectionHandler);
    short getBytesAndConvertToShort(ConnectionHandler& connectionHandler);
    };

#endif //ASSIGNMENT3_ENCODERDECODER_H