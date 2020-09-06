#define fsrpin1 A0
#define fsrpin2 A5

int fsrreading1;
int fsrreading2;

bool pressed1 = false;
bool pressed2 = false;
bool started = false;
bool paused = false;

long startTime;
long duration;
int msToWait = 50;
  
 
void setup() {
  Serial.begin(9600);
}
 
void loop()
{

  if(millis() % 5000) {
    paused = false;
  }
  
  fsrreading1 = analogRead(fsrpin1);
  fsrreading2 = analogRead(fsrpin2);

  Serial.println(fsrreading1);
  Serial.println(fsrreading2);

 
  if(fsrreading1 > 200 && pressed1 == false && paused == false) {
    pressed1 = true;
    Serial.println("Button1 pressed");
    Serial.println(fsrreading1);
  } 
  
  if(fsrreading1 < 200 && pressed1 == true) {
    Serial.println("Button1 released");
    fsrreading1 = 0;
    pressed1 = false;
  } 
  
  if(fsrreading2 > 200 && pressed2 == false && paused == false) {
    pressed2 = true;
    Serial.println("Button2 pressed");
    Serial.println(fsrreading2);
  } 
  
  if(fsrreading2 < 200 && pressed2 == true) {
    Serial.println("Button2 released");
    pressed2 = false;
    fsrreading2 = 0;
    
  }

  if((pressed1 == true) && (pressed2 == true) && (started != true)) {
    started = true;
    Serial.println("Start");
    startTime = millis();
    } else if((started == true) && ((pressed1 != true) && (pressed2 != true))) {
    started = false;
    duration = millis() - startTime;
    Serial.println("Duration");
    Serial.println(duration);
    paused = true;
  }

  delay(msToWait);

}
