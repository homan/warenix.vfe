package org.dyndns.warenix.vignettefiltersexchange.ui;

import java.util.ArrayList;
import java.util.Arrays;

import org.dyndns.warenix.util.AsyncTask;
import org.dyndns.warenix.vignettefiltersexchange.R;
import org.dyndns.warenix.vignettefiltersexchange.model.EffectResponse;
import org.dyndns.warenix.vignettefiltersexchange.model.GetRequest;
import org.dyndns.warenix.vignettefiltersexchange.model.ListRequest;
import org.dyndns.warenix.vignettefiltersexchange.util.ExpAPI;
import org.dyndns.warenix.vignettefiltersexchange.util.NetworkUtil;
import org.dyndns.warenix.vignettefiltersexchange.vignette.VignetteEffectExporter;
import org.dyndns.warenix.vignettefiltersexchange.vignette.VignetteEffectImporter.VignetteEffect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

public class EffectListFragment extends Fragment implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {

	public static final String BUNDLE_MODE = "org.dyndns.warenix.vignettefiltersexchange.ui.EffectListFragment.BUNDLE_MODE";
	public static final String BUNDLE_MAX_ID = "org.dyndns.warenix.vignettefiltersexchange.ui.EffectListFragment.BUNDLE_MAX_ID";
	public static final String BUNDLE_KEYWORD = "org.dyndns.warenix.vignettefiltersexchange.ui.EffectListFragment.BUNDLE_KEYWORD";
	public static final String BUNDLE_OBJECT_ID = "org.dyndns.warenix.vignettefiltersexchange.ui.EffectListFragment.BUNDLE_OBJECT_ID";

	protected static final String TAG = "EffectListFragment";

	public static enum ModeType {
		KEYWORD_LIST, ONE_EFFECT
	}

	private ModeType mMode;

	private String mMaxID;
	private String mKeyword;
	private String mObjectID;

	private static Gson GSON = new GsonBuilder().create();

	private ArrayList<EffectResponse> mDataList = new ArrayList<EffectResponse>();

	private EffectAdapter mAdapter;

	private ListView mListView;
	private View mMore;
	private Button mMoreButton;
	private TextView mEmptyText;

	/**
	 * Whether asynctaks is running
	 */
	private boolean mIsLoading;
	private Object mLock = new Object();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mMaxID = args.getString(BUNDLE_MAX_ID);
		mKeyword = args.getString(BUNDLE_KEYWORD);
		mMode = ModeType.values()[args.getInt(BUNDLE_MODE)];
		mObjectID = args.getString(BUNDLE_OBJECT_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, null);

		mListView = (ListView) view.findViewById(android.R.id.list);
		mEmptyText = (TextView) view.findViewById(android.R.id.empty);
		mEmptyText.setText(getResources().getString(R.string.list_loading));
		mListView.setEmptyView(mEmptyText);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		mMore = inflater.inflate(R.layout.more, null);
		mMoreButton = (Button) mMore.findViewById(R.id.more);
		mMoreButton.setOnClickListener(this);
		if (mMode == ModeType.KEYWORD_LIST) {
			mListView.addFooterView(mMore);
		}

		mAdapter = new EffectAdapter(getActivity());
		mListView.setAdapter(mAdapter);

		return view;
	}

	public static Fragment newInstance(String keyword, String maxID,
			ModeType mode, String objectID) {
		Bundle args = new Bundle();
		args.putString(BUNDLE_MAX_ID, maxID);
		args.putString(BUNDLE_KEYWORD, keyword);
		args.putInt(BUNDLE_MODE, mode.ordinal());
		args.putString(BUNDLE_OBJECT_ID, objectID);
		EffectListFragment f = new EffectListFragment();
		f.setArguments(args);
		return f;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		loadMore();
	}

	private void loadMore() {
		synchronized (mLock) {
			if (!mIsLoading) {
				mIsLoading = true;
				new AsyncTask<Void, Void, EffectResponse[]>() {

					public void onPreExecute() {
						mMoreButton.setText(getResources().getString(
								R.string.list_loading));
					}

					@Override
					protected EffectResponse[] doInBackground(Void... params) {
						if (mMode == ModeType.KEYWORD_LIST) {
							return doKeywordSearch();
						} else if (mMode == ModeType.ONE_EFFECT) {
							return doOneEffectSearch();
						}
						return null;
					}

					public void onPostExecute(EffectResponse[] v) {
						if (v == null || v.length == 0) {
							// make visual changes to alert user there's no more
							// result
							mListView.removeFooterView(mMore);
							mEmptyText.setText(getResources().getString(
									R.string.list_empty));
							if (getActivity() != null) {
								Toast.makeText(
										getActivity(),
										getResources().getString(
												R.string.toast_reached_the_end),
										Toast.LENGTH_SHORT).show();
							}
						}
						mMoreButton.setText(getResources().getString(
								R.string.list_button_more));
						mAdapter.notifyDataSetChanged();
						synchronized (mLock) {
							mIsLoading = false;
						}

					}
				}.execute();
			}
		}
	}

	public class EffectAdapter extends BaseAdapter {
		private Context mContext;

		public EffectAdapter(Context context) {
			mContext = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getEffectView(position, convertView, parent);
		}

		private View getEffectView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.effect_item, null);
			}

			EffectResponse effectResponse = mDataList.get(position);

			TextView des = (TextView) view.findViewById(R.id.des);
			des.setText(TextUtils.join(", ", effectResponse.tags));

			ImageView preview = (ImageView) view.findViewById(R.id.imageView1);

			Picasso.with(mContext).load(effectResponse.dumpyourphoto.url.large)
					.into(preview);
			return view;
		}

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.more:
			loadMore();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		EffectResponse effectResposne = mDataList.get(position);

		VignetteEffect vignetteEffect = new VignetteEffect();
		vignetteEffect.displayName = effectResposne.des;
		vignetteEffect.content = effectResposne.effect;

		VignetteEffectExporter.createExternalStoragePrivateFile(getActivity(),
				vignetteEffect);
		VignetteEffectExporter.openFile(getActivity(), vignetteEffect);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.upload, menu);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		EffectResponse effectResposne = mDataList.get(position);
		String s = VFEIntent
				.createShareFilterEffectUri(effectResposne._id.$oid);
		// Log.d(TAG, s);
		shareEffectLink(s);
		return true;
	}

	private void shareEffectLink(String s) {
		Intent intent = new Intent(Intent.ACTION_SEND);

		intent.setType("text/plain");

		intent.putExtra(Intent.EXTRA_TEXT, s);

		intent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Share Vignette Filter");

		startActivity(Intent.createChooser(intent, "Share"));
	}

	private EffectResponse[] doOneEffectSearch() {
		GetRequest request = new GetRequest();
		// request.tags = new String[] { mKeyword };
		request.object_id = mObjectID;

		String json = GSON.toJson(request);
		// Log.d(TAG, "send:" + json);
		String response = NetworkUtil.call(ExpAPI.URL_GET, json);
		// Log.d(TAG, "response:" + response);
		EffectResponse result = GSON.fromJson(response, EffectResponse.class);
		if (result == null) {

		} else {

			mDataList.addAll(Arrays.asList(result));
			// update sinceID
			mMaxID = result._id.$oid;
		}
		return new EffectResponse[] { result };
	}

	private EffectResponse[] doKeywordSearch() {
		ListRequest request = new ListRequest();
		// request.tags = new String[] { mKeyword };
		request.tags = UploadFragment.extractTags(mKeyword);
		request.max_id = mMaxID;

		String json = GSON.toJson(request);
		// Log.d(TAG, "send:" + json);
		String response = NetworkUtil.call(ExpAPI.URL_LIST, json);
		// Log.d(TAG, response);
		EffectResponse[] result = GSON.fromJson(response,
				EffectResponse[].class);
		if (result == null || result.length == 0) {

		} else {
			// Log.d(TAG, result[0].effect);

			mDataList.addAll(Arrays.asList(result));
			// update sinceID
			mMaxID = result[result.length - 1]._id.$oid;
		}
		return result;
	}
}
