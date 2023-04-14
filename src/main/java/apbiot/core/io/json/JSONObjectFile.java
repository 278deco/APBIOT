package apbiot.core.io.json;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.exceptions.JSONAssertionException;
import apbiot.core.io.objects.IOArguments;
import apbiot.core.io.objects.IOElement;
import apbiot.core.objects.enums.FileType;

public abstract class JSONObjectFile extends IOElement {
	
	protected static final Logger LOGGER = LogManager.getLogger(JSONObjectFile.class);
	private static final ObjectMapper FILES_MAPPER = new ObjectMapper().enable(DeserializationFeature.USE_LONG_FOR_INTS);
	
	private volatile Map<String, Object> dataMap;

	/**
	 * Create a new JSON file instance
	 * @param path - the path of the file
	 * @param fileName - the file name with the extension
	 */
	public JSONObjectFile(IOArguments args) {
		super(args);
		
		try {
			Files.createFile(directory.getPath().resolve(this.fileName));
			
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
	@Override
	public void saveFile() {
		preSave();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				FileWriter fw = null;
				try {
					fw = new FileWriter(directory.getPath().toFile());
					fw.write(FILES_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dataMap));
					
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
	@Override
	public void reloadFile() throws IOException {
		dataMap.clear();

		readFile();
	}
	
	/**
	 * Cast the raw file object to a JSONObject
	 * @return a JSONObject representing the file
	 */
	protected HashMap<String, Object> getData() {
		return (HashMap<String, Object>) this.dataMap;
	}

	/**
     * Cast key representing an HashMap to access data
     *
     * @return a HashMap representing a JSON Object
     */
    @SuppressWarnings("unchecked")
    protected HashMap<String, Object> getData(String src) {
        Object obj = getData().get(src);
        if(obj == null) throw new NullPointerException("The key "+ src +" doesn't exist!");
        if (obj instanceof HashMap) return (HashMap<String, Object>) obj;
        else throw new ClassCastException("The key " + src + " didn't contained a HashMap!");
    }
	
	/**
	 * Read the file's content
	 * @return the content of the json file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void readFile() throws IOException {
		try {
			this.dataMap = FILES_MAPPER.readValue(this.directory.getPath().toFile()
					, HashMap.class);
		}catch(Exception e) {
			this.dataMap = new HashMap<>();
		}
	}
	
	@Override
	public FileType getFileType() {
		return FileType.JSON;
	}
}
