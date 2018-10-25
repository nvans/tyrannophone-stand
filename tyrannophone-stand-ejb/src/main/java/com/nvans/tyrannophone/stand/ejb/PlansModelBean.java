package com.nvans.tyrannophone.stand.ejb;

import com.nvans.tyrannophone.stand.model.Plan;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PlansModelBean {

    private List<Plan> plans;

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }
}
