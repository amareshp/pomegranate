package com.qatrend.pomegranate.encryption;

import java.util.ArrayList;

import com.jcraft.jsch.UserInfo;
import com.qatrend.pomegranate.logging.PLogger;
import com.qatrend.pomegranate.ssh.SSHUtil;

/**
 * This class stores the information about the user
 * It assumes the user's credentials consist of a username and a password.
 * 
 * @author <a href="http://visitamaresh.com" target=_blank>Amaresh Pattanaik (amaresh@visitamaresh.com)</a>
 *
 */
public class MyUserInfo implements UserInfo {
	/** The user's username (same for both Windows and UNIX) */
	private String username;
	/** The user's password for Windows system */
	private String password;

	/**
	 * default constructor. Username is the value of java property user.name
	 * 
	 */
	public MyUserInfo(){
		try{
			String uName = SSHUtil.getEnvOrSystem("user.name");
			this.setUsername(uName);
			this.setPassword("");
		}
		catch(Exception ex){
			PLogger.getLogger().error( ex );
		}
	}

	/**
	 * Constructor with username and password will be ""
	 * 
	 * @param uName		User's username
	 */
	public MyUserInfo(String uName){
		try{
			this.setUsername(uName);
			this.setPassword("");
		}
		catch(Exception ex){
			PLogger.getLogger().error( ex );
		}
	}
	
	/**
	 * Constructor with username and password as input
	 * 
	 * @param uName		User's username
	 * @param password	User's password
	 */
	public MyUserInfo(String uName, String password){
		try{
			this.setUsername(uName);
			this.setPassword(password);
		}
		catch(Exception ex){
			PLogger.getLogger().error( ex );
		}
	}


	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
    public String getPassword(){ 
    	return this.password; 
    }

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassphrase(){
		return this.password;
	}
    
    public void showMessage(String str){
    	// do nothing.
    	//System.out.println("Not prompting for username and password.");
    }
    
    public boolean promptPassphrase(String str){
    	// do nothing.
    	//System.out.println("Not prompting for passphrase.");
    	return true;
    }
    
    public boolean promptPassword(String str){
    	// do nothing.
    	//System.out.println("Not prompting for password.");
    	return true;
    }
    
    public boolean promptYesNo(String str){
    	// do nothing.
    	//System.out.println("Not prompting for username and password confirmation.");
    	return true;
    }
    

}