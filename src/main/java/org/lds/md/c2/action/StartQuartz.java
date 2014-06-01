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

//@Component("StartQuartz")
public class StartQuartz implements BeanRequest, DisposableBean {
	private static final Logger log = LoggerFactory.getLogger(StartQuartz.class);
	
	//@Autowired
	//QuartzManager quartzManager = null;
	
	public void setQuartzManager(QuartzManager quartzManager) {
		//this.quartzManager = quartzManager;
	}

	@Override
	public Object get(List<String> parms) {
		Helper.fixParms(parms);

		if (parms.size() >= 1) {
			String time = parms.get(0);
			
			log.warn(time);
			
			//quartzManager.start(Integer.parseInt(time));
			
			return "Quartz Start:" + time;
		}
		return "Data";
	}

	@Override
	public void destroy() throws Exception {
		//if (quartzManager !=null) { 
		//	quartzManager.stop();
		//}
	}

}
