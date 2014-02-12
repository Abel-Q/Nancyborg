package api.gpio;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import api.communication.LibC;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * Classe générique de controle d'un GPIO
 * 
 * @author GaG <francois.prugniel@esial.net>
 * @author mickael
 * @version 1.0
 * 
 */

public class Gpio implements Closeable {

	/**
	 * Numéro du GPIO (<a
	 * href="http://elinux.org/RPi_Low-level_peripherals#Introduction"
	 * >http://elinux.org/RPi_Low-level_peripherals#Introduction</a>)
	 */
	private int num;

	/**
	 * Sens du GPIO : true = entrée, false = sortie
	 */
	private boolean entree;

	/**
	 * Constructeur d'un GPIO
	 * 
	 * @param num Numéro du GPIO
	 * @param entree Sens du GPIO
	 * @param pull une des constantes {@link #PULL_UP}, {@link #PULL_DOWN} et
	 *            {@link #FLOATING}
	 * @see #setPull(int)
	 * @throws IOException
	 */
	public Gpio(int num, boolean entree, int pull) throws IOException {
		this.num = num;
		this.entree = entree;
		this.configGpio();
		this.setPull(pull);
	}

	/**
	 * Constructeur d'un GPIO
	 * 
	 * @param num Numéro du GPIO
	 * @param entree Sens du GPIO
	 * @throws IOException
	 */
	public Gpio(int num, boolean entree) throws IOException {
		this.num = num;
		this.entree = entree;
		this.configGpio();
	}

	/**
	 * Initialisation du GPIO.
	 * <p>
	 * Cette méthode est appelé par le constructeur, il est donc inutile de la
	 * rappeler ensuite
	 * 
	 * @throws IOException
	 */
	public void configGpio() throws IOException {
		System.out.println("Configuration Gpio " + this.num);

		// On export le GPIO
		Files.write(Paths.get("/sys/class/gpio/export"), ("" + this.num).getBytes());

		// On set la direction
		String direction;
		if (this.entree) {
			direction = "in";
		} else {
			direction = "out";
		}

		Files.write(Paths.get("/sys/class/gpio/gpio" + this.num + "/direction"), direction.getBytes());
	}

	/**
	 * Fermeture du GPIO.
	 * <p>
	 * Appelez cette méthode à la fin du programme pour fermez proprement le
	 * GPIO
	 */
	public void close() {
		System.out.println("Fermeture Gpio " + this.num);

		// On unexport le GPIO
		try {
			Files.write(Paths.get("/sys/class/gpio/unexport"), ("" + this.num).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	/**
	 * @return Le GPIO est-il a 1 ?
	 * @throws IOException
	 */
	public boolean isHigh() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("/sys/class/gpio/gpio" + this.num + "/value"), Charset.defaultCharset());
		return lines.get(0).equals("1");
	}

	/**
	 * @return Le GPIO est-il a 0 ?
	 * @throws IOException
	 */
	public boolean isLow() throws IOException {
		return !this.isHigh();
	}

	/**
	 * Met le GPIO a 1
	 * 
	 * @throws IOException
	 */
	public void setHigh() throws IOException {
		Files.write(Paths.get("/sys/class/gpio/gpio" + this.num + "/value"), ("1").getBytes());
	}

	/**
	 * Met le GPIO a 0
	 * 
	 * @throws IOException
	 */
	public void setLow() throws IOException {
		Files.write(Paths.get("/sys/class/gpio/gpio" + this.num + "/value"), ("0").getBytes());
	}

	// Attention, pour ceux qui n'aiment pas le bas niveau,
	// ça devient hautement gore sous cette ligne.

	/**
	 * L'entrée est en l'air (flottante)
	 */
	public static final int FLOATING = 0;

	/**
	 * L'entrée est reliée à la masse par une résistance interne
	 */
	public static final int PULL_DOWN = 1;

	/**
	 * L'entrée est reliée au 3.3 V par une résistance interne
	 */
	public static final int PULL_UP = 2;

	private static final int BCM2708_PERI_BASE = 0x20000000;
	private static final int GPIO_BASE = (BCM2708_PERI_BASE + 0x200000);
	private static final int PULLUPDN_OFFSET = 37;  // 0x0094 / 4
	private static final int PULLUPDNCLK_OFFSET = 38;  // 0x0098 / 4

	private static final int PAGE_SIZE = (4 * 1024);
	private static final int BLOCK_SIZE = (4 * 1024);

	private static final int PROT_READ = 0x1; /* Page can be read.  */
	private static final int PROT_WRITE = 0x2; /* Page can be written.  */

	private static final int MAP_SHARED = 0x01; /* Share changes.  */
	private static final int MAP_FIXED = 0x10; /* Interpret addr exactly.  */

	/**
	 * Permet d'activer les résistances de pull-up et de pull-down sur le GPIO
	 * 
	 * @param pull une des constantes {@link #PULL_UP}, {@link #PULL_DOWN} et
	 *            {@link #FLOATING}
	 */
	public void setPull(int pull) {
		int fd = LibC.open("/dev/mem", LibC.O_RDWR | LibC.O_SYNC);

		Pointer gpio_mem = new Memory(BLOCK_SIZE + (PAGE_SIZE - 1));

		long align = Pointer.nativeValue(gpio_mem) % PAGE_SIZE;

		if (align != 0) {
			gpio_mem = gpio_mem.share(PAGE_SIZE - align);
		}

		Pointer gpio_map = LibC.mmap(gpio_mem, new NativeLong(BLOCK_SIZE), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_FIXED, fd, new NativeLong(GPIO_BASE));

		int clk_offset = PULLUPDNCLK_OFFSET + (this.num / 32);
		int shift = this.num % 32;

		gpio_map.setInt(4 * PULLUPDN_OFFSET, (gpio_map.getInt(4 * PULLUPDN_OFFSET) & ~3) | pull);

		gpio_map.setInt(4 * clk_offset, 1 << shift);

		gpio_map.setInt(4 * PULLUPDN_OFFSET, gpio_map.getInt(4 * PULLUPDN_OFFSET) & ~3);
		gpio_map.setInt(4 * clk_offset, 0);

		LibC.munmap(gpio_map, new NativeLong(BLOCK_SIZE));

		LibC.close(fd);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("-- Test GPIO --");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		for (String arg : args) {
			int n = Integer.parseInt(arg);

			Gpio gpio = new Gpio(n, true);

			System.out.println("Pull up...");
			gpio.setPull(Gpio.PULL_UP);
			in.readLine();

			System.out.println("Pull down...");
			gpio.setPull(Gpio.PULL_DOWN);
			in.readLine();

			System.out.println("En l'air...");
			gpio.setPull(Gpio.FLOATING);
			in.readLine();

			gpio.close();
		}
	}
}
