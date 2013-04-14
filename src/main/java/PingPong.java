import java.util.concurrent.CountDownLatch;

/**
 * Ping pong multithreading demo. ChainOfResponsiblity implementation.
 * 
 * @author taky
 */
public class PingPong implements Runnable {

	private static final CountDownLatch START_COUNT_DOWN = new CountDownLatch(2);

	private String msg;
	private Object executionEvent;

	public PingPong(String msg, Object executionEvent) {
		this.msg = msg;
		this.executionEvent = executionEvent;
	}

	/** {@inheritDoc} */
	@Override
	public void run() {
		try {
			whaitExecution();
			while (!Thread.interrupted()) {
				tellTheWorld();
				notifyTaskFinished();
			}
		} catch (InterruptedException e) {
			// can be interrupted when apocalypse happened
		}
	}

	private void whaitExecution() throws InterruptedException {
		synchronized (executionEvent) {
			START_COUNT_DOWN.countDown();
			executionEvent.wait();
		}
	}

	private void tellTheWorld() {
		System.out.println(msg);
	}

	private void notifyTaskFinished() throws InterruptedException {
		synchronized (executionEvent) {
			executionEvent.notify();
			executionEvent.wait();
		}
	}

	private void allowExecution() {
		synchronized (executionEvent) {
			executionEvent.notify();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		out("Ready... Set... Go!");
		out("");

		Object executionEvent = new Object();
		PingPong ping = new PingPong("ping", executionEvent);
		PingPong pong = new PingPong("pong", executionEvent);

		Thread pingThread = new Thread(ping);
		Thread pongThread = new Thread(pong);
		pingThread.start();
		pongThread.start();

		START_COUNT_DOWN.await();
		ping.allowExecution();
		Thread.sleep(100);

		pingThread.interrupt();
		pongThread.interrupt();

		out("Done!");
	}

	private static void out(String msg) {
		System.out.println(msg);
	}

}
