package org.dyndns.warenix.vignettefiltersexchange.model;

public class DumpYourPhotoResponse {

	public int id;
	public String title;
	public String file_name;
	public int views;
	public String hash;
	public ImageUrl url;

	public static class ImageUrl {
		public String small;
		public String medium;
		public String large;
		public String full;
	}
}
