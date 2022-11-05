package apbiot.core.io.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.MainInitializer;

public abstract class JSONConfiguration {

	private static ObjectMapper CONFIGURATION_MAPPER = new ObjectMapper().enable(DeserializationFeature.USE_LONG_FOR_INTS);
	
	private String path;
	
	private Map<String, Object> data;
	
	public JSONConfiguration(String paramPath, String fileName) {

		try {
			File dir = new File(paramPath);
			if(dir.mkdirs()) MainInitializer.LOGGER.info("Directory "+dir.getPath()+" has been successfully created !");
			
			this.path = dir.getAbsolutePath()+File.separator+fileName;
			
			new File(this.path).createNewFile();
			
			this.data = readConfiguration();
			
			controlRegistredProperties();
			
		} catch (IOException e) {
			MainInitializer.LOGGER.warn("Unexpected error while loading file "+this.path,e);
		}
		
	}
	
	protected abstract void controlRegistredProperties();
	
	protected boolean getBooleanProperty(String propKey) {
		return (boolean)this.data.get(propKey);
	}
	
	protected String getStringProperty(String propKey) {
		return (String)this.data.get(propKey);
	}
	
	protected Long getLongProperty(String propKey) {
		return (Long)this.data.get(propKey);
	}
	
	protected Integer getIntegerProperty(String propKey) {
		return ((Long)this.data.get(propKey)).intValue();
	}
	
	protected boolean isExistingProperty(String propKey) {
		return this.data.containsKey(propKey);
	}
	
	protected void setProperty(String propKey, boolean value) {
		this.data.put(propKey, value);
	}
	
	protected void setProperty(String propKey, String value) {
		this.data.put(propKey, value);
	}
	
	protected void setProperty(String propKey, Long value) {
		this.data.put(propKey, value);
	}
	
	protected void setProperty(String propKey, Integer value) {
		this.data.put(propKey, value);
	}
	
	protected void setPropertyIfAbsent(String propKey, boolean value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	protected void setPropertyIfAbsent(String propKey, String value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	protected void setPropertyIfAbsent(String propKey, Long value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	protected void setPropertyIfAbsent(String propKey, Integer value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	public void saveConfiguration() throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					FileWriter fw = new FileWriter(path);
					
					fw.write(CONFIGURATION_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data));
					
					fw.flush();
					fw.close();
					
					data = readConfiguration();
					
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
	public void reloadConfiguration() throws IOException {
		data.clear();
		this.data = readConfiguration();
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> readConfiguration() throws IOException {
		try {
			return CONFIGURATION_MAPPER.readValue(new File(this.path), HashMap.class);
		}catch(DatabindException e) {
			return new HashMap<>();
		}
	}
	
}
