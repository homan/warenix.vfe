package org.dyndns.warenix.vignettefiltersexchange.ui;

import org.dyndns.warenix.http.CustomMultiPartEntity.ProgressListener;
import org.dyndns.warenix.vignettefiltersexchange.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UploadProgressDialogFragment extends DialogFragment implements
		ProgressListener, android.view.View.OnClickListener {

	public static final String BUNDLE_TOTAL_SIZE = "org.dyndns.warenix.vignettefiltersexchange.ui.UploadProgressDialogFragment.BUNDLE_TOTAL_SIZE";
	public static final String BUNDLE_DES = "org.dyndns.warenix.vignettefiltersexchange.ui.UploadProgressDialogFragment.BUNDLE_DES";

	private static final int PROGRESS_MAX = 110;

	private static final String TAG = "UploadProgressDialogFragment";

	private ProgressBar mProgressBar;

	private TextView mDes;
	private Button mButton;
	private long mTotalSize;

	public static UploadProgressDialogFragment newInstance(long totalSize,
			String text) {
		Bundle args = new Bundle();
		args.putLong(BUNDLE_TOTAL_SIZE, totalSize);
		args.putString(BUNDLE_DES, text);
		UploadProgressDialogFragment f = new UploadProgressDialogFragment();
		f.setArguments(args);
		return f;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (getDialog() != null) {
			getDialog().setCanceledOnTouchOutside(false);
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

		mTotalSize = getArguments().getLong(BUNDLE_TOTAL_SIZE);
		String text = getArguments().getString(BUNDLE_DES);

		View view = inflater.inflate(R.layout.upload_progress, null);
		mButton = (Button) view.findViewById(R.id.button);
		mButton.setOnClickListener(this);
		mButton.setEnabled(false);

		mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
		mProgressBar.setMax(PROGRESS_MAX);
		mDes = (TextView) view.findViewById(R.id.des);
		mDes.setText(text);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.app_name).setView(view).create();

	}

	@Override
	public void transferred(long num) {
		int percentage = (int) Math.ceil(num * 100.0 / mTotalSize);
		// Log.d(TAG, String.format("%d/%d (%d%%)", num, mTotalSize,
		// percentage));
		mProgressBar.setProgress(percentage);
	}

	public void setDes(final String text) {
		if (mDes != null) {
			mDes.post(new Runnable() {
				public void run() {
					mDes.setText(mDes.getText() + "\n" + text);
				}
			});
		}
	}

	public void showOK(String text) {
		mProgressBar.setProgress(PROGRESS_MAX);
		setDes(text);
		mButton.post(new Runnable() {
			@Override
			public void run() {
				mButton.setEnabled(true);
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button:
			dismiss();
			break;
		}
	}

}