package com.nvans.tyrannophone.stand.ejb;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.jms.*;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Startup
@Singleton
public class JMSInitBean {

    private static final Logger log = Logger.getLogger(JMSInitBean.class.getName());

    private Connection jmsConnection;
    private Session jmsSession;
    private MessageConsumer messageConsumer;
    private ConnectionFactory jmsConnectionFactory;

    private boolean isConnected = false;

    @EJB
    private ModelUpdaterBean modelUpdaterBean;

    @PostConstruct
    public void init() {

        log.info("Fetch model");
        modelUpdaterBean.updateModel();

        log.info("JMS Initialization");

        log.info("JMS properties initialization");
        Map<String, Object> props = new HashMap<>();
        props.put(TransportConstants.HOST_PROP_NAME, "192.168.99.100");
        props.put(TransportConstants.PORT_PROP_NAME, 61616);

        TransportConfiguration transportConfiguration =
                new TransportConfiguration(NettyConnectorFactory.class.getName(), props);

        jmsConnectionFactory =
                ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration)
                        .setUser("tyrannophone")
                        .setPassword("tyrannophone");

        startJmsConnection(jmsConnectionFactory);
    }


    @Schedule(hour="*", minute = "*", second = "*/15", persistent = false)
    public void reconnectionTimer(final Timer timer) {

        if (isConnected) return;

        startJmsConnection(jmsConnectionFactory);

    }


    public void startJmsConnection(ConnectionFactory jmsConnectionFactory) {

        try {
            // Connection initialization
            jmsConnection = jmsConnectionFactory.createConnection();
            jmsConnection.setExceptionListener(ex -> {

                if ("DISCONNECT".equals(ex.getErrorCode())) {
                    log.info("JMS connection stopped.");

                    closeResource(messageConsumer);
                    closeResource(jmsSession);
                    closeResource(jmsConnection);

                    isConnected = false;
                }
            });

            jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue messageQueue = jmsSession.createQueue("UpdateQueue");
            // Create async consumer
            messageConsumer = jmsSession.createConsumer(messageQueue);
            messageConsumer.setMessageListener(message -> {
                try {
                    String msg = message.getBody(String.class);
                    log.info("Message received: " + msg);
                    modelUpdaterBean.updateModel();

                } catch (JMSException e) {
                    log.warning(e.getMessage());
                }
            });

            jmsConnection.start();
            isConnected = true;

            log.info("JMS connection established");
            modelUpdaterBean.updateModel();

        } catch (JMSException e) {
            log.info(this + " Can't establish JMS connection. Next attempt in 10 seconds. " + e.getMessage());

        }
    }


    @PreDestroy
    public void destroy() {

        closeResource(messageConsumer);
        closeResource(jmsSession);
        closeResource(jmsConnection);
    }

    private void closeResource(AutoCloseable resource) {

        if (resource == null) return;

        try {
            resource.close();
        }
        catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

}
