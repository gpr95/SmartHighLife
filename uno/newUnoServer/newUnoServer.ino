#include <SPI.h>
#include <stdio.h>
#include <RF24Network.h>
#include <RF24.h>
#include <Ethernet.h>
#include <EthernetUdp.h>

/** nRF24L01(+) radio attached to SPI and pins 6,7 */
RF24 radio(6, 7);
/** Network uses that radio */
RF24Network network(radio);

/** RF24 IDs */
const uint16_t serverNodeId = 00;

/** RF24 receive scruct */
struct payloadRF24Msg
{
  char value;
  int id;
  //n - notyfication, r - reply, g - get
  char type;
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
    remotePort = Udp.remotePort();
    /** Read the packet into packetBufffer */
    Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
    for (int i = 0; i < packetSize; i++) {
      Serial.print(packetBuffer[i]);
    }
    Serial.println(" ");

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
        turnOnLight();
        turnOffLight();
        turnOnLight();
        writeThroughUDP('D');
        break;
      case 'n':
        turnOnLight();
        turnOffLight();
        turnOnLight();
        writeThroughUDP('D');
        break;
      case 'F':
        turnOffLight();
        writeThroughUDP('D');
        break;
      case 'f':
        turnOffLight();
        writeThroughUDP('D');
        break;
      case 'G':
        getResource(packetBuffer, packetSize);
        break;
      case 'g':
        getResource(packetBuffer, packetSize);
        break;

    }

  }
  /** Update network state every loop */
  network.update();

  /** If something is waiting in network */
  if (network.available())
  {
    RF24NetworkHeader header;
    payloadRF24Msg message;
    network.read(header, &message, sizeof(message));

    /** If someone sended 1 turn the light on and write broadcast `x`*/
    //        writeThroughUDP(message.value);
    //        Serial.println(message.value);
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
  char resourceId[serverMsgSize];
  for (int i = 1; i < serverMsgSize; i++)
  {
    resourceId[i - 1] = serverMsg[i];
  }
  resourceId[serverMsgSize - 1] = '\0';

  Serial.println(" ");
  int id ;
  id = atoi(resourceId);
  Serial.print(id);
  writeThroughUDP('D');
  writeIdThroughRF24('x', id);
}

void getResource(char serverMsg[], int serverMsgSize) {
  char resourceId[serverMsgSize];
  for (int i = 1; i < serverMsgSize; i++)
  {
    resourceId[i - 1] = serverMsg[i];
  }
  resourceId[serverMsgSize - 1] = '\0';

  Serial.println(" ");
  int id ;
  id = atoi(resourceId);
  Serial.print(id);

  if (id < 40000) {
    writeIdThroughRF24('g', id);
    network.update();

    /** If something is waiting in network */
    if (network.available())
    {
      RF24NetworkHeader header;
      payloadRF24Msg message;
      network.read(header, &message, sizeof(message));

      /** If someone sended 1 turn the light on and write broadcast `x`*/
      writeThroughUDP(message.value);
      Serial.println(message.value);
    }
  }
  else {
    if (lightIsTurnedOn)
      writeThroughUDP('N');
    else
      writeThroughUDP('F');
  }

  writeThroughUDP('D');
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

void writeThroughUDP(unsigned char msg)
{
  Udp.beginPacket(remoteIp, remotePort);
  Udp.write(msg);
  Udp.endPacket();
}

void writeIdThroughRF24(char type, int id)
{
  RF24NetworkHeader header(01);
  payloadRF24Msg payload = {48, id, type};
  bool ok = network.write(header, &payload, sizeof(payload));
  if (ok)
  {
    Serial.println("ID SENDED.");
  }
  else
  {
    Serial.println(" FAILED.");
  }
}
