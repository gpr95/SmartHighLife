

#include <RF24.h>
#include <RF24Network.h>
#include <SPI.h>
#include <stdio.h>

/** nRF24L01(+) radio attached to SPI and pins 7,8 */
RF24 radio24(7, 8);
/** Network uses that radio */
RF24Network network(radio24);

/** RF24 IDs */
const uint16_t pirNodeId = 01;
const uint16_t serverNodeId = 00;

/** RF24 send scruct */
/** RF24 receive scruct */
struct payloadRF24Msg
{
  char value;
  char id;
  //n - notyfication, r - reply, g - get
  char type;
};


/** Analog input pin that the PIR is attached too */
const int inPirDigitalPIN = 4;
const int outPirDigitalPIN = 5;

/** Device id used in communication with serwer */
char id = 0;

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
int humanCounter =0;

/** Bool for discriminate whether high value was sended */
bool highValueWasSended = false;
char sendedValue = '6';


/** The setup function runs once when you press reset or power the board */
void setup()
{
  /** Initialize serial communications at 9600 bps */
  Serial.begin(9600);
  /** Initialize SPI pins for RF24 module */
  SPI.begin();
  network.begin(90, pirNodeId);
  radio24.begin();
  /** Channel 90 communication from this node */
}

/** The loop function runs over and over again forever */
void loop()
{
  /** Update network state every loop */
  network.update();

 /* if(network.available()) 
  {
    RF24NetworkHeader header;
    payloadRF24Msg message;
    network.read(header,&message,sizeof(message));

   
    if(message.id == id && message.type == 'g') 
    {
      sendhumanCounterThroughRF24();
    }
    if(message.type == 'x' && id == '0') {
      id = message.id;
      Serial.print("ID assigned! :");
    }
  }*/

  /** Read PIR value every loop */
  inPirSensorValue = digitalRead(inPirDigitalPIN);
  outPirSensorValue = digitalRead(outPirDigitalPIN);
  /*
    Serial.print("oldIN : ");
    Serial.print(inPirSensorOldValue);
    Serial.print(" IN: ");
    Serial.print(inPirSensorValue);
    Serial.print(" oldOUT: ");
    Serial.print(outPirSensorOldValue);
    Serial.print(" OUT: ");
    Serial.print(outPirSensorValue);
    Serial.print(" STATE: ");
    Serial.print(state);
    Serial.println("");*/

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
    humanCounter--;
  }
  RF24NetworkHeader header(serverNodeId);
  payloadRF24Msg payload = {val + 48, id, 'n'};
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
  RF24NetworkHeader header(serverNodeId);
  payloadRF24Msg payload = {humanCounter + 48, id, 'r'};
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

