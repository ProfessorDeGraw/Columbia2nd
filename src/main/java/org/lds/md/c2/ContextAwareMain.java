package org.lds.md.c2;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextAwareMain {
	private static final Logger log = LoggerFactory
			.getLogger(ContextAwareMain.class);
	// private ApplicationContext ac;
	private BeanRequest request;

	// @Override
	// public void setApplicationContext(ApplicationContext arg0)
	// throws BeansException {
	// ac = arg0;
	// }

	public void setRequest(BeanRequest request) {
		this.request = request;
	}

	public BeanRequest getRequest() {
		return request;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(
				"applicationContext.xml");

		ContextAwareMain demo = (ContextAwareMain) ctx
				.getBean("ContextAwareMain");
		demo.get(Arrays.asList(args));

		ctx.close();
	}

	public Object get(List<String> args) {
		log.trace("Running DB");
		// request.get(null);
		return request.get(args);
	}

}
