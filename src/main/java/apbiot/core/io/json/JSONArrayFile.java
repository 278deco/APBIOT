package apbiot.core.io.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.io.IOElement;
import apbiot.core.objects.IOArguments;
import apbiot.core.objects.enums.FileType;

public abstract class JSONArrayFile extends IOElement {
	
	private static final Logger LOGGER = LogManager.getLogger(JSONArrayFile.class);
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
			new File(this.filePath+File.separator+this.fileName).createNewFile();
			
			readFile();
			
		} catch (IOException e) {
			LOGGER.warn("Unexpected error while loading file "+this.filePath,e);
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
					FileWriter fw = new FileWriter(filePath);

					fw.write(FILES_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dataList));
					
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
	public void reloadFile() throws IOException {
		dataList.clear();
		
		readFile();
	}

	/**
	 * Cast the raw file object to a JSONArray
	 * @return a JSONArray representing the file
	 */
	protected ArrayList<Object> getData() {
		return (ArrayList<Object>) this.dataList;
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
			this.dataList = FILES_MAPPER.readValue(new File(this.filePath), ArrayList.class);
		}catch(Exception e) {
			this.dataList = new ArrayList<>();
		}
	}
	
	@Override
	public FileType getFileType() {
		return FileType.JSON;
	}
}
