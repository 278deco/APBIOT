package apbiot.core.io.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.io.IOElement;
import apbiot.core.objects.IOArguments;

public abstract class JSONConfiguration extends IOElement {
	
	private static final Logger LOGGER = LogManager.getLogger(JSONConfiguration.class);
	private static final ObjectMapper CONFIGURATION_MAPPER = new ObjectMapper().enable(DeserializationFeature.USE_LONG_FOR_INTS);
	
	private volatile Map<String, Object> data;
	
	public JSONConfiguration(IOArguments args) {
		super(args);
		
		try {
			new File(this.filePath+File.separator+this.fileName).createNewFile();
			
			readFile();
			
			controlRegistredProperties();
			
		} catch (IOException e) {
			LOGGER.warn("Unexpected error while loading file "+this.filePath,e);
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
	
	@Override
	public void saveFile() throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					FileWriter fw = new FileWriter(filePath);
					
					fw.write(CONFIGURATION_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data));
					
					fw.flush();
					fw.close();
					
					readFile();
					
				} catch (IOException e) {
					LOGGER.warn("Unexpected error while saving file "+filePath, e);
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
		data.clear();
		readFile();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void readFile() throws IOException {
		try {
			this.data = CONFIGURATION_MAPPER.readValue(new File(this.filePath), HashMap.class);
		}catch(DatabindException e) {
			this.data = new HashMap<>();
		}
	}
	
}
