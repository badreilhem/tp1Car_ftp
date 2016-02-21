package ftpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FtpFileHandler {

	private String username;
	private Path workingDirectory;

	public FtpFileHandler(String username) throws IOException {
		this.username = username;
		this.workingDirectory = Paths.get("ftp/" + username);
		Files.createDirectories(this.workingDirectory);
	}

	public String getWorkingDirectory() {
		return this.workingDirectory.toString();
	}

	public File[] list() throws IOException {
		File file = new File(this.workingDirectory.toString());
		if (!file.isDirectory())
			throw new IOException("path " + this.workingDirectory + " is not a directory");
		return file.listFiles();
	}

	public String getFileRights(File file) {
		String right = "";

		if (file.isDirectory())
			right += 'd';
		else
			right += '-';
		
		if (file.canRead())
			right += 'r';
		else
			right += '-';
		
		if (file.canWrite())
			right += 'w';
		else
			right += '-';
		
		if (file.canExecute())
			right += 'x';
		else
			right += '-';

		return right;
	}

	public void changeWorkingDirectory(String directoryName) throws IOException {
		String fileName = this.workingDirectory.toString() + "/" + directoryName;
		if (fileName.equals("ftp/" + this.username + "/.."))
			throw new IOException("You don't have the rigths for this directory");
		else {
			if (directoryName.equals("..")) {
				int lastIndex = this.workingDirectory.toString().lastIndexOf('/');
				String newName = this.workingDirectory.toString().substring(0, lastIndex);
				this.workingDirectory = Paths.get(newName);
			} else {
				File file = new File(fileName);
				if ((file.canRead() || file.canWrite()) && file.isDirectory()) {
					this.workingDirectory = Paths.get(fileName);
				} else {
					throw new IOException("path " + fileName + " is not a directory or "
							+ "you don't have the rigths for this directory");
				}
			}
		}
	}

	public void writeFile(File file, InputStream dataStream) throws FileNotFoundException {

		if (file.exists() && file.isFile())
			try {
				OutputStream targetStream = new FileOutputStream(file);

				while (dataStream.available() > 0)
					targetStream.write(dataStream.read());

				targetStream.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			throw new FileNotFoundException();

	}

	public void readFile(File file, OutputStream targetStream) throws FileNotFoundException {

		if (file.exists() && file.isFile())
			try {
				InputStream dataStream = new FileInputStream(file);

				while (dataStream.available() > 0)
					targetStream.write(dataStream.read());

				dataStream.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			throw new FileNotFoundException();
	}
	
	public boolean fileInDir(String fileName) throws IOException{
		for(File f : this.list()){
			if(f.equals(fileName))
				return true;
		}
		return false;
	}
}
