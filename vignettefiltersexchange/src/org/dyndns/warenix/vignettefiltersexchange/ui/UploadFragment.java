package org.dyndns.warenix.vignettefiltersexchange.ui;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.dyndns.warenix.dumpyourphoto.DYPAPI;
import org.dyndns.warenix.dumpyourphoto.DumpYourPhoto;
import org.dyndns.warenix.http.HTTPPostClient;
import org.dyndns.warenix.http.HTTPPostClient.StringResponseHandler;
import org.dyndns.warenix.http.HTTPPostContent;
import org.dyndns.warenix.vignettefiltersexchange.R;
import org.dyndns.warenix.vignettefiltersexchange.model.CreateRequest;
import org.dyndns.warenix.vignettefiltersexchange.model.DumpYourPhotoResponse;
import org.dyndns.warenix.vignettefiltersexchange.util.ExpAPI;
import org.dyndns.warenix.vignettefiltersexchange.util.NetworkUtil;
import org.dyndns.warenix.vignettefiltersexchange.vignette.VignetteEffectImporter.VignetteEffect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

public class UploadFragment extends Fragment implements OnClickListener {
	private static final String TAG = "UploadFragment";
	private static final int SELECT_PICTURE = 0x1234;
	private static final String BUNDLE_VIGNETTE_EFFECT = "org.dyndns.warenix.vignettefiltersexchange.ui.UploadFragment.BUNDLE_VIGNETTE_EFFECT";

	long totalFileSize;

	ImageView imageView;
	TextView filename;
	TextView effect;
	TextView des;
	TextView tags;

	VignetteEffect vignetteEffect;
	String mSelectedImagePath;
	UploadProgressDialogFragment mUploadProgressFragment;

	private static Gson GSON = new GsonBuilder().create();

	public UploadFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_upload, null);
		imageView = (ImageView) view.findViewById(R.id.imageView1);

		int[] ids = new int[] { R.id.imageView1, };
		for (int id : ids) {
			view.findViewById(id).setOnClickListener(this);
		}

		vignetteEffect = (VignetteEffect) getArguments().getSerializable(
				BUNDLE_VIGNETTE_EFFECT);
		// filename = (TextView) view.findViewById(R.id.filename);
		// filename.setText(vignetteEffect.displayName);
		effect = (TextView) view.findViewById(R.id.effect);
		effect.setText(vignetteEffect.content);
		des = (TextView) view.findViewById(R.id.des);
		tags = (TextView) view.findViewById(R.id.tags);
		return view;
	}

	public void uploadPhoto(String filePath, String contentType) {
		File image = new File(filePath);
		totalFileSize = image.length();
		Log.d("lab", String.format("file path: %s content-type: %s size: %d",
				filePath, contentType, totalFileSize));

		mUploadProgressFragment = UploadProgressDialogFragment.newInstance(
				totalFileSize,
				getResources().getString(R.string.status_upload_photo));
		mUploadProgressFragment.show(getChildFragmentManager(), "progress");

		HTTPPostContent httpPostCommunication = new DumpYourPhoto(
				mUploadProgressFragment, image);

		new HTTPPostClient().httpPost(DYPAPI.URL_UPLOAD_PHOTO,
				httpPostCommunication.getMultipartEntity(),
				new StringResponseHandler() {
					@Override
					public Object handleResponse(HttpResponse response)
							throws ClientProtocolException, IOException {

						mUploadProgressFragment.setDes(getResources()
								.getString(R.string.status_upload_cloud));

						final String responseString = (String) super
								.handleResponse(response);
						// second part
						CreateRequest request = new CreateRequest();
						// request.des = des.getText().toString();
						request.dumpyourphoto = GSON.fromJson(responseString,
								DumpYourPhotoResponse.class);
						request.effect = vignetteEffect.content;
						String line = tags.getText().toString();
						request.tags = extractTags(line);

						String createJsonRequest = GSON.toJson(request);
						// Log.d(TAG, "will send to backend:" +
						// createJsonRequest);

						String backendResponse = NetworkUtil.call(
								ExpAPI.URL_POST, createJsonRequest);
						// Log.d(TAG, "backend respose:" + backendResponse);

						mUploadProgressFragment.showOK(getResources()
								.getString(R.string.status_upload_done));
						return responseString;
					}
				});
		// 01-21 22:30:42.422: D/UPLOAD(3155):
		// {"id":"591418","title":"IMG_20140117_183245","file_name":"dc6e49dd9a684bb596b5b8a509caf40f.jpg","views":"0","hash":"gNCkqycyaw","url":{"small":"http:\/\/static.dyp.im\/gNCkqycyaw\/small\/dc6e49dd9a684bb596b5b8a509caf40f.jpg","medium":"http:\/\/static.dyp.im\/gNCkqycyaw\/medium\/dc6e49dd9a684bb596b5b8a509caf40f.jpg","large":"http:\/\/static.dyp.im\/gNCkqycyaw\/large\/dc6e49dd9a684bb596b5b8a509caf40f.jpg","full":"http:\/\/static.dyp.im\/gNCkqycyaw\/dc6e49dd9a684bb596b5b8a509caf40f.jpg"}}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imageView1: {
			browseForImage();
			break;
		}
		}
	}

	private void browseForImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	@SuppressLint("NewApi")
	public String getRealPathFromURI(Context context, Uri contentUri) {
		String[] proj = { MediaStore.Audio.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj,
				null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		String filePath = cursor.getString(column_index);
		if (filePath != null) {
			return filePath;
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			// Will return "image:x*"
			String wholeID = DocumentsContract.getDocumentId(contentUri);

			// Log.d(TAG, "wholeID" + wholeID);
			String[] parts = wholeID.split(":");
			String id = wholeID;
			if (parts.length > 1) {
				// Split at colon, use second item in the array
				id = parts[1];
			}
			// Log.d(TAG, "extracted id: " + id);
			String[] column = { MediaStore.Images.Media.DATA };
			// where id is equal to
			String sel = MediaStore.Images.Media._ID + "=?";

			cursor = context.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
					new String[] { id }, null);

			int columnIndex = cursor.getColumnIndex(column[0]);

			if (cursor.moveToFirst()) {
				filePath = cursor.getString(columnIndex);
			}
			cursor.close();
		}
		return filePath;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Log.d(TAG, "onActivityResult");
		if (requestCode == SELECT_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();
				// Log.d(TAG, "" + selectedImageUri);
				mSelectedImagePath = getRealPathFromURI(getActivity(),
						selectedImageUri);
				// Log.d(TAG, "real file path" + mSelectedImagePath);
				if (mSelectedImagePath != null) {
					Picasso.with(getActivity()).load(selectedImageUri).fit()
							.into(imageView);
				}
			}
		}

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.upload, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_upload:
			if (validateInput()) {
				new Thread() {
					public void run() {
						uploadPhoto(mSelectedImagePath, "image/jpg");
					}
				}.start();

			} else {
				Toast.makeText(
						getActivity(),
						getResources().getString(R.string.message_invalid_form),
						Toast.LENGTH_SHORT).show();
			}
		}
		return false;

	}

	private boolean validateInput() {
		if (TextUtils.isEmpty(mSelectedImagePath)) {
			return false;
		}
		String line = tags.getText().toString();
		String[] tags = extractTags(line);
		if (tags == null || tags.length == 0) {
			return false;
		}
		return true;
	}

	public static Fragment newInstance(VignetteEffect vignetteEffect) {
		Bundle args = new Bundle();
		args.putSerializable(BUNDLE_VIGNETTE_EFFECT, vignetteEffect);
		UploadFragment f = new UploadFragment();
		f.setArguments(args);
		return f;
	}

	public static String[] extractTags(String line) {
		if (line == null) {
			return null;
		}
		line = line.toLowerCase();
		String[] parts = line.split(",");
		String[] tagList = new String[parts.length];

		String tag;
		int i = 0;
		for (String s : parts) {
			tag = s.trim();
			if (tag.length() > 0) {
				tagList[i] = tag;
				++i;
			}
		}

		return Arrays.copyOf(tagList, i);
	}
}
