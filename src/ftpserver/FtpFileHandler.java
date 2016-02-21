package ftpserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FtpFileHandler {
	
	private String username;
	private Path workingDirectory;
	private HashMap<String, String> fileRights;
	public FtpFileHandler(String username) throws IOException {
		this.username = username;
		this.workingDirectory = Paths.get("ftp/" + username);
		this.fileRights = new HashMap<String, String>();
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
	
	public HashMap<String, String> getFileRights(){
		File file = new File(this.workingDirectory.toString());
		String right = "";
		for(File filei : file.listFiles()){
			if(filei.isDirectory())
				right += "d";
			if(filei.isFile())
				right += "-";
			if(filei.canRead())
				right += "r";
			if(filei.canWrite())
				right += "w";
			if(filei.canExecute())
				right += "x";
			this.fileRights.put(filei.getName(), right);
			right = "";
		}
		return this.fileRights;
	}
	
	public void changeWorkingDirectory(String directoryName) throws IOException{
		String fileName = this.workingDirectory.toString() +"/"+ directoryName;
		if(fileName.equals("ftp/" + this.username + "/.."))
			throw new IOException("You don't have the rigths for this directory");
		else{
			if(directoryName.equals("..")){
				int lastIndex = this.workingDirectory.toString().lastIndexOf('/');
				String newName = this.workingDirectory.toString().substring(0, lastIndex);
				this.workingDirectory = Paths.get(newName);
			}else{
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
}
