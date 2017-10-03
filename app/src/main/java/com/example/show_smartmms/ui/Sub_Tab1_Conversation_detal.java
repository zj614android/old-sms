package com.example.show_smartmms.ui;

import java.util.Date;
import java.util.List;

import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.common.CommonFields;
import com.example.show_smartmms.ui.Tab1_Session.ViewHolder;
import com.example.show_smartmms.utils.BitmapUtils;
import com.example.show_smartmms.utils.SmsUtils;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
/**
 * 
 * */
public class Sub_Tab1_Conversation_detal extends Activity implements
		CommonFields {

	private String currAddr;
	private String textContet;
	private String name;
	private String thread_id;
	private ListView listView;
	private EditText editText;

	private Handler handler = new Handler() {
		private Myadapter_detal myadapter;

		public void handleMessage(Message msg) {
			if (myadapter == null)
				myadapter = new Myadapter_detal();

			switch (msg.what) {
			case SET_ADAPTER:
				myadapter.setData((List<SmsBean>) msg.obj);
				listView.setAdapter(myadapter);
				break;
			case UI_REFRESH:
				myadapter.setData((List<SmsBean>) msg.obj);
				myadapter.notifyDataSetChanged();
				listView.setSelection(myadapter.getCount());
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sub_tab1_conversationdetal_layout);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// getintent
		getBaseDataFromIntent();

		// ����UI����
		handleUI();

		onclick();

		observer();
	}

	private void observer() {
		Uri uri = Uri.parse(CONTENT_URI_SMS);
		getContentResolver().registerContentObserver(uri, true,
				new detailObserver(new Handler()));
	}

	/**
	 * ���ݹ۲���ˢ��UI
	 * */
	private class detailObserver extends ContentObserver {

		public detailObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			Uri uri = Uri.parse(CONTENT_URI_SMS);
			Cursor cursor = getContentResolver().query(uri,
					CONTENT_URI_SMS_DETAIL_PROJECTION, "thread_id = ?",
					new String[] { thread_id }, "date");
			List<SmsBean> fillDetailData = SmsUtils.instance().fillDetailData(
					cursor);
			Message obtainMessage = handler.obtainMessage();
			obtainMessage.what = UI_REFRESH;
			obtainMessage.obj = fillDetailData;
			handler.sendMessage(obtainMessage);
			super.onChange(selfChange);
		}
	}

	private void onclick() {

		/**
		 * ���ŷ���
		 * **/
		Button sendmsgbutton = (Button) findViewById(R.id.sendmsg);
		sendmsgbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!TextUtils.isEmpty(currAddr)) {
					textContet = editText.getText().toString();
					if (!TextUtils.isEmpty(textContet)) {
						SmsUtils.instance().sendMessage(
								Sub_Tab1_Conversation_detal.this, currAddr,
								textContet);

						MyAsyncQueryManager myAsyncQueryManager = new MyAsyncQueryManager(
								getContentResolver(), UI_REFRESH);

						myAsyncQueryManager.startQuery(0, null,
								Uri.parse(CONTENT_URI_SMS),
								CONTENT_URI_SMS_DETAIL_PROJECTION,
								"thread_id = ?", new String[] { thread_id },
								"date");
						editText.setText("");
					}
				}
			}
		});

		/**
		 * ����
		 * */
		Button backBt = (Button) findViewById(R.id.back);
		backBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void handleUI() {
		// ���м������textview����
		TextView nameTextview = (TextView) findViewById(R.id.name);
		nameTextview.setText(name);

		// listview���ݵ�����
		prepareListData();// Ϊlistview׼�����ݡ���threadIdȥ���ݿ���߲�ѯcursor��
		listView = (ListView) findViewById(R.id.lv_conversation_detal);
		editText = (EditText) findViewById(R.id.edittext);
	}

	private void prepareListData() {
		if (thread_id != null && !thread_id.equals("")) {
			MyAsyncQueryManager myAsyncQueryManager = new MyAsyncQueryManager(
					getContentResolver(), SET_ADAPTER);
			myAsyncQueryManager.startQuery(0, null, Uri.parse(CONTENT_URI_SMS),
					CONTENT_URI_SMS_DETAIL_PROJECTION, "thread_id = ?",
					new String[] { thread_id }, "date");
		}
	}

	private class MyAsyncQueryManager extends AsyncQueryHandler {

		int dowhat;

		public MyAsyncQueryManager(ContentResolver cr, int dowhat) {
			super(cr);
			this.dowhat = dowhat;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

			List<SmsBean> fillDetailData = SmsUtils.instance().fillDetailData(cursor);

			Message obtainMessage = handler.obtainMessage();
			obtainMessage.what = dowhat;
			obtainMessage.obj = fillDetailData;

			handler.sendMessage(obtainMessage);
			super.onQueryComplete(token, cookie, cursor);
		}

	}

	private void getBaseDataFromIntent() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		name = extras.getString("name");
		thread_id = extras.getString("thread_id");
		currAddr = extras.getString("address");
	}

	private class Myadapter_detal extends BaseAdapter {

		List<SmsBean> beans;
		private ViewHolder holder;
		private SmsBean currBean;

		@Override
		public int getCount() {
			return beans.size();
		}

		public void clearData() {
			beans.clear();
		}

		public void setData(List<SmsBean> list) {
			this.beans = list;
		}

		@Override
		public Object getItem(int position) {
			return beans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			String currType = null;

			if (beans != null && beans.size() > 0) {
				currBean = beans.get(position);
				currType = currBean.getType();
			}

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(Sub_Tab1_Conversation_detal.this,
						R.layout.conversation_detail_listview_item, null);
				holder.tb_send = convertView.findViewById(R.id.send);
				holder.tb_rec = convertView.findViewById(R.id.rec);

				holder.recdate = (TextView) convertView
						.findViewById(R.id.recdate);
				holder.recbody = (TextView) convertView
						.findViewById(R.id.recbody);

				holder.senddate = (TextView) convertView
						.findViewById(R.id.senddate);
				holder.sendbody = (TextView) convertView
						.findViewById(R.id.sendbody);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 0����
			// 1�ռ���
			// 2�l��
			// 3�ݸ�
			// 4�l����
			// 5ʧ��
			// 6�ȴ��l��
			switch (Integer.parseInt(currType)) {
			case 1:
				holder.tb_send.setVisibility(View.GONE);
				holder.tb_rec.setVisibility(View.VISIBLE);

				if (!currBean.getDate().equals("")
						&& currBean.getDate() != null) {
					holder.recdate.setText(currBean.getDate());
				} else {
					holder.recdate.setText("null");
				}

				if (!currBean.getBody().equals("")
						&& currBean.getBody() != null) {
					holder.recbody.setText(currBean.getBody());
				} else {
					holder.recbody.setText("null");
				}

				break;
			case 2:
				holder.tb_rec.setVisibility(View.GONE);
				holder.tb_send.setVisibility(View.VISIBLE);

				if (!currBean.getDate().equals("")
						&& currBean.getDate() != null) {
					holder.senddate.setText(currBean.getDate());
				} else {
					holder.senddate.setText("null");
				}

				if (!currBean.getBody().equals("")
						&& currBean.getBody() != null) {
					holder.sendbody.setText(currBean.getBody());
				} else {
					holder.sendbody.setText("null");
				}
			default:
				break;
			}

			return convertView;
		}

	}

	private class ViewHolder {
		View tb_send;
		View tb_rec;

		TextView senddate;
		TextView sendbody;

		TextView recdate;
		TextView recbody;
	}
}
