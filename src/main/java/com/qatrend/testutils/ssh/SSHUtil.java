package com.qatrend.testutils.ssh;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.qatrend.testutils.encryption.EncryptionUtil;
import com.qatrend.testutils.encryption.MyUserInfo;
import com.qatrend.testutils.logging.PLogger;
import com.qatrend.testutils.regex.RegexUtil;

/**
 * Utility class for interacting with UNIX machines. 
 * This class uses jsch which is java implementation of ssh
 * 
 * @author <a href="http://visitamaresh.com" target=_blank>Amaresh Pattanaik (amaresh@visitamaresh.com)</a>
 *
 */
public class SSHUtil {
	private static SSHUtil sInstance = null;
	private JSch jsch;
	private Session session;
	private Channel channel;
	private MyUserInfo mui;
	private String host;
	private String username;
	private String password;
	private String outputStr;
	private ArrayList<String> outputStrList;
	private int exitStatus = -1;
	private SSHUtilOutput output = null;
	private AuthType authType = AuthType.USER_PASS;
	
	public enum AuthType {
		PRIVATE_KEY, ENC_CREDS, USER_PASS
	}

	/**
	 * Assumes that the environment variable or runtime parameter ENC_USER has been set to the encrypted string for username
	 * Assumes that the environment variable or runtime parameter ENC_PWD has been set to the encrypted string for password
	 * 
	 * @param rHost		the remote UNIX host. e.g. myhost.someserver.com
	 * @return			an instance of SSHUtil
	 */
	public SSHUtil(String rHost) {
		try {
			this.jsch = new JSch();
			this.host = rHost;
			EncryptionUtil encrUtil = new EncryptionUtil();
			this.username = getEnvOrSystem("ENC_USER");
			this.password = getEnvOrSystem("ENC_PWD");
			//decrypt username and password
			this.username = encrUtil.decrypt(this.username);
			this.password = encrUtil.decrypt(this.password);
			
			this.session=jsch.getSession(this.username, this.host, 22);
			this.session.setPassword(this.password);
			this.session.connect(30000);
			this.channel=session.openChannel("exec");  //shell , exec
			this.channel.setInputStream(System.in);
			this.channel.setOutputStream(System.out);
			this.channel.connect(3000);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Constructor that accepts the remote host name, username and password
	 * 
	 * @param remoteHost		the unix host name (e.g. domain.server.com)
	 * @param username			the unix username
	 * @param password			the unix password
	 */
	public SSHUtil(String remoteHost, String username, String password) {
		try {
			this.jsch = new JSch();
			this.host = remoteHost;
			this.username = username;
			this.password = password;
			this.session = this.jsch.getSession(username, remoteHost, 22);
			this.session.setPassword(password);
			this.mui = new MyUserInfo(this.username, this.password);
			//this.mui.setPassword(password);
			//this.mui.setUsername(username);
			this.session.setUserInfo(this.mui);
		} catch (Exception ex) {
			PLogger.getLogger().error( ex );
		}
	}

	/**
	 * Constructor with private key file. Use DSA key
	 * Refer to <a href="http://www.cyberciti.biz/faq/ssh-password-less-login-with-dsa-publickey-authentication/">ssh key setup</a>
	 * use command ssh-keygen -t dsa
	 * chmod 755 .ssh
	 * scp ~/.ssh/id_dsa.pub user@jerry:.ssh/authorized_keys
	 * 
	 * on remote server
	 * chmod 600 ~/.ssh/authorized_keys
	 * 
	 * @param pvtKeyFile
	 * @param passphrase
	 * @param rHost
	 * @param rUser
	 */
	public SSHUtil(File pvtKeyFile, String passphrase, String rHost, String rUser) {
		
		if(passphrase == null){
			passphrase = "";
		}
		
		String idName = "My Identification";
		this.host = rHost;
		this.username = rUser;
		PLogger.getLogger().info("host: " + this.host + " username: " + this.username);
		try {
			// Authentication informaiton
			byte[] keyArr = FileUtils.readFileToByteArray( pvtKeyFile );
			this.jsch = new JSch();
			this.jsch.addIdentity(idName, keyArr, null, passphrase.getBytes());
			//this.jsch.addIdentity(pvtKeyFile.getAbsolutePath(), passphrase);
			//this.jsch.setKnownHosts("C:\\Users\\xxxx\\.ssh\\known_hosts");
			this.session = this.jsch.getSession(rUser, rHost, 22);
			this.session.setPassword(passphrase);
			this.mui = new MyUserInfo(this.username);
			this.session.setUserInfo(this.mui);
			
			
			PLogger.getLogger().info("setting jsch identity using file: "	+ pvtKeyFile.getPath());
			PLogger.getLogger().info("host key repo id: "	+ jsch.getHostKeyRepository().getKnownHostsRepositoryID());
		} catch (Exception ex) {
			PLogger.getLogger().error( ex );
		}
	}

	public SSHUtilOutput execCmd(String cmd) {
		PLogger.getLogger().info("Host: " + this.host + " username: " + this.username);
		PLogger.getLogger().info("Executing command: " + cmd);
		try {
			this.session.connect(30000);
			this.channel=this.session.openChannel("exec");  //shell , exec
			((ChannelExec) this.channel).setCommand(cmd);
			this.channel.connect(3000);
			
			this.channel.setInputStream(null);
			this.channel.setOutputStream(System.out);
			((ChannelExec) this.channel).setErrStream(System.out);
			
			this.setOutput();
			this.channel.disconnect();
			this.session.disconnect();
		} catch (Exception ex) {
			PLogger.getLogger().error( ex );
		}
		PLogger.getLogger().info("Exit status: " + this.exitStatus);
		PLogger.getLogger().debug("Command output:\n" + this.outputStr );
		return this.output;
	}

	/**
	 * Read and print the output from the JSch Channel
	 */
	private void readAndPrintOutput() {
		try {
			PLogger.getLogger().info("START - output from " + channel.getSession().getHost());
			//this.channel.setInputStream(null);
			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);
			//this.channel.connect();
			((ChannelExec) this.channel).setErrStream(System.err);
			InputStream in = this.channel.getInputStream();
			//this.channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					PLogger.getLogger().info(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					PLogger.getLogger().info("exit-status: " + this.channel.getExitStatus());
					break;
				}
				Thread.sleep(1000);
			}
			PLogger.getLogger().info("END - output from " + this.channel.getSession().getHost());
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Set the output of a command execution.
	 * 
	 */
	private void setOutput() {
		this.output = new SSHUtilOutput();
		StringBuffer outputBuf = new StringBuffer();
		try {
			InputStream in = this.channel.getInputStream();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					//PLogger.getLogger().info( new String(tmp, 0, i) );
					outputBuf.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					//PLogger.getLogger().info("exit-status: " + this.channel.getExitStatus());
					break;
				}
				Thread.sleep(2000);
			}
		} catch (Exception ex) {
			PLogger.getLogger().error( ex );
		}
		this.output.setOutputTxt( outputBuf.toString() );
		this.outputStr = outputBuf.toString();
		this.output.setReturnCode( this.channel.getExitStatus() );
		this.exitStatus = this.channel.getExitStatus();
		RegexUtil reUtil = new RegexUtil();
		this.outputStrList = reUtil.getLinesList(this.outputStr);
	}

	/**
	 * Get the output as an ArrayList<String>
	 * 
	 * @return		an SSHUtilOutput object. The output object has output text and return code.
	 */
	private SSHUtilOutput getOutput() {
		return this.output;
	}

	
	/**
	 * Get the output text as an ArrayList of String
	 * 
	 * @return		An ArrayList of String. Each line of output is one element in the ArrayList.
	 */
	public ArrayList<String> getOutputStrList() {
		return this.outputStrList;
	}

	/**
	 * Get the output as one String
	 * 
	 * @return		The output as a String
	 */
	public String getOutputStr() {
		return this.outputStr;
	}

	/**
	 * Get the exit status of the command execution.
	 * 
	 * @return		the exit status
	 */
	public int getExitStatus() {
		return this.exitStatus;
	}

	/**
	 * Disconnect the channel and session exclusively.
	 */
	public void disconnect() {
		this.channel.disconnect();
		this.session.disconnect();
	}

	public Session getSession() {
		return session;
	}

	/**
	 * get the value of an environment or system property. First check if a property by that name exists in the environment properties.
	 * If the property does not exist in the environment, check the system properties.
	 * 
	 * @param property		name of the property to look for.
	 * @return				return the value of the property. Return null if the property is not defined in the environment or the system properties.
	 */
	public static String getEnvOrSystem(String property) {
		String value = System.getenv( property );
		if (value == null) {
			value = System.getProperty( property );
		}
		return value;
	}

}
