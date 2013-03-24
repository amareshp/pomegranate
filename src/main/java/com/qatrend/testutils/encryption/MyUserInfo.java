package com.qatrend.testutils.encryption;

import java.util.ArrayList;

import com.jcraft.jsch.UserInfo;

public class MyUserInfo implements UserInfo {
	public String username;
	public String password;
	public String passphrase;  //unix password
	public static final int WINDOWS = 1;
	public static final int UNIX = 2;

	public MyUserInfo(String uName, String password){
		try{
			this.username = uName;
			this.password = password;
			this.passphrase = "";
			
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

//	ArrayList<String> usrInfoList = encrUtil.threePartDecrypt(encCredentials);
//	MyUserInfo rUI = new MyUserInfo(this.username, usrInfoList.get(2));  //0=username, 1=Windows password, 2=UNIX password
	public MyUserInfo(String encCredentials, int osType){
		try{
			EncryptionUtil encrUtil = new EncryptionUtil();
			ArrayList<String> usrInfoList = encrUtil.threePartDecrypt(encCredentials);
			if(osType == MyUserInfo.WINDOWS) {
				this.username = usrInfoList.get(0);
				this.password = usrInfoList.get(1);
			} else {
				if(osType == MyUserInfo.UNIX) {
					this.username = usrInfoList.get(0);
					this.password = usrInfoList.get(2);
				}
				else {
					throw new Exception("invalid OS type.");
				}
			}
			
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public MyUserInfo(String encCredentials){
		try{
			EncryptionUtil encrUtil = new EncryptionUtil();
			ArrayList<String> usrInfoList = encrUtil.threePartDecrypt(encCredentials);
			this.username = usrInfoList.get(0);
			this.password = usrInfoList.get(1);
			this.passphrase = usrInfoList.get(2); //unix password
		}
		catch(Exception ex){
			System.out.println("Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
    public String getPassword(){ return this.password; }
    public String getPassphrase(){ return this.passphrase; }
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
}