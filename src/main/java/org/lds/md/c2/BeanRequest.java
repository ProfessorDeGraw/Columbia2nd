package org.lds.md.c2;

import java.util.List;

public interface BeanRequest {
	Object get(List<String> parms);

	void doWork();
}
