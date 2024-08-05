package apbiot.core.io.json;

import java.io.IOException;
import java.util.List;
import java.util.function.BiPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.json.JSONFile;
import marshmalliow.core.objects.Directory;

/**
 * Fork of JSONFile class used specifically to store properties
 * @author 278deco
 * @version 1.0.0
 */
public abstract class JSONProperties extends JSONFile {
	
	protected static final Logger LOGGER = LogManager.getLogger(JSONProperties.class);
	
	public JSONProperties(Directory dir, String name) {
		super(dir, name);
		
		try {
			
			readFile();
			
		} catch (IOException e) {
			LOGGER.error("Unexpected error while loading JSON file [dir: {}, name: {}] with error {} and message {}", this.directory.getName(), this.fileName, e.getClass().getName(), e.getMessage());
		}finally {
			controlRegistredProperties();
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
		return this.getContentAsObject().get(propKey, Boolean.class);
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a string, will throw a {@link ClassCastException}
	 * @param propKey The name of the property
	 * @return the value of the property as a string
	 * @throws ClassCastException
	 */
	protected String getStringProperty(String propKey) throws ClassCastException {
		return this.getContentAsObject().get(propKey, String.class);
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a long, will throw a {@link ClassCastException}
	 * @param propKey The name of the property
	 * @return the value of the property as a long
	 * @throws ClassCastException
	 */
	protected Long getLongProperty(String propKey) throws ClassCastException {
		return this.getContentAsObject().get(propKey, Long.class);
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
		return this.getContentAsObject().get(propKey, Integer.class);
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a float, will throw a {@link ClassCastException}<br>
	 * @param propKey The name of the property
	 * @return the value of the property as a float
	 * @throws ClassCastException
	 */
	protected Float getFloatProperty(String propKey) throws ClassCastException {
		return this.getContentAsObject().get(propKey, Float.class);
	}
	
	/**
	 * Get the value of a property contained in the configuration file<br>
	 * If the property isn't a list of object, will throw a {@link ClassCastException}<br>
	 * @param <E> The type of objects stored by this property
	 * @param propKey The name of the property
	 * @param castClass The objects' class stored by this property
	 * @return the value of the property as a list
	 * @throws ClassCastException
	 */
	@SuppressWarnings("unchecked")
	protected <E> List<E> getListProperty(String propKey, Class<E> castClass) throws ClassCastException {
		return ((List<E>)this.getContentAsObject().get(propKey));
	}
	
	/**
	 * Check if a property exist with this key
	 * @param propKey The name of the property
	 * @return if it exist or not
	 */
	protected boolean isExistingProperty(String propKey) {
		return this.getContentAsObject().containsKey(propKey);
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
		this.getContentAsObject().put(propKey, value);
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
		this.getContentAsObject().put(propKey, value);
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
		this.getContentAsObject().put(propKey, value);
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
		this.getContentAsObject().put(propKey, value);
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
		this.getContentAsObject().put(propKey, value);
	}
	
	/**
	 * Create a new list property in the configuration<br>
	 * Will override any existing property with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setPropertyIfAbsent(String, List<?>)
	 * @see JSONProperties#setPropertyIfDifferent(String, List<?>, BiPredicate)
	 */
	protected void setProperty(String propKey, List<?> value) {
		this.getContentAsObject().put(propKey, value);
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
		this.getContentAsObject().putIfAbsent(propKey, value);
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
		this.getContentAsObject().putIfAbsent(propKey, value);
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
		this.getContentAsObject().putIfAbsent(propKey, value);
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
		this.getContentAsObject().putIfAbsent(propKey, value);
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
		this.getContentAsObject().putIfAbsent(propKey, value);
	}
	
	/**
	 * Create a new list property in the configuration<br>
	 * Won't be added if another property exist with this name
	 * @param propKey The name of the property
	 * @param value The value of the property
	 * @see JSONProperties#setProperty(String, List<?>)
	 * @see JSONProperties#setPropertyIfDifferent(String, List<?>, BiPredicate)
	 */
	protected void setPropertyIfAbsent(String propKey, List<?> value) {
		this.getContentAsObject().putIfAbsent(propKey, value);
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
		if(!this.getContentAsObject().containsKey(propKey)) this.getContentAsObject().put(propKey, value);
		try {
			if(predicate.test(value, this.getBooleanProperty(propKey))) this.getContentAsObject().put(propKey, value);
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
		if(!this.getContentAsObject().containsKey(propKey)) this.getContentAsObject().put(propKey, value);
		try {
			if(predicate.test(value, this.getStringProperty(propKey))) this.getContentAsObject().put(propKey, value);
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
		if(!this.getContentAsObject().containsKey(propKey)) this.getContentAsObject().put(propKey, value);
		try {
			if(predicate.test(value, this.getLongProperty(propKey))) this.getContentAsObject().put(propKey, value);
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
		if(!this.getContentAsObject().containsKey(propKey)) this.getContentAsObject().put(propKey, value);
		try {
			if(predicate.test(value, this.getIntegerProperty(propKey))) this.getContentAsObject().put(propKey, value);
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
		if(!this.getContentAsObject().containsKey(propKey)) this.getContentAsObject().put(propKey, value);
		try {
			if(predicate.test(value, this.getFloatProperty(propKey))) this.getContentAsObject().put(propKey, value);
		}catch(ClassCastException e) { }
	}
	
}
