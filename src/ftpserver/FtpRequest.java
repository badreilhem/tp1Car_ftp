package ftpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ftpserver.FtpData.CommandAlreadyAsked;

public class FtpRequest extends Thread {

	private Socket s;
	private BufferedReader br;
	private boolean listen; 
	private BufferedWriter bw;
	private FtpFileHandler fh;
	private String username;
	private FtpData ftpd;
	
	public FtpRequest(Socket s) throws IOException {
		this.s = s;
		this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.listen = true;
		this.bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
	}

	public void run() {
		this.sendMessage("new connexion started");
		while (this.listen) {
			try {
				processRequest(this.br.readLine());
			} catch (IOException e) {
				System.err.println("can't read request from socket");
				e.printStackTrace();
			} catch (CommandAlreadyAsked e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.close();
	}
	
	private void sendMessage(String msg){
		try {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		} catch (IOException e1) {
			System.err.println("can't send message :" +msg);
			e1.printStackTrace();
		}
	}
	private void close() {
		try {
			sendMessage("Goodbye");
			if(br != null)
				this.br.close();
			if(bw != null)
				this.bw.close();
			if(s != null)
				this.s.close();
			if(ftpd != null)
				this.ftpd.close();
		} catch (IOException e) {
			System.err.println("can't close FTPRequest");
			e.printStackTrace();
		}
		System.out.println("FTPRequest successfully closed");
	}

	private void processRequest(String requete) throws CommandAlreadyAsked {
		System.out.println("received request : " + requete);
		
		switch (requete) {
		case "PORT 2121":
			processPORT("2121");
			break;
		case "PASV":
			processPASV();
			break;
		//moche
		case "USER anonymous":
			processUSER("anonymous"); //tres moche
			break;
		case "PASS password":
			processPASS("password");
			break;
		case "PWD" :
			processPWD();
			break;
		case "LIST":
			processLIST();
			break;
		case "QUIT":
			processQUIT();
			break;
		default:
			incorrectCommand(requete);
		}
	}

	
	private void processPASV() throws CommandAlreadyAsked {
		try {
			ServerSocket sv = new ServerSocket(2122);
			this.sendMessage("" + sv.getLocalSocketAddress() + sv.getLocalPort());
			this.ftpd = new FtpData(this.fh, sv);
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

	private void processPORT(String string) {
		
	}

	private void processUSER(String username) {
		System.out.println("command USER");
		this.sendMessage("command USER");
		if(this.fh != null)
			sendMessage("You already have an active session");
		else {
			this.username = username;
			this.sendMessage("please enter your password");
		}
	}
	
	private void processPASS(String password) {
		System.out.println("command PASS");
		this.sendMessage("command PASS");
		if(this.fh != null)
			sendMessage("You already have an active session");
		else if(authenticate(password))
			createSession();
	}
	
	private void processQUIT() {
		System.out.println("command QUIT");
		this.sendMessage("command QUIT");
		this.listen = false;
	}
	
	private void processPWD() {
		System.out.println("command PWD");
		this.sendMessage("command PWD");
		if(this.fh != null) 
			sendMessage(this.fh.getWorkingDirectory());
		else
			sendMessage("You must connect first");
	}
	
	private void processLIST() throws CommandAlreadyAsked {
		this.ftpd.askCommand("LIST");
		System.out.println("command LIST");
		this.sendMessage("command LIST");
		
		if(this.fh != null) {
			try {
				for(String s : this.fh.list())
					sendMessage(s);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				sendMessage(e.getMessage());
				e.printStackTrace();
			}
		}
		else
			sendMessage("You must connect first");
	}

	
	private void createSession() {
		try {
			this.fh = new FtpFileHandler(username);
		} catch (IOException e) {
			System.err.println("Can't access File system");
			sendMessage("Can't access File system");
			e.printStackTrace();
		}
	}

	private boolean authenticate(String password) {
		if(this.username != null)
			return true; //a retravailler
		return false;
	}

	public void incorrectCommand(String requete) {
		System.out.println("can't process request " + requete);
	}
}
