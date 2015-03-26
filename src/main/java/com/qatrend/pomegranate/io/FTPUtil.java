package com.qatrend.pomegranate.io;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class FTPUtil {
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());
    
    private static Session session = null;
    private static Channel channel = null;
    private static ChannelSftp channelSftp = null;
    private static JSch jsch = null;

    public static void getFile(String host, String userName, String pvtKeyPath,
            String remoteFilePath, String localDestDirectory) {
        try {
            jsch = new JSch();
            jsch.addIdentity(pvtKeyPath);
            String userHome = System.getProperty("user.home");
            String knownHostFile = userHome + "/.ssh/known_hosts";
            jsch.setKnownHosts(knownHostFile);
            session = jsch.getSession(userName, host, 22);
            java.util.Properties config = new java.util.Properties();
            // this setting will cause JSCH to automatically add all target servers' entry to the known_hosts file
            config.put("StrictHostKeyChecking", "no");  
            session.setConfig(config);
            UserInfo ui = new JschUserInfo();
            session.setUserInfo(ui);
            session.connect();
            logger.info("Connected to host: " + host);
            channel = session.openChannel("sftp");
            channel.connect();
            logger.info("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            logger.info("Home directory: " + channelSftp.getHome() );
            
            channelSftp.get(remoteFilePath, localDestDirectory);
            logger.info("Transferred file: " + remoteFilePath + " To " + localDestDirectory);
            channelSftp.exit();
            logger.info("sftp Channel exited.");
            channel.disconnect();
            logger.info("Channel disconnected.");
            session.disconnect();
            logger.info("Host Session disconnected.");

        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static void getFile(String host, String userName, String pvtKeyPath,
            String remoteFilePath, OutputStream localDestStream) {
        try {
            jsch = new JSch();
            jsch.addIdentity(pvtKeyPath);
            String userHome = System.getProperty("user.home");
            String knownHostFile = userHome + "/.ssh/known_hosts";
            jsch.setKnownHosts(knownHostFile);
            session = jsch.getSession(userName, host, 22);
            java.util.Properties config = new java.util.Properties();
            // this setting will cause JSCH to automatically add all target servers' entry to the known_hosts file
            config.put("StrictHostKeyChecking", "no");  
            session.setConfig(config);
            UserInfo ui = new JschUserInfo();
            session.setUserInfo(ui);
            session.connect();
            logger.info("Connected to host: " + host);
            channel = session.openChannel("sftp");
            channel.connect();
            //logger.info("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            //logger.info("Home directory: " + channelSftp.getHome() );
            
            channelSftp.get(remoteFilePath, localDestStream);
            logger.info("Transferred file: " + remoteFilePath + " to local.");
            channelSftp.exit();
            //logger.info("sftp Channel exited.");
            channel.disconnect();
            //logger.info("Channel disconnected.");
            session.disconnect();
            //logger.info("Host Session disconnected.");

        } catch (Exception ex) {
            logger.error(ex);
        }
    }
    
    
    public static void putFile(String host, String userName, String password, String localFilePath, String remoteDir) {
        try {
            jsch = new JSch();
            String userHome = System.getProperty("user.home");
            String knownHostFile = userHome + "/.ssh/known_hosts";
            jsch.setKnownHosts(knownHostFile);
            session = jsch.getSession(userName, host, 22);
            java.util.Properties config = new java.util.Properties();
            // this setting will cause JSCH to automatically add all target servers' entry to the known_hosts file
            config.put("StrictHostKeyChecking", "no");  
            session.setConfig(config);
            session.setPassword(password);
            session.connect();
            logger.info("Connected to host: " + host);
            channel = session.openChannel("sftp");
            channel.connect();
            logger.info("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            logger.info("Home directory: " + channelSftp.getHome() );
            channelSftp.cd(remoteDir);
            channelSftp.put(localFilePath, remoteDir);
            logger.info("Transferred file: " + localFilePath + " To remote location - " + remoteDir);
            channelSftp.exit();
            logger.info("sftp Channel exited.");
            channel.disconnect();
            logger.info("Channel disconnected.");
            session.disconnect();
            logger.info("Host Session disconnected.");

        } catch (Exception ex) {
            logger.error(ex);
        }
    }
    
    private static void list(String location) {
        try {
            Vector<Object[]> listing = channelSftp.ls(location);
            for(Object element : Collections.list(listing.elements()) ) {
                logger.info(element);
            }
        } catch(Exception ex) {
            logger.error(ex);
        }
    }

}
