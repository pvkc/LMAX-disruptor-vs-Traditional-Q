package consumerProducer;

public class ConsumerProducer {

	public static long startTime;

	public static void main(String args[]) throws Exception {

		CmdParser parser = new CmdParser();
		parser.parse(args);
		System.out.println("---------Parameters------------");
		System.out.println("Producer Size:  " + parser.producerSize + " tuples");
		System.out.println("Tuple Size:     " + parser.tupleSize + " bytes");
		System.out.println("Queue Size:     " + parser.qSize + " elements");
		System.out.println("Num Iterations: " + parser.iterations);
		System.out.println("-------------------------------");
		/*--------------------------------------------------------------------
		 * 			BENCHMARK LMAX Disruptor 
		 * -------------------------------------------------------------------*/
		BenchmarkLmaxDisruptor benchmarkLmaxDisruptor = new BenchmarkLmaxDisruptor(parser.producerSize,
				parser.tupleSize, parser.qSize, parser.iterations);
		benchmarkLmaxDisruptor.produce();
		benchmarkLmaxDisruptor.close();

		/*---------------------------------------------------------------------
		 * 				BENCHMARK BLOCKING QUEUES
		 * ------------------------------------------------------------------*/

		BenchmarkBlockingQ bQ = new BenchmarkBlockingQ(parser.producerSize, parser.tupleSize, parser.qSize,
				parser.iterations);
		
		bQ.benchmark(1, 1);

		/*
		 Thread for running producer of Blocking Q
		Thread BlockingQProducer = new Thread() {
			public void run() {
				try {
					bQ.producer();
				} catch (Exception e) {
					System.out.println("Exception in Blocking Q producer");
					System.out.println(e.getMessage());
				}
			}
		};

		 Thread for running consumer of Blocking Q
		Thread BlockingQConsumer = new Thread() {
			public void run() {
				try {
					bQ.consumer();
				} catch (Exception e) {
					System.out.println("Exception in Blocking Q consumer");
					System.out.println(e.getMessage());
				}
			}
		};

		 Start producer and Consumers
		BlockingQProducer.start();
		BlockingQConsumer.start();

		BlockingQProducer.join();
		BlockingQConsumer.join();
		System.out.println("Done");
	}
	*/
	}
}
