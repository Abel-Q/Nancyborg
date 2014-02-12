import com.sun.jna.*;

class LibC implements Library
{
	public native int ioctl(int fd, int cmd, int arg) throws LastErrorException;
	public native int ioctl(int fd, int cmd, Pointer arg) throws LastErrorException;
	public native int open(String path, int flags) throws LastErrorException;
	public native int close(int fd) throws LastErrorException;
	public native NativeLong write(int fd, byte[] buffer, NativeLong count) throws LastErrorException;
	public native NativeLong read(int fd,  byte[] buffer, NativeLong count) throws LastErrorException;

	static {
		Native.register("c");
	}

	public int write(int fd, byte[] buffer, int len)
	{
		return write(fd, buffer, new NativeLong(len)).intValue();
	}
	
	public int read(int fd, byte[] buffer, int len) {
		return read(fd, buffer, new NativeLong(len)).intValue();
	}
}
