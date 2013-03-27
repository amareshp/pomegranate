package com.qatrend.testutils.ssh;

import org.testng.annotations.Test;

import com.qatrend.testutils.logging.PLogger;

@Test
public class SSHUtilTest {
	public void sshTest(){
		String host = "stage2idi02.qa.paypal.com";
		String user = "apattanaik";
		String pwd = "Jenkins@14";
		SSHUtil sshUtil = new SSHUtil(host, user, pwd);
		SSHUtilOutput out = sshUtil.execCmd("pwd;");
		PLogger.getLogger().info("output = " + out.getOutputTxt());
	}

}
