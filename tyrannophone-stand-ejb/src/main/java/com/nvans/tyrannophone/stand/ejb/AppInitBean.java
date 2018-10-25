package com.nvans.tyrannophone.stand.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

@Startup
@Singleton
public class AppInitBean {

    private static final Logger log = Logger.getLogger(AppInitBean.class.getName());

    @EJB
    private ModelUpdaterBean modelUpdaterBean;

    @PostConstruct
    public void init() {
        log.info("Fetch model");
        modelUpdaterBean.updateModel();
    }

}
