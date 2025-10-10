import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	// Task representation
	public static class Task {
		private final int id;
		private final String payload;
		public Task(int id, String payload) { this.id = id; this.payload = payload; }
		public int getId() { return id; }
		public String getPayload() { return payload; }
	}

	// Worker runnable
	public static class Worker implements Runnable {
		private final int workerId;
		private final BlockingQueue<Task> queue;
		private final List<String> results; // synchronized list wrapper provided
		public Worker(int workerId, BlockingQueue<Task> queue, List<String> results) {
			this.workerId = workerId;
			this.queue = queue;
			this.results = results;
		}

		@Override
		public void run() {
			LOGGER.info(() -> ts() + " Worker " + workerId + " started");
			try {
				while (true) {
					Task task = queue.poll(500, TimeUnit.MILLISECONDS);
					if (task == null) {
						// graceful idle timeout indicates likely completion
						LOGGER.info(() -> ts() + " Worker " + workerId + " idle timeout, finishing");
						break;
					}
					// Simulate processing time
					try { Thread.sleep(100 + (task.getId() % 5) * 50L); } catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw ie;
					}
					String output = "worker=" + workerId + ", task=" + task.getId() + ", payload='" + task.getPayload() + "'";
					results.add(output);
					LOGGER.info(() -> ts() + " Worker " + workerId + " completed task " + task.getId());
				}
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, ts() + " Worker " + workerId + " interrupted", e);
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, ts() + " Worker " + workerId + " error", e);
			}
			LOGGER.info(() -> ts() + " Worker " + workerId + " finished");
		}
	}

	private static String ts() { return LocalDateTime.now().toString(); }

	public static void main(String[] args) {
		final int numWorkers = 4;
		final int numTasks = 20;

		BlockingQueue<Task> queue = new ArrayBlockingQueue<>(numTasks + 8);
		List<String> results = Collections.synchronizedList(new ArrayList<>());

		// Populate queue with tasks
		for (int i = 1; i <= numTasks; i++) {
			queue.add(new Task(i, "data-" + i));
		}

		ExecutorService pool = Executors.newFixedThreadPool(numWorkers);
		for (int w = 1; w <= numWorkers; w++) {
			pool.submit(new Worker(w, queue, results));
		}
		pool.shutdown();
		try {
			if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
				LOGGER.warning(ts() + " Forcing shutdownNow()");
				pool.shutdownNow();
			}
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, ts() + " awaitTermination interrupted", e);
			Thread.currentThread().interrupt();
		}

		// Display results in terminal
		System.out.println("\n=== PROCESSED TASKS RESULTS ===");
		for (String line : results) {
			System.out.println(line);
		}
		System.out.println("=== END OF RESULTS ===\n");

		LOGGER.info(ts() + " Done. processed=" + results.size());
	}
}


