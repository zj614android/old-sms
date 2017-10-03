package com.example.show_smartmms.ui;

import java.util.ArrayList;
import java.util.List;

import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.GroupBean;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.common.CommonFields;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Tab3_Group extends Activity implements CommonFields {

	private Button delgpButton;
	private TranslateAnimation translateAnimation;
	private ListView listView;
	private Button addgroup;
	private ContentResolver contentResolver;
	private GroupQueryHandler groupQueryHandler;
	public final String[] PROJECTION = { "group_name", "group_id" };
	private mAdapter adapter;
	private PopupWindow popupWindow;
	private AlertDialog.Builder builder;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			ArrayList<GroupBean> groupBeans = (ArrayList<GroupBean>) msg.obj;
			switch (msg.what) {
			case 1:// setadapter
				if (adapter == null) {
					adapter = new mAdapter();
				}
				adapter.setdata(groupBeans);
				listView.setAdapter(adapter);
				break;
			case 2:// refresh
				if (adapter == null) {
					adapter = new mAdapter();
				}
				adapter.setdata(groupBeans);
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};
	private AlertDialog changeDialog;
	private AlertDialog namedialog;
	private long firstTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3_layout);
		intiBase();
		registObserver();
		prePareData();
		onclick();
		dismisspopup();
	}

	@Override
	protected void onPause() {
		dismisspopup();
		super.onPause();
	}

	public void dismisspopup() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	/**
	 * Ⱥ�����Ի��� ��������������
	 * */
	private void alertGroupNameDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(Tab3_Group.this);
		builder.setTitle("������Ⱥ����");
//		builder.setIcon(R.drawable.appicon);
		View view = LayoutInflater.from(Tab3_Group.this).inflate(
				R.layout.add_group_dialog, null);
		final EditText username = (EditText) view
				.findViewById(R.id.addgroupname);
		username.setFilters(new InputFilter[] { new NameLengthFilter() });// ����editext����
		builder.setView(view);

		builder.setPositiveButton("ȷ��", new OnClickListener() {
			private ContentResolver resolver;

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String group_name = username.getText().toString();

				if (!TextUtils.isEmpty(group_name)) {
					resolver = getContentResolver();
					Uri uri = Uri.parse("content://db_groups/insertone");
					ContentValues values = new ContentValues();

					values.put("group_name", group_name);
					resolver.insert(uri, values);
				} else {
					Toast.makeText(Tab3_Group.this, "������Ⱥ����", 0).show();
					// if(changeDialog.isShowing()){
					// changeDialog.dismiss();
					// }
					// alertGroupNameDialog();
				}
			}
		});

		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		namedialog = builder.show();
	}

	/**
	 * ����Ⱥ�����Ի��� ��������������
	 * */
	private void alertGroupNameChangeDialog(final String old_groupName) {

		AlertDialog.Builder changeDialogBuilder = new AlertDialog.Builder(
				Tab3_Group.this);
		changeDialogBuilder.setTitle("������Ⱥ����");
		changeDialogBuilder.setIcon(R.drawable.appicon);
		View view = LayoutInflater.from(Tab3_Group.this).inflate(
				R.layout.add_group_dialog, null);
		final EditText username = (EditText) view
				.findViewById(R.id.addgroupname);
		username.setFilters(new InputFilter[] { new NameLengthFilter() });// ����editext����
		changeDialogBuilder.setView(view);

		changeDialogBuilder.setPositiveButton("ȷ��",
				new OnClickListener() {
					private ContentResolver resolver;

					@Override
					public void onClick(DialogInterface dialog, int which) {

						String tmp_gp_name = username.getText().toString();

						if (!TextUtils.isEmpty(tmp_gp_name)) {
							resolver = getContentResolver();
							String[] new_gp_name = new String[1];
							new_gp_name[0] = tmp_gp_name;
							getContentResolver()
									.update(Uri
											.parse("content://db_groups/update_group"),
											null, old_groupName, new_gp_name);
						} else {
							Toast.makeText(Tab3_Group.this, "������Ⱥ����", 0).show();
							// if(changeDialog.isShowing()){
							// changeDialog.dismiss();
							// }
							// alertGroupNameChangeDialog(old_groupName);
						}
					}
				});

		changeDialogBuilder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		changeDialog = changeDialogBuilder.show();
	}

	/**
	 * ����¼�
	 * */
	private void onclick() {

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				dismisspopup();

				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_window_content, null);
				Button delGroup = (Button) contentView
						.findViewById(R.id.addgroup);
				Button changeName = (Button) contentView
						.findViewById(R.id.delconver);
				Button gone = (Button) contentView.findViewById(R.id.seedetail);
				
				delGroup.setText("ɾ��Ⱥ��");
				changeName.setText("Ⱥ�����");
				gone.setVisibility(View.GONE);

				popupWindow = new PopupWindow(contentView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				popupWindow.setFocusable(true);
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int location[] = new int[2];
				view.getLocationOnScreen(location);
				popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP,
						location[0] + 150, location[1] - 55);

				// ����popup
				Animation set = AnimationUtils.loadAnimation(Tab3_Group.this,
						R.anim.set);
				contentView.startAnimation(set);

				delGroup.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						GroupBean currBean = (GroupBean) adapter
								.getItem(position);
						String groupName = currBean.getGroupName();

						// �Ȳ� ���Ⱥ������û�лỰ ��group �� ��ȡid ����idȥ��thread_id+group������
						Cursor gpid_cur = getContentResolver().query(
								Uri.parse("content://db_groups/"),
								new String[] { "group_id" }, "group_name = ?",
								new String[] { groupName }, null);

						// ����group_name ��ѯgroup_id
						if (gpid_cur != null && gpid_cur.getCount() > 0) {
							if (gpid_cur.moveToFirst()) {
								// type type = (type) gpid_cur.nextElement();
								final int gpid = gpid_cur.getInt(0);

								Cursor gp_name_thread_table_cur = getContentResolver()
										.query(Uri
												.parse("content://db_groups/group_thread"),
												null, "group_id = ?",
												new String[] { gpid + "" },
												null);

								// ���� ����ɾ��thread_id+group + group 2�ű�
								if (gp_name_thread_table_cur != null
										&& gp_name_thread_table_cur.getCount() > 0) {

									builder = new AlertDialog.Builder(
											Tab3_Group.this);
									builder.setTitle("��ɾ��Ⱥ�飬ͬʱҲ��ɾ��Ⱥ���ڳ�Ա����");
									builder.setIcon(R.drawable.appicon);
									builder.setPositiveButton(
											"ȡ��",
											new OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// Toast.makeText(
													// Tab3_Group.this,
													// "ȡ��ɾ��!!", 1).show();
													dismisspopup();

												}
											});
									builder.setNegativeButton(
											"ȷ��",
											new OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {

													// Toast.makeText(
													// Tab3_Group.this,
													// "���� ����ɾ��thread_id+group + group 2�ű�",
													// 0).show();

													getContentResolver()
															.delete(Uri
																	.parse("content://db_groups/delete_threads"),
																	gpid + "",
																	null);
													getContentResolver()
															.delete(Uri
																	.parse("content://db_groups/delete_group"),
																	gpid + "",
																	null);

													dismisspopup();

												}
											});

									builder.show();
								} else {
									// ����
									// Toast.makeText(Tab3_Group.this,
									// "// ���޻Ự ֱ��ɾ�� group 1�ű�", 0).show();
									getContentResolver()
											.delete(Uri
													.parse("content://db_groups/delete_group"),
													gpid + "", null);
									dismisspopup();
								}
							}
						}

					}
				});

				// Ⱥ�����
				changeName.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dismisspopup();
						// �Ȳ�ѯ��ǰitem��gp-id ��ȥupdate group��
						GroupBean currBean = (GroupBean) adapter
								.getItem(position);
						String groupName = currBean.getGroupName();
						// �����Ի���
						alertGroupNameChangeDialog(groupName);
					}
				});

				return true;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dismisspopup();
				GroupBean item = (GroupBean) adapter.getItem(position);

				String groupName = item.getGroupName();
				Intent intent = new Intent(Tab3_Group.this,
						Sub_Tab3_GroupDetail.class);
				intent.putExtra("groupName", groupName);
				startActivity(intent);
			}
		});

		addgroup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismisspopup();
				alertGroupNameDialog();
			}
		});

	}

	/**
	 * ��������
	 * */
	private class NameLengthFilter implements InputFilter {

		/**
		 * �ַ����Ƶķ�ֵ
		 * */
		final int maxLen = 12;

		/**
		 * @param source
		 *            ��ǰ���������(������һ���֡��㡱��Ҳ�����Ƕ���֡������)
		 * @param start
		 *            ��ǰ�������ֵĳ��ȵ���� (��ԶΪ0)
		 * @param end
		 *            ��ǰ�������ֵĳ��ȵ��յ� (�����롰��á���Ϊ2�������롰��ð�����Ϊ3)
		 * @param dest
		 *            ���״�����Ĳ���
		 *            (���磬���״����롰��ǰ���������Ϊ�գ����ٴ����롰���¹⡱�����ֵ��Ϊ����ǰ�������������롰���ǵ���˪
		 *            ��������ֵ��Ϊ����ǰ���¹⡱)
		 * @param dstart
		 *            ����dest�ĳ���(��destΪ����ð���������Ϊ3����destΪ"��ð�����"������Ϊ5)
		 * @param dend
		 *            ����dest�ĳ���(ͬ�ϣ�ֵ��Զ��dstartһ��)
		 * 
		 * 
		 * */

		@Override
		public CharSequence filter(CharSequence src, int start, int end,
				Spanned dest, int dstart, int dend) {
			// System.out.println("��˾ͽ -- source   " + src);
			// System.out.println("��˾ͽ -- start   " + start);
			// System.out.println("��˾ͽ -- end   " + end);
			// System.out.println("��˾ͽ -- dest  " + dest);
			// System.out.println("��˾ͽ -- dstart  " + dstart);
			// System.out.println("��˾ͽ -- dend   " + dend);
			/*
			 * �ܽ�: ps���÷�����δ�õ�start end dstart dend ���ĸ����� //
			 * ����˼��:���±�index�ͳ���count�����Ƴ���//
			 * �±꣬index����Ϊ2����һ��Ϊԭ���ַ������±꣬һ��Ϊ�������ַ������±�//
			 * 1��һ������ͨ���±���ѭ�������Ա�����ԭ���ʹ��ڵ��ַ����ĳ��ȣ�����Ŀ�ľ���Ϊ�˿��Ƴ��ȣ����Գ��Ⱥ���Ҫ
			 * -����ʽ�ܼ򵥣��ж�assic�� ��// -��С��128������Ӣ�ģ���Ӣ�����Ǿͳ��ȱ���(count)+1��//
			 * -������128�����������ǳ��ȱ���(count)��+2// -ѭ��ֱ������(count)������󳤶�(maxlength)//
			 * -�����±�(destindex)������ǰ����ַ����ĳ���(dest.length)Ϊֹ//
			 */
			int count = 0;

			int destIndex = 0;
			int srcIndex = 0;

			// �Ѵ��ڵ����ַ���
			while (destIndex < dest.length()) {

				if (count > maxLen) {
					return dest.subSequence(0, destIndex - 1);
				}

				char c = dest.charAt(destIndex++);// ��ǰ��������ַ����Ĵ���
				if (c < 128) {// ����Ӣ��assic�붼����128
					count = count + 1;
				} else {
					count = count + 2;
				}
			}

			// ����������ַ���
			while (srcIndex < src.length()) {

				if (count > maxLen) {
					break;
				}

				char c = src.charAt(srcIndex++);// �ں��������ַ����Ĵ���
				if (c < 128) {// ����Ӣ��assic�붼����128
					count = count + 1;
				} else {
					count = count + 2;
				}
			}

			if (count > maxLen) {
				return src.subSequence(0, srcIndex - 1);
			} else {
				return src.subSequence(0, srcIndex);
			}

		}
	}

	private void intiBase() {
		listView = (ListView) findViewById(R.id.tab3_lv);
		adapter = new mAdapter();
		addgroup = (Button) findViewById(R.id.tab3_left_bt);
	}

	private void registObserver() {
		getContentResolver().registerContentObserver(
				Uri.parse("content://db_groups/"), false,
				new GroupObserver(new Handler()));
	}

	private void prePareData() {
		contentResolver = getContentResolver();
		groupQueryHandler = new GroupQueryHandler(contentResolver);
		groupQueryHandler.setDoWhat(1);
		groupQueryHandler.startQuery(0, null,
				Uri.parse("content://db_groups/"), PROJECTION, null, null,
				"group_id");
	}

	/** ��ѯ */
	class GroupQueryHandler extends AsyncQueryHandler {

		int doWhat;

		public GroupQueryHandler(ContentResolver cr) {
			super(cr);
		}

		public void setDoWhat(int dowhat) {
			this.doWhat = dowhat;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			List<GroupBean> list = new ArrayList<GroupBean>();
			if (cursor != null) {
				while (cursor.moveToNext()) {
					GroupBean bean = new GroupBean();
					bean.setGroupName(cursor.getString(cursor
							.getColumnIndex("group_name")));
					list.add(bean);
				}
			}

			// ������Ϣ
			Message message = Message.obtain();
			message.what = this.doWhat;
			message.obj = list;
			handler.sendMessage(message);
			super.onQueryComplete(token, cookie, cursor);
		}

	}

	/** �۲�ˢ�� */
	private class GroupObserver extends ContentObserver {
		public GroupObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {

			System.out.println("���ݿⷢ���仯��");
			Uri uri = Uri.parse("content://db_groups");
			if (contentResolver == null) {
				if (contentResolver == null) {
					contentResolver = getContentResolver();
				}
			}

			groupQueryHandler = new GroupQueryHandler(contentResolver);
			groupQueryHandler.setDoWhat(2);
			groupQueryHandler.startQuery(0, null,
					Uri.parse("content://db_groups/"), PROJECTION, null, null,
					"group_id");

			super.onChange(selfChange);
		}
	}

	/** listview������ */
	private class mAdapter extends BaseAdapter {

		private ArrayList<GroupBean> datas;
		private ViewHolder holder;

		public mAdapter() {
		}

		public void setdata(ArrayList<GroupBean> datas) {
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return this.datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			GroupBean groupBean = datas.get(position);

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(Tab3_Group.this,
						R.layout.sub_tab3_listviewitem, null);

				holder.img = (ImageView) convertView
						.findViewById(R.id.tab3_lv_item_img);
				holder.txt = (TextView) convertView
						.findViewById(R.id.tab3_lv_item_txt);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.img.setImageResource(R.drawable.tab_group);

			String groupName = groupBean.getGroupName();
			if (!TextUtils.isEmpty(groupName)) {
				holder.txt.setText(groupName);
			} else {
				holder.txt.setText("null");
			}

			return convertView;
		}

		private class ViewHolder {
			ImageView img;
			TextView txt;
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 800) {// ������ΰ���ʱ��������800���룬���˳�
				Toast.makeText(Tab3_Group.this, "�ٰ�һ���˳�����...",
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

// Uri uri = Uri.parse(CONTENT_URI_SMS);
// Cursor cursor = getContentResolver().query(uri,
// CONTENT_URI_SMS_DETAIL_PROJECTION, "thread_id = ?", new String[]{thread_id},
// "date");
// List<SmsBean> fillDetailData = SmsUtils.instance().fillDetailData(cursor);
// Message obtainMessage = handler.obtainMessage();
// obtainMessage.what = UI_REFRESH;
// obtainMessage.obj = fillDetailData;
// handler.sendMessage(obtainMessage);

