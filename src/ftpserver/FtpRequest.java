package ftpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class FtpRequest extends Thread {

	private Socket s;
	private BufferedReader br;
	private boolean listen; 
	private BufferedWriter bw;

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
			this.br.close();
			this.s.close();
		} catch (IOException e) {
			System.err.println("can't close FTPRequest");
			e.printStackTrace();
		}
		System.out.println("FTPRequest successfully closed");
	}

	private void processRequest(String requete) {
		System.out.println("received request : " + requete);
		switch (requete) {
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

	private void processQUIT() {
		System.out.println("command QUIT");
		this.sendMessage("command QUIT");
		this.listen = false;
	}

	private void processLIST() {
		System.out.println("command LIST");
		this.sendMessage("command LIST");
	}

	public void incorrectCommand(String requete) {
		System.out.println("can't process request " + requete);
	}
}
