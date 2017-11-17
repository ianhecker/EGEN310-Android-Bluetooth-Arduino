//Constants Declaration
#define BRAKE 0
#define CW    1
#define CCW   2
#define MOTOR_1 0
#define MOTOR_2 1

//Motor 1
//Clockwise
#define MOTOR_A1_PIN 7
//Counterclockwise
#define MOTOR_B1_PIN 8

//Motor 2
//Clockwise
#define MOTOR_A2_PIN 4
//Counterclockwise
#define MOTOR_B2_PIN 9

//Speed of motors 1 & 2
#define PWM_MOTOR_1 5
#define PWM_MOTOR_2 6

//Current sensor? IDK
#define CURRENT_SEN_1 A2
#define CURRENT_SEN_2 A3

//Enable for motors
#define EN_PIN_1 A0
#define EN_PIN_2 A1

//Speed variables
short pwm = 25;

//Direction variables
unsigned short bothMotorDir = BRAKE;
unsigned short leftMotorDir = BRAKE;
unsigned short rightMotorDir = BRAKE;

//Bluetooth data
char incomingByte; // incoming data
String pwmTemp;


void setup()                         
{
  //Set Pins as OUTPUT
  pinMode(MOTOR_A1_PIN, OUTPUT);
  pinMode(MOTOR_B1_PIN, OUTPUT);

  pinMode(MOTOR_A2_PIN, OUTPUT);
  pinMode(MOTOR_B2_PIN, OUTPUT);

  pinMode(PWM_MOTOR_1, OUTPUT);
  pinMode(PWM_MOTOR_2, OUTPUT);

  pinMode(CURRENT_SEN_1, OUTPUT);
  pinMode(CURRENT_SEN_2, OUTPUT);  

  pinMode(EN_PIN_1, OUTPUT);
  pinMode(EN_PIN_2, OUTPUT);

  //Start Bluetooth data transmission at 9600 Baud
  Serial.begin(9600);
}

void loop() 
{
  
  while(Serial.available())
  {

    //If data received:
    if (Serial.available() > 0) {
      //Store incoming data as char
      incomingByte = Serial.read();
    
      //Enable motors
      digitalWrite(EN_PIN_1, HIGH);
      digitalWrite(EN_PIN_2, HIGH); 
       

      switch(incomingByte)
      {
        case 'A':
          Forward();
          break;
          
        case 'B':
          Backward();
          break;
          
        case 'C':
          Right();
          break;
          
        case 'D':
          Left();
          break;
          
        case 'E':
          Stop();
          break;

        case 'Q':
          pwm = 25;      
          break;
          
        case 'R':
          pwm = 50;
          break;
          
        case 'S':
          pwm = 75;        
          break;
          
        case 'T':
          pwm = 100;        
          break;
          
        case 'U':
          pwm = 125;
          break;
          
        case 'V':
          pwm = 150;        
          break;
          
        case 'W':
          pwm = 175;        
          break;
          
        case 'X':
          pwm = 200;        
          break;
          
        case 'Y':
          pwm = 225;       
          break;
        
        case 'Z':
          pwm = 250;       
          break;
      }
    }
  }
}

void Stop()
{
  bothMotorDir = BRAKE;
  motorGo(MOTOR_1, bothMotorDir, 0);
  motorGo(MOTOR_2, bothMotorDir, 0);
}

void Forward()
{
  bothMotorDir = CW;
  motorGo(MOTOR_1, bothMotorDir, pwm);
  motorGo(MOTOR_2, bothMotorDir, pwm);
}

void Backward()
{
  bothMotorDir = CCW;
  motorGo(MOTOR_1, bothMotorDir, pwm);
  motorGo(MOTOR_2, bothMotorDir, pwm);
}

void Right()
{
  leftMotorDir = CW;
  rightMotorDir = CCW;
  motorGo(MOTOR_1, leftMotorDir, pwm);
  motorGo(MOTOR_2, rightMotorDir, pwm);
}

void Left()
{
  leftMotorDir = CCW;
  rightMotorDir = CW;
  motorGo(MOTOR_1, leftMotorDir, pwm);
  motorGo(MOTOR_2, rightMotorDir, pwm);  
}

void motorGo(uint8_t motor, uint8_t direct, uint8_t pwm)         //Function that controls the variables: motor(0 ou 1), direction (cw ou ccw) e pwm (entra 0 e 255);
{
  if(motor == MOTOR_1)
  {
    if(direct == CW)
    {
      digitalWrite(MOTOR_A1_PIN, LOW); 
      digitalWrite(MOTOR_B1_PIN, HIGH);
    }
    else if(direct == CCW)
    {
      digitalWrite(MOTOR_A1_PIN, HIGH);
      digitalWrite(MOTOR_B1_PIN, LOW);      
    }
    else
    {
      digitalWrite(MOTOR_A1_PIN, LOW);
      digitalWrite(MOTOR_B1_PIN, LOW);            
    }
    
    analogWrite(PWM_MOTOR_1, pwm); 
  }
  else if(motor == MOTOR_2)
  {
    if(direct == CW)
    {
      digitalWrite(MOTOR_A2_PIN, LOW);
      digitalWrite(MOTOR_B2_PIN, HIGH);
    }
    else if(direct == CCW)
    {
      digitalWrite(MOTOR_A2_PIN, HIGH); //Problem Child, writing to HIGH causes both direction
      digitalWrite(MOTOR_B2_PIN, LOW);      
    }
    else
    {
      digitalWrite(MOTOR_A2_PIN, LOW);
      digitalWrite(MOTOR_B2_PIN, LOW);            
    }
    
    analogWrite(PWM_MOTOR_2, pwm);
  }
}


