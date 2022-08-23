package apbiot.core.file;

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
import java.util.Optional;
import java.util.Random;

import apbiot.core.MainInitializer;
import apbiot.core.helper.StringHelper;

/**
 * Manage text file easily (like .txt file) with reading, writing methods
 * @version 1.0
 * @since 3.0
 * @author 278deco
 */
public class TextFileManager {

	private File file;
	private String path;
	private Optional<String> fileName;
	
	private List<String> content;
	
	/**
	 * Create new TextFileManager instance
	 * @param file - the text file instance
	 * @param readFileInstantly - if true the file will be read after the class has been initialized
	 * @throws IOException
	 */
	public TextFileManager(File file, boolean readFileInstantly) throws IOException {
		if(file == null) throw new IOException("Invalid or null file");
		
		this.file = file;
		this.path = file.getCanonicalPath();
		this.fileName = Optional.empty();
		this.content = new ArrayList<>();
		
		if(readFileInstantly) readTextFile();
	}
	
	/**
	 * Create new TextFileManager instance
	 * @param filePath - the text file's path
	 * @param readFileInstantly - if true the file will be read after the class has been initialized
	 * @throws IOException
	 */
	public TextFileManager(String filePath, boolean readFileInstantly) throws IOException {
		if(filePath == "" ) throw new IOException("Invalid or null file path");
		
		this.file = new File(filePath);
		this.path = filePath;
		this.fileName = Optional.empty();
		this.content = new ArrayList<>();
		
		if(readFileInstantly) readTextFile();
	}
	
	/**
	 * Create new TextFileManager instance
	 * @param filePath - the text file's path
	 * @param readFileInstantly - if true the file will be read after the class has been initialized
	 * @throws IOException
	 */
	public TextFileManager(String path, String fileName, boolean readFileInstantly) throws IOException {
		if(path == "" && fileName == "") throw new IOException("Invalid or null file path");
		String filepath = path+File.separator+fileName;
		
		this.file = new File(filepath);
		this.path = filepath;
		this.fileName = Optional.of(fileName);
		this.content = new ArrayList<>();
		
		if(readFileInstantly) readTextFile();
	}
	
	/**
	 * Read and add all file's content in a list
	 * @throws IOException
	 */
	public void readTextFile() throws IOException {
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
				MainInitializer.LOGGER.warn("Unexpected error while creating file "+this.path,e);
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
	public void saveTextFile() throws IOException {
		FileOutputStream output = null;
		OutputStreamWriter fileWriter = null;
		BufferedWriter buffer = null;
		
		try {
			output = new FileOutputStream(this.file);
			fileWriter = new OutputStreamWriter(output, "UTF-8");
			buffer = new BufferedWriter(fileWriter);
			
			for(String line : this.content) {
				buffer.write(line);
				buffer.newLine();
			}
			
		}catch(IOException e) {
			MainInitializer.LOGGER.warn("Unexpected error while writing file "+this.path,e);
		}finally {
			if(buffer != null) buffer.close();
			if(output != null) output.close();
			if(fileWriter != null) fileWriter.close();
		}
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
	 * @param i - the index of the line
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
	
	/**
	 * Get the absolute file path 
	 * @return the file path
	 */
	public String getFilePath() {
		return this.path;
	}
	
	/**
	 * Get the file name if present, else optional while be empty
	 * @return the file name
	 */
	public Optional<String> getFileName() {
		return this.fileName;
	}
	
}
