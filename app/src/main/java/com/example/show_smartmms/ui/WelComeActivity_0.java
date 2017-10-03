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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��
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
	 * Ϊ��ǰӦ����������ݷ�ʽ
	 * 
	 * @param cx
	 * @param appName
	 *            ��ݷ�ʽ����
	 */
	public static void addShortcut(Context cx) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		Intent shortcutIntent = cx.getPackageManager()
				.getLaunchIntentForPackage(cx.getPackageName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// ��ȡ��ǰӦ������
		String title = null;
		try {
			final PackageManager pm = cx.getPackageManager();
			title = pm.getApplicationLabel(
					pm.getApplicationInfo(cx.getPackageName(),
							PackageManager.GET_META_DATA)).toString();
		} catch (Exception e) {
		}
		// ��ݷ�ʽ����
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
		// �������ظ���������һ����Ч��
		shortcut.putExtra("duplicate", false);
		// ��ݷ�ʽ��ͼ��
		Parcelable iconResource = Intent.ShortcutIconResource.fromContext(cx,
				R.drawable.appicon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
		cx.sendBroadcast(shortcut);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 800) {// ������ΰ���ʱ��������800���룬���˳�
				Toast.makeText(WelComeActivity_0.this, "�ٰ�һ���˳�����...",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// ����firstTime
				return true;
			} else {
				System.exit(0);// �����˳�����
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
