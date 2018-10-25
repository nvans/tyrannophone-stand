package com.nvans.controller;

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.event.Observes;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class EventPatcher {

    private static final Logger log = Logger.getLogger(EventPatcher.class.getName());

    @Inject
    @Push(channel = "events")
    private PushContext pushContext;

    public void sendMessage(@Observes String event) {

        log.info("Event caught");
        log.info("Sending message to web socket");
        pushContext.send(event);
    }

}
