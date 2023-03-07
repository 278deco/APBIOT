package apbiot.core.io.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.helper.StringHelper;
import apbiot.core.io.IOElement;
import apbiot.core.objects.IOArguments;
import apbiot.core.objects.enums.FileType;

/**
 * Manage text file easily (like .txt file) with reading, writing methods
 * @version 1.0
 * @since 3.0
 * @author 278deco
 */
public class TextFile extends IOElement {

	private static final Logger LOGGER = LogManager.getLogger(TextFile.class);
	
	protected File file;
	
	private List<String> content;
	
	/**
	 * Create new TextFile instance
	 * @param args arguments required by the TextFile class
	 * <p><strong>Required arguments :</strong><br>
	 * - The textfile's path<br>
	 * - The textfile's name</p>
	 * @throws IOException
	 */
	public TextFile(IOArguments args) throws IOException {
		super(args);
		
		this.file = new File(this.filePath+File.separator+this.fileName);
		this.content = new ArrayList<>();
		
		readFile();
	}
	
	/**
	 * Read and add all file's content in a list
	 * @throws IOException
	 */
	@Override
	protected void readFile() throws IOException {
		FileInputStream input = null;
		InputStreamReader fileReader = null;
		BufferedReader buffer = null;
		
		try {
			input = new FileInputStream(this.file);
			fileReader = new InputStreamReader(input, "UTF-8");
			buffer = new BufferedReader(fileReader);
			
			if(!this.content.isEmpty()) this.content.clear();
			
			String line;
			while( (line = buffer.readLine()) != null) {
				this.content.add(line);
			}
			
		}catch(IOException e) {
			try {
				this.file.createNewFile();
			}catch(IOException e1) {
				LOGGER.warn("Unexpected error while reading file "+this.filePath,e);
			}
		}finally {
			if(buffer != null) buffer.close();
			if(input != null) input.close();
			if(fileReader != null) fileReader.close();
		}
	}
	
	/**
	 * Write all lines contained in the list
	 * @throws IOException
	 */
	@Override
	public void saveFile() throws IOException {
		FileOutputStream output = null;
		OutputStreamWriter fileWriter = null;
		BufferedWriter buffer = null;
		
		try {
			output = new FileOutputStream(this.file);
			fileWriter = new OutputStreamWriter(output, "UTF-8");
			buffer = new BufferedWriter(fileWriter);
			
			for(int i = 0; i < this.content.size(); i++) {
				buffer.write(this.content.get(i));
				if(i != this.content.size()-1) buffer.newLine();
			}
			
		}catch(IOException e) {
			LOGGER.warn("Unexpected error while writing file "+this.filePath,e);
		}finally {
			if(buffer != null) buffer.close();
			if(output != null) output.close();
			if(fileWriter != null) fileWriter.close();
		}
	}
	
	/**
	 * Reload the file instance running in the program
	 * Any change made to the original file that are not saved will be overwritten
	 * @throws IOException
	 */
	@Override
	public void reloadFile() throws IOException {
		this.content.clear();
		
		readFile();
	}
	
	/**
	 * Add a new line to content of the file
	 * If the file is never saved, the line while only be added to this instance of the TextFileManager
	 * @see fr.o278deco.devbot.file.TextFileManager#saveTextFile()
	 * @param lines all the line which needs to added
	 */
	public void addNewLine(String... lines) {
		for(String line : lines) {
			if(line != null && line != "" && StringHelper.deleteBlankSpaceExcess(line) != " ") this.content.add(line);
		}
	}
	
	/**
	 * Clear all file's content
	 */
	public void clearContent() {
		this.content.clear();
	}
	
	/**
	 * Return the content of the i line
	 * @param i the index of the line
	 * @return the String line
	 */
	public String getLine(int i) {
		return this.content.size() <= i ? null : this.content.get(i);
	}
	
	/**
	 * Get file's content size
	 * @return list containing the content
	 */
	public int getContentSize() {
		return this.content.size();
	}
	
	/**
	 * Get a random line of file's content
	 * @return a random string
	 */
	public String getRandomLine() {
		return getContentSize() > 0 ? getLine(new Random().nextInt(getContentSize())) : "";
	}

	@Override
	public FileType getFileType() {
		return FileType.PLAIN_TEXT;
	}
	
}
