/*
  Copyright (C) 2012 James Coliz, Jr. <maniacbug@ymail.com>

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  version 2 as published by the Free Software Foundation.

  Update 2014 - TMRh20
*/

/**
   Simplest possible example of using RF24Network,

   RECEIVER NODE
   Listens for messages from the transmitter and prints them out.
*/

#include <RF24Network.h>
#include <RF24.h>
#include <SPI.h>


RF24 radio(7, 8);               // nRF24L01(+) radio attached using Getting Started board

RF24Network network(radio);      // Network uses that radio
const uint16_t this_node = 00;    // Address of our node in Octal format ( 04,031, etc)
const uint16_t other_node = 01;   // Address of the other node in Octal format

struct payload_t {                 // Structure of our payload
  unsigned long value;
  unsigned long id;
  unsigned long type;
};

int id = 0;

/** Analog input pin that the PIR is attached too */
const int inPirDigitalPIN = 4;
const int outPirDigitalPIN = 5;

/** Value read from the pir values = HIRH/LOW */
int inPirSensorValue = 0;
int outPirSensorValue = 0;

int inPirSensorOldValue = 0;
int outPirSensorOldValue = 0;
/**
    0 - nic
    1 - wchodzi
    2 - wychodzi
*/
int state = 0;
unsigned int humanCounter =0;

/** Bool for discriminate whether high value was sended */
bool highValueWasSended = false;
char sendedValue = '6';

void setup(void)
{
  Serial.begin(57600);
  Serial.println("RF24Network/examples/helloworld_rx/");

  SPI.begin();
  radio.begin();
  network.begin(/*channel*/ 90, /*node address*/ this_node);
}

void loop(void) {

  network.update();                  // Check the network regularly


  while ( network.available() ) {     // Is there anything ready for us?

    RF24NetworkHeader header;        // If so, grab it and print it out
    payload_t payload;
    network.read(header, &payload, sizeof(payload));
    Serial.print("Received packet id");
    Serial.print(payload.id);
    Serial.print(" value ");
    Serial.print(payload.value);
    Serial.print(" type ");
    Serial.println(payload.type);
    Serial.print(" my id ");
    Serial.println(id);
    if (payload.type == 5 && id == 0) {
      id = payload.id;
      sendAck(id);
    } else if (payload.type == 0 && id == payload.id) {
      sendhumanCounterThroughRF24();
    } else if (payload.type == 3 && id == payload.id) {
      humanCounter = payload.value;
    }
  }
  /** Read PIR value every loop */
  inPirSensorValue = digitalRead(inPirDigitalPIN);
  outPirSensorValue = digitalRead(outPirDigitalPIN);

  if (inPirSensorOldValue == LOW && inPirSensorValue == HIGH && outPirSensorOldValue == LOW && outPirSensorValue == LOW) //wchodzi
    // sendValueThroughRF24(0);
    state = 1;
  else if (inPirSensorOldValue == HIGH && inPirSensorValue == HIGH && outPirSensorOldValue == LOW && outPirSensorValue == LOW) //wchodzi
    // sendValueThroughRF24(0);
    state = 1;
  else if (inPirSensorOldValue == LOW && inPirSensorValue == LOW && outPirSensorOldValue == LOW && outPirSensorValue == HIGH) //wychodzi
    //  sendValueThroughRF24(1);
    state = 2;
  else if (inPirSensorOldValue == LOW && inPirSensorValue == LOW && outPirSensorOldValue == HIGH && outPirSensorValue == HIGH) //wychodzi
    //  sendValueThroughRF24(1);
    state = 2;
  else if (inPirSensorOldValue == HIGH && outPirSensorOldValue == LOW && outPirSensorValue == HIGH && state == 1) //wszedl
    sendValueThroughRF24(1);
  else if (inPirSensorOldValue == LOW && inPirSensorValue == HIGH && outPirSensorOldValue == HIGH && state == 2) //wyszedl
    sendValueThroughRF24(0);
  else
    state = 0;

  outPirSensorOldValue = outPirSensorValue;
  inPirSensorOldValue = inPirSensorValue;
}

void sendValueThroughRF24(unsigned int val)
{
 /* if (id == '0') {
    Serial.print("NO ID! :");
    return;
  }*/
  if (val == sendedValue)
    return;
  Serial.print("SENDING :");
  if (val == 2)
    Serial.println("wchodzi");
  if (val == 3)
    Serial.println("wychodzi");
  if (val == 1) {
    Serial.println("wszedl");
    humanCounter++;
  }
  if (val == 0) {
    Serial.println("wyszedl");
    if(humanCounter > 0)  
      humanCounter--;
  }
  int result = 0;
  if (humanCounter > 0) {
    result = 1;
  }
  RF24NetworkHeader header(other_node);
  payload_t payload = {result, id, 2};
  bool ok = network.write(header, &payload, sizeof(payload));
  if (ok)
  {
    Serial.println(" SENDED.");
    sendedValue = val + 48;
  }
  else
  {
    Serial.println(" FAILED.");
  }
}

void sendhumanCounterThroughRF24()
{
  Serial.print(" human counter: ");
  Serial.print(humanCounter);
  RF24NetworkHeader header(other_node);
  payload_t payload = {humanCounter, id, 1};
  bool ok = network.write(header, &payload, sizeof(payload));
  if (ok)
  {
    Serial.println(" REPLY SENDED.");
  }
  else
  {
    Serial.println(" REPLY FAILED.");
  }
}

void sendAck(unsigned long value)
{
  Serial.print("Sending ACK...");
  payload_t payload = { value, id, 4 };
  RF24NetworkHeader header(/*to node*/ other_node);
  bool ok = network.write(header, &payload, sizeof(payload));
  if (ok)
    Serial.println("ok.");
  else
    Serial.println("failed.");
}

