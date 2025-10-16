package apbiot.core.helper;

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

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import marshmalliow.core.directory.Directory;
import marshmalliow.core.directory.LocalDirectory;

public class LocalDirectoryHelper {

	/**
	 * Get a directory contained in a set by its name
	 * 
	 * @param directories The set of directories
	 * @param nameId      The name of the directory researched
	 * @return The directory if it exist or null
	 * @since 5.0
	 * @deprecated since 6.2.3
	 */
	public static Directory getDirectoryByName(Set<Directory> directories, String nameId) {
		return directories.stream().filter(dir -> dir.id().equals(nameId)).findFirst().orElse(null);
	}

	/**
	 * Generate a path and return it
	 * 
	 * @param pathName  The name of the path to create
	 * @param onSuccess The action to perform when the path has been created (return
	 *                  true if path was created, false if it was loaded)
	 * @param onError   The action to perform when an error has occured
	 * @return the path or null if an error has occured
	 * @see java.io.File#mkdirs()
	 * @since 5.0
	 */
	public static Path generatePath(String pathName, Consumer<Boolean> onSuccess, Consumer<Throwable> onError) {
		Objects.requireNonNull(onSuccess);
		Objects.requireNonNull(onError);

		final Path temp = Path.of(pathName);

		try {
			onSuccess.accept(!Files.exists(temp));
			Files.createDirectories(temp);
		} catch (Exception e) {
			onError.accept(e);

			return null;
		}

		return temp;
	}

	/**
	 * Generate a path with specified logging messages
	 * 
	 * @param pathName        The name of the path to create
	 * @param loadMessage     The message to be displayed when the path has been
	 *                        loaded
	 * @param creationMessage The message to be displayed when the path has been
	 *                        created
	 * @param errorMessage    The message to be display when an error has occured
	 * @param logger          The logger used by the program
	 * @return the path or null if an error has occured
	 * @see apbiot.core.helper.FileHelper#generateDirectory(String, Consumer,
	 *      Consumer)
	 * @since 4.0
	 */
	public static Path generatePathWithLogging(String pathName, String loadMessage, String creationMessage,
			String errorMessage, Logger logger) {
		return generatePath(pathName, response -> logger.info(response ? creationMessage : loadMessage),
				error -> logger.error(errorMessage + "" + error));
	}

	/**
	 * Generate a path with default logging messages
	 * 
	 * @param pathName        The name of the path to create
	 * @param loadMessage     The message to be displayed when the path has been
	 *                        loaded
	 * @param creationMessage The message to be displayed when the path has been
	 *                        created
	 * @param errorMessage    The message to be display when an error has occured
	 * @param logger          The logger used by the program
	 * @return the path or null if an error has occured
	 * @see apbiot.core.helper.FileHelper#generateDirectory(String, Consumer,
	 *      Consumer)
	 * @since 4.0
	 */
	public static Path generatePathWithLogging(String pathName, Logger logger) {
		return generatePath(pathName,
				response -> logger.info(response ? "Directory " + pathName + " has been successfully created !"
						: "Directory " + pathName + " has been successfully loaded !"),
				error -> logger.error(error));
	}

	/**
	 * Count every files in a specified directory matching with the specified file
	 * name
	 * 
	 * @param dir      The directory
	 * @param fileName The file's name
	 * @return The number of file found. If the directory doesn't exist, return -1
	 * @since 4.0
	 */
	public static int countFileWithName(Path dir, String fileName) {
		return getDirectorySubfiles(dir, fileName, 0).size();
	}

	/**
	 * Count every files in a specified directory matching with the specified file
	 * name
	 * 
	 * @param dir      The directory
	 * @param fileName The file's name
	 * @return The number of file found. If the directory doesn't exist, return -1
	 */
	public static int countFileWithName(LocalDirectory dir, String fileName) {
		return countFileWithName(dir.path(), fileName);
	}

	/**
	 * Count every files in a specified directory matching with the specified file
	 * name
	 * 
	 * @param dir      The directory
	 * @param fileName The file's name
	 * @return The number of file found. If the directory doesn't exist, return -1
	 * @since 4.0
	 */
	public static int countFile(Path dir) {
		return getDirectorySubfiles(dir, 0).size();
	}

	/**
	 * Count every files in a specified directory matching with the specified file
	 * name
	 * 
	 * @param dir      The directory
	 * @param fileName The file's name
	 * @return The number of file found. If the directory doesn't exist, return -1
	 * @since 4.0
	 */
	public static int countFile(LocalDirectory dir) {
		return countFile(dir.path());
	}

	/**
	 * Get every files as path in a specified directory matching with the specified
	 * file name
	 * 
	 * @param dir       The directory
	 * @param fileName  The file's name
	 * @param threshold The maximum number of file to be fetched from the method.
	 *                  Set it to 0 or lower to have no threshold.
	 * @return A set of path representing the found files or an empty one if an
	 *         error occur
	 * @since 4.0
	 */
	public static Set<Path> getDirectorySubfiles(Path dir, String fileName, int threshold) {
		final Set<Path> result = new HashSet<>();
		if (!Files.exists(dir) && !Files.isDirectory(dir))
			return result;

		DirectoryStream<Path> paths = null;

		try {
			paths = Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {

				@Override
				public boolean accept(Path entry) throws IOException {
					return entry.getFileName().toString().matches(".*" + fileName + ".*");
				}
			});

			final Iterator<Path> iterator = paths.iterator();
			int index = 0;
			while (iterator.hasNext() && (index < threshold || threshold <= 0)) {
				result.add(iterator.next());
			}

		} catch (IOException e) {
			return result;
		} finally {
			if (paths != null)
				try {
					paths.close();
				} catch (IOException e) {
				}
		}

		return result;
	}

	/**
	 * Get every files as path in a specified directory matching with the specified
	 * file name
	 * 
	 * @param dir       The directory
	 * @param fileName  The file's name
	 * @param threshold The maximum number of file to be fetched from the method.
	 *                  Set it to 0 or lower to have no threshold.
	 * @return A set of path representing the found files or an empty one if an
	 *         error occur
	 * @since 4.0
	 */
	public static Set<Path> getDirectorySubfiles(LocalDirectory dir, String fileName, int threshold) {
		return getDirectorySubfiles(dir.path(), fileName, threshold);
	}

	/**
	 * Get every files as path in a specified directory matching with the specified
	 * file name
	 * 
	 * @param dir       The directory
	 * @param threshold The maximum number of file to be fetched from the method.
	 *                  Set it to 0 or lower to have no threshold.
	 * @return A set of path representing the found files or an empty one if an
	 *         error occur
	 * @since 4.0
	 */
	public static Set<Path> getDirectorySubfiles(Path dir, int threshold) {
		final Set<Path> result = new HashSet<>();
		if (!Files.exists(dir) && !Files.isDirectory(dir))
			return result;

		try (final Stream<Path> paths = Files.list(dir)) {

			final Iterator<Path> iterator = paths.iterator();
			int index = 0;
			while (iterator.hasNext() && (index < threshold || threshold <= 0)) {
				result.add(iterator.next());
			}
		} catch (IOException e) {
			return result;
		}

		return result;
	}

	/**
	 * Get every files as path in a specified directory matching with the specified
	 * file name
	 * 
	 * @param dir       The directory
	 * @param threshold The maximum number of file to be fetched from the method.
	 *                  Set it to 0 or lower to have no threshold.
	 * @return A set of path representing the found files or an empty one if an
	 *         error occur
	 * @since 4.0
	 */
	public static Set<Path> getDirectorySubfiles(LocalDirectory dir, int threshold) {
		return getDirectorySubfiles(dir.path(), threshold);
	}

}
