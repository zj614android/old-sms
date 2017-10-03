package com.example.show_smartmms.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.show_smartmms.R;

public class WelComeActivity_0 extends Activity {

	protected boolean isComplete;
	private long firstTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标
		setContentView(R.layout.welcom_0);
		ImageView imageView = (ImageView) findViewById(R.id.bg_welcom);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
//		alphaAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);
		imageView.startAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent(WelComeActivity_0.this,
								WelComeActivity_1.class);
						startActivity(intent);
						finish();
					}
				}, 2500);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		addShortcut(getApplicationContext());
	}

	/**
	 * 为当前应用添加桌面快捷方式
	 * 
	 * @param cx
	 * @param appName
	 *            快捷方式名称
	 */
	public static void addShortcut(Context cx) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Intent shortcutIntent = cx.getPackageManager()
				.getLaunchIntentForPackage(cx.getPackageName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// 获取当前应用名称
		String title = null;
		try {
			final PackageManager pm = cx.getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(cx.getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}
		// 快捷方式名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// 不允许重复创建（不一定有效）
		shortcut.putExtra("duplicate", false);
		// 快捷方式的图标
		Parcelable iconResource = Intent.ShortcutIconResource.fromContext(cx,
				R.drawable.appicon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
		cx.sendBroadcast(shortcut);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 800) {// 如果两次按键时间间隔大于800毫秒，则不退出
				Toast.makeText(WelComeActivity_0.this, "再按一次退出程序...",
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
