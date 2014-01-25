package org.dyndns.warenix.vignettefiltersexchange.ui;

import android.content.Intent;
import android.net.Uri;

public class VFEIntent {
	public static final String scheme = "http";
	public static final String host = "warenix.vfe";

	public static String getFilterEffectID(Intent intent) {
		String id = null;
		if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			Uri data = intent.getData();
			if (data.getHost().equals(host)) {
				id = data.getLastPathSegment();
			}
		}
		return id;
	}

	public static String createShareFilterEffectUri(String id) {
		return String.format("%s://%s/%s", scheme, host, id);
	}
}
