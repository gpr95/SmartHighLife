
#include <RF24.h>
#include <RF24Network.h>
#include <SPI.h>
#include <stdio.h>

unsigned char level=0;

/** nRF24L01(+) radio attached to SPI and pins 7,8 */
RF24 radio24(7, 8);
/** Network uses that radio */
RF24Network network(radio24);

/** RF24 IDs */
const uint16_t pirNodeId = 01;
const uint16_t serverNodeId = 00;

/** RF24 send scruct */
struct payloadRF24Msg
{
  char value;
  char id;
  //n - notyfication, r - reply, g - get, p - put
  char type;
};

/** Device id used in communication with serwer */
char id = 0;




void setup(){

pinMode(3, OUTPUT);
 /** Initialize serial communications at 9600 bps */
  Serial.begin(9600);
  /** Initialize SPI pins for RF24 module */
  SPI.begin();
  network.begin(90, pirNodeId);
  radio24.begin();
  /** Channel 90 communication from this node */

}

void loop(){


network.update();

  if(network.available()) 
  {
    Serial.print("cos odebra! :");
    RF24NetworkHeader header;
    payloadRF24Msg message;
    network.read(header,&message,sizeof(message));

    /** If someone sended 1 turn the light on and write broadcast `x`*/
    if(message.id == id && message.type == 'p') 
    {
      if(message.value = '0') {
        level == 0;
      } else {
        level = 255;
      }
    }
    if(message.type == 'x') {
      id = message.id;
      Serial.print("ID assigned! :");
    }
  }

}
