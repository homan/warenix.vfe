package org.dyndns.warenix.vignettefiltersexchange.model;

public class ListRequest {

	public String[] tags;
	public int limit = 10;
	/**
	 * exclusive
	 */
	public String max_id;
	/**
	 * inclusive
	 */
	public String since_id;
}
