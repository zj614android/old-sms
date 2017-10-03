package com.example.show_smartmms.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.GroupBean;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.common.CommonFields;
import com.example.show_smartmms.ui.Tab1_Session.ConversationQueryHandler;
import com.example.show_smartmms.ui.Tab1_Session.ViewHolder;
import com.example.show_smartmms.utils.BitmapUtils;
import com.example.show_smartmms.utils.SmsUtils;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Sub_Tab3_GroupDetail extends Activity implements CommonFields {

	protected static final String[] GROUP_DETAIL_PROJECTION = { "group_id" };
	private Intent intent;
	private String groupName;
	private mGroup_detail_adapter adapter;
	private ListView listView;
	private SmsBean smsBean;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			List<SmsBean> datas;
			switch (msg.what) {
			case 0:
				datas = (List<SmsBean>) msg.obj;
				adapter.setdata(datas);
				listView.setAdapter(adapter);
				break;
			case 1:
				datas = (List<SmsBean>) msg.obj;
				adapter.setdata(datas);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_tab3_group_detail);

		listView = (ListView) findViewById(R.id.lv_group_detail_item);
		adapter = new mGroup_detail_adapter();

		intent = getIntent();
		groupName = intent.getStringExtra("groupName");
		System.out.println("�� groupName      " + groupName);

		// ����Ⱥ������ѯthread_ids ����Ҫ�첽��
		Group_Detail_AsyncQueryHandler asyncQueryHandler = new Group_Detail_AsyncQueryHandler(
				getContentResolver());

		asyncQueryHandler.setDowhat(0);
		asyncQueryHandler.startQuery(0, null,
				Uri.parse("content://db_groups/"), GROUP_DETAIL_PROJECTION,
				"group_name = ?", new String[] { groupName }, null);

	}

	/**
	 * �첽��ѯ���ݿ�
	 * */
	private class Group_Detail_AsyncQueryHandler extends AsyncQueryHandler {

		ContentResolver cr;
		int what;

		public Group_Detail_AsyncQueryHandler(ContentResolver cr) {
			super(cr);
			this.cr = cr;
		}

		public void setDowhat(int what) {
			this.what = what;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null) {
				System.out.println("��ѯ����groupid");

				int count = cursor.getCount();
				int[] gpidarr = new int[count];
				count = 0;
				//ֻ������һ��
				while (cursor.moveToNext()) {
					int gpid = cursor.getInt(0);
					gpidarr[count] = gpid;
					count++;
				}

				int gpid = 0;
				if (gpidarr.length > 1) {
					for (int i = 0; i < gpidarr.length; i++) {

					}
				} else {
					gpid = gpidarr[0];
					Uri uri = Uri.parse("content://db_groups/group_thread");
					
					Cursor thread_ids_cursor = getContentResolver().query(uri,
							new String[] { "thread_id" }, "group_id = ?",
							new String[] { gpid + "" }, null);
					
					if (thread_ids_cursor.getCount() > 0) {
						List<SmsBean> fillGroupData = fillGroupData(
								thread_ids_cursor, this.cr);

						Message obtain = Message.obtain();
						obtain.obj = fillGroupData;
						obtain.what = what;
						handler.sendMessage(obtain);
					}
				}

			}
			super.onQueryComplete(token, cookie, cursor);
		}

	}

	/**
	 * �첽��ѯ���ݿ�(2)
	 * */
	public List<SmsBean> fillGroupData(Cursor cursor, ContentResolver cr) {
		List<SmsBean> list = new ArrayList<SmsBean>();
		if (cursor != null && cursor.getCount() > 0) {
			int columnCount;
			String thread_id;
			int index = 0;
			while (cursor.moveToNext()) {
				columnCount = cursor.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					thread_id = cursor.getString(i);
					System.out.println("��ϸ__" + thread_id);
					Uri one_people_conversation_uri = Uri
							.parse("content://sms/conversations/");
					Cursor one_people_cursor = cr.query(
							one_people_conversation_uri, SMS_PROJECTION,
							"thread_id = ?", new String[] { thread_id + "" },
							null);

					if (one_people_cursor != null
							&& one_people_cursor.getCount() > 0) {
						// System.out.println("one_people_cursor ��  cursor.getCount()=="
						// +one_people_cursor.getCount());
						// SmsUtils.printCursor(one_people_cursor);
						List<SmsBean> tempList = SmsUtils.instance()
								.fillDetailData(one_people_cursor);
						smsBean = tempList.get(0);
					} else {
						System.out.println("one_people_cursor == null");
					}

				}
				list.add(smsBean);
				index++;
			}
			// sendmsg msg.obj<=list
		}
		return list;
	}

	private class mGroup_detail_adapter extends BaseAdapter {
		List<SmsBean> datas;
		private ViewHolder holder;

		@Override
		public int getCount() {
			if (datas != null && datas.size() > 0) {
				return datas.size();
			} else {
				return 10;
			}
		}

		public void setdata(List<SmsBean> datas) {
			this.datas = datas;
		}

		@Override
		public Object getItem(int position) {
			if (datas != null && datas.size() > 0) {
				return datas.get(position);
			} else {
				return 0;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(Sub_Tab3_GroupDetail.this,
						R.layout.tab1_lv_item, null);
				holder.avtImg = (ImageView) convertView
						.findViewById(R.id.imageView1);// ͷ��
				holder.nameTxt = (TextView) convertView
						.findViewById(R.id.textView1);// ����
				holder.bodyTxt = (TextView) convertView
						.findViewById(R.id.textView2);// body
				holder.date = (TextView) convertView
						.findViewById(R.id.textView3);
				holder.cbox = (CheckBox) convertView.findViewById(R.id.cb);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.cbox.setVisibility(View.GONE);

			System.out.println("���--this.datas.size()---" + this.datas.size());
			System.out.println("���--position----" + position);
			for (SmsBean bean : this.datas) {
				System.out.println("���--bean--" + bean);
			}

			SmsBean currBean = this.datas.get(position);

			String address = currBean.getAddress();
			String contactName = SmsUtils.instance().getContactName(
					getContentResolver(), address);
			if (TextUtils.isEmpty(contactName)) {
				contactName = address + "";
			}

			if (TextUtils.isEmpty(contactName)) {
				holder.nameTxt.setText("null");
			} else {
				holder.nameTxt.setText(contactName);
			}

			String body = currBean.getBody();
			if (TextUtils.isEmpty(body)) {
				holder.bodyTxt.setText("null");
			} else {
				holder.bodyTxt.setText(currBean.getBody());
			}

			String date = currBean.getDate();
			if (TextUtils.isEmpty(date)) {
				holder.date.setText("null");
			} else {
				holder.date.setText(currBean.getDate());
			}

			Bitmap bitMapAvantar = SmsUtils.instance().getContactAvantar(
					getContentResolver(), address);

			// ����ͷ��
			if (bitMapAvantar != null) {
				holder.avtImg.setImageBitmap(bitMapAvantar);
			} else {
				holder.avtImg
						.setImageResource(R.drawable.ic_unknow_contact_picture);
			}

			return convertView;
		}

		class ViewHolder {
			ImageView avtImg;
			TextView nameTxt;
			TextView bodyTxt;
			TextView date;
			CheckBox cbox;
		}

	}

}
