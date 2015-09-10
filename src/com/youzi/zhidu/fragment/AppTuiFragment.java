package com.youzi.zhidu.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.youzi.zhidu.R;
import com.youzi.zhidu.adapter.TuiAdapter;

@SuppressWarnings("unused")
public class AppTuiFragment extends Fragment {
	ListView lv;
	TuiAdapter adapter;
	String[] items = {
			"�������������������ǴҴ�ææ������������Ʒζ�������⣬����������һ�����ʶ���Ƶ�ۺ�App����Щ��Ƶ���������ʹ��..",
			"һ�����Լ�����򵥵���������app��������Ҳ��Ҫ��ּ��ˣ��һ���żǵ��Ҽ���˲�������¾ͷ����ˣ�Ϊʲô��.." };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_apptui, container, false);
		initView(view);

		return view;
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		lv = (ListView) view.findViewById(R.id.lv);
		adapter = new TuiAdapter(items, getActivity());
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					Intent i = getIntent(getActivity(),"com.bugkr.beautyidea");
					boolean b = judge(getActivity(), i);
					if (b == false) {
						startActivity(i);
					}
					break;
				case 1:
					Intent it = getIntent(getActivity(),"com.shangpuyun.yunzhang");
					boolean bo = judge(getActivity(), it);
					if (bo == false) {
						startActivity(it);
					}
					break;
				default:
					break;
				}
			}
		});
	}

	public static Intent getIntent(Context paramContext,String packageName) {
		StringBuilder localStringBuilder = new StringBuilder()
				.append("market://details?id=");
		String str = /* paramContext.getPackageName(); */packageName;
		localStringBuilder.append(str);
		Uri localUri = Uri.parse(localStringBuilder.toString());
		return new Intent("android.intent.action.VIEW", localUri);
	}

	// ֱ����ת���ж��Ƿ�����г�Ӧ��
	public static void start(Context paramContext, String paramString) {
		Uri localUri = Uri.parse(paramString);
		Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		paramContext.startActivity(localIntent);
	}

	public static boolean judge(Context paramContext, Intent paramIntent) {
		List<ResolveInfo> localList = paramContext.getPackageManager()
				.queryIntentActivities(paramIntent,
						PackageManager.GET_INTENT_FILTERS);
		if ((localList != null) && (localList.size() > 0)) {
			return false;
		} else {
			return true;
		}
	}
}
