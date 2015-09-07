#include <mbed.h>
#include <mbed_rpc.h>
#include <PololuQik2.h>
#include <PID.h>
#include <QEI.h>

static const PinName TX_PIN = p9;
static const PinName RX_PIN = p10;
static const PinName RST_PIN = p11;
static const PinName ERR_PIN = p12;
static const PinName CODER_A_PIN = p13;
static const PinName CODER_B_PIN = p14;
static const float PID_PERIOD = 1e-3;
static const int TICKS_PER_TURN = 1920;

static int enable = 0;
static float motorOutput = 0;
static float processValue = 0;
static float setPoint = 0;
static float speed = 0;

static PID pid(5, 0, 0, PID_PERIOD);
static PololuQik2 qik(TX_PIN, RX_PIN, RST_PIN, ERR_PIN, NULL, false);
static QEI qei(CODER_A_PIN, CODER_B_PIN, NC, 64, QEI::X4_ENCODING);
static RpcDigitalIn endstopIn(p5, "Endstop");
static DigitalOut endstopOut(p6, 1);

Serial pc(USBTX, USBRX);

extern "C" void HardFault_Handler()
{
    error("Hardfault");
}

void run_pid() {
    static float previous_position = 0;
    if (!endstopIn.read()) {
        qei.reset();
    }
    processValue = qei.getPulses() / (float)TICKS_PER_TURN;
    speed = (processValue - previous_position) / PID_PERIOD;
    previous_position = processValue;
    pid.setProcessValue(processValue);
    pid.setSetPoint(setPoint);

    if (enable) {
        if (fabs(processValue - setPoint) < 0.01) {
            motorOutput = 0;
        } else {
            motorOutput = pid.compute();
        }
    }

    qik.setMotor0Speed(motorOutput * -30);
}

int main() {
    pc.baud(115200);
    qik.begin();


    pid.setInputLimits(-1.0, 1.0);
    pid.setOutputLimits(-1.0, 1.0);
    pid.setMode(AUTO_MODE);

    Ticker pid_ticker;
    pid_ticker.attach(run_pid, PID_PERIOD);

    RPCVariable<int> rpcEnable(&enable, "Enable");
    RPCVariable<float> rpcOutput(&motorOutput, "Output");
    RPCVariable<float> rpcSetPoint(&setPoint, "SetPoint");
    RPCVariable<float> rpcPosition(&processValue, "Position");
    RPCVariable<float> rpcSpeed(&speed, "Speed");

    RPC::add_rpc_class<RpcDigitalIn>();
    RPC::add_rpc_class<RpcDigitalOut>();
    

    for (;;) {
        char inbuff[256], outbuff[256];
        fgets(inbuff, sizeof(inbuff), stdin);
        int size = strlen(inbuff);
        for (;;) {
            if (inbuff[size - 1] == '\r' || inbuff[size - 1] == '\n')
                inbuff[size - 1] = 0;
            else
                break;
            size--;
        }

        RPC::call(inbuff, outbuff); 
        printf("%s\r\n", outbuff);
    }

    return 0;
}
