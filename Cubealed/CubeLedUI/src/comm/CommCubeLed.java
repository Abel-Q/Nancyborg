package comm;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CommCubeLed {
    private RXTXPort port;
    private InputStream is;
    private OutputStream os;

    public CommCubeLed() throws IOException {
        try {
            CommPortIdentifier id = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
            port = (RXTXPort) id.open(id.getClass().getName(), 2000);
            port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.setDTR(false);
            port.setDTR(true);

            is = port.getInputStream();
            os = port.getOutputStream();

            os.write(0);
            Thread.sleep(1500);
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setAutomaticMode() throws IOException {
        write(0);
    }

    public void setLed(int x, int y, int z, int eclat) throws IOException {
        sendCommands(eclat | ((x & 3) << 8) | ((y & 3) << 10) | ((z & 0x3) << 12));
    }

    public void reset() throws IOException {
        sendCommands(0xC000);
    }

    public void delay(int ms) throws IOException {
        if (ms < 0 || ms > 0x3FFF) {
            throw new IllegalArgumentException("Delay must be between 0 and " + 0x3FFF + " ms");
        }

        sendCommands(0x8000 | (ms & 0x3FFF));
    }

    public void sendCommands(int... commands) throws IOException {
        int size = commands.length;

        if (size == 0) {
            throw new RuntimeException("0-size commands");
        }

        write(size);

        for (int cmd : commands) {
            cmd = cmd & 0xFFFF;
            is.read();
            os.write(cmd >> 8);
            os.write(cmd);
        }
    }

    private void write(int c) throws IOException {
        c = c & 0xFF;
        os.write(c);
        os.flush();
    }

    public static void main(String[] args) throws Exception {
        CommCubeLed comm = new CommCubeLed();

        for (;;) {
            comm.reset();

            for (int z = 0; z < 4; z++) {
                for (int y = 0; y < 4; y++) {
                    for (int x = 0; x < 4; x++) {
                        comm.setLed(x, y, z, 255);
                        Thread.sleep(0);
                    }
                }
            }
        }
    }

    public void close() {
        port.close();
    }
}
