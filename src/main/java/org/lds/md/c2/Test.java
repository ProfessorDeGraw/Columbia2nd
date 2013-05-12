package org.lds.md.c2;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component("Test")
public class Test implements BeanRequest {
	
	private static final Logger log = LoggerFactory.getLogger(HelloDatabase.class);

	@Override
	public Object get(List<String> parms) {
		if (parms.size()>0) {
		return parms.get(0);
		}
		return "Test";
	}

	@Override
	public void doWork() {
		log.trace("do work");
	}

}
