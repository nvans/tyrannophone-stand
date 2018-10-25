package com.nvans.tyrannophone.stand.ejb;

import org.apache.activemq.artemis.api.core.ActiveMQDisconnectedException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.ejb.*;

import javax.jms.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Properties;
import java.util.logging.Logger;

@Startup
@Singleton
public class JMSInitBean {

    private static final Logger log = Logger.getLogger(JMSInitBean.class.getName());

    private Context jndiContext;
    private Connection jmsConnection;
    private Session jmsSession;
    private MessageConsumer messageConsumer;
    private ConnectionFactory jmsConnectionFactory;
    private Queue messageQueue;

    private boolean isConnected = false;

    @EJB
    private ModelUpdaterBean modelUpdaterBean;

    @PostConstruct
    public void init() {

        log.info("Fetch model");
        modelUpdaterBean.updateModel();

        log.info("JMS Initialization");

        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        props.put(Context.PROVIDER_URL, "tcp://localhost:61617");
        props.put("ConnectionFactory", "failover:tcp://localhost:61617");
        props.put("queue.queue/UpdateQueue", "UpdateQueue");

        try {
            // Look-up remote resources
            jndiContext = new InitialContext(props);
            jmsConnectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            messageQueue = (Queue) jndiContext.lookup("queue/UpdateQueue");

            log.info("Attempt to connect JMS server");
            startJmsConnection(jmsConnectionFactory, messageQueue);


        } catch (NamingException e) {

            log.severe("Can't establish JMS connection. " + e.getMessage());

            destroy();
        }

    }


    @Schedule(hour="*", minute = "*", second = "*/15", persistent = false)
    public void reconnectionTimer(final Timer timer) {

        if (isConnected) return;

        startJmsConnection(jmsConnectionFactory, messageQueue);

    }


    public void startJmsConnection(ConnectionFactory jmsConnectionFactory, Queue messageQueue) {

        log.info("Attempt to connect JMS server");

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
            log.info("Can't establish JMS connection. Next attempt in 10 seconds.");
        }
    }


    @PreDestroy
    public void destroy() {

        closeResource(messageConsumer);
        closeResource(jmsSession);
        closeResource(jmsConnection);

        if (jndiContext != null) {
            try {
                jndiContext.close();
            } catch (NamingException e) {
                log.warning(e.getMessage());
            }
        }

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
