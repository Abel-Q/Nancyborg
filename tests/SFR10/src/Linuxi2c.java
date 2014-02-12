import java.io.Closeable;

/**
 LinuxI2C v0.1
 Basic i2c commands

 @author Peter Simon <epnomis@gmail.com>
 */

class Linuxi2c implements Closeable
{
	private static LibC clib = new LibC();
	private static final int O_RDWR = 0x02;
	private static final int I2C_SLAVE = 0x0703;  /* Use this slave address */

	private int fd = 0;

	public void open(int dev, int devAddr) {
		fd = clib.open("/dev/i2c-" + dev, O_RDWR);
		clib.ioctl(fd, I2C_SLAVE, devAddr);
	}

	public int readByte() {
		byte[] buff = new byte[1];
		clib.read(fd, buff, 1);

		return buff[0] & 0xff;
	}

	public int writeBytes(byte... bytes) {
		return clib.write(fd, bytes, bytes.length);
	}
	
	public void close() {
		clib.close(fd);
	}
}
