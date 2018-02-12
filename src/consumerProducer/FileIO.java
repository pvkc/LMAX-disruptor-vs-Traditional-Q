package consumerProducer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileIO {
	@SuppressWarnings("unused")
	private String fileName;
	private FileWriter fWriter;
	private FileReader fReader;
	private int REC_LEN;

	public FileIO(String fname, int recLen) throws IOException {
		// TODO Auto-generated constructor stub
		fWriter = new FileWriter(fname,true);
		fReader = new FileReader(fname);
		fileName = fname;
		REC_LEN = recLen;
	}

	public void read(List<String> buffer, int startRec, int numRec) throws IOException {		
		char [] cbuf = new char[REC_LEN];
		for (int i = 0; i < numRec; i++) {
			int bytesRead = fReader.read(cbuf, (startRec+i)*REC_LEN, REC_LEN);
			System.out.println(bytesRead);
			buffer.add(new String(cbuf));
		}
	}
	
	public void write(List<String> buffer, int startRec, int numRec) throws IOException {

		for (int i = 0; i < numRec; i++) {
			fWriter.write(buffer.get(i+startRec));
		}
		fWriter.flush();
	}

}
