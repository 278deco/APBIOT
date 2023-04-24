package apbiot.core.io.json;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.io.objects.IOArguments;
import apbiot.core.io.objects.IOElement;

public abstract class JSONProperties extends IOElement {
	
	protected static final Logger LOGGER = LogManager.getLogger(JSONProperties.class);
	private static final ObjectMapper CONFIGURATION_MAPPER = new ObjectMapper().enable(DeserializationFeature.USE_LONG_FOR_INTS);
	
	private volatile Map<String, Object> data;
	
	public JSONProperties(IOArguments args) {
		super(args);
		
		try {
			final Path temp = directory.getPath().resolve(this.fileName);
			
			if(!Files.exists(temp))
				Files.createFile(temp);
			
			readFile();
			
			controlRegistredProperties();
			
		} catch (IOException e) {
			LOGGER.error("Unexpected error while loading JSON file [dir: {}, name: {}] with error {} and message {}", this.directory.getName(), this.fileName, e.getClass().getName(), e.getMessage());
		}
		
	}
	
	/**
     * Using this function to check if the value contained in the file are those expected
     */
	protected abstract void controlRegistredProperties();
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a boolean, will throw a {@link ClassCastException}
	 * @param propKey The name of the property
	 * @return the value of the property as a boolean
	 * @throws ClassCastException
	 */
	protected boolean getBooleanProperty(String propKey) throws ClassCastException {
		return (boolean)this.data.get(propKey);
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a string, will throw a {@link ClassCastException}
	 * @param propKey The name of the property
	 * @return the value of the property as a string
	 * @throws ClassCastException
	 */
	protected String getStringProperty(String propKey) throws ClassCastException {
		return (String)this.data.get(propKey);
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a long, will throw a {@link ClassCastException}
	 * @param propKey The name of the property
	 * @return the value of the property as a long
	 * @throws ClassCastException
	 */
	protected Long getLongProperty(String propKey) throws ClassCastException {
		return (Long)this.data.get(propKey);
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a integer, will throw a {@link ClassCastException}<br>
	 * @implNote As all number are stored using {@link Long}, any integer stored is read
	 * as a long before being casted
	 * @param propKey The name of the property
	 * @return the value of the property as a integer
	 * @throws ClassCastException
	 */
	protected Integer getIntegerProperty(String propKey) throws ClassCastException {
		return ((Long)this.data.get(propKey)).intValue();
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a float, will throw a {@link ClassCastException}<br>
	 * @param propKey The name of the property
	 * @return the value of the property as a float
	 * @throws ClassCastException
	 */
	protected Float getFloatProperty(String propKey) throws ClassCastException {
		return ((Float)this.data.get(propKey));
	}
	
	/**
	 * Check if a property exist with this key
	 * @param propKey The name of the property
	 * @return if it exist or not
	 */
	protected boolean isExistingProperty(String propKey) {
		return this.data.containsKey(propKey);
	}
	
	/**
	 * Create a new boolean property in the configuration<br>
	 * Will override any existing property with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setPropertyIfAbsent(String, boolean)
	 * @see JSONProperties#setPropertyIfDifferent(String, boolean, BiPredicate)
	 */
	protected void setProperty(String propKey, boolean value) {
		this.data.put(propKey, value);
	}
	
	/**
	 * Create a new string property in the configuration<br>
	 * Will override any existing property with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setPropertyIfAbsent(String, String)
	 * @see JSONProperties#setPropertyIfDifferent(String, String, BiPredicate)
	 */
	protected void setProperty(String propKey, String value) {
		this.data.put(propKey, value);
	}
	
	/**
	 * Create a new long property in the configuration<br>
	 * Will override any existing property with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setPropertyIfAbsent(String, Long)
	 * @see JSONProperties#setPropertyIfDifferent(String, Long, BiPredicate)
	 */
	protected void setProperty(String propKey, Long value) {
		this.data.put(propKey, value);
	}
	
	/**
	 * Create a new integer property in the configuration<br>
	 * Will override any existing property with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setPropertyIfAbsent(String, Integer)
	 * @see JSONProperties#setPropertyIfDifferent(String, Integer, BiPredicate)
	 */
	protected void setProperty(String propKey, Integer value) {
		this.data.put(propKey, value);
	}
	
	/**
	 * Create a new float property in the configuration<br>
	 * Will override any existing property with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setPropertyIfAbsent(String, Float)
	 * @see JSONProperties#setPropertyIfDifferent(String, Float, BiPredicate)
	 */
	protected void setProperty(String propKey, Float value) {
		this.data.put(propKey, value);
	}
	
	/**
	 * Create a new boolean property in the configuration<br>
	 * Won't be added if another property exist with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setProperty(String, boolean)
	 * @see JSONProperties#setPropertyIfDifferent(String, boolean, BiPredicate)
	 */
	protected void setPropertyIfAbsent(String propKey, boolean value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	/**
	 * Create a new string property in the configuration<br>
	 * Won't be added if another property exist with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setProperty(String, String)
	 * @see JSONProperties#setPropertyIfDifferent(String, String, BiPredicate)
	 */
	protected void setPropertyIfAbsent(String propKey, String value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	/**
	 * Create a new long property in the configuration<br>
	 * Won't be added if another property exist with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setProperty(String, Long)
	 * @see JSONProperties#setPropertyIfDifferent(String, Long, BiPredicate)
	 */
	protected void setPropertyIfAbsent(String propKey, Long value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	/**
	 * Create a new integer property in the configuration<br>
	 * Won't be added if another property exist with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setProperty(String, Integer)
	 * @see JSONProperties#setPropertyIfDifferent(String, Integer, BiPredicate)
	 */
	protected void setPropertyIfAbsent(String propKey, Integer value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	/**
	 * Create a new float property in the configuration<br>
	 * Won't be added if another property exist with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setProperty(String, Float)
	 * @see JSONProperties#setPropertyIfDifferent(String, Float, BiPredicate)
	 */
	protected void setPropertyIfAbsent(String propKey, Float value) {
		this.data.putIfAbsent(propKey, value);
	}
	
	/**
	 * Create a new boolean property in the configuration<br>
	 * If the property doesn't exist, it is created. Otherwise, the function checks
	 * if the new value fit the {@link BiPredicate} and replace the old value by the new one if it true<br>
	 * The file's property will always be the second argument of the predicate, the method argument will always be first
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @param predicate The predicate chosen to compare the two values for the property
	 * @see JSONProperties#setProperty(String, boolean)
	 * @see JSONProperties#setPropertyIfAbsent(String, boolean)
	 */
	protected void setPropertyIfDifferent(String propKey, boolean value, BiPredicate<Boolean, Boolean> predicate) {
		if(!this.data.containsKey(propKey)) this.data.put(propKey, value);
		try {
			if(predicate.test(value, this.getBooleanProperty(propKey))) this.data.put(propKey, value);
		}catch(ClassCastException e) { }
	}
	
	/**
	 * Create a new string property in the configuration<br>
	 * If the property doesn't exist, it is created. Otherwise, the function checks
	 * if the new value fit the {@link BiPredicate} and replace the old value by the new one if it true<br>
	 * The file's property will always be the second argument of the predicate, the method argument will always be first
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @param predicate The predicate chosen to compare the two values for the property
	 * @see JSONProperties#setProperty(String, String)
	 * @see JSONProperties#setPropertyIfAbsent(String, String)
	 */
	protected void setPropertyIfDifferent(String propKey, String value, BiPredicate<String, String> predicate) {
		if(!this.data.containsKey(propKey)) this.data.put(propKey, value);
		try {
			if(predicate.test(value, this.getStringProperty(propKey))) this.data.put(propKey, value);
		}catch(ClassCastException e) { }
	}
	
	/**
	 * Create a new long property in the configuration<br>
	 * If the property doesn't exist, it is created. Otherwise, the function checks
	 * if the new value fit the {@link BiPredicate} and replace the old value by the new one if it true<br>
	 * The file's property will always be the second argument of the predicate, the method argument will always be first
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @param predicate The predicate chosen to compare the two values for the property
	 * @see JSONProperties#setProperty(String, Long)
	 * @see JSONProperties#setPropertyIfAbsent(String, Long)
	 */
	protected void setPropertyIfDifferent(String propKey, Long value, BiPredicate<Long, Long> predicate) {
		if(!this.data.containsKey(propKey)) this.data.put(propKey, value);
		try {
			if(predicate.test(value, this.getLongProperty(propKey))) this.data.put(propKey, value);
		}catch(ClassCastException e) { }
	}
	
	/**
	 * Create a new integer property in the configuration<br>
	 * If the property doesn't exist, it is created. Otherwise, the function checks
	 * if the new value fit the {@link BiPredicate} and replace the old value by the new one if it true<br>
	 * The file's property will always be the second argument of the predicate, the method argument will always be first
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @param predicate The predicate chosen to compare the two values for the property
	 * @see JSONProperties#setProperty(String, Integer)
	 * @see JSONProperties#setPropertyIfAbsent(String, Integer)
	 */
	protected void setPropertyIfDifferent(String propKey, Integer value, BiPredicate<Integer, Integer> predicate) {
		if(!this.data.containsKey(propKey)) this.data.put(propKey, value);
		try {
			if(predicate.test(value, this.getIntegerProperty(propKey))) this.data.put(propKey, value);
		}catch(ClassCastException e) { }
	}
	
	/**
	 * Create a new float property in the configuration<br>
	 * If the property doesn't exist, it is created. Otherwise, the function checks
	 * if the new value fit the {@link BiPredicate} and replace the old value by the new one if it true<br>
	 * The file's property will always be the second argument of the predicate, the method argument will always be first
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @param predicate The predicate chosen to compare the two values for the property
	 * @see JSONProperties#setProperty(String, Float)
	 * @see JSONProperties#setPropertyIfAbsent(String, Float)
	 */
	protected void setPropertyIfDifferent(String propKey, Float value, BiPredicate<Float, Float> predicate) {
		if(!this.data.containsKey(propKey)) this.data.put(propKey, value);
		try {
			if(predicate.test(value, this.getFloatProperty(propKey))) this.data.put(propKey, value);
		}catch(ClassCastException e) { }
	}
	
	/**
	 * Save the file and write the content on the disk<br>
	 * This method will always return true as the file is saved in his own thread
	 * @return true
	 */
	@Override
	public boolean saveFile() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					FileWriter fw = new FileWriter(directory.getPath().resolve(fileName).toFile());
					
					fw.write(CONFIGURATION_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(data));
					
					fw.flush();
					fw.close();
					
					readFile();
					
				} catch (IOException e) {
					LOGGER.error("Unexpected error while saving JSON Configuration [dir: {}, name: {}] with error {} and message {}", directory.getName(), fileName, e.getClass().getName(), e.getMessage());
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
		data.clear();
		
		return readFile();
	}
	
	/**
	 * Read JSON file's content
	 * @return true if the file has been read successfully
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean readFile() {
		try {
			this.data = CONFIGURATION_MAPPER.readValue(directory.getPath().resolve(fileName).toFile(), HashMap.class);
			
			return true;
		}catch(IOException e) {
			this.data = new HashMap<>();
			
			return false;
		}
	}
	
}
