import java.io.IOException;
import java.util.ArrayList;


public class Main {

	private static int mean(ArrayList<Integer> ech) {
		int sum = 0;
		
		for (int i : ech) {
			sum += i;
		}
		
		return sum / ech.size();
	}
	
	public static void main(String[] args) throws IOException {
		int version_raspberry = RaspberryPi.getHardwareVersion();
		
		System.out.println("Version Raspberry Pi : " + version_raspberry);
		
		int dev = (version_raspberry >= 4 ? 1 : 0);
		SFR10 sfr = SFR10.scan(dev);

		System.out.println("Version SFR10 : " + sfr.getVersion());

		int nech = 3;
		ArrayList<Integer> ech = new ArrayList<Integer>();

		for (int i = 0; i < nech; i++) {
			ech.add(0);
		}

		sfr.setMaxGain(16);
		sfr.setMaxRange(255);

		for (;;) {
			ech.add(sfr.getCentimeters());
			ech.remove(0);
			
			System.out.println(mean(ech));
		}
	}
}
