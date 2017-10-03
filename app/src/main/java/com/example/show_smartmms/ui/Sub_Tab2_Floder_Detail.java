package com.example.show_smartmms.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.common.CommonFields;
import com.example.show_smartmms.utils.SmsUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Sub_Tab2_Floder_Detail extends Activity implements CommonFields {

	// 进
	// onCreate
	// onResume

	// 出
	// onPause
	// onStop
	// onDestroy
	private ArrayList<SmsBean> datasinit;
	private ListView listview;
	private ContentResolver contentResolver;
	private mAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.floader_detail);
		listview = (ListView) findViewById(R.id.listView1);
		datasinit = (ArrayList<SmsBean>) getIntent().getSerializableExtra("datas");

		int comeInType = (Integer) getIntent().getSerializableExtra("type");
//		initTitle(comeInType);

		ArrayList<SmsBean> handleData = handleData(datasinit);

		adapter = new mAdapter();

		contentResolver = getContentResolver();

		adapter.setdata(handleData);
		listview.setAdapter(adapter);
		onclick();
	}

	private void onclick() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SmsBean item = (SmsBean) adapter.getItem(position);
				Intent intent = new Intent(Sub_Tab2_Floder_Detail.this,Sub_Tab2_infosContent.class);
				intent.putExtra("data", (Serializable)item);
				startActivity(intent);
			}
		});

	}

	private void initTitle(int comeInType) {
		switch (comeInType) {
		case INBOX_POSITION:
			setTitle("收件箱");
			break;
		case OUTBOX_POSITION:
			setTitle("发件箱");
			break;
		case SENT_POSITION:
			setTitle("已发送");
			break;
		case DRAFT_POSITION:
			setTitle("草稿箱");
			break;
		default:
			break;
		}
	}

	private ArrayList<SmsBean> handleData(ArrayList<SmsBean> datas) {

		// 找出 日期 边界线 新加入数据到集合中去

		HashSet<SmsBean> set = new HashSet<SmsBean>();
		set.addAll(datas);

		ArrayList<SmsBean> tmpArrList = new ArrayList<SmsBean>();
		tmpArrList.addAll(set);
		Collections.sort(tmpArrList);// 排序

		for (SmsBean smsBean : tmpArrList) {
			System.out.println("处理数据：" + smsBean);
		}

		// 对的
		for (int i = 0; i < set.size(); i++) {
			SmsBean tmpbean = new SmsBean();
			SmsBean curbean = tmpArrList.get(i);
			tmpbean.setDate(curbean.getDate());
			tmpbean.setViewtype(1);
			datas.add(curbean.getIndex() + i, tmpbean);
		}

		set.clear();
		set = null;
		tmpArrList.clear();
		tmpArrList = null;

		return datas;
	}

	private class mAdapter extends BaseAdapter {

		ArrayList<SmsBean> adapterdatas;
		private ViewHolder_1 holder1;
		private ViewHolder_2 holder2;
		private String date_2;
		private int viewtype;
		private SmsBean currsmsBean;

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return adapterdatas.get(position).getViewtype();
		}

		@Override
		public int getCount() {
			System.out.println("this.datas.size() == "
					+ this.adapterdatas.size());
			return this.adapterdatas.size();
		}

		public void setdata(ArrayList<SmsBean> datas) {
			for (SmsBean smsBean : datas) {
				System.out.println("handleData == " + smsBean);
			}
			this.adapterdatas = datas;
		}

		@Override
		public Object getItem(int position) {
			return this.adapterdatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			System.out.println("position" + position);

			if (position < adapterdatas.size())
				currsmsBean = this.adapterdatas.get(position);

			viewtype = currsmsBean.getViewtype();

			if (convertView == null) {
				switch (viewtype) {
				case 0:// 普通
					holder1 = new ViewHolder_1();
					convertView = View.inflate(Sub_Tab2_Floder_Detail.this,
							R.layout.sub_tab2_listviewitem, null);
					holder1.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder1.content = (TextView) convertView
							.findViewById(R.id.tv_content);
					holder1.avantar = (ImageView) convertView
							.findViewById(R.id.img_avantar);
					holder1.tv_time = (TextView) convertView
							.findViewById(R.id.tv_time);
					convertView.setTag(holder1);
					break;
				case 1:// 日期
					holder2 = new ViewHolder_2();
					convertView = View.inflate(Sub_Tab2_Floder_Detail.this,
							R.layout.sub_tab2_list_item_date, null);
					holder2.date = (TextView) convertView
							.findViewById(R.id.tv_wan);
					convertView.setTag(holder2);
					break;
				}
			} else {
				switch (viewtype) {
				case 0:// 普通
					holder1 = (ViewHolder_1) convertView.getTag();
					break;
				case 1:// 日期
					holder2 = (ViewHolder_2) convertView.getTag();
					break;
				}
			}

			switch (viewtype) {
			case 0:// 普通
				String date = currsmsBean.getDate();
				String body = currsmsBean.getBody();
				String name = currsmsBean.getName();
				String address = currsmsBean.getAddress();

				holder1.tv_name.setText(name);
				holder1.content.setText(body);
				contentResolver = getContentResolver();

				Bitmap bitMapAvantar = null;
				if (address != null && !address.equals(""))
					bitMapAvantar = SmsUtils.instance().getContactAvantar(
							contentResolver, address);

				if (bitMapAvantar != null) {
					holder1.avantar.setImageBitmap(bitMapAvantar);
				} else {
					holder1.avantar
							.setImageResource(R.drawable.ic_unknow_contact_picture);
				}

				holder1.tv_time.setText(date.split(" ")[1]);

				break;
			case 1:// 日期
				date_2 = currsmsBean.getDate();
				holder2.date.setText(date_2.split(" ")[0]);
				break;
			}

			return convertView;
		}

	}

	private class ViewHolder_1 {
		ImageView avantar;
		TextView content;
		TextView tv_name;
		TextView tv_time;
	}

	private class ViewHolder_2 {
		TextView date;
	}
}
