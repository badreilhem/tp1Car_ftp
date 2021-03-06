package ftpserver;


/**
 * Cette classe contient la plupart des codes retournés par le serveur
 * @author badreddine et cojez
 *
 */
public class ReturnString {
	public static final String restartMarkerReply = "110 	Restart marker reply";
	public static final String serviceReadyIn = "120 	Service Ready in ";
	public static final String dataConnectionAlreadyOpen = "125 	Data Connection already open; transfer starting.";
	public static final String fileStatusOk = "150 	File status okay; about to open data connection.";
	public static final String commandOk = "200 	Command OK";
	public static final String portSuccessful = "200 	Port command successful";
	public static final String commandSuperfluousNotImpl = "202 	Command not implemented, superfluous at this site.";
	public static final String systemStatus = "211 	";
	public static final String dirStatus = "212 	";
	public static final String fileStatus = "213 	";
	public static final String helpMsg = "214 	";
	public static final String nameSystType = "215 	UNIX Type: I";
	public static final String serviceReady = "220 	Service ready for new user.";
	public static final String goodBye = "221 	Goodbye.";
	public static final String dataConnectionOpen = "225 	Data connection open; no transfer in progress.";
	public static final String closingDataConnection = "226 	Closing data connection.";
	public static final String enteringPassiveMode = "227 Entering Passive Mode";
	public static final String userLogged = "230 	User logged in, proceed.";
	public static final String fileActionComplete = "250 	Requested file action okay, completed.";
	public static final String folderCreated = "257 	";
	public static final String needPass = "331 	User name okay, need password.";
	public static final String needAccount = "332 	Need account for login.";
	public static final String fileActionPendingInfo = "350 	Requested file action pending further information.";
	public static final String serviceNotAvailable = "421 	Service not available, closing control connection.";
	public static final String userLimitReached = "421 	User limit reached.";
	public static final String connectionDenied = "421 	You are not authorized to make the connection.";
	public static final String maxConnectionReached = "421 	Max connections reached.";
	public static final String maxConnectionExceeded = "421 	Max connections exceeded.";
	public static final String cannotOpenDataConnection = "425 	Cannot open data connection.";
	public static final String connectionClosed = "426 	Connection closed; transfer aborted.";
	public static final String fileActionNotTaken = "450 	Requested file action not taken.";
	public static final String requestedActionAborted = "451 	Requested action aborted: local error in processing.";
	public static final String requestedActionNotTaken = "452 	Requested action not taken. Insufficient storage space in system.";
	public static final String syntaxError = "500 	Syntax error, command unrecognized, command line too long.";
	public static final String parameterSyntaxError = "501 	Syntax error in parameters or arguments.";
	public static final String commandNotImpl = "502 	Command not implemented.";
	public static final String badSequenceOfCommands = "503 	Bad sequence of commands.";
	public static final String commandNotImplForParam = "504 	Command not implemented for that parameter.";
	public static final String userNotLogged = "530 	User not logged in.";
	public static final String userAlreadyLogged = "531 	User already logged in.";
	public static final String needAccountForStoring = "532 	Need account for storing files.";
	public static final String fileUnavailable = "550 	Requested action not taken. File unavailable, not found, not accessible ";
	public static final String exceededStorageAllocation = "552 	Requested file action aborted. Exceeded storage allocation.";
	public static final String fileNameNotAllowed = "553 	Requested action not taken. File name not allowed.";
	public static final String connectionResetByPeer = "10054 	Connection reset by peer. The connection was forcibly closed by the remote host.";
	public static final String cannotConnectToRemote = "10060 	Cannot connect to remote server.";
	public static final String connectionRefused = "10061 	Cannot connect to remote server. The connection is actively refused by the server.";
	public static final String directoryNotEmpty = "10066 	Directory not empty.";
	public static final String serverFull = "10068 	Too many users, server is full.";
}
