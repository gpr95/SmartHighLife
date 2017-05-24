/*
  Copyright (C) 2012 James Coliz, Jr. <maniacbug@ymail.com>

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  version 2 as published by the Free Software Foundation.

  Update 2014 - TMRh20
*/

/**
   Simplest possible example of using RF24Network

   TRANSMITTER NODE
   Every 2 seconds, send a payload to the receiver node.
*/

#include <RF24Network.h>
#include <RF24.h>
#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>

RF24 radio(6, 7);                   // nRF24L01(+) radio attached using Getting Started board

RF24Network network(radio);          // Network uses that radio

const uint16_t this_node = 01;        // Address of our node in Octal format
const uint16_t other_node = 00;       // Address of the other node in Octal format

const unsigned long interval = 2000; //ms  // How often to send 'hello world to the other unit

unsigned long last_sent;             // When did we last send?
unsigned long packets_sent;          // How many have we sent already


struct payload_t {                  // Structure of our payload
  unsigned long value;
  unsigned long id;
  unsigned long type;
};

/** ETHERNET COMMUNICATION */
EthernetUDP Udp;
byte mac[] = { 0x00, 0xAA, 0xBB, 0xCC, 0xDE, 0x02 };
unsigned int localPort = 8888;

int remoteNotificationPort;
IPAddress remoteIp;
int remotePort;

void setup(void)
{
  Serial.begin(57600);
  Serial.println("RF24Network/examples/helloworld_tx/");

  SPI.begin();
  radio.begin();
  network.begin(/*channel*/ 90, /*node address*/ this_node);

  /** Start ethernet outputs on automate DHCP IP */
  Ethernet.begin(mac);
  Udp.begin(localPort);

  /** Relay output */
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);

  /** Turn off relay (on LOW it is turned on) */
  digitalWrite(8, HIGH); // turn OFF
  digitalWrite(9, HIGH); // turn OFF

  /** Turn off light - "click" on pilot `off` value */
  turnOffLight();
}
boolean needToSendRF = false;
boolean lightIsTurnedOn = false;
unsigned long globalValue;
unsigned long globalId;
unsigned long globalType;

void loop() {
  network.update();                          // Check the network regularly
  unsigned long now = millis();              // If it's time to send a message, send it!
  if ( needToSendRF )
  {
    boolean receivedAck = false;

    do {
      Serial.print("Sending...");
      payload_t payload = { globalValue, globalId , globalType};
      RF24NetworkHeader header(/*to node*/ other_node);
      bool ok = network.write(header, &payload, sizeof(payload));
      if (ok)
        Serial.println("ok.");
      else
        Serial.println("failed.");

      network.update();
      while ( network.available() ) {
        RF24NetworkHeader header;        // If so, grab it and print it out
        payload_t payload;
        network.read(header, &payload, sizeof(payload));
        Serial.print("Received packet #");
        Serial.print(payload.id);
        Serial.print(" at ");
        Serial.print(payload.value);
        Serial.print(" with type ");
        Serial.println(payload.type);
        if (payload.type == 4)
          receivedAck = true;
        if (payload.type == 1) {
          if (payload.value == 1)
            writeThroughUDP('N');
          else if (payload.value == 0)
            writeThroughUDP('F');
          else {
            writeThroughUDP(payload.value);
          }
          receivedAck = true;
        }

      }

      delay(50);
    } while (!receivedAck);
    needToSendRF = false;
  }

  if ( network.available() ) {
    RF24NetworkHeader header;        // If so, grab it and print it out
    payload_t payload;
    network.read(header, &payload, sizeof(payload));
    Serial.print("Received packet #");
    Serial.print(payload.id);
    Serial.print(" at ");
    Serial.print(payload.value);
    Serial.print(" with type ");
    Serial.println(payload.type);
    if (payload.type == 2) {
      char message[3];
      message[0] = 'T';
      message[1] = (char) payload.id;
      message[2] = (char) payload.value;
      for(int i =0 ; i<3; i++)
        Serial.print(message[i]);
      Udp.beginPacket(remoteIp, remoteNotificationPort);
      Udp.write(message,3);
      Udp.endPacket();
    }
  }



    /** WAIT FOR RASPBERRY TO PING */
    int packetSize = Udp.parsePacket();
    if (packetSize) {
      char packetBuffer[UDP_TX_PACKET_MAX_SIZE];
      remoteIp = Udp.remoteIP();
      remotePort = Udp.remotePort();
      /** Read the packet into packetBufffer */
      Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
      for (int i = 0; i < packetSize; i++) {
        Serial.print(packetBuffer[i]);
      }
      Serial.println(" ");
      unsigned int longId;
      switch (packetBuffer[0]) {
        case 'X':
          initializeRemoteIp();
          break;
        case 'x':
          initializeRemoteIp();
          break;
        case 'A':
          addResource(packetBuffer, packetSize);
          break;
        case 'a':
          addResource(packetBuffer, packetSize);
          break;
        case 'N':
          longId = packetBuffer[1];
          if (longId > 65)
            writeThroughRF24(1, longId, 3);
          else
            turnOnLight();
          writeThroughUDP('D');
          break;
        case 'n':
          longId = packetBuffer[1];
          if (longId > 65)
            writeThroughRF24(1, longId, 3);
          else
            turnOnLight();
          writeThroughUDP('D');
          break;
        case 'F':
          longId = packetBuffer[1];
          if (longId > 65)
            writeThroughRF24(0, longId, 3);
          else
            turnOnLight();
          writeThroughUDP('D');
          break;
        case 'f':
          longId = packetBuffer[1];
          if (longId > 65)
            writeThroughRF24(0, longId, 3);
          else
            turnOnLight();
          writeThroughUDP('D');
          break;
        case 'G':
          getResource(packetBuffer, packetSize);
          break;
        case 'g':
          getResource(packetBuffer, packetSize);
          break;
        case 'T':
          remoteNotificationPort = Udp.remotePort();
          break;
      }

    }
  }

  void initializeRemoteIp() {
    //remoteIp = Udp.remoteIP();
    //remotePort = Udp.remotePort();
    Serial.print("RASPBERRY IP:");
    for (int i = 0; i < 4; i++) {
      Serial.print(remoteIp[i], DEC);
      if (i < 3) {
        Serial.print(".");
      }
    }
    Serial.println(" ");
    writeThroughUDP('D');
  }

  void addResource(char serverMsg[], int serverMsgSize) {
    Serial.println(" ");
    char id =  serverMsg[1];
    unsigned long idLong = id;
    Serial.print(id);
    writeThroughUDP('D');
    writeThroughRF24(48, idLong, 5);
  }

  void getResource(char serverMsg[], int serverMsgSize) {
    Serial.println(" ");
    char id =  serverMsg[1];
    Serial.print(id);
    unsigned long idLong = id;
    if (idLong > 65) {
      writeThroughRF24(48, idLong, 0);
    }
    else {
      if (lightIsTurnedOn)
        writeThroughUDP('N');
      else
        writeThroughUDP('F');
    }
  }

  void writeThroughUDP(unsigned char msg)
  {
    Udp.beginPacket(remoteIp, remotePort);
    Udp.write(msg);
    Udp.endPacket();
  }


  void writeThroughRF24(unsigned long value, unsigned long id, unsigned long type)
  {
    globalValue = value;
    globalId = id;
    globalType = type;
    needToSendRF = true;
  }


  void turnOnLight()
  {
    if (lightIsTurnedOn)
      return;

    /** Turn on relay-1 - for 0.5s */
    digitalWrite(8, LOW);
    delay(500);
    digitalWrite(8, HIGH);

    lightIsTurnedOn = true;
    Serial.println("Light ON");
  }

  void turnOffLight()
  {
    if (!lightIsTurnedOn)
      return;
    /** Turn on relay-2 - for 0.5s */
    digitalWrite(9, LOW);
    delay(500);
    digitalWrite(9, HIGH);

    lightIsTurnedOn = false;
    Serial.println("Light OFF");
  }


