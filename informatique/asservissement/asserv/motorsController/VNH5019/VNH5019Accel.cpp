#include "VNH5019Accel.h"
#include <cmath>

VNH5019Accel::VNH5019Accel(PinName INA_, PinName INB_, PinName ENDIAG_, PinName CS_, PinName PWM_)
    : Driver(INA_, INB_, ENDIAG_, CS_, PWM_),
      CurrentSpeed(0.0),
      CurrentBrake(0.0),
      RequestedSpeed(0.0),
      RequestedBrake(0.0)
{
    timer.attach_us(this, &VNH5019Accel::Interrupt, VNH5019Tick_us);
}

// set motor speed from -1.0 to +1.0
void VNH5019Accel::speed(float Speed)
{
    if (Speed < -1.0)
        Speed = 1.0;
    else if (Speed > 1.0)
        Speed = 1.0;
    RequestedSpeed = Speed;
}

// stop (no current to the motors)
void VNH5019Accel::stop()
{
    RequestedSpeed = 0.0;
}

// Brake, with strength 0..1
void VNH5019Accel::brake(float Brake)
{
    if (Brake < 0.0)
        Brake = 0;
    else if (Brake > 1.0)
        Brake = 1.0;
    RequestedBrake = Brake;
    RequestedSpeed = 0.0;
}

// returns the current through the motor, in mA
float VNH5019Accel::get_current_mA()
{
    return Driver.get_current_mA();
}

// returns true if there has been a fault
bool VNH5019Accel::is_fault()
{
    return Driver.is_fault();
}

// Clears the fault condition
// PRECONDITION: is_fault()
void VNH5019Accel::clear_fault()
{
    timer.detach();
    Driver.clear_fault();
    timer.attach_us(this, &VNH5019Accel::Interrupt, VNH5019Tick_us);
}

// disable the motor, and set outputs to zero.  This is a low power mode.
void VNH5019Accel::disable()
{
    timer.detach();
    Driver.disable();
}

// enable the motor.
void VNH5019Accel::enable()
{
    timer.detach();
    Driver.enable();
    timer.attach_us(this, &VNH5019Accel::Interrupt, VNH5019Tick_us);
}

// set the PWM period of oscillation in seconds
void VNH5019Accel::set_pwm_period(float p)
{
    Driver.set_pwm_period(p);
}

void VNH5019Accel::Interrupt()
{
    float MyRequestedSpeed = RequestedSpeed;
    if (CurrentSpeed != MyRequestedSpeed)
    {
        if (std::abs(CurrentSpeed-MyRequestedSpeed) < VNH5019ChangePerTick)
            CurrentSpeed = MyRequestedSpeed;
        else if (MyRequestedSpeed > CurrentSpeed)
            CurrentSpeed += VNH5019ChangePerTick;
        else
            CurrentSpeed -= VNH5019ChangePerTick;
        Driver.speed(CurrentSpeed);
    }
    else
    {
        float MyRequestedBrake = RequestedBrake;
        if (CurrentBrake != MyRequestedBrake)
        {
            if (std::abs(CurrentBrake-MyRequestedBrake) < VNH5019BrakeChangePerTick)
                CurrentBrake = MyRequestedBrake;
            else if (MyRequestedBrake > CurrentBrake)
                CurrentBrake += VNH5019BrakeChangePerTick;
          else
                CurrentBrake -= VNH5019BrakeChangePerTick;
            Driver.brake(CurrentBrake);
        }
    }
}

double DualVNH5019AccelMotorShield::factor = 127.0;

// User-defined pin selection.
DualVNH5019AccelMotorShield::DualVNH5019AccelMotorShield(PinName INA1_, PinName INB1_, PinName ENDIAG1_, PinName CS1_, PinName PWM1_,
                            PinName INA2_, PinName INB2_, PinName ENDIAG2_, PinName CS2_, PinName PWM2_)
 : m1(INA1_, INB1_, ENDIAG1_, CS1_, PWM1_),
   m2(INA2_, INB2_, ENDIAG2_, CS2_, PWM2_)
{
}

DualVNH5019AccelMotorShield::~DualVNH5019AccelMotorShield(){
    m1.stop();
    m2.stop();
}


void DualVNH5019AccelMotorShield::vitesseG(int vitMoteur)
{
    if (Config::reglageCodeurs)
        return;

    if (Config::inverseMoteurG) {
        vitMoteur = -vitMoteur;
    }

    if (vitMoteur > Config::V_MAX_POS_MOTOR) {
        vitMoteur = Config::V_MAX_POS_MOTOR;
    } else if (vitMoteur > 0 && vitMoteur < Config::V_MIN_POS_MOTOR) {
        vitMoteur = 0;
    }

    if (vitMoteur < Config::V_MAX_NEG_MOTOR) {
        vitMoteur = Config::V_MAX_NEG_MOTOR;
    } else if (vitMoteur < 0 && vitMoteur > Config::V_MIN_NEG_MOTOR) {
        vitMoteur = 0;
    }

    if (!Config::swapMoteurs) {
        m1.speed(vitMoteur / factor);
    } else {
        m2.speed(vitMoteur / factor);
    }

    this->vitMoteurG = vitMoteur;
}



void DualVNH5019AccelMotorShield::vitesseD(int vitMoteur)
{
    if (Config::reglageCodeurs)
        return;

    if (Config::inverseMoteurG) {
        vitMoteur = -vitMoteur;
    }

    if (vitMoteur > Config::V_MAX_POS_MOTOR) {
        vitMoteur = Config::V_MAX_POS_MOTOR;
    } else if (vitMoteur > 0 && vitMoteur < Config::V_MIN_POS_MOTOR) {
        vitMoteur = 0;
    }

    if (vitMoteur < Config::V_MAX_NEG_MOTOR) {
        vitMoteur = Config::V_MAX_NEG_MOTOR;
    } else if (vitMoteur < 0 && vitMoteur > Config::V_MIN_NEG_MOTOR) {
        vitMoteur = 0;
    }

    if (!Config::swapMoteurs) {
        m2.speed(vitMoteur / factor);
    } else {
        m1.speed(vitMoteur / factor);
    }

    this->vitMoteurD = vitMoteur;
}
