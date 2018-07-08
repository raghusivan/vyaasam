import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
 
/**
 * @author Crunchify.com
 * 
 */
 
public class CrunchifyJava8StreamReadFile {
 
	public static void main(String args[]) {
 
		String crunchifyFile = "C:\\Users\\raghus\\Documents\\NetBeansProjects\\JavaFXApplication3\\src\\filterTable\\PersonTable.fxml";
 
		// lines() and Stream Approach
		CrunchifyReadFile1(crunchifyFile);
 
		// newBufferedReader and Stream Approach
		CrunchifyReadFile2(crunchifyFile);
	}
 
	// Read file using lines() and Stream Approach
	private static void CrunchifyReadFile1(String crunchifyFile) {
 
		Stream<String> crunchifyStream = null;
		try {
 
			// Read all lines from a file as a Stream. Bytes from the file are decoded into characters using the UTF-8 charset
			crunchifyStream = Files.lines(Paths.get(crunchifyFile));
 
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		log("============= Result from lines() and Stream Approach =============");
		crunchifyStream.forEach(System.out::println);
	}
 
	// Read file using newBufferedReader and Stream Approach
	private static void CrunchifyReadFile2(String crunchifyFile) {
		List<String> crunchifyList = new ArrayList<>();
 
		BufferedReader crunchifyBufferReader = null;
		try {
 
			// newBufferedReader opens a file for reading
			crunchifyBufferReader = Files.newBufferedReader(Paths.get(crunchifyFile));
 
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		// toList: returns a Collector that accumulates the input elements into a new List
		// lines(): returns a Stream, the elements of which are lines read from this BufferedReader
		crunchifyList = crunchifyBufferReader.lines().collect(Collectors.toList());
 
		log("\n============= Result from newBufferedReader and Stream Approach =============");
 
		// forEach: performs the given action for each element of the Iterable until all elements have been processed or the
		// action throws an exception.
		crunchifyList.forEach(System.out::println);
 
	}
 
	private static void log(String string) {
		System.out.println(string);
 
	}
 
}