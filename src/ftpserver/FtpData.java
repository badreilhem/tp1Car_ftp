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

/**
 * Cette classe permet au serveur d'envoyer des données sans bloquer la gestion des commandes
 * @author badreddine et cojez
 *
 */
public class FtpData extends Thread {

	private Socket s;
	private ServerSocket sv;
	private BufferedWriter requestBw;
	private FtpFileHandler fh;

	private String[] command;
	private boolean closed;

	public FtpData(FtpFileHandler fh, ServerSocket sv, BufferedWriter requestBw) throws IOException {
		this.sv = sv;
		this.command = null;
		this.fh = fh;
		this.s = null;
		this.requestBw = requestBw;
		this.closed = false;
	}

	public FtpData(FtpFileHandler fh, Socket s, BufferedWriter requestBw) {
		this.s = s;
		this.command = null;
		this.fh = fh;
		this.requestBw = requestBw;
		this.closed = false;
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
		System.out.println("process " + command[0]);

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

	}

	private void processLIST() {
		if (this.fh != null) {
			sendMessage(requestBw, ReturnString.fileStatusOk);
			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				for (File f : this.fh.list())
					sendMessage(bw, this.fh.getFileRights(f) + ' ' + f.getName());
				sendMessage(requestBw, ReturnString.closingDataConnection);
				bw.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} else
			sendMessage(requestBw, "You must connect first");

	}

	private void processSTOR() {
		if (this.fh != null) {
			try {
				sendMessage(requestBw, ReturnString.fileStatusOk);
				File dest = new File(fh.getWorkingDirectory() + '/' + command[1] );
				if(!dest.exists())
					dest.createNewFile();
				InputStream dataStream = s.getInputStream();
				fh.writeFile(dest, dataStream);
				sendMessage(requestBw, ReturnString.closingDataConnection);
			} catch (FileNotFoundException e) {
				sendMessage(requestBw, ReturnString.fileUnavailable);
			} catch (IOException e) {
				System.err.println("Error while opening file ");
			}
		} else
			sendMessage(requestBw, "You must connect first");

	}

	private void processRETR() {
		if (this.fh != null) {
			try {
				sendMessage(requestBw, ReturnString.fileStatusOk);
				File src = new File(fh.getWorkingDirectory() + '/' + command[1]);
				OutputStream targetStream = s.getOutputStream();
				fh.readFile(src, targetStream);
				sendMessage(requestBw, ReturnString.closingDataConnection);
			} catch (FileNotFoundException e) {
				sendMessage(requestBw, ReturnString.fileUnavailable);
			} catch (IOException e) {
				System.err.println("Error while opening file ");
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
		this.closed = true;
		try {
			if (s != null)
				this.s.close();
			if (sv != null)
				this.sv.close();
		} catch (IOException e) {
			System.err.println("can't close FTPData");
			e.printStackTrace();
		}
		System.out.println("FTPData successfully closed");
	}
	
	public boolean isClosed() {
		return this.closed;
	}

	// EXCEPTIONS
	public class CommandAlreadyAsked extends Exception {

		private static final long serialVersionUID = -7581484883396476122L;

	}
}
