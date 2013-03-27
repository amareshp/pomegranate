package com.qatrend.testutils.encryption;

import java.util.ArrayList;

import com.jcraft.jsch.UserInfo;
import com.qatrend.testutils.logging.PLogger;

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
	 * Constructor with username and password as input
	 * 
	 * @param uName		User's username
	 * @param password	User's password
	 */
	public MyUserInfo(String uName, String password){
		try{
			this.username = uName;
			this.password = password;
		}
		catch(Exception ex){
			PLogger.getLogger().error( ex );
		}
	}

	/**
	 * Construct a MyUserInfo object from encrypted credentials
	 * 
	 * @param encCredentials		
	 * @param osType
	 */
	public MyUserInfo(String encCredentials){
		try{
			EncryptionUtil encrUtil = new EncryptionUtil();
			ArrayList<String> usrInfoList = encrUtil.threePartDecrypt(encCredentials);
			this.username = usrInfoList.get(0);
			this.password = usrInfoList.get(1);
		}
		catch(Exception ex){
			PLogger.getLogger().error("Exception: " + ex.getMessage());
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