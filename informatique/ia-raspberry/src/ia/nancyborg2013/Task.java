package ia.nancyborg2013;

import java.util.LinkedList;

public abstract class Task implements Runnable {
	private Task nextTask;
	protected boolean current;
	
	public abstract void run();
	
	public void setNext(Task t) {
		nextTask = t;
	}
	
	public void notifyEnd() {
		current = false;
		System.out.println("Fin tâche...");
		if(nextTask != null) {
			System.out.println("Début du prochain...");
			nextTask.run();
			System.out.println("Fin du prochain...");
		}
		System.out.println("Fin de la fin...");
	}
	
	public abstract void manageDetection(boolean av, boolean ar);
	
	public void updateDetection(boolean av, boolean ar) {
		if(current)
			manageDetection(av, ar);
		else if(nextTask != null) nextTask.updateDetection(av, ar);
	}
}
