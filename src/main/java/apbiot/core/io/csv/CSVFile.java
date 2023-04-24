package apbiot.core.io.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.io.files.TextFile;
import apbiot.core.io.objects.IOArguments;
import apbiot.core.io.objects.IOElement;
import apbiot.core.objects.enums.FileType;

public class CSVFile extends IOElement {
	
	private static final Logger LOGGER = LogManager.getLogger(TextFile.class);
	
	private CSVDocument document;
	private String separator;
	
	/**
	 * Create a new representation of a CSVFile
	 * @param args arguments required by the CSVFile class
	 * <p><strong>Required arguments :</strong><br>
	 * - The CSVfile's path<br>
	 * - The CSVfile's name</p>
	 * @param separator the separator used in the csv file
	 * @throws Exception
	 */
	public CSVFile(IOArguments args, @Nullable String separator) throws Exception {
		super(args);
		
		this.separator = separator != null ? separator : ",";
		this.document = new CSVDocument();
		
		try {
			Files.createFile(directory.getPath().resolve(this.fileName));
			
			readFile();
		}catch(IOException e) {
			LOGGER.error("Unexpected error while loading CSV file [dir: {}, name: {}] with message {}", this.directory.getName(), this.fileName, e.getMessage());
		}
	}

	/**
	 * Write all lines contained in the list
	 * @throws IOException
	 */
	@Override
	public boolean saveFile() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				FileOutputStream output = null;
				OutputStreamWriter fileWriter = null;
				BufferedWriter buffer = null;
				
				try {	
					output = new FileOutputStream(directory.getPath().toFile());
					fileWriter = new OutputStreamWriter(output, "UTF-8");
					buffer = new BufferedWriter(fileWriter);
					
					for(int i = 0; i < document.getRowCount(); i++) {
						buffer.write(formatRow(document.getRow(i), separator));
						if(i != document.getRowCount()-1) buffer.newLine();
					}
					
				}catch(IOException e) {
					LOGGER.error("Unexpected error while saving CSV file [dir: {}, name: {}] with message {}", directory.getName(), fileName, e.getMessage());
				}finally {
					try { if(output != null) output.close(); }catch(IOException e) {}
					try { if(buffer != null) buffer.close(); }catch(IOException e) {}
					try { if(fileWriter != null) fileWriter.close(); }catch(IOException e) {}
				}
				
			}
		},"File-Save-Thread").start();
		
		return true;
	}

	/**
	 * Reload the file instance running in the program
	 * Any change made to the original file that are not saved will be overwritten
	 */
	@Override
	public boolean reloadFile() {
		this.document = new CSVDocument();
		
		return readFile();
	}

	/**
	 * Read and add all file's content in a list
	 * @throws IOException
	 */
	@Override
	protected boolean readFile() {
		FileInputStream input = null;
		InputStreamReader fileReader = null;
		BufferedReader buffer = null;
		
		try {
			input = new FileInputStream(this.directory.getPath().toFile());
			fileReader = new InputStreamReader(input, "UTF-8");
			buffer = new BufferedReader(fileReader);
			
			String line;
			while( (line = buffer.readLine()) != null) {
				this.document.addRow(parseRow(line, this.separator));
			}
			
		}catch(IOException e) {
			LOGGER.error("Unexpected error while loading CSV file [dir: {}, name: {}] with message {}", this.directory.getName(), this.fileName, e.getMessage());
			
			return false;
		}finally {
			try { if(input != null) input.close(); }catch(IOException e) {}
			try { if(buffer != null) buffer.close(); }catch(IOException e) {}
			try { if(fileReader != null) fileReader.close(); }catch(IOException e) {}
		}
		
		return true;
	}
	
	private String formatRow(List<CSVCell> row, String separator) {
		final StringBuilder sb = new StringBuilder();
		row.forEach(cell -> sb.append(cell.getContent()).append(";"));
		sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}
	
	private List<CSVCell> parseRow(final  String line, final String separator) throws MissingFormatArgumentException {
		final List<CSVCell> result = new ArrayList<>();
		
		int offsetSepa = 0; int indexSepa = 0;
		int indexCancel = 0; int offsetCancel = 0;
		
		while((indexSepa = line.indexOf(separator, offsetSepa)) != -1) {
			if((indexCancel = line.indexOf("\"", offsetCancel)) != -1 && indexCancel < indexSepa) {
				final int endCancel = line.indexOf("\"",indexCancel+1)+1; //Search the second ' in the string
				if(endCancel == 0) throw new MissingFormatArgumentException("Couldn't find the second apostrophe needed to close the argument");
				
				result.add(new CSVCell(line.substring(indexCancel, endCancel)));
				offsetSepa = (endCancel==line.length() ? endCancel : endCancel+1);
				offsetCancel = endCancel;
			}else {
				result.add(new CSVCell(line.substring(offsetSepa, indexSepa)));
				offsetSepa = indexSepa+1;
			}
		}
		if(offsetSepa != line.length()) result.add(new CSVCell(line.substring(offsetSepa, line.length())));
		
		return result;
	}
	
	/**
	 * Define a new document for this CSV File
	 * @param document the new document
	 * @see apbiot.core.io.csv.CSVFile#setNewDocument(CSVDocument, boolean)
	 * @throws Exception
	 */
	public void setNewDocument(CSVDocument document) throws Exception {
		this.setNewDocument(document, false);
	}
	
	/**
	 * Define a new document for this CSV File
	 * @param document the new document
	 * @param save set it to true to automatically save the file to the disk after changing the document
	 * @throws Exception
	 */
	public void setNewDocument(CSVDocument document, boolean save) throws Exception {
		this.document = document;
		if(save) saveFile();
	}
	
	/**
	 * Get the stored document of this instance
	 * @return the stored document
	 */
	public CSVDocument getDocument() {
		return this.document;
	}
	
	@Override
	public FileType getFileType() {
		return FileType.CSV;
	}
	
}
