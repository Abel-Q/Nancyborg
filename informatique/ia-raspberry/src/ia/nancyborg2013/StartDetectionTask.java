package ia.nancyborg2013;

public class StartDetectionTask extends Task {

	private Thread thread;

	public StartDetectionTask(Thread threadDetection) {
		this.thread = threadDetection;
	}
	@Override
	public void run() {
		this.thread.start();
		
		notifyEnd();
	}

	@Override
	public void manageDetection(boolean av, boolean ar) {
		// TODO Auto-generated method stub

	}

}
