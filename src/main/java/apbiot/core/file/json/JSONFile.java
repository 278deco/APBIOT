package apbiot.core.file.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.MainInitializer;

public abstract class JSONFile {

	private static ObjectMapper FILES_MAPPER = new ObjectMapper().enable(DeserializationFeature.USE_LONG_FOR_INTS);
	
	protected String path;
	
	private Map<String, Object> dataMap;
	private List<Object> dataList;
	
	protected JSONFileType fileType;
	
	/**
	 * Create a new JSON file instance
	 * @param path - the path of the file
	 * @param fileName - the file name with the extension
	 */
	public JSONFile(String path, String fileName, JSONFileType fileType) {
		this.path = path+File.separator+fileName;
		this.fileType = fileType;
		
		try {
			new File(this.path).createNewFile();
			
			readFile();
			
		} catch (IOException e) {
			MainInitializer.LOGGER.warn("Unexpected error while loading file "+this.path,e);
		}
	}
	
	public JSONFile(String path, String fileName) {
		this(path, fileName, JSONFileType.OBJECT);
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

					fw.write(FILES_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(fileType == JSONFileType.OBJECT ? dataMap : dataList));
					
					fw.flush();
					fw.close();
					
					readFile();
					
				} catch (IOException e) {
					MainInitializer.LOGGER.warn("Unexpected error while saving file "+path, e);
				}
				
			}
		},"File-Save-Thread").start();
	}
	
	/**
	 * Reload the file instance running in the program
	 * Any change made to the original file that are not saved will be overwritten
	 * @throws IOException
	 * @throws ParseException
	 */
	public void reloadFile() throws IOException {
		if(fileType == JSONFileType.OBJECT) dataMap.clear();
		else dataList.clear();
		
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
	protected HashMap<String, Object> getFileAsObject() {
		if(fileType != JSONFileType.OBJECT) throw new ClassCastException("Couldn't cast file object to a JSONObject");
		else return (HashMap<String, Object>) this.dataMap;
	}
	
	/**
	 * Cast the raw file object to a JSONArray
	 * @return a JSONArray representing the file
	 */
	protected ArrayList<Object> getFileAsArray() {
		if(fileType != JSONFileType.ARRAY) throw new ClassCastException("Couldn't cast file object to a JSONArray");
		else return (ArrayList<Object>) this.dataList;
	}

	/**
	 * Read the file's content
	 * @return the content of the json file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected void readFile() throws IOException {
		try {

			if(fileType == JSONFileType.OBJECT) this.dataMap = FILES_MAPPER.readValue(new File(this.path), HashMap.class);
			else this.dataList = FILES_MAPPER.readValue(new File(this.path), ArrayList.class);
			
		}catch(Exception e) {
			if(fileType == JSONFileType.OBJECT)  this.dataMap = new HashMap<>();
			else this.dataList = new ArrayList<>();
		}
	}
	
	/**
	 * Define if the JSON File's main element while be a JSON Array or JSON Object
	 * @author 278deco
	 * @see org.json.simple.JSONObject
	 * @see org.json.simple.JSONArray
	 */
	public enum JSONFileType {
		ARRAY,
		OBJECT;
	}
}
