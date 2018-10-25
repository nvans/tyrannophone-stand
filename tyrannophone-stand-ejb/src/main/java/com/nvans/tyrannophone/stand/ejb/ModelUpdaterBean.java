package com.nvans.tyrannophone.stand.ejb;

import com.nvans.tyrannophone.stand.model.Plan;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class ModelUpdaterBean {

    private static final Logger log = Logger.getLogger(ModelUpdaterBean.class.getName());
    private static final String PLANS_RESOURCE_URL = "http://localhost:8080/api/plans";

    @Inject
    private Event<String> event;

    @Inject
    private PlansModelBean plansModel;

    @SuppressWarnings("unchecked")
    public void updateModel() {

        log.info("updateModel() method invoked");
        log.info("Attempting connect remote resource " + PLANS_RESOURCE_URL);

        String eventMessage = "update";

        Client restClient = ClientBuilder.newClient();
        WebTarget webTarget = restClient.target(PLANS_RESOURCE_URL);

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        try (Response response = invocationBuilder.get()) {

            List<Plan> plans = response.readEntity(List.class);
            plansModel.setPlans(plans);

            log.info("Plans model updated");

        }
        catch (ProcessingException e) {
            log.warning("Unable to connect");
            eventMessage = "unavailable";
        }

        restClient.close();

        log.info("Event fired");
        event.fire(eventMessage);
    }
}
