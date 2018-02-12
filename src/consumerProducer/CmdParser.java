package consumerProducer;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class CmdParser {

	@Option(name = "-t", usage = "Tuple Size")
	public int tupleSize;

	@Option(name = "-p", usage = "Number of items to insert in queue")
	public long producerSize;

	@Option(name = "-q", usage = "Queue Size")
	public int qSize;
	
	@Option(name = "-i", usage = "Number of iteration per experiment")
	public int iterations;
	
	public void parse(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		parser.parseArgument(args);
		return;
	}

}
