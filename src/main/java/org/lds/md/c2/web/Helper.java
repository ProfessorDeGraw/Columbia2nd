package org.lds.md.c2.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
	private static final Logger log = LoggerFactory.getLogger(Helper.class);

	public static void fixParms(List<String> parms) {
		for ( int i=0; i<parms.size(); i++) {
			try {
				parms.set(i, URLDecoder.decode(parms.get(i), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				log.warn("Encode Error:", e);
			}
		}
	}
}
