package consumerProducer;

import java.util.Vector;
import java.util.stream.DoubleStream;

/**
 * @author pvkc Class to benchmark existing blocking q implementation.
 */
public class BenchmarkBlockingQ {
	// Number of elements to put in the queue
	long PRODUCER_SIZE = 100000;
	// The size of each element to be inserted in queue
	int DATA_SIZE = 4 * 1024;
	// The max capacity (number of elem) in the queue
	int Q_SIZE = 1024;

	// Number of iterations per experiment
	int iterations;

	// The blocking queue to be used
	BufferedLinkedBlockingQueue blockingQueue;

	long startTimeBlockingQ;
	long stopTimeBlockingQ;

	/**
	 * @param producerSize:
	 *            Number of elements to put in the queue
	 * @param dataSize:
	 *            The size of each element to be inserted in queue
	 * @param qSize:
	 *            The max capacity (number of elem) in the queue
	 * @param iterations:
	 *            Number of runs of given experiment
	 */
	public BenchmarkBlockingQ(final long producerSize, final int dataSize, final int qSize, final int iterations) {
		PRODUCER_SIZE = producerSize;
		DATA_SIZE = dataSize;
		Q_SIZE = qSize;
		this.iterations = iterations;
		blockingQueue = new BufferedLinkedBlockingQueue(qSize * 8);
	}

	/**
	 * @throws Exception
	 *             Generates data for the queue. Pre-allocate byte vector and push
	 *             them to the q
	 */
	void producer() throws Exception {
		Vector<byte[]> producer_vector = new Vector<byte[]>((int) PRODUCER_SIZE);
		for (int i = 0; i < PRODUCER_SIZE; i++) {
			producer_vector.add(new byte[DATA_SIZE]);
		}

		// Start Clock before putting data in Q
		// startTimeBlockingQ.put(Thread.currentThread(), System.nanoTime());
		startTimeBlockingQ = System.nanoTime();
		for (byte[] bs : producer_vector) {
			blockingQueue.put(bs);
			// Thread.sleep(1);
		}
		blockingQueue.put(new DataEndMarker());
	}

	/**
	 * @throws Exception
	 *             Simply reads all the data from the queue.
	 */
	Object consumer() throws Exception {
		Object data = blockingQueue.take();

		// Would this be optimized out.??
		while (!(data instanceof DataEndMarker)) {
			if (data == null) {
				data = blockingQueue.take();
				continue;
			}
			data = blockingQueue.take();
		}
		stopTimeBlockingQ = System.nanoTime();

		// blockingQueue.close();
		// synchronized (System.out) {
		// System.out.println("---------Blocking Q Statistics--------------");
		// System.out.println("Data Size (Bytes/Element): " + DATA_SIZE);
		// System.out.println("Q Size (Elements): " + Q_SIZE);
		// System.out.println("Data Elements Pushed: " + PRODUCER_SIZE);
		// double latency = (stopTimeBlockingQ - startTimeBlockingQ) / 1000000000.00;
		// System.out.println("Time diff: (Latency) " + latency + "Sec");
		// System.out.println("Throughput (Per Sec.): " + PRODUCER_SIZE / latency);
		// System.out.println("---------------------------------------------");
		// }
		return data;
	}

	/**
	 * Class that implements runnable for producer of BlockingQ. Takes a
	 * BencmarkBlockingQ as input (in constructor) and runs the producer of that
	 * object.
	 */
	static class ProducerThread implements Runnable {
		BenchmarkBlockingQ blockingQ;

		public ProducerThread(BenchmarkBlockingQ bQ) {
			this.blockingQ = bQ;
		}

		public void run() {
			try {
				blockingQ.producer();
			} catch (Exception e) {
				System.out.println("Error in producer thread" + Thread.currentThread());
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Class that implements runnable for consumer of BlockingQ. Takes a
	 * BenchmarkBlockingQ as input (in constructor) and runs the consumer of that
	 * object.
	 */
	static class ConsumerThread implements Runnable {
		BenchmarkBlockingQ blockingQ;

		public ConsumerThread(BenchmarkBlockingQ bQ) {
			this.blockingQ = bQ;
		}

		public void run() {
			try {
				blockingQ.consumer();
			} catch (Exception e) {
				System.out.println("Error in blocking q conusmer thread " + Thread.currentThread());
				System.out.println(e.getMessage());
			}
		}
	}

	/*
	 * Function that handles launching of consumers and producers, and their
	 * appropriate timing
	 */
	void benchmark(int numProducers, int numConsumers) throws InterruptedException {
		// ProducerThread [] producerThreads = new ProducerThread [numProducers];
		Thread[] pThreads = new Thread[numProducers];
		Thread[] cThreads = new Thread[numConsumers];
		long startTime = 0, endTime = 0;
		double timeDiffSec;// = new double[iterations];
		double throughput; // = new double[iterations];
		System.out.println("--------BlockingQ Results-----------");

		startTime = System.currentTimeMillis();
		for (int j = 0; j < iterations; j++) {

			for (int i = 0; i < numProducers; i++) {
				// producerThreads[i] = new ProducerThread(this);
				pThreads[i] = new Thread(new ProducerThread(this));
				pThreads[i].start();
			}

			for (int i = 0; i < cThreads.length; i++) {
				cThreads[i] = new Thread(new ConsumerThread(this));
				cThreads[i].start();
			}

			for (int i = 0; i < pThreads.length; i++) {
				pThreads[i].join();
			}

			for (int i = 0; i < cThreads.length; i++) {
				cThreads[i].join();
			}

			// System.out.printf("Iteration %d, time taken %f", j, timeDiffSec[j]);
			// System.out.printf("Iteration %d, Throughput: %f (Trans/sec) \n", j,
			// throughput[j]);
		}
		endTime = System.currentTimeMillis();
		timeDiffSec = -(startTime - endTime) / 1000.00;
		throughput = PRODUCER_SIZE * iterations / timeDiffSec;

		System.out.printf("Avg. throughput of Blocking Q: %f (Trans/Sec) \n", throughput);
	}
	// --- Class ends ---
}
