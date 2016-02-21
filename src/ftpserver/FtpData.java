package ftpserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FtpData extends Thread {

	private Socket s;
	private ServerSocket sv;
	private BufferedWriter requestBw;
	private FtpFileHandler fh;

	private String[] command;

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

	private void processCommand(String[] command) {
		// TODO Auto-generated method stub

		System.out.println("process " + command[0]);

		// if(command == null)
		// return;

		switch (command[0].toLowerCase()) {
		case "list":
			processLIST();
			break;
		case "stor":
			if(command.length == 2)
				processSTOR();
			else
				sendMessage(requestBw, ReturnString.parameterSyntaxError + " in STOR command");
			break;
		case "retr":
			if(command.length == 2)
				processRETR();
			else
				sendMessage(requestBw, ReturnString.parameterSyntaxError + " in RETR command");
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
				for (File f : this.fh.list())
					sendMessage(bw, this.fh.getFileRights(f) + ' ' + f.getName());
				bw.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} else
			sendMessage(requestBw, "You must connect first");

	}

	private void processSTOR() {
		if (this.fh != null) {
			sendMessage(requestBw, ReturnString.fileStatusOk);
			try {
				File dest = new File(fh.getWorkingDirectory() + '/' + command[1] );
				if(!dest.exists())
					dest.createNewFile();
				InputStream dataStream = s.getInputStream();
				fh.writeFile(dest, dataStream);
			} catch (FileNotFoundException e) {
				sendMessage(requestBw, ReturnString.fileUnavailable);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			sendMessage(requestBw, "You must connect first");

	}

	private void processRETR() {
		if (this.fh != null) {
			sendMessage(requestBw, ReturnString.fileStatusOk);
			try {
				File src = new File(fh.getWorkingDirectory() + '/' + command[1]);
				OutputStream targetStream = s.getOutputStream();
				fh.readFile(src, targetStream);
			} catch (FileNotFoundException e) {
				sendMessage(requestBw, ReturnString.fileUnavailable);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			sendMessage(requestBw, "You must connect first");

	}

	public synchronized void askCommand(String[] command) throws CommandAlreadyAsked {
		System.out.println("asked command " + command[0]);
		if (this.command != null)
			throw new CommandAlreadyAsked();
		this.command = command;
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
			sendMessage(requestBw, ReturnString.closingDataConnection);
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
