from time import sleep
import serial

SERIAL_PORT = '/dev/serial/by-id/usb-Prolific_Technology_Inc._USB-Serial_Controller-if00-port0'

ser = serial.Serial(SERIAL_PORT, 115200)

ser.sendBreak()
ser.flushOutput()
ser.flushInput()

l = 0

while True:
    ser.write(b'T')
    print('>> T')
    for z in range(4):
        ser.flush()
        s = ser.read()
        print('s=%r' % s)
        assert s == b'?'
        for y in range(4):
            print('y=%d' % y)
            for x in range(4):
                val = 255 if (z*4*4 + y*4 + x == l) else 0
                ser.write(bytes([val]))
    ser.flush()
    assert ser.read() == b'T'
    sleep(0.1)
    l = (l + 1)
