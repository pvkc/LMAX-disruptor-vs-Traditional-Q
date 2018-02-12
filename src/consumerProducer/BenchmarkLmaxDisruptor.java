package consumerProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.DoubleStream;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import consumerProducer.LmaxDisruptor.ObjectConsumer;
import consumerProducer.LmaxDisruptor.ObjectEvent;
import consumerProducer.LmaxDisruptor.ObjectEventFactory;
import consumerProducer.LmaxDisruptor.ObjectProducer;

public class BenchmarkLmaxDisruptor {

	// The contents of string event
	ObjectEventFactory factory = new ObjectEventFactory();

	// Ring buffer size (number of elements)
	int qSize = 1024;

	Disruptor<ObjectEvent> disruptor;

	RingBuffer<ObjectEvent> ringBuffer;

	int tupleSize = 1 * 1024;
	long producerSize = 10;
	CountDownLatch countDownLatch = new CountDownLatch(1);
	int iterations = 5;

	/**
	 * @param producerSize:
	 *            Number of elements to put in the queue
	 * @param tupleSize:
	 *            The size of each element to be inserted in queue
	 * @param qSize:
	 *            The max capacity (number of elem) in the queue
	 * @param iterations:
	 *            Number of times the experiment to be repeated
	 */

	public BenchmarkLmaxDisruptor(final long producerSize, final int tupleSize, final int qSize, final int iterations) {
		this.iterations = iterations;
		this.qSize = qSize;
		this.tupleSize = tupleSize;
		this.producerSize = producerSize;

		// Create a new disruptor instance
		disruptor = new Disruptor<>(factory, qSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE,
				new YieldingWaitStrategy());

		// Create a consumer to handle the events
		ObjectConsumer objectConsumer = new ObjectConsumer(producerSize * iterations, countDownLatch);
		// objectConsumer.setMaxItems(producerSize);

		// Point disruptor to the function to be invoked when data is put in queue.
		disruptor.handleEventsWith(objectConsumer); // , new ObjectConsumer1());

		disruptor.start();

		// Get the buffer from the disruptor, pass to the producers to fill in.
		ringBuffer = disruptor.getRingBuffer();

	}

	public void produce() throws InterruptedException {
		System.out.println("---------Disruptor Statistics--------------");

		ObjectProducer producer = new ObjectProducer(ringBuffer);

		// Pre-allocate the before timer starts
		List<byte[]> byteList = new ArrayList<byte[]>();
		for (int i = 0; i < producerSize; i++) {
			byteList.add(new byte[tupleSize]);
		}
		double diffInSec;// = new double[iterations];
		double throughput;// = new double[iterations];
		// For each iteration start the counter and push the data to the queue
		long startTimeDisruptor = System.currentTimeMillis();

		// Repeat the experiment ITERATION times
		for (int i = 0; i < iterations; i++) {
			for (byte[] bs : byteList) {
				producer.onData(bs);
			}
		}

		// Latch is released when the last consumer of the queue finishes processing.
		countDownLatch.await();

		long endTimeDisruptor = System.currentTimeMillis();

		diffInSec = (endTimeDisruptor - startTimeDisruptor) / 1000.00;
		throughput = producerSize * iterations / diffInSec;

		System.out.printf("Avg. throughput of Disruptor: %f Trans/sec \n", throughput);
		System.out.println("-------------------------------------------");
	}

	public void close() throws Exception {
		if (disruptor != null) {
			disruptor.shutdown();
		}
	}
}
