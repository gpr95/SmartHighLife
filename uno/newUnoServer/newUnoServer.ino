#include <SPI.h>
#include <RF24Network.h>
#include <RF24.h>
#include <Ethernet.h> 
#include <EthernetUdp.h> 

/** nRF24L01(+) radio attached to SPI and pins 6,7 */
  RF24 radio(6,7);
/** Network uses that radio */
  RF24Network network(radio);

/** RF24 IDs */
  const uint16_t pirNodeId = 01;
  const uint16_t serverNodeId = 00;
  
/** RF24 receive scruct */
  struct payloadRF24Msg 
  {
    unsigned char value;
  };


/** ETHERNET COMMUNICATION */
  EthernetUDP Udp; 
  byte mac[] = { 0x00, 0xAA, 0xBB, 0xCC, 0xDE, 0x02 }; 
  unsigned int localPort = 8888;
        
  IPAddress remoteIp;
  int remotePort;
  //EthernetServer server(80);


/** Light will be setup on turn down */
bool lightIsTurnedOn = false;
int millisTurnOff = 0;





/** The setup function runs once when you press reset or power the board */
void setup() 
{
  /** Initialize serial communications at 9600 bps */
  Serial.begin(9600);
  /** Initialize SPI pins for RF24 module */
  SPI.begin();
  
  radio.begin();
  /** Channel 90 communication to this node */
  network.begin(90, serverNodeId);

  /** Relay output */
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);

  /** Turn off relay (on LOW it is turned on) */
  digitalWrite(8, HIGH); // turn OFF
  digitalWrite(9, HIGH); // turn OFF

  /** Turn off light - "click" on pilot `off` value */
  turnOffLight();

  /** Start ethernet outputs on automate DHCP IP */
  Ethernet.begin(mac); 
  Udp.begin(localPort); 

}

/** The loop function runs over and over again forever */
void loop() 
{
  /** WAIT FOR RASPBERRY TO PING */
  int packetSize = Udp.parsePacket();
  if (packetSize) {
    char packetBuffer[UDP_TX_PACKET_MAX_SIZE];
    remoteIp = Udp.remoteIP();
    Serial.print("RASPBERRY IP:");
    for (int i = 0; i < 4; i++) {
      Serial.print(remoteIp[i], DEC);
      if (i < 3) {
        Serial.print(".");
      }
    }
    remotePort = Udp.remotePort();
    // read the packet into packetBufffer
    Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    writeThroughUDP('D');
  }
  /** Update network state every loop */
//  network.update();
//  
//  /** If something is waiting in network */
//  if(network.available()) 
//  {
//    RF24NetworkHeader header;
//    payloadRF24Msg message;
//    network.read(header,&message,sizeof(message));
//
//    /** If someone sended 1 turn the light on and write broadcast `x`*/
//      writeThroughUDP(message.value);
//      Serial.println(message.value);
//  }

  // if there's data available, read a packet
  

  // if an incoming client connects, there will be bytes available to read:
//  EthernetClient client = server.available();
//  if (client) {
//    String remoteIp = "";
//    while (client.connected()) {
//      char c = client.read();
//      remoteIp += c;
//    }
//    // read bytes from the incoming client and write them back
//    // to any clients connected to the server:
//    server.write(client.read());
//  }

  /** If light is on after 5s turn it off */
  if(millis()-millisTurnOff > 5000 && lightIsTurnedOn) 
  {
    turnOffLight();
    millisTurnOff = millis();
  }

// OLD STAFF
// EthernetClient client = server.available();
//    if (client) 
//    {
//      while (client.connected()) {
//      char c = client.read();
//      if(c == 'x') 
//        turnOnLight();
//     if(c == 'o') 
//       turnOffLight();
//   }


/** 
 *    delay(1); // give the web browser time to receive the data
 *    client.stop(); // close the connection:
 */
                 
}

void turnOnLight() 
{
   if(lightIsTurnedOn)
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
   if(!lightIsTurnedOn)
     return;
   /** Turn on relay-2 - for 0.5s */
   digitalWrite(9, LOW);
   delay(500);                      
   digitalWrite(9, HIGH);
   
   lightIsTurnedOn = false;
   Serial.println("Light OFF");
}

void writeThroughUDP(unsigned char msg) 
{
    Udp.beginPacket(remoteIp, remotePort); 
    Udp.write(msg);
    Udp.endPacket();
}
