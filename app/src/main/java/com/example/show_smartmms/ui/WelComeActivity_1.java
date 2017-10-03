package com.example.show_smartmms.ui;

import java.util.ArrayList;

import com.example.show_smartmms.MainActivity;
import com.example.show_smartmms.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WelComeActivity_1 extends Activity {

	private ViewPager viewpager;
	private ArrayList<View> viewlist;
	private MyViewPagerAdapter adapter;
	private RelativeLayout rl1;
	private RelativeLayout rl2;
	private View tab1;
	private View tab2;
	private long firstTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标
		setContentView(R.layout.welcom_1);

		viewpager = (ViewPager) findViewById(R.id.viewpager);
		rl1 = (RelativeLayout) findViewById(R.id.rl_tab1);
		rl2 = (RelativeLayout) findViewById(R.id.rl_tab2);

		tab1 = View.inflate(WelComeActivity_1.this, R.layout.welcom_1_0, null);
		tab2 = View.inflate(WelComeActivity_1.this, R.layout.welcom_1_1, null);

		viewlist = new ArrayList<View>();
		viewlist.add(tab1);
		viewlist.add(tab2);

		adapter = new MyViewPagerAdapter();
		viewpager.setAdapter(adapter);

		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				Log.i("NavigationActivity", "onPageSelected____当一接触到屏幕的时候回调此方法");

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.i("NavigationActivity", "onPageScrolled____当屏幕被滑动的时候回调此方法");
				if (arg0 == 0) {
					rl1.setVisibility(View.VISIBLE);
					rl2.setVisibility(View.INVISIBLE);
				} else {
					rl2.setVisibility(View.VISIBLE);
					rl1.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				Log.i("NavigationActivity",
						"onPageScrollStateChanged____当滑动屏幕时屏幕中的页面被已经切换了的时候回调此方法");
			}
		});

		Button start_btn = (Button) tab2.findViewById(R.id.start_btn);
		start_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WelComeActivity_1.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private class MyViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return viewlist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewlist.get(position));
			return viewlist.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(viewlist.get(position));
		}

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 800) {// 如果两次按键时间间隔大于800毫秒，则不退出
				Toast.makeText(WelComeActivity_1.this, "再按一次退出程序...",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				System.exit(0);// 否则退出程序
			}
		}
		return super.onKeyUp(keyCode, event);
	}

}
