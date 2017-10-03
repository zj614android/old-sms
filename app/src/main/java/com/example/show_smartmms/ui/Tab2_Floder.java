/**
 * 
 */
package com.example.show_smartmms.ui;

import java.io.Serializable;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.List;

import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.common.CommonFields;
import com.example.show_smartmms.utils.SmsUtils;

import android.app.ListActivity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 */
public class Tab2_Floder extends ListActivity implements CommonFields {

	private String count_result;
	private ListView listView;
	private mAdapter adapter;
	private ArrayList<String> counts;
	private mFloderCountHandler counthandler;
	private int onclickType;
	private long firstTime;
	
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				if (counts.size() == 4)
					Toast.makeText(Tab2_Floder.this, "数据填充完毕", 0).show();
				adapter.setData(counts);
				adapter.notifyDataSetChanged();
				break;
			case 1:// 文件短信详情页面
				ArrayList<SmsBean> obj = (ArrayList<SmsBean>) msg.obj;
				Intent intent = new Intent(Tab2_Floder.this,
						Sub_Tab2_Floder_Detail.class);
				intent.putExtra("datas", (Serializable) obj);
				intent.putExtra("type", onclickType);
				startActivity(intent);
				break;
			}
		};
	};
	

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 800) {// 如果两次按键时间间隔大于800毫秒，则不退出
				Toast.makeText(Tab2_Floder.this, "再按一次退出程序...",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				System.exit(0);// 否则退出程序
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = getListView();
		adapter = new mAdapter();
		counts = new ArrayList<String>();
		listView.setBackgroundResource(R.drawable.lv_bg);
		ContentResolver contentResolver = getContentResolver();

		for (int i = 0; i < 4; i++) {
			queryFloder_count(contentResolver,
					(SmsUtils.instance().getFloderUri(i)), i);
		}

		listView.setAdapter(adapter);
		onclick();
		observer();
	}

	private void observer() {

		Uri uri = null;
		getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"), true,
				new mObserver(new Handler(), 0));
	}

	/**
	 * 点击事件，文件夹详情页面入口
	 * */
	private void onclick() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			private ContentResolver resolver;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (resolver == null)
					resolver = getContentResolver();

				Uri floderUri = SmsUtils.instance().getFloderUri(position);

				switch (position) {
				case INBOX_POSITION:
					queryFloder_detail(resolver, floderUri);
					onclickType = INBOX_POSITION;
					break;
				case OUTBOX_POSITION:
					queryFloder_detail(resolver, floderUri);
					onclickType = OUTBOX_POSITION;
					break;
				case SENT_POSITION:
					queryFloder_detail(resolver, floderUri);
					onclickType = SENT_POSITION;
					break;
				case DRAFT_POSITION:
					queryFloder_detail(resolver, floderUri);
					onclickType = DRAFT_POSITION;
					break;
				default:
					break;
				}
			}
		});

	}

	private class mAdapter extends BaseAdapter {

		private ArrayList<String> arr;

		@Override
		public int getCount() {
			return 4;
		}

		public void setData(ArrayList<String> arr) {
			this.arr = arr;
		}

		@Override
		public Object getItem(int position) {
			return arr.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = View.inflate(Tab2_Floder.this, R.layout.floaderitem,
					null);
			TextView name = (TextView) view.findViewById(R.id.name);
			TextView count = (TextView) view.findViewById(R.id.count);
			ImageView lefticon = (ImageView) view.findViewById(R.id.lefticon);

			switch (position) {
			case INBOX_POSITION:
				name.setText("收件箱");
				lefticon.setImageResource(R.drawable.a_f_inbox);
				break;
			case OUTBOX_POSITION:
				name.setText("发件箱");
				lefticon.setImageResource(R.drawable.a_f_outbox);
				break;
			case SENT_POSITION:
				name.setText("已发送");
				lefticon.setImageResource(R.drawable.a_f_sent);
				break;
			case DRAFT_POSITION:
				name.setText("草稿箱");
				lefticon.setImageResource(R.drawable.a_f_draft);
				break;
			default:
				break;
			}

			if (arr != null) {
				count.setText(arr.get(position));
			}

			return view;
		}
	}

	/**
	 * 详情数据填充
	 * */
	public void queryFloder_detail(ContentResolver resolver, Uri uri) {
		mFloderDetailHandler detailHandler = new mFloderDetailHandler(resolver);
		detailHandler.startQuery(0, null, uri, /* projection */null, /* selection */
				null, /* selectionArgs */null, "date");
	}

	private class mFloderDetailHandler extends AsyncQueryHandler {

		public mFloderDetailHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			ArrayList<SmsBean> floader_detail_datas = SmsUtils.instance()
					.fillFloderDetail(cursor, getContentResolver());
			Message obtainMessage = handler.obtainMessage();
			obtainMessage.obj = floader_detail_datas;
			obtainMessage.what = 1;
			handler.sendMessage(obtainMessage);
			super.onQueryComplete(token, cookie, cursor);
		}
	}

	/**
	 * 每个条目个数填充
	 * */
	public void queryFloder_count(ContentResolver resolver, Uri uri, int token) {
		counthandler = new mFloderCountHandler(resolver);
		counthandler.startQuery(token, null, uri, new String[] { "count(*)" },
				null, null, null);
	}

	private class mFloderCountHandler extends AsyncQueryHandler {

		public mFloderCountHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

			if (cursor != null && cursor.moveToFirst()) {
				count_result = String.valueOf(cursor.getInt(0));
				counts.add(count_result);
			}

			if (token >= 3) {
				Message obtainMessage = handler.obtainMessage();
				obtainMessage.what = 0;
				handler.sendMessage(obtainMessage);
			}

			super.onQueryComplete(token, cookie, cursor);
		}
	}

	private class mObserver extends ContentObserver {

		int which = 0;

		public mObserver(Handler handler, int which) {
			super(handler);
			System.out.println("囧which :: new ");
			this.which = which;
		}

		@Override
		public void onChange(boolean selfChange) {
			System.out.println("囧which :: onChange ");
			// switch (which) {
			// case 0:
			// System.out.println("囧which ::" + 0);
			// break;
			// case 1:
			// System.out.println("囧which ::" + 1);
			// break;
			// case 2:
			// System.out.println("囧which ::" + 2);
			// break;
			// case 3:
			// System.out.println("囧which ::" + 3);
			// break;
			// default:
			// break;
			// }

			counts.clear();
			if (counthandler != null && counts != null && counts.size() == 0)
				for (int j = 0; j < 4; j++) {
					System.out.println("囧which :: onChange jjj" + j);
					Uri uri = SmsUtils.instance().getFloderUri(j);
					counthandler.startQuery(j, null, uri,
							new String[] { "count(*)" }, null, null, null);
				}

			super.onChange(selfChange);
		}
	}

}
