 /////////////////////////////////////////////////////
//OP Codes
/////////////////////////////////////////////////////
//*Basic OP Codes*//
//1 - Channel 1 Red
//2 - Channel 1 Blue
//3 - Channel 1 Green
//4 - Channel 1 White
//5 - Channel 2 Red
//6 - Channel 2 Blue
//7 - Channel 2 Green
//8 - Channel 2 White
//9 - Channel 3 White Band 1
//10 - ONI
//11 - Channel 1+2 Red
//12 - Channel 1+2 Blue
//13 - Channel 1+2 Green
//14 - Channel 1+2 White
//14 - Channel 1+2+3 White

//*Super white*//
//15 - Channel 1
//16 - Channel 2
//17 - Channel 1+2+3 Slow Fade
//18 - Channel 1+2
//19 - Channel 1+2+3

//*RGBW*//
//20 - Channel 1
//22 - ONI
//21 - Channel 2
//23 - Channel 1+2

//*System*//
//25 - Firmware version

//*Context*//
//26 - Lock the current context
//27 - Restore the previously saves context
//28 - Context Dump

//*Begin Version*//
String version = "TenjinLightingSystem-b5/July-2015;";
//*End Version*//

String cmdBuffer;
char cBuffer;
int cmdsBuffer[5];
int slv;
int cmdBufferIndex = 0;

boolean contextLocked = false;
int context[9];

void setup() {
  //Channel 1
  pinMode(7, OUTPUT); //White
  pinMode(6, OUTPUT); //Blue
  pinMode(5, OUTPUT); //Red
  pinMode(4, OUTPUT); //Green
  
  //Channel 2
  pinMode(8, OUTPUT); //Red
  pinMode(9, OUTPUT); //Blue
  pinMode(10, OUTPUT); //White
  pinMode(11, OUTPUT); //Green
  
  //Channel 3
  pinMode(3, OUTPUT); //White
  
  Serial.begin(9600);
}

void loop() {
  while(Serial.available()) {
      cBuffer = Serial.read();
      
      if (cBuffer == ',') {
        cmdsBuffer[cmdBufferIndex] = cmdBuffer.toInt();
        cmdBufferIndex++;
        
        if (cmdBufferIndex > 4) {
          Serial.print("cmd_buffer_overflow_err;");
          cmdBufferIndex = 0;
          cmdBuffer = "";
          break;
        }
        
        cmdBuffer = "";
      } else if (cBuffer == ';') {
        cmdsBuffer[cmdBufferIndex] = cmdBuffer.toInt();
        cmdBufferIndex++;
        
        slv = cmdsBuffer[1];

        switch (cmdsBuffer[0]) {
            case 1:
              op_1();
              break;
            case 2:
              op_2();
              break;
            case 3:
              op_3();
              break;
            case 4:
              op_4();
              break;
            case 5:
              op_5();
              break;
            case 6:
              op_6();
              break;
            case 7:
              op_7();
              break;
            case 8:
              op_8();
              break;
            case 9:
              op_9();
              break;
            case 10:
              op_10();
              break;
            case 11:
              op_11();
              break;
            case 12:
              op_12();
              break;
            case 13:
              op_13();
              break;
            case 14:
              op_14();
              break;
            case 15:
              op_15();
              break;
            case 16:
              op_16();
              break;
            case 17:
              op_17();
              break;
            case 18:
              op_18();
              break;
            case 19:
              op_19();
              break;
            case 20:
              op_20();
              break;
            case 21:
              op_21();
              break;
            case 22:
              op_22();
              break;
            case 23:
              op_23();
              break;
            case 24:
              op_24();
              break;
            case 25:
              op_25();
              break;
            case 26:
              op_26();
              break;
            case 27:
              op_27();
              break;
            case 28:
              op_28();
              break;
          }
          
        cmdBufferIndex = 0;
        cmdBuffer = "";
      } else {
        cmdBuffer.concat(cBuffer);
      }
      
      delay(1);
  }
}

void op_1() {
  analogWrite(5, slv);

  if (!contextLocked) {
    context[1] = slv;
  }
}

void op_2() {
  analogWrite(6, slv);
  
  if (!contextLocked) {
    context[3] = slv;
  }
}

void op_3() {
  analogWrite(4, slv);

  if (!contextLocked) {
    context[2] = slv;
  }
}

void op_4() {
  analogWrite(7, slv);
  
  if (!contextLocked) {
    context[0] = slv;
  }
}

void op_5() {
  analogWrite(8, slv);

  if (!contextLocked) {
    context[5] = slv;
  }
}

void op_6() {
  analogWrite(9, slv);

  if (!contextLocked) {
    context[7] = slv;
  }
}

void op_7() {
  analogWrite(11, slv);

  if (!contextLocked) {
    context[6] = slv;
  }
}

void op_8() {
  analogWrite(10, slv);
  
  if (!contextLocked) {
    context[4] = slv;
  }
}

void op_9() {
  analogWrite(3, slv);

  if (!contextLocked) {
    context[8] = slv;
  }
}

void op_10() {
  Serial.print("op_code_not_impl");
}

void op_11() {
  analogWrite(5, slv);
  analogWrite(8, slv);

  if (!contextLocked) {
    context[1] = slv;
    context[5] = slv;
  }
}

void op_12() {
  analogWrite(6, slv);
  analogWrite(9, slv);

  if (!contextLocked) {
    context[3] = slv;
    context[7] = slv;
  }
}

void op_13() {
  analogWrite(4, slv);
  analogWrite(11, slv);

  if (!contextLocked) {
    context[2] = slv;
    context[6] = slv;
  }
}

void op_14() {
  analogWrite(7, slv);
  analogWrite(10, slv);

  if (!contextLocked) {
    context[0] = slv;
    context[4] = slv;
  }
}

void op_15() {
  analogWrite(7, slv);
  analogWrite(6, slv);
  analogWrite(5, slv);
  analogWrite(4, slv);

  if (!contextLocked) {
    context[0] = slv;
    context[1] = slv;
    context[2] = slv;
    context[3] = slv;
  }
}

void op_16() {
  analogWrite(8, slv);
  analogWrite(9, slv);
  analogWrite(10, slv);
  analogWrite(11, slv);

  if (!contextLocked) {
    context[4] = slv;
    context[5] = slv;
    context[6] = slv;
    context[7] = slv;
  }
}

void op_17() {
  if (cmdsBuffer[2]) {
    for (int i = slv; i > 0; i--) {
      analogWrite(7, i);
      analogWrite(6, i);
      analogWrite(5, i);
      analogWrite(4, i);
      analogWrite(8, i);
      analogWrite(9, i);
      analogWrite(10, i);
      analogWrite(11, i);
      analogWrite(3, i);
      
      delay(cmdsBuffer[3]);
    }
  } else {
    for (int i = 0; i < slv; i++) {
      analogWrite(7, i);
      analogWrite(6, i);
      analogWrite(5, i);
      analogWrite(4, i);
      analogWrite(8, i);
      analogWrite(9, i);
      analogWrite(10, i);
      analogWrite(11, i);
      analogWrite(3, i);
      
      delay(cmdsBuffer[3]);
    }
  }

  if (!contextLocked) {
    context[0] = slv;
    context[1] = slv;
    context[2] = slv;
    context[3] = slv;
    context[4] = slv;
    context[5] = slv;
    context[6] = slv;
    context[7] = slv;
    context[8] = slv;
  }
}

void op_18() {
  analogWrite(7, slv);
  analogWrite(6, slv);
  analogWrite(5, slv);
  analogWrite(4, slv);
  analogWrite(8, slv);
  analogWrite(9, slv);
  analogWrite(10, slv);
  analogWrite(11, slv);

  if (!contextLocked) {
    context[0] = slv;
    context[1] = slv;
    context[2] = slv;
    context[3] = slv;
    context[4] = slv;
    context[5] = slv;
    context[6] = slv;
    context[7] = slv;
  }
}

void op_19() {
  analogWrite(7, slv);
  analogWrite(6, slv);
  analogWrite(5, slv);
  analogWrite(4, slv);
  analogWrite(8, slv);
  analogWrite(9, slv);
  analogWrite(10, slv);
  analogWrite(11, slv);
  analogWrite(3, slv);

  if (!contextLocked) {
    context[0] = slv;
    context[1] = slv;
    context[2] = slv;
    context[3] = slv;
    context[4] = slv;
    context[5] = slv;
    context[6] = slv;
    context[7] = slv;
    context[8] = slv;
  }
}

void op_20() {
  analogWrite(5, slv);
  analogWrite(4, cmdsBuffer[2]);
  analogWrite(6, cmdsBuffer[3]);
  analogWrite(7, cmdsBuffer[4]);

  if (!contextLocked) {
    context[0] = cmdsBuffer[4];
    context[1] = slv;
    context[2] = cmdsBuffer[2];
    context[3] = cmdsBuffer[3];
  }
}

void op_21() {
  analogWrite(8, slv);
  analogWrite(11, cmdsBuffer[2]);
  analogWrite(9, cmdsBuffer[3]);
  analogWrite(10, cmdsBuffer[4]);

  if (!contextLocked) {
    context[4] = cmdsBuffer[4];
    context[5] = slv;
    context[6] = cmdsBuffer[2];
    context[7] = cmdsBuffer[3];
  }
}

void op_22() {
  Serial.print("op_code_not_impl");
}

void op_23() {
  analogWrite(5, slv);
  analogWrite(4, cmdsBuffer[2]);
  analogWrite(6, cmdsBuffer[3]);
  analogWrite(10, cmdsBuffer[4]);
  analogWrite(8, slv);
  analogWrite(11, cmdsBuffer[2]);
  analogWrite(9, cmdsBuffer[3]);
  analogWrite(10, cmdsBuffer[4]);

  if (!contextLocked) {
    context[0] = cmdsBuffer[4];
    context[1] = slv;
    context[2] = cmdsBuffer[2];
    context[3] = cmdsBuffer[3];
    context[4] = cmdsBuffer[4];
    context[5] = slv;
    context[6] = cmdsBuffer[2];
    context[7] = cmdsBuffer[3];
  }
}

void op_24() {
  analogWrite(3, slv);

  if (!contextLocked) {
    context[8] = slv;
  } 
}

void op_25() {
  Serial.print(version);
}

void op_26() {
  contextLocked = true;
}

void op_27() {
  contextLocked = false;

  analogWrite(3, context[8]);
  analogWrite(4, context[2]);
  analogWrite(5, context[1]);
  analogWrite(6, context[3]);
  analogWrite(7, context[0]);
  analogWrite(8, context[5]);
  analogWrite(9, context[7]);
  analogWrite(10, context[4]);
  analogWrite(11, context[6]);
}

void op_28() {
  Serial.print("CTX_" + String(context[0]) + "," + String(context[1]) + "," + String(context[2]) + "," + String(context[3]) + "," + String(context[4]) + "," + String(context[5]) + "," + String(context[6]) + "," + String(context[7]) + "," + String(context[8]) + ";");
}
