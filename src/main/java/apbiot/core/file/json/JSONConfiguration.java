package apbiot.core.file.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.MainInitializer;

public abstract class JSONConfiguration {

	private JSONParser parser;
	private String path;
	
	private JSONObject fileObject;
	
	public JSONConfiguration(String paramPath, String fileName) {
		this.parser = new JSONParser();
		
		try {
			File dir = new File(paramPath);
			if(dir.mkdirs()) MainInitializer.LOGGER.info("Directory "+dir.getPath()+" has been successfully created !");
			
			this.path = dir.getAbsolutePath()+File.separator+fileName;
			
			new File(this.path).createNewFile();
			
			fileObject = readFile();
			
			controlRegistredProperties();
			
		} catch (IOException | ParseException e) {
			MainInitializer.LOGGER.warn("Unexpected error while loading file "+this.path,e);
		}
		
	}
	
	public abstract void controlRegistredProperties();
	
	public boolean getBooleanProperty(String propKey) {
		return (boolean)fileObject.get(propKey);
	}
	
	public String getStringProperty(String propKey) {
		return (String)fileObject.get(propKey);
	}
	
	public Long getLongProperty(String propKey) {
		return (Long)fileObject.get(propKey);
	}
	
	public boolean isExistingProperty(String propKey) {
		return fileObject.containsKey(propKey);
	}
	
	@SuppressWarnings("unchecked")
	public void setProperty(String propKey, boolean value) {
		fileObject.put(propKey, value);
	}
	
	@SuppressWarnings("unchecked")
	public void setProperty(String propKey, String value) {
		fileObject.put(propKey, value);
	}
	
	@SuppressWarnings("unchecked")
	public void setProperty(String propKey, Long value) {
		fileObject.put(propKey, value);
	}
	
	@SuppressWarnings("unchecked")
	public void setPropertyIfAbsent(String propKey, boolean value) {
		fileObject.putIfAbsent(propKey, value);
	}
	
	@SuppressWarnings("unchecked")
	public void setPropertyIfAbsent(String propKey, String value) {
		fileObject.putIfAbsent(propKey, value);
	}
	
	@SuppressWarnings("unchecked")
	public void setPropertyIfAbsent(String propKey, Long value) {
		fileObject.putIfAbsent(propKey, value);
	}
	
	public void saveFile() throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					FileWriter fw = new FileWriter(path);
					
					ObjectMapper objMapper = new ObjectMapper();
					fw.write(objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileObject));
					
					fw.flush();
					fw.close();
					
					fileObject = readFile();
					
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
		fileObject = null;
		fileObject = readFile();
	}
	
	private JSONObject readFile() throws FileNotFoundException, IOException, ParseException {
		FileReader fReader = null;
		try {
			fReader = new FileReader(this.path);
			
			return (JSONObject)parser.parse(fReader);
		}catch(Exception e) {
			return new JSONObject();
		}finally {
			fReader.close();
			parser.reset();
		}
	}
	
}
