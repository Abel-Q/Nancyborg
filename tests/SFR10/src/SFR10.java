
public class SFR10 {
	private static final byte REG_COMMAND = 0;
	private static final byte REG_MAXGAIN = 1;
	private static final byte REG_RANGE = 2;

	private static final byte RANGE_INCHES = 0x50;
	private static final byte RANGE_CENTIMETERS = 0x51;
	private static final byte RANGE_MICROSECONDS = 0x52;
	
	private static final byte CHG_ADDR1 = (byte) 0xA0;
	private static final byte CHG_ADDR2 = (byte) 0xAA;
	private static final byte CHG_ADDR3 = (byte) 0xA5;
	
	private Linuxi2c i2c = new Linuxi2c();
	private int i2cdev;
	private int i2caddr;
	
	public static SFR10 scan(int i2cdev) {
		for (int addr = 0x70; addr < 0x80; addr++) {
			try {
				SFR10 sfr10 = new SFR10(i2cdev, addr);
				
				if (sfr10.getVersion() > 0) {
					return sfr10;
				}
			} catch (Exception ex) {
				continue;
			}
		}
		
		return null;
	}

	public SFR10(int i2cdev, int i2caddr) {
		this.i2cdev = i2cdev;
		this.i2caddr = convertAddress(i2caddr);
		init();
	}
	
	public SFR10(int i2cdev) {
		this(i2cdev, 0xE0);
	}

	private int convertAddress(int addr) {
		if (addr >= 0xE0 && addr <= 0xFE && (addr % 2) == 0) {
			return addr >> 1;
		} else if (addr == 0 || (addr >= 0x70 && addr <= 0x7F)) {
			return addr;
		} else {
			throw new IllegalArgumentException("Invalid address");
		}
	}

	private void init() {
		i2c.open(i2cdev, i2caddr);
	}

	public int getVersion() {
		return readRegister(REG_COMMAND);
	}

	public int getInches() {
		writeRegister(0, RANGE_INCHES);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
		
		return readRegister(3) | (readRegister(2) << 8);
	}
	
	public int getMicroSeconds() {
		writeRegister(0, RANGE_MICROSECONDS);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
		
		return readRegister(3) | (readRegister(2) << 8);
	}
	
	public int getCentimeters() {
		writeRegister(REG_COMMAND, RANGE_CENTIMETERS);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
		
		return readRegister(3) | (readRegister(2) << 8);
	}
	
	public void setMaxGain(int reg_val) {
		writeRegister(REG_MAXGAIN, (byte) reg_val);
	}
	
	public void setMaxRange(int reg_val) {
		writeRegister(REG_RANGE, (byte) reg_val);
	}


	public synchronized void writeRegister(int regaddr, byte... vals) {
		byte[] towrite = new byte[vals.length + 1];
		towrite[0] = (byte) regaddr;
		System.arraycopy(vals, 0, towrite, 1, vals.length);
		i2c.writeBytes(towrite);
	}

	public synchronized int readRegister(int addr) {
		i2c.writeBytes((byte) addr);
		return i2c.readByte();
	}
	
	public void changeAddress(int newaddr) {
		newaddr = convertAddress(newaddr);

		writeRegister(REG_COMMAND, CHG_ADDR1);
		writeRegister(REG_COMMAND, CHG_ADDR2);
		writeRegister(REG_COMMAND, CHG_ADDR3);
		writeRegister(REG_COMMAND, (byte) (newaddr << 1));

		i2c.close();
		i2caddr = newaddr;
		init();
	}
}
