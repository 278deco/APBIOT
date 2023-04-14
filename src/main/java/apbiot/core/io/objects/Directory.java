package apbiot.core.io.objects;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Directory {
	
	private String name;
	private Path directory;
	
	public Directory(String pathDir) {
		final Path temp = Paths.get(pathDir);
		if(!Files.isDirectory(temp)) throw new InvalidPathException(pathDir, "The directory cannot be constructed with a file path");
			
		this.directory = temp;
		this.name = pathDir;
	}
	
	public Directory(Path path) {
		if(!Files.isDirectory(path)) throw new InvalidPathException(path.toString(), "The directory cannot be constructed with a file path");
		this.name = path.toString();
		this.directory = path;
	}
	
	public Directory(String name, String pathDir) {
		final Path temp = Paths.get(pathDir);
		if(!Files.isDirectory(temp)) throw new InvalidPathException(pathDir, "The directory cannot be constructed with a file path");
		
		this.directory = temp;
		this.name = name;
	}
	
	public Directory(String name, Path path) {
		if(!Files.isDirectory(path)) throw new InvalidPathException(path.toString(), "The directory cannot be constructed with a file path");
		
		this.directory = path;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Path getPath() {
		return directory;
	}
	
	public boolean isPathSimilar(Path path) {
		return path.equals(this.getPath());
	}
	
	public boolean isNameSimilar(String nameID) {
		return nameID.equals(this.getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Directory && areEquals((Directory)obj);
	}

	private boolean areEquals(Directory obj) {
		return isPathSimilar(obj.getPath()) && obj.getName().equalsIgnoreCase(this.getName());
	}
}
