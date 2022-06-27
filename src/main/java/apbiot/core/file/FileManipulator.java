package apbiot.core.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Vector;

import apbiot.core.exceptions.FilePointerNullException;
/**
 * A class who manipulate different type of file (most likely basic file, no yml or json)
 * @see apbiot.core.file.TextFileManager
 * @deprecated 3.0
 * @author 278de
 * @author Syl2010
 * @see java.util.Vector<String>
 */
public class FileManipulator extends Vector<String> {
	private static final long serialVersionUID = 1L;
	private InputStream fis;
	private String filePath;
	
	public FileManipulator(String name) throws IOException {

		super(100);
		if ((name == null) || (name == "")) throw new IOException("Invalid or null file name");
		
		setupFile(name);
		readFile();
	}

	public FileManipulator(File file) throws IOException {

		super(100);
		if(file == null) throw new IOException("Invalid or null file");
		
		setupFile(file.getCanonicalPath());
		readFile();
	}

	public FileManipulator(InputStream stream, String name) throws IOException {

		super(100);
		if(stream == null) throw new IOException("Invalid or null InputStream");
		
		this.fis = stream;
		this.filePath = name;
		readFile();
	}
	
	public FileManipulator(FileManipulator file) throws IOException {

		super(100);
		if (file == null) throw new IOException("Null FileManip object");

		for (String ligne : file) {
			add(ligne);
		}
	}

	private void setupFile(String name) throws IOException {
		try {
			fis = new FileInputStream(name);
		} catch (FileNotFoundException e) {
			try {
				new FileOutputStream(name).close();
			} catch (IOException e1) {
				throw new IOException("Error while creating the file named :" + name, e1);
			}
			return;
		}
	}
	
	private void readFile() throws IOException {
		
		InputStreamReader isr;
		BufferedReader br;
		String ligne;

		isr = new InputStreamReader(fis);
		br = new BufferedReader(isr);

		try {
			ligne = br.readLine();
			while (ligne != null) {
				add(ligne);
				ligne = br.readLine();
			}
		} catch (IOException e) {
			br.close();
			throw new IOException("Error while reading the file named : " + this.filePath, e);
		}
		br.close();
	}

	
	public void write() throws IOException {
		FileOutputStream fos;
		OutputStreamWriter osw;
		BufferedWriter bw;
		int taille;

		try {
			fos = new FileOutputStream(this.filePath);
		} catch (IOException e) {
			throw new IOException("Error while writing the file named : " + this.filePath, e);
		}
		osw = new OutputStreamWriter(fos);
		bw = new BufferedWriter(osw);
		taille = size();

		try {
			for (int i = 0; i < taille; i++) {
				bw.write(get(i));
				bw.newLine();
			}
		} catch (IOException e) {
			bw.close();
			throw new IOException("Error while writing the file named : " + this.filePath, e);
		}
		try {
			bw.flush();
		} catch (IOException e) {
			bw.close();
			throw new IOException("Error while saving the file named :" + this.filePath, e);
		}

		bw.close();
	}


	public FileManipulator deleteEmptyLine() {

		FileManipulator output = null;
		try {
			output = new FileManipulator(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(output == null)
			try {
				throw new FilePointerNullException("");
			} catch (FilePointerNullException e) {
				e.printStackTrace();
			}
		
		String line;
		int size = output.size();

		for (int i = 0; i < size; i++) {
			line = output.get(i);
			if (line.equals("")) {
				output.remove(i);
				i--;
			}
		}
		return output;
	}

	public FileManipulator deleteBlankSpace() {
		FileManipulator output = null;
		
		try {
			output = new FileManipulator(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(output == null)
			try {
				throw new FilePointerNullException("");
			} catch (FilePointerNullException e) {
				e.printStackTrace();
			}
		
		String line;
		int size = output.size();

		for (int i = 0; i < size; i++) {
			line = output.get(i);
			if (line.contains(" ")) {
				String new_ligne = line.trim();
				output.set(i, new_ligne);
			}
		}
		return output;
	}

	@Override
	public void clear() {
		super.clear();
		try {
			write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return new File(this.filePath);
	}

	public void print() {
		int size;
		size = size();
		for (int i = 0; i < size; i++) {
			System.out.println(i + " | " + get(i));
		}
	}

	public List<File> fileRecursivelyRemove(File folder) {

		Vector<File> output = new Vector<File>();

		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				output.addAll(fileRecursivelyRemove(file));
			}
		}
		try {
			Files.delete(folder.toPath());
			output.add(folder);
		} catch (IOException e) {
			throw new Error(e);
		}
		return output;
	}

	public List<File> listAllFilesAndFolder(File folder) {
		Vector<File> output = new Vector<File>();

		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				output.addAll(listAllFilesAndFolder(file));
			}
		} else {
			output.add(folder);
		}
		return output;
	}

}
