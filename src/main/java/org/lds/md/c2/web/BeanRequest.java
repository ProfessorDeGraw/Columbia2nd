package org.lds.md.c2.web;

import java.util.List;

import org.springframework.beans.factory.DisposableBean;

public interface BeanRequest extends DisposableBean {
	Object get(List<String> parms);
}
