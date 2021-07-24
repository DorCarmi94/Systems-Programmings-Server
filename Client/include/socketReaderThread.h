#ifndef ASSIGNMENT3_SOCKETREADERTHREAD_H
#define ASSIGNMENT3_SOCKETREADERTHREAD_H

#include "connectionHandler.h"

class socketReaderThread {
public:
    socketReaderThread (ConnectionHandler& h,bool* b,bool* logoutBool);
    void run();

private:
    ConnectionHandler &handler;
    bool * terminated;
    bool* logoutBool;
};

#endif //ASSIGNMENT3_SOCKETREADERTHREAD_H