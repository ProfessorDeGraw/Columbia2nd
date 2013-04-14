package org.lds.md.c2;

import java.util.List;

import org.springframework.stereotype.Component;

@Component("Test")
public class Test implements BeanRequest {

	@Override
	public Object get(List<String> parms) {
		if (parms.size()>0) {
		return parms.get(0);
		}
		return "Test";
	}

}
