int ledPin = 13; // Set the pin to digital I/O 13
boolean ledState = LOW;
const byte numChars = 64;
char receivedCommand[numChars]; // an array to store the received data

boolean newData = false;

void setup() {
  pinMode(ledPin, OUTPUT); // Set pin as OUTPUT
  Serial.begin(9600);
  Serial.println("arduino:SETUP");
}

void loop() {
  recvWithEndMarker();
  processNewData();
}

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
  ledState = !ledState; //flip the ledState
  digitalWrite(ledPin, ledState); 
  if (command == "HORN") {
    commandHandled = true;
  } else if (command == "BELL") {
    commandHandled = true;
  } else {
    // NO HANDLER
  }
  Serial.print("arduino:ECHO:");
  if (!commandHandled) {
    Serial.print("(no_handler):");
  }
  Serial.println(receivedCommand);
}
