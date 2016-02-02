package ftpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private void listenConnexion() throws IOException {
		ServerSocket sv = new ServerSocket(2121);
		System.out.println("server successfully started");
		while(true){
			Socket s = sv.accept();
			FtpRequest fr = new FtpRequest(s);
			fr.start();
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		Server server = new Server();
//		server.listenConnexion();
		
		FtpData ftpd = new FtpData();
		ftpd.start();
		ftpd.notify();
		ftpd.test = "loul";
		ftpd.notify();
	}

}
