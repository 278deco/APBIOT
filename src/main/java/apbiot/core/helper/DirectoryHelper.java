package apbiot.core.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import apbiot.core.io.objects.Directory;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class DirectoryHelper {

	/**
	 * Get a directory contained in a set by its name
	 * @param directories The set of directories
	 * @param nameId The name of the directory researched
	 * @return The directory if it exist or null
	 */
	public static Directory getDirectoryByName(Set<Directory> directories, String nameId) {
		return directories.stream().filter(dir -> dir.isNameSimilar(nameId)).findFirst().orElse(null);
	}
	
	/**
	 * Generate a directory and return it
	 * @param directory The directory's path
	 * @param onSuccess The action to perform when the directory has been created (return true if directory was created, false if it was loaded)
	 * @param onError The action to perform when an error has occured
	 * @return the directory
	 * @see java.io.File#mkdirs()
	 */
	public static Path generateDirectory(String directory, Consumer<Boolean> onSuccess, Consumer<Throwable> onError) {
		Objects.requireNonNull(onSuccess);
		Objects.requireNonNull(onError);
		
		final Path temp = Path.of(directory);
		
		try {
			onSuccess.accept(!Files.exists(temp));
			Files.createDirectories(temp);
		}catch(Exception e) {
			onError.accept(e);
			
			return null;
		}
		
		return temp;
	}
	
	/**
	 * Generate a directory with specified logging messages
	 * @param directory The directory's path
	 * @param loadMessage The message to be displayed when the directory has been loaded
	 * @param creationMessage The message to be displayed when the directory has been created
	 * @param errorMessage The message to be display when an error has occured
	 * @param logger The logger used by the program
	 * @return the directory
	 * @see apbiot.core.helper.FileHelper#generateDirectory(String, Consumer, Consumer)
	 */
	public static Path generateDirectoryWithLogging(String directory, String loadMessage, String creationMessage, String errorMessage, Logger logger) {
		return generateDirectory(directory, response -> logger.info(response ? creationMessage : loadMessage), error -> logger.error(errorMessage+""+error));
	}
	
	/**
	 * Generate a directory with default logging messages
	 * @param directory The directory's path
	 * @param loadMessage The message to be displayed when the directory has been loaded
	 * @param creationMessage The message to be displayed when the directory has been created
	 * @param errorMessage The message to be display when an error has occured
	 * @param logger The logger used by the program
	 * @return the directory
	 * @see apbiot.core.helper.FileHelper#generateDirectory(String, Consumer, Consumer)
	 */
	public static Path generateDirectoryWithLogging(String directory, Logger logger) {
		return generateDirectory(directory, response -> logger.info(response ? "Directory "+directory+" has been successfully created !" : "Directory "+directory+" has been successfully loaded !"), error -> logger.error(error));
	}
	
	/**
	 * Count every files in a specified directory matching with the specified file name
	 * @param dir The directory
	 * @param fileName The file's name
	 * @return The number of file found. If the directory doesn't exist, return -1
	 */
	public static long countFileWithName(Path dir, String fileName) {
		if(!Files.exists(dir) && !Files.isDirectory(dir)) return -1L;
		
		DirectoryStream<Path> paths = null;
		long estimateCount = 0;
		
		try {
			paths =Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
	
				@Override
				public boolean accept(Path entry) throws IOException {
					return entry.getFileName().toString().matches(".*"+fileName+".*");
				}
			});
			
			estimateCount = paths.spliterator().estimateSize();
		}catch(IOException e) {
			return -1L;
		}finally {
			if(paths != null) try { paths.close(); } catch (IOException e) {}
		}
		
		return estimateCount;
	}
	
	/**
	 * Count every files in a specified directory matching with the specified file name
	 * @param dir The directory
	 * @param fileName The file's name
	 * @return The number of file found. If the directory doesn't exist, return -1
	 */
	public static long countFile(Path dir) {
		if(!Files.exists(dir) && !Files.isDirectory(dir)) return -1L;
		
		Stream<Path> paths = null;
		long estimateCount = 0;
		
		try {
			paths = Files.list(dir);
			
			estimateCount = paths.count();
		}catch(IOException e) {
			return -1L;
		}finally {
			if(paths != null) paths.close();
		}
		
		return estimateCount;
	}
	
	/**
	 * Get every files as path in a specified directory matching with the specified file name
	 * @param dir The directory
	 * @param fileName The file's name
	 * @param threshold The maximum number of file to be fetched from the method. Set it to 0 or lower to have no threshold.
	 * @return A set of path representing the found files or an empty one if an error occur
	 */
	public static Set<Path> getDirectorySubfiles(Path dir, String fileName, int threshold) {
		final Set<Path> result = new HashSet<>();
		if(!Files.exists(dir) && !Files.isDirectory(dir)) return result;
		
		DirectoryStream<Path> paths = null;
		
		try {
			paths =Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
	
				@Override
				public boolean accept(Path entry) throws IOException {
					return entry.getFileName().toString().matches(".*"+fileName+".*");
				}
			});
			
			final Iterator<Path> iterator = paths.iterator();
			int index = 0;
			while(iterator.hasNext() && (index < threshold || threshold <= 0 )) {
				result.add(iterator.next());
			}
			
		}catch(IOException e) {
			return result;
		}finally {
			if(paths != null) try { paths.close(); } catch (IOException e) {}
		}
		
		return result;
	}
	
	/**
	 * Get every files as path in a specified directory matching with the specified file name
	 * @param dir The directory
	 * @param fileName The file's name
	 * @param threshold The maximum number of file to be fetched from the method. Set it to 0 or lower to have no threshold.
	 * @return A set of path representing the found files or an empty one if an error occur
	 */
	public static Set<Path> getDirectorySubfiles(Path dir, int threshold) {
		final Set<Path> result = new HashSet<>();
		if(!Files.exists(dir) && !Files.isDirectory(dir)) return result;
		
		Stream<Path> paths = null;
		
		try {
			paths = Files.list(dir);
			
			final Iterator<Path> iterator = paths.iterator();
			int index = 0;
			while(iterator.hasNext() && (index < threshold || threshold <= 0 )) {
				result.add(iterator.next());
			}
			
		}catch(IOException e) {
			return result;
		}finally {
			if(paths != null) paths.close(); 
		}
		
		return result;
	}
	
}
