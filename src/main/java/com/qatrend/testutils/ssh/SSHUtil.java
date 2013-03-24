package com.qatrend.testutils.ssh;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.qatrend.testutils.collection.CollectionUtil;
import com.qatrend.testutils.encryption.EncryptionUtil;
import com.qatrend.testutils.encryption.MyUserInfo;
import com.qatrend.testutils.logging.PLogger;
import com.qatrend.testutils.regex.RegexUtil;

public class SSHUtil {
	private static SSHUtil instance = null;
	private Session session;
	private Channel channel;
	private JSch jsch;
	private MyUserInfo mui;
	private String host;
	private String username;
	private String outputStr;
	private ArrayList<String> outputStrList;
	private int exitStatus = -1;
	private AuthType authType;
	
	public enum AuthType {
		PrivateKey, EncCredentials
	}
	
	public static SSHUtil getInstance(String rHost){
		if(instance == null) {
			// instantiate
			String encCredentials = System.getenv("ENC_PWD");
			if (encCredentials == null) {
				encCredentials = System.getProperty("ENC_PWD");
			}
			if (encCredentials != null) {
				instance = new SSHUtil( rHost, AuthType.EncCredentials );
				instance.authType = AuthType.EncCredentials;
			}
			else {
				instance = new SSHUtil( rHost, AuthType.PrivateKey );
				instance.authType = AuthType.PrivateKey;
			}
			
		}
		return instance;
	}


	public SSHUtil(String rHost) {
		try {
			this.jsch = new JSch();
			EncryptionUtil encrUtil = new EncryptionUtil();
			// get the Bluefin hostname and username
			this.host = rHost;
			// get the encrypted password string from maven parameters
			String encCredentials = System.getenv("ENC_PWD");
			if (encCredentials == null) {
				encCredentials = System.getProperty("ENC_PWD");
			}
			// System.out.println("Encrypted credentials: " + encCredentials);
			ArrayList<String> usrInfoList = encrUtil
					.threePartDecrypt(encCredentials);
			MyUserInfo rUI = new MyUserInfo(this.username, usrInfoList.get(2)); // 0=username,
																				// 1=Windows
																				// password,
																				// 2=UNIX
																				// password
			this.mui = rUI;
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public SSHUtil(String rHost, MyUserInfo rUI) {
		try {
			this.jsch = new JSch();
			this.mui = rUI;
			this.username = mui.username;
			this.host = rHost;
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public SSHUtil(File pvtKeyFile, String rHost, String rUser) {
		String passPhrase = "";
		String idName = "My Identification";
		this.host = rHost;
		this.username = rUser;
		System.out.println("host: " + this.host + " username: " + this.username);
		// this.mui = new MyUserInfo(this.username, "");
		// mui.setUsername(this.username);
		// mui.setPassphrase("");
		// Hashtable<String, String> config = new Hashtable<String, String>();
		// config.put(pvtKeyFile.getPath(), "ssh-dss");
		// this.session.setConfig(config);

		try {
			// Authentication informaiton
			byte[] keyArr = FileUtils.readFileToByteArray( pvtKeyFile );
			this.jsch = new JSch();
			this.jsch.addIdentity(idName, keyArr, null, passPhrase.getBytes());
			this.jsch.addIdentity("src/test/resources/keys/id_dsa", "");
			this.jsch.setKnownHosts("src/test/resources/keys/known_hosts");

			System.out.println("setting jsch identity using file: "
					+ pvtKeyFile.getPath());
			System.out.println("host key repo id: "
					+ jsch.getHostKeyRepository().getKnownHostsRepositoryID());
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
	}

	public SSHUtil(String rHost, AuthType authType) {
		switch(authType) {
		case EncCredentials: new SSHUtil(rHost);
		case PrivateKey: 
			String passPhrase = "";
			String idName = "My Identification";
			this.host = rHost;
			String encCredentials = System.getenv("ENC_PWD");
			if (encCredentials == null) {
				encCredentials = System.getProperty("ENC_PWD");
			}
			// System.out.println("Encrypted credentials: " + encCredentials);
			EncryptionUtil encrUtil = new EncryptionUtil();
			ArrayList<String> usrInfoList = encrUtil.threePartDecrypt(encCredentials);
			MyUserInfo rUI = new MyUserInfo(this.username, usrInfoList.get(2)); // 0=username,
																				// 1=Windows
																				// password,
																				// 2=UNIX
																				// password
			this.mui = rUI;
			this.username = rUI.username;
			String keyFilePath = "src/test/resources/keys/" + this.username + "_key";
			String knownHostsFilePath = "src/test/resources/keys/" + this.username + "_known_hosts";
			System.out.println("host: " + this.host + " username: " + this.username + " key file: " + keyFilePath);
			File pvtKeyFile = new File(keyFilePath);
			// this.mui = new MyUserInfo(this.username, "");
			// mui.setUsername(this.username);
			// mui.setPassphrase("");
			//Hashtable<String, String> config = new Hashtable<String, String>();
			//config.put(pvtKeyFile.getPath(), "ssh-dss");
			//this.session.setConfig(config);

			try {
				// Authentication informaiton
				
				byte[] keyArr = FileUtils.readFileToByteArray( pvtKeyFile );
				this.jsch = new JSch();
				this.jsch.addIdentity(idName, keyArr, null, passPhrase.getBytes());
				//this.jsch.addIdentity("src/test/resources/keys/id_dsa", "");
				//this.jsch.setKnownHosts("src/test/resources/keys/known_hosts");
				this.jsch.setKnownHosts( knownHostsFilePath );
				this.session=jsch.getSession(this.username, this.host, 22);
		        Properties config = new Properties();   
		        config.setProperty("StrictHostKeyChecking", "no");   
		        this.session.setConfig(config);   

				System.out.println("setting jsch identity using file: "
						+ keyFilePath );
				System.out.println("host key repo id: "
						+ jsch.getHostKeyRepository().getKnownHostsRepositoryID());
			} catch (Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
			}
		
		}
		
	}

	
	
	public int execCmd(String cmd) {
		CollectionUtil colUtil = new CollectionUtil();
		try {
			this.session = this.jsch.getSession(this.username, host,
					22);
			this.session.setUserInfo(this.mui);
			this.session.connect();
			this.channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(cmd);
			this.setOutput();
			// this.disconnect();
			
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		return this.exitStatus;
	}

	public int execShellCmd(String cmd) {
		try {
			this.session = this.jsch.getSession(this.username, this.host, 22);
			// this.session.setUserInfo( this.mui );
			// this.session.setConfig("StrictHostKeyChecking", "no");
			this.session.connect();
			this.channel = this.session.openChannel("exec");
			((ChannelExec) channel).setCommand(cmd);
			//cmd = cmd + "\n";
			//InputStream is = new ByteArrayInputStream(cmd.getBytes("UTF-8"));
			//channel.setInputStream(System.in);
			//Thread.sleep(1000);
			//channel.setInputStream(is);
			channel.connect();
			this.setOutput();
			//channel.setOutputStream(System.out);
			// this.setOutput();
			this.disconnect();
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		return this.exitStatus;
	}

	public static void readAndPrintOutput(Channel channel) {
		try {
			System.out.println("INFO: START - output from "
					+ channel.getSession().getHost());
			channel.setInputStream(null);
			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ channel.getExitStatus());
					break;
				}
				Thread.sleep(1000);
			}
			System.out.println("INFO: END - output from "
					+ channel.getSession().getHost());
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void readAndPrintOutput() {
		try {
			System.out.println("INFO: START - output from "
					+ channel.getSession().getHost());
			this.channel.setInputStream(null);
			((ChannelExec) this.channel).setErrStream(System.err);
			InputStream in = this.channel.getInputStream();
			this.channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ this.channel.getExitStatus());
					break;
				}
				Thread.sleep(2000);
			}
			System.out.println("INFO: END - output from "
					+ this.channel.getSession().getHost());
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	// output[0] = output string, output[1]=exit status
	public ArrayList<String> getOutput() {
		ArrayList<String> output = new ArrayList<String>();
		StringBuffer outputStr = new StringBuffer();
		try {
			// System.out.println("INFO: START - output from " +
			// channel.getSession().getHost());
			this.channel.setInputStream(null);
			((ChannelExec) this.channel).setErrStream(System.err);
			InputStream in = this.channel.getInputStream();
			this.channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					// System.out.print(new String(tmp, 0, i));
					outputStr.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ this.channel.getExitStatus());
					break;
				}
				Thread.sleep(2000);
			}
			// System.out.println("INFO: END - output from " +
			// this.channel.getSession().getHost());
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		output.add(outputStr.toString());
		String exitStatus = Integer.toString(this.channel.getExitStatus());
		output.add(exitStatus);
		channel.disconnect();
		return output;
	}

	public void setOutput() {
		StringBuffer outputStr = new StringBuffer();
		try {
			// System.out.println("INFO: START - output from " +
			// channel.getSession().getHost());
			this.channel.setInputStream(null);
			((ChannelExec) this.channel).setErrStream(System.err);
			InputStream in = this.channel.getInputStream();
			this.channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					// System.out.print(new String(tmp, 0, i));
					outputStr.append(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: "
							+ this.channel.getExitStatus());
					break;
				}
				Thread.sleep(2000);
			}
			// System.out.println("INFO: END - output from " +
			// this.channel.getSession().getHost());
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		RegexUtil reUtil = new RegexUtil();
		this.outputStr = outputStr.toString();
		System.out.println(outputStr);
		this.outputStrList = reUtil.getLinesList(this.outputStr);
		this.exitStatus = this.channel.getExitStatus();
		// output.add(outputStr.toString());
		// String exitStatus = Integer.toString(this.channel.getExitStatus());
		// output.add( exitStatus );
		channel.disconnect();
	}

	public ArrayList<String> getOutputStrList() {
		return this.outputStrList;
	}

	public String getOutputStr() {
		return this.outputStr;
	}

	public int getExitStatus() {
		return this.exitStatus;
	}

	public void disconnect() {
		this.channel.disconnect();
		this.session.disconnect();
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}


}
