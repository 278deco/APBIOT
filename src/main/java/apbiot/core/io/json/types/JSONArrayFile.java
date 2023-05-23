package apbiot.core.io.json.types;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
			final Path temp = directory.getPath().resolve(this.fileName);
			
			if(!Files.exists(temp))
				Files.createFile(temp);
			
			readFile();
			reviewFormat();
			
		} catch (IOException e) {
			LOGGER.error("Unexpected error while loading JSON file [dir: {}, name: {}] with error {} and message {}", this.directory.getName(), this.fileName, e.getClass().getName(), e.getMessage());
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
	 * Save the file and write the content on the disk<br>
	 * This method will always return true as the file is saved in his own thread
	 * @return true
	 */
	@Override
	public boolean saveFile(boolean forceSave) {
		preSave();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				FileWriter fw = null;
				try {
					fw = new FileWriter(directory.getPath().resolve(fileName).toFile());
					fw.write(FILES_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dataList));
					
					fw.flush();
					
					readFile();
					
				} catch (IOException e) {
					LOGGER.error("Unexpected error while saving JSON file [dir: {}, name: {}] with error {} and message {}", directory.getName(), fileName, e.getClass().getName(), e.getMessage());
				}finally {
					try { if(fw != null) fw.close(); }catch(IOException e) {}
				}	
			}
		},"File-Save-Thread").start();
		
		return true;
	}
	
	/**
	 * Reload the file instance running in the program<br>
	 * Any change made to the original file that are not saved will be overwritten
	 * @return true if the file has been correctly reloaded
	 */
	@Override
	public boolean reloadFile() {
		dataList.clear();
		
		return readFile();
	}

	/**
	 * Get the data stored in the JSON file
	 * @return a HashMap of the content
	 */
	protected List<Object> getData() {
		return (List<Object>) this.dataList;
	}

	/**
	 * Read JSON file's content
	 * @return true if the file has been read successfully
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean readFile() {
		try {
			this.dataList = Collections.synchronizedList(FILES_MAPPER.readValue(this.directory.getPath().resolve(fileName).toFile(), ArrayList.class));
			if(this.dataList == null) throw new NullPointerException();
			
			return true;
		}catch(Exception e) {
			this.dataList = Collections.synchronizedList(new ArrayList<>());
			
			return false;
		}
	}
	
	@Override
	public FileType getFileType() {
		return FileType.JSON;
	}
}
