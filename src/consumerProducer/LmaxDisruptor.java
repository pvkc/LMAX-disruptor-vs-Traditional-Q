package consumerProducer;

import java.util.concurrent.CountDownLatch;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;

public class LmaxDisruptor {
	public static long endTime;

	public static class ObjectEvent {
		// Class that defines the type of data in the queue (Ring buffer)
		@SuppressWarnings("unused")
		private Object object;

		public void set(Object obj) {
			this.object = obj;
		}
	}

	public static class ObjectEventFactory implements EventFactory<ObjectEvent> {
		// A factory so that the Ring buffer uses this for its memory allocation.
		public ObjectEvent newInstance() {
			return new ObjectEvent();
		}
	}

	public static class ObjectConsumer implements EventHandler<ObjectEvent> {
		// The consumer function to be triggered when something in the queue
		long maxSequence;
		CountDownLatch latch;

		public void setMaxItems(long maxItems) {
			maxSequence = maxItems;
		}

		public void setCountDownLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		public ObjectConsumer(long maxSequence, CountDownLatch latch) {
			this.latch = latch;
			this.maxSequence = maxSequence;
		}

		public void onEvent(ObjectEvent event, long sequence, boolean endOfBatch) throws InterruptedException {
			// Thread.sleep(1000);
			// System.out.println(Thread.currentThread());
			if (sequence == maxSequence - 1) {				
				latch.countDown();
			}
		}
	}

	public static class ObjectConsumer1 implements EventHandler<ObjectEvent> {
		public void onEvent(ObjectEvent event, long sequence, boolean endOfBatch) throws InterruptedException {
			System.out.println("Hello from Object-Comsumer-1" + Thread.currentThread());
		}
	}

	public static class ObjectProducer {
		// Each producer has a ring buffer attached to it.
		// The produced good is kept in the ring buffer.
		private final RingBuffer<ObjectEvent> ringBuffer;

		public ObjectProducer(RingBuffer<ObjectEvent> rBuff) {
			ringBuffer = rBuff;
		}

		public void onData(Object obj) {
			// Next available slot in the ring buffer.
			long sequence = ringBuffer.next();

			try {
				// Get the event/data associated with the slot
				ObjectEvent objectEvent = ringBuffer.get(sequence);
				// Update the event with new data
				objectEvent.set(obj);
			} finally {
				// Commit changes to the ring buffer
				ringBuffer.publish(sequence);
			}
		}

	}

}
