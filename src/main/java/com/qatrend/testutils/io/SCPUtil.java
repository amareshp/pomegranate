package com.qatrend.testutils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.qatrend.testutils.encryption.EncryptionUtil;
import com.qatrend.testutils.encryption.MyUserInfo;
import com.qatrend.testutils.ssh.SSHUtil;
import com.qatrend.testutils.ssh.SSHUtil.AuthType;

public class SCPUtil {
	private static SCPUtil instance = null;
	AuthType authType;
	Session session;
	ChannelSftp channel;
	JSch jsch;
	MyUserInfo mui;
	String host;
	String username;
	public static String defaultHost = "xxxx";
	public static String defaultUser = "";

	public static SCPUtil getInstance(){
		if(instance == null) {
			// instantiate
			String encCredentials = System.getenv("ENC_PWD");
			if (encCredentials == null) {
				encCredentials = System.getProperty("ENC_PWD");
			}
			if (encCredentials != null) {
				instance = new SCPUtil( AuthType.EncCredentials );
				instance.authType = AuthType.EncCredentials;
			}
			else {
				instance = new SCPUtil( AuthType.PrivateKey );
				instance.authType = AuthType.PrivateKey;
			}
		}
		return instance;
	}
	
	
	public SCPUtil() {
		try{
			String sshUser = defaultUser;
			this.jsch=new JSch();
			//get the Bluefin hostname and username
			this.host = defaultHost;
			this.username = defaultUser;
	        Properties config = new Properties();   
	        config.setProperty("StrictHostKeyChecking", "no");   

			String encCredentials = System.getenv("ENC_PWD");
			if(encCredentials == null) {
				encCredentials = System.getProperty("ENC_PWD");
			}
			if(encCredentials != null) {
				EncryptionUtil encrUtil = new EncryptionUtil();
				//get the encrypted password string from maven parameters
				//System.out.println("Encrypted credentials: " + encCredentials);
				ArrayList<String> usrInfoList = encrUtil.threePartDecrypt(encCredentials);
				//MyUserInfo rUI = new MyUserInfo(this.username, usrInfoList.get(2));  //0=username, 1=Windows password, 2=UNIX password
				this.mui = new MyUserInfo(this.username, usrInfoList.get(2));  //0=username, 1=Windows password, 2=UNIX password
				this.session=jsch.getSession(this.username, this.host, 22);
			    this.session.setUserInfo(this.mui);
		        this.session.setConfig(config);   

		        this.authType = AuthType.EncCredentials;
			}
			else{
				if(sshUser.equals("ppdeploy")) {
					//do nothing
				}
				else {
					this.authType = authType.PrivateKey;
					new SCPUtil(AuthType.PrivateKey);
				}
			}
			
		}
		catch( Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public SCPUtil( AuthType authType ) {
		switch(authType) {
		case EncCredentials: new SCPUtil();
		case PrivateKey: 
			String passPhrase = "";
			String idName = "My Identification";
			this.host = defaultHost;
			this.username = defaultUser;
			String keyFilePath = "src/test/resources/keys/" + this.username + "_key";
			String knownHostsFilePath = "src/test/resources/keys/" + this.username + "_known_hosts";
			System.out.println("host: " + this.host + " username: " + this.username + " key file: " + keyFilePath);
			File pvtKeyFile = new File(keyFilePath);
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
	
	
	public SCPUtil(String rHost) {
		try{
			this.jsch=new JSch();
			EncryptionUtil encrUtil = new EncryptionUtil();
			//get the Bluefin hostname and username
			this.host = rHost;
			this.username = defaultUser;
			//get the encrypted password string from maven parameters
			String encCredentials = System.getenv("ENC_PWD");
			if(encCredentials == null) {
				encCredentials = System.getProperty("ENC_PWD");
			}
			//System.out.println("Encrypted credentials: " + encCredentials);
			ArrayList<String> usrInfoList = encrUtil.threePartDecrypt(encCredentials);
			MyUserInfo rUI = new MyUserInfo(this.username, usrInfoList.get(2));  //0=username, 1=Windows password, 2=UNIX password
			this.mui = rUI;
			this.session=jsch.getSession(this.username, this.host, 22);
		    this.session.setUserInfo(rUI);
	        Properties config = new Properties();   
	        config.setProperty("StrictHostKeyChecking", "no");   
	        this.session.setConfig(config);   
		}
		catch( Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public SCPUtil(String rHost, MyUserInfo rUI){
		//
		try{
			this.username = rUI.getUsername();
			this.host = rHost;
	        JSch jsch = new JSch();   
			this.session=jsch.getSession(this.username, this.host, 22);
		    this.session.setUserInfo(rUI);
	        Properties config = new Properties();   
	        config.setProperty("StrictHostKeyChecking", "no");   
	        this.session.setConfig(config);   
			
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
		
	public static void readAndPrintOutput(Channel channel) {
		//
		try{
			//
			System.out.println("INFO: START - output from " + channel.getSession().getHost());
			channel.setInputStream(null);
			// FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			// ((ChannelExec)channel).setErrStream(fos);
			((ChannelExec)channel).setErrStream(System.err);
			InputStream in=channel.getInputStream();
			channel.connect();
			byte[] tmp=new byte[1024];
			while(true){
				while(in.available() > 0){
					int i=in.read(tmp, 0, 1024);
					if(i < 0)break;
					System.out.print(new String(tmp, 0, i));
				}
				if(channel.isClosed()){
					System.out.println("exit-status: "+channel.getExitStatus());
					break;
				}
				Thread.sleep(1000);
			}
			System.out.println("INFO: END - output from " + channel.getSession().getHost());
		}
		catch (Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void scpFile(String lFilePath, String rDir, String rFileName){
	try{
		this.connect();
		System.out.println("INFO: Copying local file " + lFilePath + " to remote location: " + rDir + "/" + rFileName);
		SSHUtil sshu = new SSHUtil( this.host, this.mui);
		sshu.execCmd("mkdir " + rDir); //relative or absolute directory path
        this.channel.cd(rDir);   
        this.channel.put(new FileInputStream(lFilePath), rFileName);
        this.disconnect();
        System.out.println("INFO: Finished copying local file " + lFilePath + " to remote location: " + rDir + "/" + rFileName);
	}			
	catch(Exception ex){
		System.out.println("Exception: " + ex.getMessage());
		ex.printStackTrace();
	}
}

	public void scpPutFile(String lFilePath, String rDir, String rFileName){
		String sshUser = defaultUser;
		System.out.println("INFO: Copying local file " + lFilePath + " to remote location: " + rDir + "/" + rFileName);

		try{
			this.connect();
			//System.out.println("INFO: Copying local file " + lFilePath + " to remote location: " + rDir + "/" + rFileName);
			//SSHUtil sshu = new SSHUtil( this.host, this.mui);
			//sshu.execCmd("mkdir " + rDir); //relative or absolute directory path
	        this.channel.cd(rDir);
	        this.channel.put(new FileInputStream(lFilePath), rFileName);
	        this.disconnect();
	        //System.out.println("INFO: Finished copying local file " + lFilePath + " to remote location: " + rDir + "/" + rFileName);
			System.out.println("INFO: Finished copying local file " + lFilePath + " to remote location: " + rDir + "/" + rFileName);
		}			
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

//	public void scpPutFile(String lFilePath, String rFilePath){
//		try{
//			this.connect();
//			System.out.println("INFO: Copying local file " + lFilePath + " to remote location: " + rFilePath);
//	        this.channel.put(new FileInputStream(lFilePath), rFilePath);
//	        this.disconnect();
//	        System.out.println("INFO: Finished copying local file " + lFilePath + " to remote location: " + rFilePath);
//		}			
//		catch(Exception ex){
//			System.out.println("Exception: " + ex.getMessage());
//			ex.printStackTrace();
//		}
//	}
	
	
	public void scpGetFile(String rFilePath, String lFilePath){
		String sshUser = defaultUser;
		System.out.println("INFO: Copying remote file " + rFilePath + " to local location: " + lFilePath);
		try{
			this.connect();
			//System.out.println("INFO: Copying remote file " + rFilePath + " to local location: " + lFilePath);
			this.channel.get(rFilePath, new FileOutputStream(lFilePath) );
	        this.disconnect();
	        //System.out.println("INFO: Finished copying remote file " + rFilePath + " to local location: " + lFilePath );
			System.out.println("INFO: Finished copying remote file " + rFilePath + " to local location: " + lFilePath );
		}			
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public void connect(){
		try{
	        this.session.connect();   
	        this.channel = (ChannelSftp)session.openChannel("sftp");   
	        this.channel.connect();   
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public void disconnect(){
        this.channel.disconnect();   
        this.session.disconnect();   
	}


	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}


	public ChannelSftp getChannel() {
		return channel;
	}


	public void setChannel(ChannelSftp channel) {
		this.channel = channel;
	}
}
