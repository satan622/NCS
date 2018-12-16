#include <SoftwareSerial.h>

#define RED_PIN 8
#define SOUND_PIN A1
#define NOISE_PIN A0
#define LVONE   20// 수정
#define LVTWO   40//
#define LVTHREE 60//
#define LVFOUR  80//
#define LVFIVE  100//
#define DB      60//데시벨 기준

SoftwareSerial bt(0, 1);
  
void setup() {
  Serial.begin(9600);
  bt.begin(9600);
  pinMode(RED_PIN, OUTPUT);
}

void loop() {
  bt.write("P101");
  levelingNoisewave();
  decibel();
  delay(1000);
  
}

void decibel(){
  int sound, db, volt;
  double cal;
  sound = analogRead(SOUND_PIN);
  volt = sound * 100;
  cal = log10 (volt);
  db = 2*(cal * 10); 
  bt.write(db);

  if(db>=DB) {
    digitalWrite(RED_PIN, HIGH);
    delay(1000);
    digitalWrite(RED_PIN, LOW);
  }
}

void levelingNoisewave(){//블루투스로 진동값 레벨 전달
  int noisewave;
  noisewave = analogRead(NOISE_PIN); // 진동값 측정
  if(noisewave >= LVONE) bt.write("LV1");
  else if(noisewave >= LVTWO) bt.write("LV2");
  else if(noisewave >= LVTHREE) bt.write("LV3");
  else if(noisewave >= LVFOUR) bt.write("LV4");
  else if(noisewave >= LVFIVE) bt.write("LV5");
}
