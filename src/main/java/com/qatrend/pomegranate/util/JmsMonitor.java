package com.qatrend.pomegranate.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSubscriber;

import org.apache.log4j.Logger;

import com.qatrend.pomegranate.io.FileUtil;
import com.tibco.tibjms.Tibjms;
import com.tibco.tibjms.TibjmsTopicConnectionFactory;

public class JmsMonitor implements Runnable {

    private static Logger logger = Logger.getLogger(new Exception().getStackTrace()[0].getClassName());

    static String qName = "####NULL####";
    private String serverUrl, userName = "admin", pwd = "admin123", topicName, threadName;
    private int monitorNum;
    private TibjmsTopicConnectionFactory cFactory = null;
    private TopicConnection connection = null;
    private Session session = null;
    public Thread t;
    private TimeUtil timer = new TimeUtil();
    private int MAX_SECONDS = 3600;
    private boolean suspended = false;

    public JmsMonitor(String serverUrl, String queueName) {
        this.serverUrl = serverUrl;
        this.qName = queueName;
        // https://docs.tibco.com/pub/ems/8.2.1/doc/html/TIB_ems_users_guide/wwhelp/wwhimpl/common/html/wwhelp.htm#href=EMS.5.135.htm&single=true
        // https://docs.tibco.com/pub/enterprise_message_service/7.0.1-march-2013/doc/html/tib_ems_api_reference/api/javadoc/com/tibco/tibjms/Tibjms.html
        this.topicName = "$sys.monitor.Q.a." + queueName;
        // this.topicName = "$sys.monitor.Q.*." + queueName;
        this.monitorNum = RandomUtil.randInt(1000, 9999);
        this.threadName = "Thread-" + this.monitorNum + " Monitoring queue: " + this.qName + " on server: " + this.serverUrl;
    }

    public JmsMonitor(String serverUrl, String queueName, int maxSeconds) {
        this(serverUrl, queueName);
        this.MAX_SECONDS = maxSeconds;
    }

    @Override
    public void run() {

        cFactory = new TibjmsTopicConnectionFactory(this.serverUrl);

        try {
            // TibjmsAdmin admin = new TibjmsAdmin(this.serverUrl,
            // this.userName, this.pwd);
            connection = cFactory.createTopicConnection(this.userName, this.pwd);
            session = connection.createSession(false, javax.jms.Session.CLIENT_ACKNOWLEDGE);
            Topic topic = session.createTopic(this.topicName);
            // MessageConsumer consumer = session.createConsumer(topic);
            TopicSubscriber consumer = session.createDurableSubscriber(topic, this.threadName);
            consumer.setMessageListener(new MessageListener() {
                Map<String, Object> messageMap = new HashMap<String, Object>();

                @Override
                public void onMessage(Message message) {
                    if (message != null) {
                        try {
                            String dest = message.getStringProperty("target_dest_name");
                            String msgId = message.getStringProperty("msg_id");
                            // printMessage(message);
                            if (message instanceof TextMessage) {
                                TextMessage msg = (TextMessage) message;
                                // BeaverFileUtil.appendToFile(BeaverJmsUtil2.JMS_MESSAGE_FILE,
                                // msg.getText());
                                // logger.info(msg.getText());
                            }
                            if (message instanceof MapMessage) {
                                byte[] bytes = ((MapMessage) message).getBytes("message_bytes");
                                TextMessage msg = (TextMessage) Tibjms.createFromBytes(bytes);
                                FileUtil.appendToFile(JmsUtil.JMS_MESSAGE_FILE, msg.getText());
                                logger.info(msg.getText());
                            }
                            if (!messageMap.containsKey(msgId)) {
                                messageMap.put(msgId, message);
                            }
                        } catch (JMSException e) {
                            logger.error(e);
                        } catch (IllegalArgumentException e) {
                            logger.error(e);
                        }
                    }
                }
            });
            connection.start();
            logger.info("Started: " + this.threadName);
            while( !suspended && timer.timeElapsedSeconds() < MAX_SECONDS) {
                Thread.sleep(1000);
            }
            if(!suspended) {
                logger.info(MAX_SECONDS + " seconds elapsed. Stopping " + this.threadName);
                session.close();
                connection.close();
            }
        } catch (Exception e) {
            logger.info(e);
        }
    }

    public void start() {
        this.t = new Thread(this, this.threadName);
        this.t.start();
    }

    public void suspend() {
        try {
            suspended = true;
            session.close();
            connection.close();
        } catch (JMSException e) {
            logger.error(e);
        }
    }

    private static void printMessage(Message message) {
        try {
            Enumeration e = message.getPropertyNames();
            while (e.hasMoreElements()) {
                String prop = e.nextElement().toString();
                logger.info(prop + ": " + message.getObjectProperty(prop));
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }
}