package com.nvans.controller;

import com.nvans.tyrannophone.stand.ejb.PlansModelBean;
import com.nvans.tyrannophone.stand.model.Plan;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Named
public class Home {

    @Inject
    private PlansModelBean plansModel;


    public List<Plan> getPlans() {

        return plansModel.getPlans();
    }

}
