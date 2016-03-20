#ifndef DualVNH5019MotorShield_h
#define DualVNH5019MotorShield_h

#include <mbed.h>

class VNH5019
{
    public:
        VNH5019(PinName INA_, PinName INB_, PinName ENDIAG_, PinName CS_, PinName PWM_);

        // set motor speed from -1.0 to +1.0
        void speed(float Speed);

        // stop (no current to the motors)
        void stop();

        // Brake, with strength 0..1
        void brake(float Brake);

        // returns the current through the motor, in mA
        float get_current_mA();

        // returns true if there has been a fault
        bool is_fault();

        // Clears the fault condition
        // PRECONDITION: is_fault()
        void clear_fault();

        // disable the motor, and set outputs to zero.  This is a low power mode.
        void disable();

        // enable the motor.
        void enable();

        // set the PWM period of oscillation in seconds
        void set_pwm_period(float p)
        { PWM.period(p); }

    private:
        void init();

        DigitalOut   INA;
        DigitalOut   INB;
        DigitalInOut ENDIAG;
        AnalogIn     CS;
        PwmOut       PWM;
};

// Helper class for the Pololu dual VNH5019 motor shield.
// The default constructor uses the default arduino pins.
// The motors can be accessed either by .m1 or .m2, or by operator()(i) where i is 1 or 2.
class DualVNH5019MotorShield
{
   public:

    // User-defined pin selection.
      DualVNH5019MotorShield(PinName INA1_, PinName INB1_, PinName ENDIAG1_, PinName CS1_, PinName PWM1_,
                             PinName INA2_, PinName INB2_, PinName ENDIAG2_, PinName CS2_, PinName PWM2_);

      // returns the given motor object, 1 or 2.
      VNH5019& operator()(int m);

      VNH5019 m1;
      VNH5019 m2;
};

#endif
