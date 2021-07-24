#include <stdlib.h>
#include <thread>
#include "../include/connectionHandler.h"
#include "../include/encoderDecoder.h"
#include "../include/socketReaderThread.h"
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
using namespace std;
int main (int argc, char *argv[]) {
    bool* terminated=new bool(false);
    bool* logoutBool=new bool(false);
    if (argc < 3) {
        cerr << "Usage: " << argv[0] << " host port" << endl << endl;
        return -1;
    }
    string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
        if (!connectionHandler.connect()) {
        cerr << "Cannot connect to " << host << ":" << port << endl;
        return 1;
    }
    socketReaderThread sockReadT(connectionHandler,terminated,logoutBool); //the runnable object
    thread th1(&socketReaderThread::run, &sockReadT); //start thread t1
    encoderDecoder e; //object of encode and decode
    //From here we will see the rest of the ehco client implementation:
    while (!*terminated) {
        const short bufsize = 1024;
        char buf[bufsize];
        cin.getline(buf, bufsize);
		string line(buf);
		char clientRequest[bufsize] ;
		int sizeOfRequest=e.encodeLineFromUser(line,clientRequest); //encode line from user
        connectionHandler.sendBytes(clientRequest,sizeOfRequest); //Send client's request to server
        if(line =="LOGOUT"){
        /**            in case we need to logout we want to make sure that the thread of the main
         will update the terminate bool before we enter the while loop again
        */
            while (!*logoutBool){}
            *logoutBool=false;
        }
    }
    th1.join();
    delete(terminated);
    delete (logoutBool);
}