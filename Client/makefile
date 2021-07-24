CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: EchoClient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/socketReaderThread.o bin/encoderDecoder.o $(LDFLAGS)

EchoClient: bin/connectionHandler.o bin/echoClient.o bin/socketReaderThread.o bin/encoderDecoder.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/socketReaderThread.o: src/socketReaderThread.cpp
	g++ $(CFLAGS) -o bin/socketReaderThread.o src/socketReaderThread.cpp

bin/encoderDecoder.o: src/encoderDecoder.cpp
	g++ $(CFLAGS) -o bin/encoderDecoder.o src/encoderDecoder.cpp


.PHONY: clean
clean:
	rm -f bin/*
