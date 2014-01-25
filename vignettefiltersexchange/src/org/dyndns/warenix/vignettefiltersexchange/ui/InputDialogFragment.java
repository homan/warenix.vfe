package org.dyndns.warenix.vignettefiltersexchange.ui;

import org.dyndns.warenix.vignettefiltersexchange.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

public class InputDialogFragment extends DialogFragment implements
		OnClickListener {

	public static final String BUNDLE_KEYWORD = "org.dyndns.warenix.vignettefiltersexchange.ui.InputDialogFragment.BUNDLE_KEYWORD";

	private EditText mKeywordTex;

	public static InputDialogFragment newInstance(String keyword) {
		Bundle args = new Bundle();
		args.putString(BUNDLE_KEYWORD, keyword);

		InputDialogFragment f = new InputDialogFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mKeywordTex = new EditText(getActivity());
		mKeywordTex.setInputType(InputType.TYPE_CLASS_TEXT);
		mKeywordTex.setText(getArguments().getString(BUNDLE_KEYWORD));
		mKeywordTex.requestFocus();

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.app_name)
				.setMessage(
						getResources()
								.getString(R.string.message_input_keyword))
				.setPositiveButton(
						getResources().getString(android.R.string.ok), this)
				.setNegativeButton(
						getResources().getString(android.R.string.cancel), null)
				.setView(mKeywordTex).create();

	}

	@Override
	public void onClick(DialogInterface dialog, int position) {
		String value = mKeywordTex.getText().toString();
		((InputDialogFragmentListener) getActivity()).onTextResult(value);
		dialog.dismiss();
	}

	public interface InputDialogFragmentListener {
		public void onTextResult(String text);
	}
}