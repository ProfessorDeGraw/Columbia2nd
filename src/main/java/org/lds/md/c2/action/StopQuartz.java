package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.quartz.QuartzManager;
import org.lds.md.c2.web.BeanRequest;
import org.lds.md.c2.web.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component("StopQuartz")
public class StopQuartz implements BeanRequest, DisposableBean {
	private static final Logger log = LoggerFactory.getLogger(StopQuartz.class);
	
	//@Autowired
	//QuartzManager quartzManager = null;
	
	public void setQuartzManager(QuartzManager quartzManager) {
		//this.quartzManager = quartzManager;
	}

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		//quartzManager.stop();
		
		log.warn("Quartz Stop");
		
		return "Quartz Stop";
	}

	@Override
	public void destroy() throws Exception {
		//if ( quartzManager != null ) {
		//	quartzManager.stop();
		//}
	}

}
