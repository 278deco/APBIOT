package apbiot.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.logging.log4j.Logger;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class FileHelper {
	
	/**
	 * Generate a directory and return it
	 * @param directory - the directory's path
	 * @param onSuccess - The action to perform when the directory has been created (return true if directory was created, false if it was loaded)
	 * @param onError - The action to perform when an error has occured
	 * @return the directory
	 * @see java.io.File#mkdirs()
	 */
	public static File generateDirectory(String directory, Consumer<Boolean> onSuccess, Consumer<Throwable> onError) {
		Objects.requireNonNull(onSuccess);
		Objects.requireNonNull(onError);
		
		File temp = new File(directory);
		
		try {
			onSuccess.accept(temp.mkdirs());
		}catch(Exception e) {
			onError.accept(e);
			
			return null;
		}
		
		return temp;
	}
	
	/**
	 * Generate a directory with specified logging messages
	 * @param directory - the directory's path
	 * @param loadMessage - the message to be displayed when the directory has been loaded
	 * @param creationMessage - the message to be displayed when the directory has been created
	 * @param errorMessage - the message to be display when an error has occured
	 * @param logger - the logger used by the program
	 * @return the directory
	 * @see apbiot.core.helper.FileHelper#generateDirectory(String, Consumer, Consumer)
	 */
	public static File generateDirectoryWithLogging(String directory, String loadMessage, String creationMessage, String errorMessage, Logger logger) {
		return generateDirectory(directory, response -> logger.info(response ? creationMessage : loadMessage), error -> logger.error(errorMessage+""+error));
	}
	
	/**
	 * Generate a directory with default logging messages
	 * @param directory - the directory's path
	 * @param loadMessage - the message to be displayed when the directory has been loaded
	 * @param creationMessage - the message to be displayed when the directory has been created
	 * @param errorMessage - the message to be display when an error has occured
	 * @param logger - the logger used by the program
	 * @return the directory
	 * @see apbiot.core.helper.FileHelper#generateDirectory(String, Consumer, Consumer)
	 */
	public static File generateDirectoryWithLogging(String directory, Logger logger) {
		return generateDirectory(directory, response -> logger.info(response ? "Directory "+directory+" has been successfully created !" : "Directory "+directory+" has been successfully loaded !"), error -> logger.error(error));
	}
	
	/**
	 * Count every files in a specified directory matching with the specified file name
	 * @param dir - the directory
	 * @param fileName - the file's name
	 * @return the number of file found. If the directory doesn't exist, return -1
	 */
	public static int countFileWithName(File dir, String fileName) {
		if(!dir.exists()) return -1;
		
		String[] res = dir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File file, String name) {
				return name.matches(".*"+fileName+".*");
			}
		});
		
		return res != null ? res.length : 0;
	}
	
	/**
	 * Get every files in a specified directory matching with the specified file name
	 * @param dir - the directory
	 * @param fileName - the file's name
	 * @return the list of file found
	 */
	public static File[] getFilesWithName(File dir, String fileName) {
		if(!dir.exists()) return null;
		
		File[] res = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File file, String name) {
				return name.matches(".*"+fileName+".*");
			}
		});
		
		return res;
	}
	
	/**
	 * Get every file's path in a specified directory matching with the specified file name
	 * @param dir - the directory
	 * @param fileName - the file's name
	 * @return the list of file's path found
	 */
	public static List<String> getFilePathWithName(File dir, String fileName) {
		if(!dir.exists()) return null;
		
		File[] res = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File file, String name) {
				return name.matches(".*"+fileName+".*");
			}
		});
		
		List<String> result = new ArrayList<>();
		for(File f : res) {
			result.add(f.getAbsolutePath());
		}
		
		return result;
	}
	
	/**
	 * @deprecated
	 * @since 4.0
	 * @see apbiot.core.helper.FileHelper#getRandomFilePath(List)
	 */
	public static FileInputStream getRandomElement(List<FileInputStream> list) {
		return list.get(new Random().nextInt(list.size()));
	}
	
	/**
	 * Return a random path pointing to a file from a list of files
	 * @param list - A list of file's path
	 * @return an element from the list
	 */
	public static String getRandomFilePath(List<String> pathList) {
		return pathList.get(new Random().nextInt(pathList.size()));
	}
	
}
