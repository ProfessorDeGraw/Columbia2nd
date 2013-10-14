package org.lds.md.c2.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("QuartzManager")
public class QuartzManager  {
	private static final Logger log = LoggerFactory.getLogger(QuartzManager.class);
	
	private  Scheduler scheduler = null;
	
	public void start(int intervalInSeconds) {
		if (scheduler == null) {
			try {
				scheduler = StdSchedulerFactory.getDefaultScheduler();
				
				scheduler.start();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// define the job and tie it to our HelloJob class
		    JobDetail job = JobBuilder.newJob(HelloJob.class)
		        .withIdentity("job1", "group1")
		        .build();

		    // Trigger the job to run now, and then repeat every 40 seconds
		    Trigger trigger = TriggerBuilder.newTrigger()
		        .withIdentity("trigger1", "group1")
		        .startNow()
		        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
		                .withIntervalInSeconds(intervalInSeconds)
		                .repeatForever())            
		        .build();

		    // Tell quartz to schedule the job using our trigger
		    try {
				scheduler.scheduleJob(job, trigger);
				
				log.warn("Quartz Scheduled");
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			scheduler=null;
			log.warn("Quartz Deleted");
		}
	}
}
