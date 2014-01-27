package org.dyndns.warenix.vignettefiltersexchange;

import org.dyndns.warenix.vignettefiltersexchange.ui.EffectListFragment;
import org.dyndns.warenix.vignettefiltersexchange.ui.EffectListFragment.ModeType;
import org.dyndns.warenix.vignettefiltersexchange.ui.InputDialogFragment;
import org.dyndns.warenix.vignettefiltersexchange.ui.InputDialogFragment.InputDialogFragmentListener;
import org.dyndns.warenix.vignettefiltersexchange.ui.UploadFragment;
import org.dyndns.warenix.vignettefiltersexchange.ui.VFEIntent;
import org.dyndns.warenix.vignettefiltersexchange.vignette.VignetteEffectImporter;
import org.dyndns.warenix.vignettefiltersexchange.vignette.VignetteEffectImporter.VignetteEffect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements
		InputDialogFragmentListener {

	private static final String TAG = "MainActivity";

	private String mKeyword = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent1 = getIntent();
		String action = intent1.getAction();
		Bundle extras = intent1.getExtras();
		Log.d(TAG, intent1.toString());
		Log.d(TAG, action.toString());
		Log.d(TAG, extras == null ? "none" : extras.toString());

		if (savedInstanceState == null) {
			Intent intent = getIntent();
			String effectID = VFEIntent.getFilterEffectID(intent);
			if (effectID != null) {
				// show effect id only
				Log.d(TAG, "show id:" + effectID);
				showList(null, effectID, ModeType.ONE_EFFECT);
			} else {
				VignetteEffect vignetteEffect = VignetteEffectImporter.create(
						getApplicationContext(), intent);

				if (vignetteEffect != null) {
					Log.d(TAG, vignetteEffect.toString());
					showUpload(vignetteEffect);
				} else {
					Log.d(TAG, "no effect");
					showList(mKeyword, null, ModeType.KEYWORD_LIST);
				}
			}
		}
	}

	private void showList(String keyword, String objectID, ModeType mode) {
		Fragment f = EffectListFragment.newInstance(keyword, null, mode,
				objectID);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, f, null).commit();
	}

	private void showUpload(VignetteEffect vignetteEffect) {

		Fragment f = UploadFragment.newInstance(vignetteEffect);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, f, null).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			InputDialogFragment.newInstance(mKeyword).show(
					getSupportFragmentManager(), "dialog");
		}
		return false;

	}

	@Override
	public void onTextResult(String text) {
		mKeyword = text.trim();
		showList(text, null, ModeType.KEYWORD_LIST);
	}

}
