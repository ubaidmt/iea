package mx.com.sacs.bulkloader.util;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;

public class CSVUtil {
	
	public static CsvReader getCsvReader(String csvFileReference, char csvDelimeter) throws FileNotFoundException {
		CsvReader csvReader = new CsvReader (csvFileReference, csvDelimeter);
		return csvReader;
	}
	
	public static CsvWriter getCsvWriter(String csvFileReference, char csvDelimeter) {
		CsvWriter csvWriter = new CsvWriter(csvFileReference, csvDelimeter, Charset.defaultCharset());
		return csvWriter;
	}
	
	public static void writeRecord(CsvWriter csvWriter, String[] rowValues, int resultStatus, String resultMessage) throws IOException {
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < rowValues.length; i++)
			values.add(rowValues[i]);
		values.add(Integer.toString(resultStatus));
		values.add(resultMessage);
		csvWriter.writeRecord((String[]) values.toArray(new String[0]));
	}
	
	public static int getRowCount(String csvFileReference, char csvDelimeter, boolean excludeHeaders) {
		int count = 0;
		CsvReader localReader = null;
		
		try {
			
			localReader = new CsvReader (csvFileReference, csvDelimeter);
			if (excludeHeaders)
				localReader.readHeaders();
			while (localReader.readRecord())
				count++;
			
		} catch (Exception e) {
			
		} finally {
			if (localReader != null)
				localReader.close();
		}
		
		return count;
	}
		
}
