import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaspberryPi {
	public static int getHardwareVersion() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
		Pattern patt = Pattern.compile("^Revision\\s*:\\s*([0-9a-f]+)");
		String line;
		int val = -1;

		do {
			line = reader.readLine();
			Matcher match = patt.matcher(line);
			
			if (match.matches()) {
				val = Integer.parseInt(match.group(1), 16);
				break;
			}
		} while (line != null);

		reader.close();

		return val;
	}
}
