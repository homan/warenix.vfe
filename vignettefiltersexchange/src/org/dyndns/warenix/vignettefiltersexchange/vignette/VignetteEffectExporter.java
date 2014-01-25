package org.dyndns.warenix.vignettefiltersexchange.vignette;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dyndns.warenix.vignettefiltersexchange.vignette.VignetteEffectImporter.VignetteEffect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class VignetteEffectExporter {

	private static final String TEMP_EFFECT_FILENAME = "effect.vfx";

	public static void createExternalStoragePrivateFile(Context context,
			VignetteEffect vignetteEffect) {
		// Create a path where we will place our private file on external
		// storage.
		File file = getLocalTempEffectFile(context, vignetteEffect);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(vignetteEffect.content.getBytes());
		} catch (IOException e) {
			Log.w("ExternalStorage", "Error writing " + file, e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
			}
		}
	}

	private static File getLocalTempEffectFile(Context context,
			VignetteEffect vignetteEffect) {
		File file = new File(context.getExternalFilesDir(null),
				TEMP_EFFECT_FILENAME);
		return file;
	}

	public static void openFile(Context context, VignetteEffect vignetteEffect) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = getLocalTempEffectFile(context, vignetteEffect);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.vignette-effects");
		context.startActivity(intent);
	}
}
