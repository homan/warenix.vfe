package org.dyndns.warenix.vignettefiltersexchange.util;

public class ExpAPI {

	public static final String API_BASE = "http://exp-warenix.rhcloud.com";
	public static final String SERVICE_VFE = "/vignettefiltersexchange";
	public static final String ACTION_LIST = "/list";
	public static final String ACTION_POST = "/insert";
	public static final String ACTION_GET = "/get";

	public static final String URL_LIST = API_BASE + SERVICE_VFE + ACTION_LIST;
	public static final String URL_POST = API_BASE + SERVICE_VFE + ACTION_POST;
	public static final String URL_GET = API_BASE + SERVICE_VFE + ACTION_GET;
}
