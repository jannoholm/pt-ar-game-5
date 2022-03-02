#include <TFT_eSPI.h>
#include <SPI.h>
#include <Button2.h>

#ifndef TFT_DISPOFF
#define TFT_DISPOFF 0x28
#endif

#ifndef TFT_SLPIN
#define TFT_SLPIN   0x10
#endif

#define TFT_MOSI            19
#define TFT_SCLK            18
#define TFT_CS              5
#define TFT_DC              16
#define TFT_RST             23

#define TFT_BL          4   // Display backlight control pin

#define BUTTON_YELLOW 2
#define BUTTON_RED 15
#define BUTTON_BLUE 13
#define BUTTON_GREEN 12

Button2 btnY(BUTTON_YELLOW);
Button2 btnR(BUTTON_RED);
Button2 btnB(BUTTON_BLUE);
Button2 btnG(BUTTON_GREEN);

TFT_eSPI tft = TFT_eSPI(135, 240); // Invoke custom library

boolean pressedButtons[4] = {false, false, false, false};

boolean fastSend = false;
unsigned long lastSend = millis();

unsigned int slowSendLoopNumber = 7;
unsigned int slowSendLoopCount = 0;
unsigned long sendInterval = 100;

void initTFT() {
    tft.init();
    tft.setRotation(1);
    tft.fillScreen(TFT_BLACK);
    tft.setTextSize(2);
    tft.setTextColor(TFT_GREEN);
    tft.setCursor(0, 0);
    tft.setTextDatum(MC_DATUM);
    tft.setTextSize(1);

    if (TFT_BL > 0) { // TFT_BL has been set in the TFT_eSPI library in the User Setup file TTGO_T_Display.h
        ledcSetup(0, 9000, 10);
        ledcAttachPin(TFT_BL, 0);
        ledcWrite(0, 100);
    }

    tft.setSwapBytes(true);
}

void pressed(Button2& btn) {
  String button = "None";
  if (btn == btnY) {
    pressedButtons[2] = true;

    tft.fillScreen(TFT_YELLOW);
  } else if (btn == btnR) {
    pressedButtons[0] = true;

    tft.fillScreen(TFT_RED);
  } else if (btn == btnB) {
    pressedButtons[1] = true;

    tft.fillScreen(TFT_BLUE);
  } else if (btn == btnG) {
    pressedButtons[3] = true;

    tft.fillScreen(TFT_GREEN);
  } else {
    tft.fillScreen(TFT_BLACK);
    tft.setTextDatum(MC_DATUM);
    tft.drawString("UNKNOWN", tft.width() / 2, tft.height() / 2 - 16);
  }
}

void released(Button2& btn) {
 if (btn == btnY) {
    pressedButtons[2] = false;
  } else if (btn == btnR) {
    pressedButtons[0] = false;
  } else if (btn == btnB) {
    pressedButtons[1] = false;
  } else if (btn == btnG) {
    pressedButtons[3] = false;
  }
}

void buttonInit() {
    btnY.setPressedHandler(pressed);
    btnR.setPressedHandler(pressed);
    btnB.setPressedHandler(pressed);
    btnG.setPressedHandler(pressed);

    btnY.setReleasedHandler(released);
    btnR.setReleasedHandler(released);
    btnB.setReleasedHandler(released);
    btnG.setReleasedHandler(released);
}

void setup() {
  // open the serial port:
  Serial.begin(115200);
  Serial.println("Starting up...");
  Serial.println("Initing TFT...");
  initTFT();
  Serial.println("Initing Buttons...");
  buttonInit();
  Serial.println("Complete...");
}

boolean hasDataToSend() {
  return pressedButtons[0] || pressedButtons[1] || pressedButtons[2] || pressedButtons[3];
}

void serialSend() {
  if (pressedButtons[0]) {
    Serial.println("R");
  }
  if (pressedButtons[1]) {
    Serial.println("B");
  }
  if (pressedButtons[2]) {
    Serial.println("Y");
  }
  if (pressedButtons[3]) {
    Serial.println("G");
  }
}

void loop() {
  btnY.loop();
  btnR.loop();
  btnB.loop();
  btnG.loop();


  unsigned long timeDelta = millis() - lastSend;

  if (!hasDataToSend()) {
      fastSend = false;
      slowSendLoopCount = 0;
  }

  if (timeDelta > sendInterval) {

    if (!fastSend && hasDataToSend()) {
      slowSendLoopCount = slowSendLoopNumber;
      fastSend = true;
      serialSend();
    }

    if (fastSend && slowSendLoopCount == 0)
      serialSend();

    if (slowSendLoopCount > 0)
      slowSendLoopCount --;

    lastSend = millis();
  }
    
}
