package com.nvans.controller;

import com.nvans.tyrannophone.stand.ejb.ModelUpdaterBean;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.jms.*;
import javax.naming.NamingException;
import java.io.Serializable;

@Named("greeter")
@SessionScoped
public class Greeter implements Serializable {

    private static final long serialVersionUID = 1L;


    @EJB
    private ModelUpdaterBean modelUpdaterBean;

    private String message;

    private String test;

    public void setName(String name) {
//        message = modelUpdaterBean.sayHello(name);
    }

    public String getMessage() throws NamingException, JMSException {

//        Properties props = new Properties();
//        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
//        props.put(Context.PROVIDER_URL, "tcp://localhost:61617");
//        props.put("ConnectionFactory", "tcp://localhost:61617");
//        props.put("queue.queue/ExampleQueue", "ExampleQueue");
//
//        try {
//
//            Context context = new InitialContext(props);
//            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
//            Queue queue = (Queue) context.lookup("queue/ExampleQueue");
//            Connection connection = connectionFactory.createConnection();
//            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//            MessageConsumer messageConsumer = session.createConsumer(queue);
//            messageConsumer.setMessageListener(new UpdatePlansReceiveMessageBean());
////            connection.start();
//
//        } catch (NamingException | JMSException e) {
//            e.printStackTrace();
//        }
//        TextMessage messageReceived = (TextMessage) messageConsumer.receive(1000);
//        System.out.println("Received message:" + messageReceived.getText());

        return this.message;
    }

}
