from time import sleep
import serial

SERIAL_PORT = '/dev/serial/by-id/usb-Prolific_Technology_Inc._USB-Serial_Controller-if00-port0'

ser = serial.Serial(SERIAL_PORT, 9600)

ser.flushOutput()
ser.flushInput()
ser.sendBreak()

s = bytes()
for it in (range(64),
           range(63, -1, -1)
           ):
    s += bytes(b'C')
    for n in it:
        for k in range(min(8, n), -1, -1):
            s += b'L' + bytes([k, int(255 * (8-k)/8)])

        s += b'S' + bytes([20])

ser.write(b'A')
ser.write(bytes([len(s) // 256, len(s) % 256]))

print(''.join('\\x' + '%.2x' % c for c in s))
print(len(s))

for i in range(0, len(s), 32):
    ser.flush()
    r = ser.read()
    print('r=%r' % (r,))
    assert(r == b'?')
    print('write=%d' % ser.write(s[i:i + 32]))

ser.flush()

assert(ser.read() == b'A')
