package ftpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FtpData extends Thread {

	private Socket s;
	private ServerSocket sv;
	private BufferedReader br;
	private boolean listen;
	private BufferedWriter bw;
	private FtpFileHandler fh;
	private String username;

	private String command;

	public FtpData(FtpFileHandler fh1, ServerSocket sv) throws IOException {
		this.sv = sv;
		this.command = null;
		this.fh = fh1;
	}
	
	//test
	public FtpData() {
		this.listen = true;
		this.command = null;		
	}
			
	public void run() {
		System.out.println("start FTPData");
		waitConnexion();
		waitCommand();
		processCommand(this.command);
		System.out.println("close FTPData");
		this.close();
	}

	private synchronized void waitConnexion() {
		try {
			this.s = this.sv.accept();
		} catch (IOException e) {
			System.err.println("bug while waiting connexion");
			this.close();
			e.printStackTrace();
		}		
	}

	private void processCommand(String command) {
		// TODO Auto-generated method stub
		
		System.out.println("process " + command);
		/*switch (command) {
		case "LIST":
			processLIST();
			break;
		}*/
//		this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
	//	this.setBw(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())));

	}

	public synchronized void askCommand(String str) throws CommandAlreadyAsked {
		System.out.println("asked command " + str);
		if (this.command != null)
			throw new CommandAlreadyAsked();
		this.command = str;
		this.notify();
	}

	public synchronized void waitCommand() {
		System.out.println("waiting for a command");
		try {
			while (!commandAsked()) {
				this.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("stop waiting");
	}

	public synchronized boolean commandAsked() {
		return this.command != null;
	}
/*
	private void sendMessage(String msg) {
		try {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		} catch (IOException e1) {
			System.err.println("can't send message :" + msg);
			e1.printStackTrace();
		}
	}*/

	public void close() {
		try {
			if(br != null)
				this.br.close();
			if(bw != null)
				this.bw.close();
			if(sv != null)
				this.sv.close();
			if(s != null)
				this.s.close();
		} catch (IOException e) {
			System.err.println("can't close FTPRequest");
			e.printStackTrace();
		}
		System.out.println("FTPRequest successfully closed");
	}

	
	
	public BufferedWriter getBw() {
		return bw;
	}

	public void setBw(BufferedWriter bw) {
		this.bw = bw;
	}

	//EXCEPTIONS
	public class CommandAlreadyAsked extends Exception {

	}
}
