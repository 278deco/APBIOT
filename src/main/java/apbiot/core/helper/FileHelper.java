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
	 * @deprecated 4.0
	 * @since 1.0
	 * @see DirectoryHelper#countFileWithName(java.nio.file.Path, String)
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
	 * @deprecated 4.0
	 * @since 1.0
	 * @see DirectoryHelper#getDirectorySubfiles(java.nio.file.Path, String, int)
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
	 * @deprecated 4.0
	 * @since 1.0
	 * @see DirectoryHelper#getDirectorySubfiles(java.nio.file.Path, String, int)
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
