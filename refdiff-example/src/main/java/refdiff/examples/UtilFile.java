package refdiff.examples;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public class UtilFile {
	
	/**
	 * Writing the list in the CSV files, a position for line.
	 * @param fileName - File name (i.e., "output.csv")
	 * @param listMsg - Message list
	 */
	public static void writeFile(final String path, final String fileName, final List<String> listMsg){
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(path + "/" + fileName, true), "utf-8"));
			for(String msg: listMsg){
				writer.write(msg + "\n");
			}
			writer.close();
		} catch (IOException e) {
			System.err.println("Error writing results in the output file [" + fileName + "]. " + e);
		} 
	}
}
