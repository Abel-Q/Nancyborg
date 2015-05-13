package ia.common;

import ia.nancyborg2015.Ia;
import navigation.Point;
import roboticinception.rplidar.RpLidarHighLevelDriver;
import roboticinception.rplidar.RpLidarScan;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;

/**
 * Created by mickael on 12/05/15.
 */
public class DetectionRPLidar implements Runnable {
	private final Ia ia;
	private RpLidarHighLevelDriver rplidar;

	public DetectionRPLidar(Ia ia, String device, int baudrate) {
		this.ia = ia;
		rplidar = new RpLidarHighLevelDriver(ia);
		rplidar.initialize(device, baudrate);
		//rplidar.driver.setVerbose(true);

		new Thread(this).start();
	}

	@Override
	public void run() {
		ArrayList<Boolean> detection = new ArrayList<>();

		//detection.addAll(Arrays.asList(false, false));
		//detection.addAll(Arrays.asList(false));
		boolean stopped = false;

		for (;;) {

			RpLidarScan scan = new RpLidarScan();
			long start = System.currentTimeMillis();
			rplidar.blockCollectScan(scan, 0);
			//System.out.println("Temps scan: " + (System.currentTimeMillis() - start));

			double min = Double.MAX_VALUE;
			double min_angle = 0;
			int det = 0;

			for (int i = 0; i < scan.used.size(); i++) {
				int index = scan.used.get(i);
				float angle = index / 64f;
				float distance = scan.distance[index];
				Point pos_asserv = scan.pos_asserv[index];


				if (distance > 1000 || distance <= 120) {
					continue;
				}

				if (distance > 800) {
					continue;
				}

				if (pos_asserv.getX() < 100 || pos_asserv.getY() < 100 || pos_asserv.getX() > (3000-100) || Math.abs(pos_asserv.getY()) > (2000-100)) {
					System.out.println("Detection hors table (" + pos_asserv + ")");
					continue;
				}

				det++;

				if (distance < min) {
					min = distance;
					min_angle = angle;
				}
				//System.out.println("Angle " + angle + ": " + distance);
			}


			detection.add(det > 1);

			System.out.println(detection.toString());
			if (detection.stream().allMatch(e -> e)) {
				if (!stopped) {
					System.out.println("********* STOP ***********");
					ia.asserv.halt();
					stopped = true;
				}
			} else if (detection.stream().allMatch(e -> !e)) {
				if (stopped) {
					System.out.println("******** REPART ***********");
					stopped = false;
				}
			}

			detection.remove(0);

			if (min == Double.MAX_VALUE) {
				//System.out.println("RIEN");
			}
			else {
				//System.out.println("MIN : " + min + " at " + min_angle);
			}


			//Thread.sleep(10);
		}
	}

	public static void main(String[] args) {
		try {
			Ia ia = new Ia();
			DetectionRPLidar det = new DetectionRPLidar(ia, FileSystems.getDefault().getPath("/dev/serial/by-id/usb-Silicon_Labs_CP2102_USB_to_UART_Bridge_Controller_0001-if00-port0").toRealPath().toString(), 115200);

			System.out.println("go");
			ia.asserv.go(1500, false);

/*			System.out.println("go 0 0 ");
			ia.asserv.gotoPosition(0, 0, true);

			System.out.println("turn 180");
			ia.asserv.turn(180, true);
*/
			while (true)
				ia.sleep(1000);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
