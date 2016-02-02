package ftpserver;

public class FtpData extends Thread {

	public String test;
	
	public FtpData() {
		test = "0";
	}
	
	public void run() {
		while(true) {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(test);
		}
	}
	
}
