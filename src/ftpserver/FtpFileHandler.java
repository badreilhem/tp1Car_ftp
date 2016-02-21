package ftpserver;

import java.io.File;
import java.io.IOException;
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

	public String[] list() throws IOException {
		File file = new File(this.workingDirectory.toString());
		if(!file.isDirectory())
			throw new IOException("path " + this.workingDirectory + " is not a directory");
		return file.list();
	}
	
	public void changeWorkingDirectory(String directoryName) throws IOException{
		String fileName = this.workingDirectory.toString() + directoryName;
		if(fileName.equals("ftp/" + this.username))
			throw new IOException("You don't have the rigths for this directory");
		else{
			File file = new File(fileName);
			if((file.canRead() || file.canWrite()) && file.isDirectory()){
				this.workingDirectory = Paths.get(fileName);
			}else{
				throw new IOException("path " + fileName + " is not a directory or " +
						"you don't have the rigths for this directory");
			}
		}
	}
}
