package org.dyndns.warenix.vignettefiltersexchange.vignette;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class VignetteEffectImporter {

	public static class VignetteEffect implements Serializable {
		public String displayName;
		public int size;
		public String content;

		public String toString() {
			return String.format("[%s] [%d]\n%s", displayName, size, content);
		}
	}

	private static final String TAG = "VignetteEffectImporter";

	public static VignetteEffect create(Context context, Intent intent) {
		if (intent != null) {
			if (Intent.ACTION_SEND.equals(intent.getAction())) {
				Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
				return create(context, uri);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param context
	 * @param uri
	 *            <p>
	 *            content://uk.co.neilandtheresa.NewVignette//sdcard1/VIE/uk.co.
	 *            neilandtheresa.NewVignette/shared/Effects.vfx
	 *            </p>
	 * @return
	 */
	public static VignetteEffect create(Context context, Uri uri) {
		if (uri != null) {
			Cursor cursor = context.getContentResolver().query(uri, null, null,
					null, null);
			if (cursor != null && cursor.moveToPosition(0)) {

				VignetteEffect vignetteEffect = new VignetteEffect();

				// since we don't know what is the content provider, dump cursor
				// columns
				// int l = cursor.getColumnCount();
				// for (int i = 0; i < l; ++i) {
				// String name = cursor.getColumnName(i);
				// Log.d(TAG, i + ":" + name + " v:" + cursor.getString(i));
				// }

				vignetteEffect.displayName = cursor.getString(cursor
						.getColumnIndex("_display_name"));
				vignetteEffect.size = cursor.getInt(cursor
						.getColumnIndex("_size"));

				InputStream fileInputStream = null;
				try {
					fileInputStream = context.getContentResolver()
							.openInputStream(uri);
					Log.d(TAG, "fis length:" + fileInputStream.available());

					StringBuffer fileContent = new StringBuffer("");

					byte[] buffer = new byte[1024];
					int read = 0;
					while ((read = fileInputStream.read(buffer)) != -1) {
						// don't use the complete buffer, only the used part of
						// it.
						fileContent.append(new String(buffer, 0, read));
					}
					// Log.d(TAG, "buffer:" + fileContent.toString());
					vignetteEffect.content = fileContent.toString();
					return vignetteEffect;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fileInputStream != null) {
						try {
							fileInputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			} else {
				Log.d(TAG, "Cannot move to first");
			}
		}
		return null;
	}
}
