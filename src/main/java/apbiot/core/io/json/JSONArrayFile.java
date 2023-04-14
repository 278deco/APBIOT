package apbiot.core.io.json;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.exceptions.JSONAssertionException;
import apbiot.core.io.objects.IOArguments;
import apbiot.core.io.objects.IOElement;
import apbiot.core.objects.enums.FileType;

public abstract class JSONArrayFile extends IOElement {
	
	protected static final Logger LOGGER = LogManager.getLogger(JSONArrayFile.class);
	private static final ObjectMapper FILES_MAPPER = new ObjectMapper().enable(DeserializationFeature.USE_LONG_FOR_INTS);

	private volatile List<Object> dataList;

	/**
	 * Create a new JSON file instance
	 * @param path - the path of the file
	 * @param fileName - the file name with the extension
	 */
	public JSONArrayFile(IOArguments args) {
		super(args);
		
		try {
			Files.createFile(this.directory.getPath().resolve(this.fileName));
			
			readFile();
			reviewFormat();
			
		} catch (IOException e) {
			LOGGER.error("Unexpected error while loading JSON file [dir: {}, name: {}] with message {}", this.directory.getName(), this.fileName, e.getMessage());
		} catch(JSONAssertionException e) {
			LOGGER.warn("A JSON Assertion Exception has been thrown : {}", e.getMessage());
		}
	}
	
	/**
     * Using this function to check if the value contained in the file are those expected
     */
    public abstract void reviewFormat();
	
	/**
	 * Using this function to apply change to the content right before saving the file
	 */
	public abstract void preSave();
	
	/**
	 * Save the file and write the content
	 * @throws IOException
	 */
	public void saveFile() throws IOException {
		preSave();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				FileWriter fw = null;
				try {
					fw = new FileWriter(directory.getPath().toFile());
					fw.write(FILES_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dataList));
					
					fw.flush();
					
					readFile();
					
				} catch (IOException e) {
					LOGGER.error("Unexpected error while saving JSON file [dir: {}, name: {}] with message {}", directory.getName(), fileName, e.getMessage());
				}finally {
					try { if(fw != null) fw.close(); }catch(IOException e) {}
				}	
			}
		},"File-Save-Thread").start();
	}
	
	/**
	 * Reload the file instance running in the program
	 * Any change made to the original file that are not saved will be overwritten
	 * @throws IOException
	 */
	public void reloadFile() throws IOException {
		dataList.clear();
		
		readFile();
	}

	/**
	 * Cast the raw file object to a JSONArray
	 * @return a JSONArray representing the file
	 */
	protected ArrayList<Object> getData() {
		return (ArrayList<Object>) this.dataList;
	}

	/**
	 * Read the file's content
	 * @return the content of the json file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected void readFile() throws IOException {
		try {
			this.dataList = FILES_MAPPER.readValue(this.directory.getPath().toFile(), ArrayList.class);
		}catch(Exception e) {
			this.dataList = new ArrayList<>();
		}
	}
	
	@Override
	public FileType getFileType() {
		return FileType.JSON;
	}
}
