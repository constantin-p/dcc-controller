#define DCC_PIN  4 // DCC out

// DCC_PACKET Timer
#define TIMER_SHORT 0x8D               // 58  usec pulse length 141 255-141 = 114
#define TIMER_LONG  0x1B               // 116 usec pulse length  27 255- 27 = 228
#define PREAMBLE  0                    // definitions for state machine
#define SEPERATOR 1                    // definitions for state machine
#define SENDBYTE  2                    // definitions for state machine

unsigned char last_timer = TIMER_SHORT;  // store last timer value
unsigned char flag = 0;                  // used for short or long pulse
bool second_isr = false;                 // pulse up or down
unsigned char state = PREAMBLE;
unsigned char preamble_count = 16;
unsigned char outbyte = 0;
unsigned char cbit = 0x80;

// DCC_PACKET Structure
#define MAXMSG  2
struct Message {
  unsigned char data[7];
  unsigned char len;
};

struct Message msg[MAXMSG] = { 
  { { 0xFF,   0,   0xFF, 0, 0, 0, 0}, 3}, // idle msg
  { { 0,      0,      0, 0, 0, 0, 0}, 3}  // command message
  //  ^byte1, ^byte2, ^byte3
};
                                
int msgIndex = 0;  
int byteIndex = 0;



void SetupDCCTimer() {
  //Timer2 Settings: Timer Prescaler /8, mode 0
  //Timer clock = 16MHz/8 = 2MHz oder 0,5usec 
  TCCR2A = 0; //page 203 - 206 ATmega328/P
  
  TCCR2B = 2; //Page 206
  
  /*     bit 2     bit 1     bit0
          0         0         0       Timer/Counter stopped 
          0         0         1       No Prescaling
          0         1         0       Prescaling by 8
          0         0         0       Prescaling by 32
          1         0         0       Prescaling by 64
          1         0         1       Prescaling by 128
          1         1         0       Prescaling by 256
          1         1         1       Prescaling by 1024
  */
  TIMSK2 = 1<<TOIE2;   // Timer2 Overflow Interrupt Enable - page 211 ATmega328/P   
  TCNT2 = TIMER_SHORT; // load the timer for its first cycle
}

// DCC_PACKET Interrupt handler
ISR(TIMER2_OVF_vect) { // Timer2 overflow interrupt vector handler
  // Capture the current timer value TCTN2. This is how much error we have
  // due to interrupt latency and the work in this function
  // Reload the timer and correct for latency.  
  unsigned char latency;
  
  
  if (second_isr) {
    // for every second interupt just toggle signal
    digitalWrite(DCC_PIN, 1); // 1 is high, 0 is low
    second_isr = false;    
    latency = TCNT2;    // set timer to last value
    TCNT2 = latency + last_timer; 
  } else  {  // != every second interrupt, advance bit or state
    digitalWrite(DCC_PIN,0);
    second_isr = true;
    switch(state) {
      case PREAMBLE:
        flag=1; // short pulse
        preamble_count--;
        if (preamble_count == 0) {  
          //Serial.print(" ");
          state = SEPERATOR; // advance to next state
          msgIndex++; // get next message
          if (msgIndex >= MAXMSG) {  
            msgIndex = 0; 
          }  
          byteIndex = 0; //start msg with byte 0
        }
        break;
      case SEPERATOR:
        // Serial.print(" ");
        flag = 0; // long pulse and then advance to next state
        state = SENDBYTE; // goto next byte ...
        outbyte = msg[msgIndex].data[byteIndex];
        cbit = 0x80;  // send this bit next time first
        break;
      case SENDBYTE:
        if ((outbyte & cbit) != 0) { 
          flag = 1;  // send short pulse
        } else {
          flag = 0;  // send long pulse
        }
        cbit = cbit >> 1;
        if (cbit == 0) {  // last bit sent 
          byteIndex++;
          //Serial.print(" ");
          if (byteIndex >= msg[msgIndex].len) { // is there a next byte?  
            // this was already the XOR byte then advance to preamble
            // ONLY FOR DEBUG
            // Serial.println();
            state = PREAMBLE;
            preamble_count = 16;

          } else { // send separtor and advance to next byte
            state = SEPERATOR;
          }
        }
        break;
    } // end switch   

    if (flag) {  // data = 1 short pulse
      latency = TCNT2;
      TCNT2 = latency + TIMER_SHORT;
      last_timer = TIMER_SHORT;
      // ONLY FOR DEBUG
      // Serial.print("1");
    } else {   // data = 0 long pulse
      latency = TCNT2;
      TCNT2 = latency + TIMER_LONG; 
      last_timer = TIMER_LONG;
      // ONLY FOR DEBUG
      // Serial.print("0");
    } 
  }
}

void assemble_dcc_package(byte addr, byte data) {
  byte addr_XOR_data = addr ^ data;
  Serial.print("byte1:");
  Serial.print(addr, BIN);
  Serial.print(":byte2:");
  Serial.print(data, BIN);
  Serial.print(":byte3[");
  Serial.print(addr_XOR_data, BIN);
  Serial.println("]");

  // Edit DCC PACKAGE
  noInterrupts();
  msg[1].data[0] = addr; 
  msg[1].data[1] = data;
  msg[1].data[2] = addr_XOR_data;
  interrupts();
}

// DCC_FUNCTIONS
void sendMsg_ESTOP(byte address) { // DCC_CTRL:ESTOP
  Serial.print(">>: (msg sendMsg_ESTOP:" + (String)address +") | ");

  // 8: 0
  // 7: 1 (Speed & direction)
  // 6: 0-> reverse | 1-> forward
  // 5: 0 ? (S1)
  // 4: S5      0       0
  // 3: S4      0       0
  // 2: S3      0       0
  // 1: S2      1       0
  //       eStop^,  stop^

  //             pos  8765 4321
  // 0x61 = 0000 0000 0110 0001
  byte data = 0x61;
  assemble_dcc_package(address, data);
}

void sendMsg_STOP(byte address) { // DCC_CTRL:STOP
  Serial.print(">>: (msg sendMsg_STOP:" + (String)address +") | ");


  //             pos  8765 4321
  // 0x60 = 0000 0000 0110 0000
  byte data = 0x60;
  assemble_dcc_package(address, data);
}

void sendMsg_REVERSE(byte address, byte speed) { // DCC_CTRL:REVERSE
  Serial.print(">>: (msg sendMsg_REVERSE:" + (String)address + ":speed:" + (String)speed + ") | ");

  //             pos  8765 4321
  // 0x40 = 0000 0000 0100 0000
  byte data = 0x40;
  
  //             pos  8765 4321
  // 0x0F = 0000 0000 0000 1111
  data = data | (0x0F & speed);
  assemble_dcc_package(address, data);;
}

void sendMsg_FORWARD(byte address, byte speed) { // DCC_CTRL:FORWARD
  Serial.print(">>: (msg sendMsg_FORWARD:" + (String)address + ":speed:" + (String)speed + ") | ");

  //             pos  8765 4321
  // 0x60 = 0000 0000 0110 0000
  byte data = 0x60;

  //             pos  8765 4321
  // 0x0F = 0000 0000 0000 1111
  data = data | (0x0F & speed);
  assemble_dcc_package(address, data);
}

void sendMsg_HEADLIGHT(byte address) { // DCC_CTRL:HEADLIGHT
  Serial.print(">>: (msg sendMsg_HEADLIGHT:" + (String)address +") | ");
  
  // 8: 1
  // 7: 0 (Functions)
  // 6: 0
  // 5: LIGHT 
  // 4: BELL
  // 3: HORN1
  // 2: HORN2
  // 1: SOUND

  //             pos  8765 4321
  // 0x90 = 0000 0000 1001 0000
  byte data = 0x90;
  assemble_dcc_package(address, data);
} 

void sendMsg_BELL(byte address) { // DCC_CTRL:BELL
  Serial.print(">>: (msg sendMsg_BELL:" + (String)address +") | ");

  //             pos  8765 4321
  // 0x88 = 0000 0000 1000 1000
  byte data = 0x88;
  assemble_dcc_package(address, data);
}

void sendMsg_HORN(byte address) { // DCC_CTRL:HORN
  Serial.print(">>: (msg sendMsg_HORN:" + (String)address +") | ");
  
  //             pos  8765 4321
  // 0x84 = 0000 0000 1000 1000
  byte data = 0x84;
  assemble_dcc_package(address, data);
}

void sendMsg_HORN_VARIANT(byte address) { // DCC_CTRL:HORN_VARIANT
  Serial.print(">>: (msg sendMsg_HORN_VARIANT:" + (String)address +") | ");

  // 8: 1
  // 7: 0 (Functions)
  // 6: 0
  // 5: LIGHT 
  // 4: BELL
  // 3: HORN1
  // 2: HORN2
  // 1: SOUND
  
  //             pos  8765 4321
  // 0x82 = 0000 0000 1000 0010
  byte data = 0x82;
  assemble_dcc_package(address, data);
}

void sendMsg_SOUND(byte address) { // DCC_CTRL:SOUND
  Serial.print(">>: (msg sendMsg_SOUND:" + (String)address +") | ");
  
  //             pos  8765 4321
  // 0x81 = 0000 0000 1000 0001
  byte data = 0x81;
  assemble_dcc_package(address, data);
}

void sendMsg_SWITCH_ON(byte address) { // DCC_CTRL:SWITCH_ON
  Serial.print(">>: (msg sendMsg_SWITCH_ON:" + (String)address +") | ");
  
  // 8: 1
  // 7: 0
  // 6: A6
  // 5: A5
  // 4: A4
  // 3: A3
  // 2: A2
  // 1: A1
  
//  byte addr = ((address / 4) + 1) & 63;
//
//  byte data = 0x81;
//
//  //             pos  8765 4321
//  // 0x80 = 0000 0000 1000 0000
//  
//  0x80
}

void sendMsg_SWITCH_OFF(byte address) { // DCC_CTRL:SWITCH_OFF
  Serial.print(">>: (msg sendMsg_SWITCH_OFF:" + (String)address +") | ");
 
} 

// DCC_CONTROLLER Message System
int ledPin = 13; // Set the pin to digital I/O 13
boolean ledState = LOW;
const byte numChars = 64;
char receivedCommand[numChars]; // an array to store the received data

boolean newData = false;

void recvWithEndMarker() {
  static byte ndx = 0;
  char endMarker = '\n';
  char rc;

  while (Serial.available() > 0 && newData == false) {
    rc = Serial.read();
    if (rc != endMarker) {
      receivedCommand[ndx] = rc;
      ndx++;
      if (ndx >= numChars) {
        ndx = numChars - 1;
      }
    } else {
      receivedCommand[ndx] = '\0'; // terminate the string
      ndx = 0;
      newData = true;
    }
  }
}

void processNewData() {
  if (newData == true) {
    ledState = !ledState; //flip the ledState
    digitalWrite(ledPin, ledState); 
  
 
    dispatchCommand(receivedCommand);
    newData = false;
  }
}

void dispatchCommand(String command) {
  bool commandHandled = false;

  String commandSource = getValue(command, ':', 0);
  String commandAddress = getValue(command, ':', 1);
  String commandType = getValue(command, ':', 2);

  int commandAddressINT = commandAddress.toInt();
  if (commandAddressINT < 1 || commandAddressINT > 127) { // 127 >= value >= 1
    Serial.println("INVALID_ADDRESS arduino:ECHO:" + (String)receivedCommand);
    return;
  }
  
  if (commandSource == "DCC_CTRL") {
    Serial.print("received: DCC_CTRL address:" + (String)commandAddressINT + " type: " + commandType + " | ");
  
    if (commandType == "ESTOP") {
      commandHandled = true;
      Serial.print("(handle " + commandType +") | ");
      // handle STOP message
      sendMsg_ESTOP(commandAddressINT);
    } else if (commandType == "STOP") {
      commandHandled = true;
      Serial.print("(handle " + commandType +") | ");
      // handle STOP message
      sendMsg_STOP(commandAddressINT);
    } else if (commandType == "REVERSE" || commandType == "FORWARD") {
      int commandArgINT = getValue(command, ':', 3).toInt();
      if (commandArgINT < 0 || commandArgINT >= 16) { // 16 steps (0..15)
        Serial.println("INVALID_SPEED arduino:ECHO:" + (String)receivedCommand);
        return;
      }


      if (commandType == "REVERSE") {
        commandHandled = true;
        Serial.print("speed: " + (String)commandArgINT + " (handle " + commandType +") | ");
        // handle REVERSE message
        sendMsg_REVERSE(commandAddressINT, commandArgINT);
      } else { // commandType == "FORWARD"
        commandHandled = true;
        Serial.print("speed: " + (String)commandArgINT + " (handle " + commandType +") | ");
        // handle FORWARD message
        sendMsg_FORWARD(commandAddressINT, commandArgINT);
      }
    } else if (commandType == "BELL") {
      commandHandled = true;
      Serial.print("(handle " + commandType +") | ");
      // handle BELL message
      sendMsg_BELL(commandAddressINT);
    } else if (commandType == "HORN") {
      commandHandled = true;
      Serial.print("(handle " + commandType +") | ");
      // handle HORN message
      sendMsg_HORN(commandAddressINT);
    } else if (commandType == "HORN_VARIANT") {
      commandHandled = true;
      Serial.print("(handle " + commandType +") | ");
      // handle HORN_VARIANT message
      sendMsg_HORN_VARIANT(commandAddressINT);
    } else if (commandType == "SOUND") {
      commandHandled = true;
      Serial.print("(handle " + commandType +") | ");
      // handle SOUND message
      sendMsg_SOUND(commandAddressINT);
    } else if (commandType == "HEADLIGHT") {
      commandHandled = true;
      Serial.print("(handle " + commandType +") | ");
      // handle HEADLIGHT message
      sendMsg_HEADLIGHT(commandAddressINT);
    }
  }

  
  Serial.print("arduino:ECHO:");
  if (!commandHandled) {
    Serial.print("(no_handler):");
  }
  Serial.println(receivedCommand);
}

// HELPERS
String getValue(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i = 0; i <= maxIndex && found <= index; i++){
    if(data.charAt(i) == separator || i == maxIndex){
      found++;
      strIndex[0] = strIndex[1]+1;
      strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found > index
    ? data.substring(strIndex[0], strIndex[1])
    : "\0";
}

// SETUP & LOOP
void setup() {
  pinMode(ledPin, OUTPUT); // Set pin as OUTPUT (inidicate activity)
  pinMode(DCC_PIN, OUTPUT);
  Serial.begin(9600);
  Serial.println("arduino:SETUP");
  SetupDCCTimer();
}

void loop() {
  recvWithEndMarker();
  processNewData();
}

