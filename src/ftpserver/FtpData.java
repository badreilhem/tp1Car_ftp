package ftpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class FtpData extends Thread {

	private Socket s;
	private BufferedReader br;
	private boolean listen;
	private BufferedWriter bw;
	private FtpFileHandler fh;
	private String username;

	private String command;

	public FtpData(Socket s) throws IOException {
		this.s = s;
		this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		this.listen = true;
		this.bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		this.command = null;
	}
	
	//test
	public FtpData() {
		this.listen = true;
		
	}
			
	public void run() {
		System.out.println("start FTPData");
		while (this.listen) {
			waitCommand();
			processCommand(this.command);
		}
		System.out.println("close FTPData");
		//this.close();
	}

	private void processCommand(String command) {
		// TODO Auto-generated method stub
		
		System.out.println("process " + command);
		if(command == "quit") {
			this.listen = false;
		}
		this.command = null;
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

	private void sendMessage(String msg) {
		try {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		} catch (IOException e1) {
			System.err.println("can't send message :" + msg);
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

	
	
	//EXCEPTIONS
	public class CommandAlreadyAsked extends Exception {

	}
}
