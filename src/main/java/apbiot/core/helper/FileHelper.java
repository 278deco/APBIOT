package apbiot.core.helper;

import java.util.List;
import java.util.Random;

public class FileHelper {
	
	/**
	 * Return a random path pointing to a file from a list of files
	 * @param list A list of file's path
	 * @return an element from the list
	 * @since 5.0
	 */
	public static String getRandomFilePath(List<String> pathList, Random random) {
		return pathList.get(random.nextInt(pathList.size()));
	}
	
}
