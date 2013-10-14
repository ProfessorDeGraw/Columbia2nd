package org.lds.md.c2.action;

import java.util.List;

import org.lds.md.c2.web.BeanRequest;
import org.springframework.stereotype.Component;

@Component("Test")
public class Test implements BeanRequest {

	// private static final Logger log = LoggerFactory
	// .getLogger(HelloDatabase.class);

	@Override
	public Object get(List<String> parms) {
		if (parms.size() > 0) {
			return parms.get(0);
		}
		return "Test";
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}
}
