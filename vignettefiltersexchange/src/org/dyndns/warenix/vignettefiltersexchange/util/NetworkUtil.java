package org.dyndns.warenix.vignettefiltersexchange.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class NetworkUtil {
	public static String call(String uri, String json) {
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(json, "utf-8"));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse response = new DefaultHttpClient().execute(httpPost);
			String str = EntityUtils.toString(response.getEntity(), "utf-8");
			return str;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
