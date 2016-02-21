package ftpserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FtpData extends Thread {

	private Socket s;
	private ServerSocket sv;
	private BufferedWriter requestBw;
	private FtpFileHandler fh;

	private String command;

	public FtpData(FtpFileHandler fh, ServerSocket sv, BufferedWriter requestBw) throws IOException {
		this.sv = sv;
		this.command = null;
		this.fh = fh;
		this.s = null;
		this.requestBw = requestBw;
	}

	public FtpData(FtpFileHandler fh, Socket s, BufferedWriter requestBw) {
		this.s = s;
		this.command = null;
		this.fh = fh;
		this.requestBw = requestBw;
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
		if(s == null)
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

		// if(command == null)
		// return;

		switch (command) {
		case "LIST":
			processLIST();
			break;
		}

		// this.br = new BufferedReader(new
		// InputStreamReader(s.getInputStream()));
		// this.setBw(new BufferedWriter(new
		// OutputStreamWriter(s.getOutputStream())));

	}

	private void processLIST() {
		if (this.fh != null) {
			sendMessage(requestBw, ReturnString.fileStatusOk);
			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				for (String s : this.fh.list())
					sendMessage(bw, s+" "+this.fh.getFileRights().get(s));
				bw.close();
				sendMessage(requestBw, ReturnString.closingDataConnection);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} else
			sendMessage(requestBw, "You must connect first");

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

	private void sendMessage(BufferedWriter bw, String msg) {
		try {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		} catch (IOException e1) {
			System.err.println("can't send message :" + msg);
			e1.printStackTrace();
		}
	}

	public void close() {
		try {
			if (sv != null)
				this.sv.close();
			if (s != null)
				this.s.close();
		} catch (IOException e) {
			System.err.println("can't close FTPData");
			e.printStackTrace();
		}
		System.out.println("FTPData successfully closed");
	}

	// EXCEPTIONS
	public class CommandAlreadyAsked extends Exception {

	}
}
