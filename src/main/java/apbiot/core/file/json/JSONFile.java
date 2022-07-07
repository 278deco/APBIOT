package apbiot.core.file.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.MainInitializer;

public abstract class JSONFile {

	protected JSONParser parser;
	protected String path;
	
	protected JSONFileType fileType;
	private Object fileRawObject;
	
	/**
	 * Create a new JSON file instance
	 * @param path - the path of the file
	 * @param fileName - the file name with the extension
	 */
	public JSONFile(String path, String fileName, JSONFileType fileType) {
		this.parser = new JSONParser();
		this.path = path+File.separator+fileName;
		this.fileType = fileType;
		
		try {
			new File(this.path).createNewFile();
			
			fileRawObject = readFile();
			
		} catch (IOException | ParseException e) {
			MainInitializer.LOGGER.warn("Unexpected error while loading file "+this.path,e);
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
					
					ObjectMapper objMapper = new ObjectMapper();
					fw.write(objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileRawObject));
					
					fw.flush();
					fw.close();
					
					fileRawObject = readFile();
					
				} catch (IOException e) {
					MainInitializer.LOGGER.warn("Unexpected error while saving file "+path, e);
				} catch (ParseException e1) {
					MainInitializer.LOGGER.warn("Unexpected error while parsing file "+path, e1);
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
	public void reloadFile() throws IOException, ParseException {
		fileRawObject = null;
		fileRawObject = readFile();
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
	protected JSONObject getFileAsObject() {
		if(fileType != JSONFileType.OBJECT) throw new ClassCastException("Couldn't cast file object to a JSONObject");
		else return (JSONObject) fileRawObject;
	}
	
	/**
	 * Cast the raw file object to a JSONArray
	 * @return a JSONArray representing the file
	 */
	protected JSONArray getFileAsArray() {
		if(fileType != JSONFileType.ARRAY) throw new ClassCastException("Couldn't cast file object to a JSONArray");
		else return (JSONArray) fileRawObject;
	}
	
	/**
	 * Read the file's content
	 * @return the content of the json file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	protected Object readFile() throws FileNotFoundException, IOException, ParseException {
		FileReader fReader = null;
		try {
			fReader = new FileReader(this.path);
			
			return parser.parse(fReader);
		}catch(Exception e) {
			return fileType == JSONFileType.OBJECT ? new JSONObject() : new JSONArray();
		}finally {
			fReader.close();
			parser.reset();
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
