package ftpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import ftpserver.FtpData.CommandAlreadyAsked;

/**
 * Cette classe permet de gérer les commandes ftp reçues par le serveur
 * @author badreddine et cojez
 *
 */
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
		this.bw = new BufferedWriter(
				new OutputStreamWriter(s.getOutputStream()));
	}

	public void run() {
		this.sendMessage(ReturnString.serviceReady);
		String request;
		while (this.listen) {
			try {
				if ((request = this.br.readLine()) != null)
					processRequest(request);
			} catch (IOException e) {
				System.err.println("can't read request from socket");
				e.printStackTrace();
			} catch (CommandAlreadyAsked e) {
				System.err.println("There is already a pending command");
			}
		}
		this.close();
	}

	private void sendMessage(String msg) {
		try {
			bw.write(msg);
			bw.newLine();
			bw.flush();
		} catch (SocketException se) {
			System.err
					.println("can't send message " + msg + ", pipe is broken");
		} catch (IOException e1) {
			System.err.println("can't send message :" + msg);
		}
	}

	private void close() {
		try {
			sendMessage(ReturnString.goodBye);
			if (ftpd != null)
				this.ftpd.close();
			if (br != null)
				this.br.close();
			if (bw != null)
				this.bw.close();
			if (s != null)
				this.s.close();
		} catch (IOException e) {
			System.err.println("can't close FTPRequest");
			e.printStackTrace();
		}
		System.out.println("FTPRequest successfully closed");
	}

	private void processRequest(String requete) throws CommandAlreadyAsked,
			IOException {
		System.out.println("received request : " + requete);

		if (requete != null) {
			String[] parsedCommand = requete.split(" ");

			switch (parsedCommand[0].toLowerCase()) {
			case "port":
				if (parsedCommand.length == 2) {
					String[] parsedAddr = parsedCommand[1].split(",");
					if (parsedAddr.length == 6)
						processPORT(parsedAddr[0], parsedAddr[1],
								parsedAddr[2], parsedAddr[3], parsedAddr[4],
								parsedAddr[5]);
					else
						sendMessage(ReturnString.parameterSyntaxError
								+ " > ip1 ip2 ip3 ip4 port1 port2");
				}
				break;
			case "pasv":
				processPASV();
				break;
			case "user":
				if (parsedCommand.length == 2)
					processUSER(parsedCommand[1]);
				else
					sendMessage(ReturnString.parameterSyntaxError
							+ " please enter your user name");
				break;
			case "pass":
				if (parsedCommand.length == 2)
					processPASS(parsedCommand[1]);
				else
					sendMessage(ReturnString.parameterSyntaxError
							+ " please enter your password");
				break;
			case "pwd":
				processPWD();
				break;
			case "cwd":
				if (parsedCommand.length == 2)
					processCWD(parsedCommand[1]);
				else
					sendMessage(ReturnString.parameterSyntaxError
							+ " please enter a directory");
				break;
			case "cdup":
				processCDUP();
				break;
			case "list":
				processLIST(parsedCommand);
				break;
			case "stor":
				processSTOR(parsedCommand);
				break;
			case "retr":
				processRETR(parsedCommand);
				break;
			case "syst":
				processSYST();
				break;
			case "quit":
				processQUIT();
				break;
			default:
				incorrectCommand(requete);
			}
		}
	}

	private void processCDUP() {
		if (this.fh != null)
			try {
				this.fh.changeWorkingDirectory("..");
				this.sendMessage(ReturnString.commandOk);
			} catch (IOException e) {
				sendMessage(ReturnString.fileUnavailable);
			}
		else
			sendMessage(ReturnString.userNotLogged + "You must connect first");

	}

	private void processCWD(String directoryName) {
		try {
			if (directoryName.equals("..")) {
				processCDUP();
			} else {
				if (this.fh.fileInDir(directoryName)) {
					this.sendMessage(ReturnString.commandOk);
					this.fh.changeWorkingDirectory(directoryName);
				} else {
					sendMessage(ReturnString.fileUnavailable);
				}
			}
		} catch (IOException e) {
			sendMessage(ReturnString.fileUnavailable);
		}
	}

	private void processPASV() throws CommandAlreadyAsked {
		if (this.ftpd == null || this.ftpd.isClosed())
			try {
				ServerSocket sv = new ServerSocket(2124);
				if (sv.getInetAddress().getAddress()[0] == 0)
					this.sendMessage(ReturnString.enteringPassiveMode
							+ ' '
							+ hostToString(InetAddress.getLocalHost()
									.getAddress(), sv.getLocalPort()));
				else
					this.sendMessage(ReturnString.enteringPassiveMode
							+ ' '
							+ hostToString(sv.getInetAddress().getAddress(),
									sv.getLocalPort()));
				this.ftpd = new FtpData(this.fh, sv, this.bw);
				this.ftpd.start();

			} catch (IOException e) {
				sendMessage(ReturnString.connectionRefused + "ciicic");
			}
		else
			sendMessage(ReturnString.connectionDenied
					+ "please wait before data connexion ends");

	}

	private String hostToString(byte[] addr, int port) {
		String str = "(";
		for (int i : addr) {
			str += i + ",";
		}
		str += port / 256 + ",";
		str += port % 256;
		str += ')';
		return str;
	}

	private void processPORT(String ip1, String ip2, String ip3, String ip4,
			String port1, String port2) {
		if (this.ftpd == null || this.ftpd.isClosed())
			try {
				String ip = ip1 + '.' + ip2 + '.' + ip3 + '.' + ip4;
				int port = Integer.parseInt(port1) * 256
						+ Integer.parseInt(port2);
				Socket s = new Socket(ip, port);
				this.ftpd = new FtpData(this.fh, s, this.bw);
				this.ftpd.start();
				this.sendMessage(ReturnString.portSuccessful);

			} catch (IOException e) {
				sendMessage(ReturnString.connectionRefused);
			}
		else
			sendMessage(ReturnString.connectionDenied
					+ "please wait before data connexion ends");
	}

	private void processUSER(String username) {
		System.out.println("command USER");
		if (this.fh != null)
			sendMessage(ReturnString.userAlreadyLogged
					+ "You already have an active session");
		else {
			this.username = username;
			this.sendMessage(ReturnString.needPass);
		}
	}

	private void processPASS(String password) {
		System.out.println("command PASS");
		if (this.fh != null)
			sendMessage(ReturnString.userAlreadyLogged
					+ "You already have an active session");
		else if (authenticate(password))
			createSession();
	}

	private void processQUIT() {
		System.out.println("command QUIT");
		this.listen = false;
	}

	private void processPWD() {
		System.out.println("command PWD");
		if (this.fh != null) {

			sendMessage(this.fh.getWorkingDirectory());

		} else
			sendMessage(ReturnString.userNotLogged + "You must connect first");
	}

	private void processLIST(String[] parsedCommand) throws CommandAlreadyAsked {
		System.out.println("command LIST");
		if (ftpd != null && !ftpd.isClosed())
			this.ftpd.askCommand(parsedCommand);
		else
			sendMessage(ReturnString.userNotLogged + "You must connect first");
	}

	private void processRETR(String[] parsedCommand) throws CommandAlreadyAsked {
		System.out.println("command RETR");
		if (ftpd != null && !ftpd.isClosed())
			this.ftpd.askCommand(parsedCommand);
		else
			sendMessage(ReturnString.userNotLogged + "You must connect first");

	}

	private void processSTOR(String[] parsedCommand) throws CommandAlreadyAsked {
		System.out.println("command STOR");
		if (ftpd != null && !ftpd.isClosed())
			this.ftpd.askCommand(parsedCommand);
		else
			sendMessage(ReturnString.userNotLogged + "You must connect first");
	}

	private void processSYST() {
		sendMessage(ReturnString.nameSystType);
	}

	private void createSession() {
		try {
			this.fh = new FtpFileHandler(username);
			sendMessage(ReturnString.userLogged);
		} catch (IOException e) {
			System.err.println("Can't access File system");
			sendMessage(ReturnString.needAccountForStoring
					+ "Can't access File system");
		}
	}

	private boolean authenticate(String password) {
		return true;
	}

	public void incorrectCommand(String requete) {
		System.out.println("can't process request " + requete);
		sendMessage(ReturnString.syntaxError);
	}

}
