package ftpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ftpserver.FtpData.CommandAlreadyAsked;

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
		
		//DEMO FTPDATA, tout devrait s'arrêter
		FtpData ftpd = new FtpData();
		ftpd.start();
		try {
			ftpd.askCommand("quitf");
		} catch (CommandAlreadyAsked e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			ftpd.askCommand("quit");
		} catch (CommandAlreadyAsked e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
