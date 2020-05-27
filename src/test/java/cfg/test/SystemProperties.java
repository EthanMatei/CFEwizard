package cfg.test;

import java.net.UnknownHostException;
import java.util.Properties;

public class SystemProperties {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties props = System.getProperties();
		props.list(System.out);
		
		java.net.InetAddress localMachine = null;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
			System.out.println("Hostname of local machine: " + localMachine.getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}		
	}

}
