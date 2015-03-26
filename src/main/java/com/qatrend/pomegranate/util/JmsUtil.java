package com.qatrend.pomegranate.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class JmsUtil {

    public static String JMS_MESSAGE_FILE = "logs/jms-messages.txt";
    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());
    public ArrayList<JmsMonitor> monitors = new ArrayList<JmsMonitor>();
    TimeUtil timer = new TimeUtil();
    

    public ArrayList<JmsMonitor> monitorTibcoQueue(String serversCsv, String qName) {
        List<String> servers = null;
        if (serversCsv.contains(",")) {
            servers = Arrays.asList(serversCsv.split(","));
        }
        // truncate the file
        // BeaverFileUtil.truncateFile(JMS_MESSAGE_FILE);

        // Start monitoring threads for all the servers.
        for (String server : servers) {
            JmsMonitor monitor = new JmsMonitor(server, qName);
            monitor.start();
            this.monitors.add(monitor);
        }
        return this.monitors;
    }

    public void monitorTibcoQueueBlocking(String serversCsv, String qName, int monitorForSeconds) {
        List<String> servers = null;
        if (serversCsv.contains(",")) {
            servers = Arrays.asList(serversCsv.split(","));
        }
        // truncate the file
        // BeaverFileUtil.truncateFile(JMS_MESSAGE_FILE);

        // Start monitoring threads for all the servers.
        for (String server : servers) {
            JmsMonitor monitor = new JmsMonitor(server, qName, monitorForSeconds);
            monitor.start();
            this.monitors.add(monitor);
        }
        try {
            Thread.sleep(30000 + (monitorForSeconds * 1000));
        } catch(Exception ex) {
            logger.error(ex);
        }
    }
    
    public void stopMonitors() {
        for (JmsMonitor monitor : this.monitors) {
            try {
                Thread.sleep(3000);
                monitor.suspend();
                monitor.t.join();
                if(!monitor.t.isAlive()) {
                    logger.info("Stopped: " + monitor.t.getName());
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }


    public static void sendTopicMessage(String serverUrl, String topicName, String messageStr, String userName, String password) {
        Connection connection = null;
        Session session = null;
        MessageProducer msgProducer = null;
        Destination destination = null;
        try {
            TextMessage msg;
            logger.info("Publishing to destination '" + topicName + "'\n");
            ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);
            connection = factory.createConnection(userName, password);
            /* create the session */
            session = connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);
            /* create the destination */
            destination = session.createTopic(topicName);
            /* create the producer */
            msgProducer = session.createProducer(null);
            /* publish messages */
            /* create text message */
            msg = session.createTextMessage();
            /* set message text */
            msg.setText(messageStr);
            /* publish message */
            msgProducer.send(destination, msg);
            logger.info("Published message: " + messageStr);
            /* close the connection */
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }    

    public static void sendQueueMessage(String serverUrl, String queueName, String messageStr, String userName, String password) {
        Connection connection = null;
        Session session = null;
        MessageProducer msgProducer = null;
        Destination destination = null;
        try {
            TextMessage msg;
            logger.info("Publishing to destination '" + queueName + "'\n");
            ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(serverUrl);
            connection = factory.createConnection(userName, password);
            /* create the session */
            session = connection.createSession(javax.jms.Session.AUTO_ACKNOWLEDGE);
            /* create the destination */
            destination = session.createQueue(queueName);
            /* create the producer */
            msgProducer = session.createProducer(null);
            /* publish messages */
            /* create text message */
            msg = session.createTextMessage();
            /* set message text */
            msg.setText(messageStr);
            /* publish message */
            msgProducer.send(destination, msg);
            logger.info("Published message: " + messageStr);
            /* close the connection */
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }    
    
}