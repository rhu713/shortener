package shortener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class SimpleFileDatabase {
	public SimpleFileDatabase(String filename) {
		filename_ = filename;
	}
	
	public int AddRecord(String record) {
		try {
			File f = new File(filename_);
			if (!f.exists()) {
				f.createNewFile();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(f));
			int lines = 0;
			while (reader.readLine() != null) {
				lines++;
			}
			
			BufferedWriter output = new BufferedWriter(new FileWriter(f, true));
			output.append(record + "\n");
			output.close();
			
			return lines;
		} catch (IOException e) {
			return -1;
		}
	}
	
	// Returns the <record_num> record in the database. Returns null if there is no <record_num> record.
	public String GetRecord(int record_num) {
		try {
			File f = new File(filename_);
			if (!f.exists()) {
				f.createNewFile();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(f));

			int line_num = 0;
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (line_num == record_num) {
					return line;
				}
				
				line_num++;
			}

			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	private String filename_;
}
