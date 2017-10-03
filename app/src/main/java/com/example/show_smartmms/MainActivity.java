package com.example.show_smartmms;

import com.example.show_smartmms.common.CommonFields;
import com.example.show_smartmms.db.DBManager;
import com.example.show_smartmms.db.DBopenhelper;
import com.example.show_smartmms.ui.Tab1_Session;
import com.example.show_smartmms.ui.Tab2_Floder;
import com.example.show_smartmms.ui.Tab3_Group;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity implements CommonFields {

	private TabHost tabHost;
	private View tab1_sessionView;
	private View tab2_floderView;
	private View tab3_groupView;
	private int width_basic;// 位移步长
	private int current_X_pointer;
	private RelativeLayout tab1_relativeLayout;
	private View slide_background;// 滑动背景块儿
	private float startX = 0;
	private TranslateAnimation translateAnimation;
	private boolean isComplete = true;
	private DBopenhelper dbhelper;
	private long mExitTime;
	private long firstTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标
		// 全屏
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		init();
		onclick();

		DBManager.instance().init(MainActivity.this);
	}

	// 处理标签的点击事件
	private void onclick() {

		tab1_sessionView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(tabHost.getCurrentTabTag().equals("tab1")) && isComplete) {
					translateMoveToAction(startX, width_basic * 0);
					tabHost.setCurrentTabByTag("tab1");
				}
			}
		});

		tab2_floderView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(tabHost.getCurrentTabTag().equals("tab2")) && isComplete) {
					translateMoveToAction(startX, width_basic * 1);
					tabHost.setCurrentTabByTag("tab2");
				}
			}
		});

		tab3_groupView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(tabHost.getCurrentTabTag().equals("tab3")) && isComplete) {
					translateMoveToAction(startX, width_basic * 2);
					tabHost.setCurrentTabByTag("tab3");
				}
			}
		});
	}

	private void init() {
		tabHost = getTabHost();

		TabSpec tab1_session = tabHost.newTabSpec("tab1");
		TabSpec tab2_floder = tabHost.newTabSpec("tab2");
		TabSpec tab3_group = tabHost.newTabSpec("tab3");

		final RelativeLayout tabs = (RelativeLayout) findViewById(R.id.rl_tbw);
		// ------找到并且重置滑动view的宽和高------
		// tab1
		tab1_sessionView = getView("会话", R.drawable.tab_conversation);
		tab1_relativeLayout = (RelativeLayout) tab1_sessionView
				.findViewById(R.id.rl_background);

		slide_background = findViewById(R.id.slideview);
		slide_background.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.slide_background));
		slide_background.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						slide_background.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);// 向下兼容，否则修改minsdk为16
						LayoutParams lp = (LayoutParams) slide_background
								.getLayoutParams();
						lp.width = tab1_relativeLayout.getWidth();
						lp.height = tab1_relativeLayout.getHeight();
						lp.leftMargin = tab1_relativeLayout.getLeft();
						lp.topMargin = tab1_relativeLayout.getTop();
						width_basic = tabs.getWidth() / 3;// tabs是涵盖整个tabweight的相对布局，所以用它的宽除以3就能得到滑动动画的基准步长
					}
				});

		// ------找到并且重置滑动view的宽和高------
		tab1_session.setIndicator(tab1_sessionView);// 将tab1设置到tabhost里边

		// tab2
		tab2_floderView = getView("文件", R.drawable.tab_folder);// 生成一个view
		tab2_floder.setIndicator(tab2_floderView);// 将tab2设置到tabhost里边

		// tab3
		tab3_groupView = getView("群组", R.drawable.tab_group);
		tab3_group.setIndicator(tab3_groupView);

		tab1_session.setContent(new Intent(this, Tab1_Session.class));
		tab2_floder.setContent(new Intent(this, Tab2_Floder.class));
		tab3_group.setContent(new Intent(this, Tab3_Group.class));

		tabHost.addTab(tab1_session);
		tabHost.addTab(tab2_floder);
		tabHost.addTab(tab3_group);
	}

	private View getView(String tabName, int ImgResId) {
		View view = View.inflate(MainActivity.this, R.layout.tabview_layout,
				null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_tab);
		TextView textView = (TextView) view.findViewById(R.id.tv_tab);
		textView.setText(tabName);
		imageView.setImageResource(ImgResId);
		return view;
	}


	private void translateMoveToAction(float fromXDelta, float toXDelta) {
		translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
		translateAnimation.setDuration(1000);
		translateAnimation.setFillAfter(true);
		slide_background.startAnimation(translateAnimation);
		startX = toXDelta;

		translateAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				isComplete = false;
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				isComplete = false;
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				isComplete = true;
			}
		});
	}

}
