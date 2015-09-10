package com.youzi.zhidu.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.analytics.MobclickAgent;
import com.youzi.zhidu.R;
import com.youzi.zhidu.adapter.ArticleAdapter;
import com.youzi.zhidu.adapter.HomeAdapter;
import com.youzi.zhidu.bean.Article;
import com.youzi.zhidu.bean.HomeBean;
import com.youzi.zhidu.commont.APIURL;
import com.youzi.zhidu.lib.NetWorkUtil;

@SuppressWarnings("unused")
public class HomeFragment extends Fragment implements
		android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener {
	private HomeAdapter adapter;
	ListView listView;
	private CountDownTimer timer;
	ProgressBar progressbar;
	SwipeRefreshLayout swipe;
	List<HomeBean> sections;
	FinalDb db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		initView(view);
		return view;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) private void initView(View view) {
		db = FinalDb.create(getActivity(), false);
		progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
		listView = (ListView) view.findViewById(R.id.listView);
		swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
		swipe.setOnRefreshListener(this);
		// ����ˢ�µ���ʽ
		swipe.setColorSchemeResources(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		timer = new CountDownTimer(9 * 100, 100) {

			@Override
			public void onTick(long millisUntilFinished) {
				long a = millisUntilFinished / 100;
				if (a == 1) {
					getData(APIURL.SERVER_URL + "item_a.json");
					Log.e("APIURL:", APIURL.SERVER_URL + "item_a.json");
					adapter = new HomeAdapter(getActivity(), listView);
					listView.setAdapter(adapter);
				} else {
					progressbar.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFinish() {

			}
		};
		sections = db.findAllByWhere(HomeBean.class, "1=1", "id");
		if (NetWorkUtil.networkCanUse(getActivity())) {
			timer.start();
		} else {
			progressbar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.net_error_loading));
			if (sections != null || sections.size() != 0) {
				adapter = new HomeAdapter(getActivity(), listView);
				listView.setAdapter(adapter);
				adapter.resetData(sections);
			}
		}
	}

	private void getData(String jsonurl) {
		if (isAdded() == true) {

			Ion.with(getActivity(), jsonurl).asJsonObject()
					.setCallback(new FutureCallback<JsonObject>() {

						@SuppressLint("ShowToast") @Override
						public void onCompleted(Exception e, JsonObject result) {
							Log.e("------------->	", "" + result);
							if (e != null) {
								return;
							}
							String code = result.get("code").getAsString();

							if ("200".equals(code)) {

								Log.e("", "200");
								Type type = new TypeToken<List<HomeBean>>() {
								}.getType();
								Gson gson = new Gson();
								sections = gson.fromJson(result.get("msg"),
										type);
								adapter.resetData(sections);
								progressbar.setVisibility(View.GONE);
								add(sections);
							} else if ("401".equals(code)) {
							} else {
								Toast.makeText(getActivity(),
										result.get("msg").getAsString(), 1)
										.show();
							}
						}
					});
		}
	}

	// public void onResume() {
	// super.onResume();
	// MobclickAgent.onPageStart("MainScreen"); //ͳ��ҳ��
	// }
	// public void onPause() {
	// super.onPause();
	// MobclickAgent.onPageEnd("MainScreen");
	// }
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// MobclickAgent.onPageStart("MainScreen"); //ͳ��ҳ��
		MobclickAgent.onResume(getActivity()); // ͳ��ʱ��
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(getActivity());
	}

	@Override
	public void onRefresh() {

		new Handler().postDelayed(new Runnable() {
			@SuppressLint("ShowToast") public void run() {

				if (NetWorkUtil.networkCanUse(getActivity())) {
					getData(APIURL.SERVER_URL + "item_a.json");
					adapter = new HomeAdapter(getActivity(), listView);
					listView.setAdapter(adapter);
				} else {
					Toast.makeText(getActivity(), "��������ʧ��..", 1).show();
					progressbar.setVisibility(View.GONE);
					if (sections != null || sections.size() != 0) {
						adapter = new HomeAdapter(getActivity(), listView);
						listView.setAdapter(adapter);
						adapter.resetData(sections);
					}
				}

				swipe.setRefreshing(false);

			}
		}, 1500);
	}

	private void add(List<HomeBean> a) {
		if (sections != null && sections.size() > 0) {
			for (HomeBean tmpSection : a) {
				HomeBean oldSection = db
						.findById(tmpSection.id, HomeBean.class);
				if (oldSection == null) {
					db.save(tmpSection);
				} else {
					db.update(tmpSection);
				}
			}
		}

	}
}
