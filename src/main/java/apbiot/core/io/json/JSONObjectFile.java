package apbiot.core.io.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JSONObjectFile {
	
	private static final Logger LOGGER = LogManager.getLogger(JSONObjectFile.class);
	private static final ObjectMapper FILES_MAPPER = new ObjectMapper().enable(DeserializationFeature.USE_LONG_FOR_INTS);
	
	protected String path;
	
	private volatile Map<String, Object> dataMap;

	/**
	 * Create a new JSON file instance
	 * @param path - the path of the file
	 * @param fileName - the file name with the extension
	 */
	public JSONObjectFile(String path, String fileName) {
		this.path = path+File.separator+fileName;
		
		try {
			new File(this.path).createNewFile();
			
			readFile();
			
		} catch (IOException e) {
			LOGGER.warn("Unexpected error while loading file "+this.path,e);
		}
	}

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
				try {
					FileWriter fw = new FileWriter(path);

					fw.write(FILES_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dataMap));
					
					fw.flush();
					fw.close();
					
					readFile();
					
				} catch (IOException e) {
					LOGGER.warn("Unexpected error while saving file "+path, e);
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
		dataMap.clear();

		readFile();
	}
	
	/**
	 * Get the file path (absolute path + file name)
	 * @return a string containing path
	 */
	public String getFilePath() {
		return this.path;
	}
	
	/**
	 * Cast the raw file object to a JSONObject
	 * @return a JSONObject representing the file
	 */
	protected HashMap<String, Object> getData() {
		return (HashMap<String, Object>) this.dataMap;
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
			this.dataMap = FILES_MAPPER.readValue(new File(this.path), HashMap.class);
		}catch(Exception e) {
			this.dataMap = new HashMap<>();
		}
	}
}
