#include "../include/socketReaderThread.h"
#include "../include/connectionHandler.h"
#include "../include/encoderDecoder.h"

socketReaderThread::socketReaderThread (ConnectionHandler& h,bool* t,bool* logoutBool): handler(h) ,terminated(t), logoutBool(logoutBool){};

void socketReaderThread::run() {
    encoderDecoder e; //object of encode and decode
    while (!*terminated)
        e.decodeLineFromServer(handler,logoutBool,terminated);
};