package com.example.show_smartmms.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.example.show_smartmms.MainActivity;
import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.common.CommonFields;
import com.example.show_smartmms.utils.SmsUtils;
import com.example.show_smartmms.utils.SmsUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 */
public class Tab1_Session extends Activity implements CommonFields {

	private ListView listView;
	private Button bt_cancel_select;
	private Button bt_crete_info;
	private Button bt_delete_info;
	private Button bt_select_all;
	private ContentResolver resolver;
	private ArrayList<SmsBean> allSmsInfos;
	private boolean EditMode = false;
	private MyAdapter myAdapter;
	private HashSet<String> checksSet;
	private ConversationQueryHandler conversationAsyncQueryHandler;
	private PopupWindow popupWindow;
	public final String[] PROJECTION = { "group_name", "group_id" };
	private AlertDialog.Builder delInfosAlertBuilder;
	private Button bt_edit_select;
	final List<String> delList = new ArrayList<String>();
	private boolean key_clock = true;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SET_ADAPTER:
				allSmsInfos = (ArrayList<SmsBean>) msg.obj;
				myAdapter.setData(allSmsInfos);
				listView.setAdapter(myAdapter);
				break;
			case NOTIFY_DATA_CHANGED:
				allSmsInfos = (ArrayList<SmsBean>) msg.obj;
				myAdapter.setData(allSmsInfos);
				myAdapter.notifyDataSetChanged();
				break;
			// case UI_REFRESH:
			// myAdapter.notifyDataSetChanged();
			// break;
			default:
				break;
			}
		};
	};
	private long firstTime;

	@Override
	protected void onPause() {
		super.onPause();
		dismisspopup();
		key_clock = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		key_clock = true;
	}

	
	/** ��2���˳�app */
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			long secondTime = System.currentTimeMillis();
//			if (secondTime - firstTime > 800) {// ������ΰ���ʱ��������800���룬���˳�
//				Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����...",
//						Toast.LENGTH_SHORT).show();
//				firstTime = secondTime;// ����firstTime
//				return true;
//			} else {
//				System.exit(0);// �����˳�����
//			}
//		}
//		return super.onKeyUp(keyCode, event);
//	}
	/**
	 * ���ط��ؼ�
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (EditMode) {
				EditMode = false;
				refreshUI();
				clearCheckSet();
				return true;
			}
			
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 800) {// ������ΰ���ʱ��������800���룬���˳�
				Toast.makeText(Tab1_Session.this, "�ٰ�һ���˳�����...",
						Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// ����firstTime
				return true;
			} else {
				System.exit(0);// �����˳�����
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ���checkbox��¼��
	 * */
	private void clearCheckSet() {
		checksSet.clear();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��
		setContentView(R.layout.tab1_layout);
		findview();
		init();
		onClick();
		obServer();
	}

	private void obServer() {
		Uri uri = Uri.parse(CONTENT_URI_SMS);
		getContentResolver().registerContentObserver(uri, true,
				new ConversationObserver(new Handler()));// ע���������ҪΪtrue��Ϊfalse�޷��۲쵽
	}

	private class ConversationObserver extends ContentObserver {

		public ConversationObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {

			if (conversationAsyncQueryHandler == null) {
				conversationAsyncQueryHandler = new ConversationQueryHandler(
						resolver);
			}

			conversationAsyncQueryHandler.doWhat(NOTIFY_DATA_CHANGED);
			conversationAsyncQueryHandler.startQuery(0, null,
					Uri.parse(CONTENT_URI_SMS_CONVERSATIONS), SMS_PROJECTION,
					null, null, "date desc");

			super.onChange(selfChange);
		}
	}

	private void onClick() {
		// listview����¼�
		listView.setOnItemClickListener(new OnItemClickListener() {
			private CheckBox cb;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dismisspopup();
				SmsBean bean = (SmsBean) parent.getAdapter().getItem(position);

				if (EditMode) {
					cb = (CheckBox) view.findViewById(R.id.cb);
					cb.setChecked(!cb.isChecked());
					String thread_id = bean.getThread_id();

					if (cb.isChecked()) {
						if (!checksSet.contains(thread_id))
							checksSet.add(thread_id);
					} else {
						if (checksSet.contains(thread_id))
							checksSet.remove(thread_id);
					}

					refreshUI();

				} else {
					// �����������ҳ�� Sub_Tab1_Conversation_detal
					Intent intent = new Intent(Tab1_Session.this,
							Sub_Tab1_Conversation_detal.class);
					if (bean != null) {
						String currAddr = bean.getAddress();
						String name = SmsUtils.instance().getContactName(
								resolver, currAddr);
						if (name == null || name.equals("")) {
							name = currAddr;
						}
						String thread_id = bean.getThread_id();
						intent.putExtra("name", name);
						intent.putExtra("thread_id", thread_id);
						intent.putExtra("address", currAddr);
					}
					startActivity(intent);
				}

			}
		});

		// ɾ����ť�ĵ���¼�
		bt_delete_info.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismisspopup();
				delInfosAlertBuilder.show();
			}
		});

		// ȫѡ
		bt_select_all.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismisspopup();
				MyAdapter adapter = (MyAdapter) listView.getAdapter();
				List<SmsBean> data = adapter.getData();
				for (SmsBean smsBean : data) {
					if (!checksSet.contains(smsBean.getThread_id()))
						checksSet.add(smsBean.getThread_id());
				}
				adapter.notifyDataSetChanged();
				refreshUI();

			}
		});

		// ȡ��ȫ����ť�ĵ���¼�
		bt_cancel_select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismisspopup();
				checksSet.clear();
				MyAdapter adapter = (MyAdapter) listView.getAdapter();
				adapter.notifyDataSetChanged();
				refreshUI();
			}
		});

		// ������Ϣ
		bt_crete_info.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (key_clock) {
					dismisspopup();
					ArrayList arrayList = new ArrayList<SmsBean>();
					Intent intent = new Intent(Tab1_Session.this,
							Sub_Tab1_CreatInfo.class);

					intent.putExtra("datalist", (Serializable) SmsUtils
							.instance().queryAllContacters(resolver));
					startActivity(intent);
				}
			}
		});

		bt_edit_select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditMode = true;
				refreshUI();
			}
		});

		// ����
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			private AlertDialog.Builder joinWhichGroupDialog;

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				View contentView = View.inflate(getApplicationContext(),
						R.layout.popup_window_content, null);
				Button addgroup_bt = (Button) contentView
						.findViewById(R.id.addgroup);
				Button delconver_bt = (Button) contentView
						.findViewById(R.id.delconver);
				Button seedetail_bt = (Button) contentView
						.findViewById(R.id.seedetail);

				popupWindow = new PopupWindow(contentView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int location[] = new int[2];
				view.getLocationOnScreen(location);
				popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP,
						location[0] + 80, location[1] - 55);

				/**
				 * fromXType : fromXValue : toXType: toXValue: fromYType:
				 * fromYValue: toYType: toYValue:
				 * */
				// TranslateAnimation translateAnimation = new
				// TranslateAnimation(
				// Animation.RELATIVE_TO_SELF, 0.0f,
				// Animation.RELATIVE_TO_SELF, 0.2f,
				// Animation.RELATIVE_TO_SELF, 0.0f,
				// Animation.RELATIVE_TO_SELF, 0.0f);
				// translateAnimation.setDuration(500);
				// contentView.startAnimation(translateAnimation);

				// ����popup
				Animation set = AnimationUtils.loadAnimation(Tab1_Session.this,
						R.anim.set);
				contentView.startAnimation(set);

				addgroup_bt.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// if��Ⱥ��
						// ��û��Ⱥ��Ĵ�������һ����ʾ����û�д����κ�Ⱥ�飬��Ҫ����Ⱥ����

						Uri uri = Uri.parse("content://db_groups");
						Cursor gpnameCursor = getContentResolver().query(uri,
								PROJECTION, null, null, null);

						if (gpnameCursor.getCount() == 0) {
							// ȥ����Ⱥ��
							willCreatGroupDialog();
						} else {
							// if��Ⱥ��
							// ��ѯ��������ǰitem�Ƿ��й���Ⱥ�飬
							SmsBean bean = (SmsBean) myAdapter
									.getItem(position);
							final String thread_id = bean.getThread_id();
							uri = Uri.parse("content://db_groups/group_thread");
							Cursor cursor = getContentResolver().query(uri,
									new String[] { "group_id" },
									"thread_id = ?",
									new String[] { thread_id }, null);

							if (cursor.getCount() == 0) {// ����ǰ����Ŀ��������ĳ��Ⱥ�飬
								// �򵯳��Ի�����ʾ����ĳ��Ⱥ��
								// ��ӵ�Ⱥ�� [һ��dialog������Ƕ��һ��listview�г��е�Ⱥ��]
								// Toast.makeText(Tab1_Session.this,
								// "����ǰ����Ŀ��������ĳ��Ⱥ�飬 �򵯳��Ի�����ʾ����ĳ��Ⱥ��!!",
								// 1).show();
								final String[] itemsArr = new String[gpnameCursor
										.getCount()];
								final int[] groupid_arr = new int[gpnameCursor
										.getCount()];

								joinWhichGroupDialog = new AlertDialog.Builder(
										Tab1_Session.this);
								joinWhichGroupDialog.setTitle("��ѡ����Ҫ�����Ⱥ�飺");
								joinWhichGroupDialog
										.setIcon(R.drawable.appicon);

								int count = 0;
								while (gpnameCursor.moveToNext()) {
									String gpname = gpnameCursor.getString(gpnameCursor
											.getColumnIndex("group_name"));
									int group_id = gpnameCursor.getInt(gpnameCursor
											.getColumnIndex("group_id"));
									itemsArr[count] = gpname;
									groupid_arr[count] = group_id;
									count++;
								}

								joinWhichGroupDialog.setItems(itemsArr,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												Toast.makeText(
														Tab1_Session.this,
														"which = " + which, 1)
														.show();
												// д�������
												Uri uri = Uri
														.parse("content://db_groups/insertone_threads");
												ContentValues values = new ContentValues();
												values.put("group_id",
														groupid_arr[which]);
												values.put("thread_id",
														thread_id);
												System.out
														.println("�� + group_id  groupid_arr[which] "
																+ groupid_arr[which]);
												System.out
														.println("�� + thread_id "
																+ thread_id);
												getContentResolver().insert(
														uri, values);
											}
										});
								joinWhichGroupDialog.show();

							} else {// �Ѿ���ӵ�ĳȺ��
									// ����ǰ����Ŀ������ĳ��Ⱥ�� ����ʾȺ����� ���Ļ��ߴӵ�ǰȺ���Ƴ�
									// [һ��dialog ������ʾ��ǰȺ����������Ϊ�ӵ�ǰȺ���Ƴ������߸���Ⱥ��]
								Toast.makeText(Tab1_Session.this,
										"����ǰ����Ŀ������ĳ��Ⱥ�� ����ʾȺ����� ���Ļ��ߴӵ�ǰȺ���Ƴ�!!",
										1).show();
							}

						}
						dismisspopup();
					}
				});

				/**
				 * ɾ����Ϣ
				 * */
				delconver_bt.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String currItemThreadId = null;
						SmsBean item = (SmsBean) myAdapter.getItem(position);
						currItemThreadId = item.getThread_id();
						Cursor cur = resolver.query(
								Uri.parse("content://db_groups/group_thread"),
								new String[] { "thread_id" }, "thread_id = ?",
								new String[] { currItemThreadId }, null);

						// public int delete(Uri uri, String threadid, String[]
						// whereArgs)
						// public void delthreads(SQLiteDatabase db, int gpid)
						if (cur != null && cur.getCount() > 0) {
							resolver.delete(
									Uri.parse("content://db_groups/delete_threads_where_thread_id"),
									currItemThreadId, null);
						}
						resolver.delete(
								Uri.parse("content://sms/conversations/"
										+ currItemThreadId), null, null);// ѭ��ɾ��ÿһ����Ϣ��uri

						dismisspopup();
					}
				});

				seedetail_bt.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						SmsBean bean = (SmsBean) myAdapter.getItem(position);
						Intent intent = new Intent(Tab1_Session.this,
								Sub_Tab1_Conversation_detal.class);
						if (bean != null) {
							String currAddr = bean.getAddress();
							String name = SmsUtils.instance().getContactName(
									resolver, currAddr);
							if (name == null || name.equals("")) {
								name = currAddr;
							}
							String thread_id = bean.getThread_id();
							intent.putExtra("name", name);
							intent.putExtra("thread_id", thread_id);
							intent.putExtra("address", currAddr);
						}
						startActivity(intent);
						dismisspopup();
					}
				});

				return true;
			}

		});

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				dismisspopup();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismisspopup();
			}
		});

	}

	private void init() {
		resolver = getContentResolver();
		myAdapter = new MyAdapter();
		getConversationData(resolver);
		checksSet = new HashSet<String>();
		initDialog();
	}

	private void initDialog() {

		delInfosAlertBuilder = new AlertDialog.Builder(this);
		delInfosAlertBuilder.setTitle("��ʾ:");
		delInfosAlertBuilder.setMessage("ȷ��Ҫɾ����Ϣ��");
		delInfosAlertBuilder.setIcon(R.drawable.appicon);
		delInfosAlertBuilder.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					private Thread delthread;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ProgressDialog progressDialog = new ProgressDialog(
								Tab1_Session.this);
						progressDialog.setTitle("ɾ����Ϣ:");
						progressDialog.setMessage("����ɾ������,���Ժ�...");
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressDialog.show();
						delInfo(progressDialog);
					}

					/**
					 * ɾ����Ϣ
					 * */
					private void delInfo(final ProgressDialog progressDialog) {

						// ���߳���ֱ�Ӳ���set���ϻ���һ���쳣�������̲߳�ͬ�����쳣,�������ﵹ��һ��list
						delList.clear();
						for (String iterable_element : checksSet) {
							delList.add(iterable_element);
						}
						progressDialog.setMax(delList.size());

						if (delthread == null) {

							delthread = new Thread(new Runnable() {
								public void run() {

									if (resolver != null && delList.size() > 0
											&& myAdapter != null) {

										for (int i = 0; i < delList.size(); i++) {
											resolver.delete(
													Uri.parse("content://sms/conversations/"
															+ delList.get(i)),
													null, null);// ѭ��ɾ��ÿһ����Ϣ��uri
											SystemClock.sleep(100);// ͣ0.2��
											progressDialog.setProgress(i + 1);
										}

										// ���·������Ϊˢ��UI��
										if (conversationAsyncQueryHandler == null) {
											conversationAsyncQueryHandler = new ConversationQueryHandler(
													resolver);
										}

										conversationAsyncQueryHandler
												.doWhat(NOTIFY_DATA_CHANGED);
										conversationAsyncQueryHandler.startQuery(
												0,
												null,
												Uri.parse(CONTENT_URI_SMS_CONVERSATIONS),
												SMS_PROJECTION, null, null,
												"date desc");

										progressDialog.dismiss();
									}

								}
							});
						}

						delthread.start();
					}
				});

		delInfosAlertBuilder.setNegativeButton("ȡ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(Tab1_Session.this, "ȡ��ɾ��!!", 1).show();
					}
				});
	}

	private void findview() {
		listView = (ListView) findViewById(R.id.lv_tab1);
		bt_cancel_select = (Button) findViewById(R.id.bt_cancel_select);
		bt_crete_info = (Button) findViewById(R.id.bt_crete_info);
		bt_delete_info = (Button) findViewById(R.id.bt_delete_info);
		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		bt_edit_select = (Button) findViewById(R.id.bt_edit_select);
	}

	// ------------------------------------------------------------------------
	/**
	 * �Ự����׼��
	 **/
	public void getConversationData(ContentResolver resolver) {
		conversationAsyncQueryHandler = new ConversationQueryHandler(resolver);
		conversationAsyncQueryHandler.doWhat(SET_ADAPTER);
		conversationAsyncQueryHandler.startQuery(0, null,
				Uri.parse(CONTENT_URI_SMS_CONVERSATIONS), SMS_PROJECTION, null,
				null, "date desc");
	}

	class ConversationQueryHandler extends AsyncQueryHandler {

		int doWhat;

		public ConversationQueryHandler(ContentResolver cr) {
			super(cr);
		}

		public void doWhat(int doWhat) {
			this.doWhat = doWhat;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			allSmsInfos = SmsUtils.instance().fillConversationData(cursor,getContentResolver());
			for (SmsBean iterable_element : allSmsInfos) {
				System.out.println("���������      " + iterable_element);
			}
			Message obtainMessage = handler.obtainMessage();
			obtainMessage.what = doWhat;
			obtainMessage.obj = allSmsInfos;
			handler.sendMessage(obtainMessage);
			super.onQueryComplete(token, cookie, cursor);
		}
	}

	// -------------------------------------------------------------------------------------------

	class MyAdapter extends BaseAdapter {

		List<SmsBean> infos;
		private ViewHolder holder;
		private Bitmap bitMapAvantar;
		private String currPositionAddress;
		private String currContactName;
		private String currSmsBody;
		private String date;
		private SmsBean currSmsBean;

		public MyAdapter() {
		}

		public void setData(List<SmsBean> infos) {
			this.infos = infos;
		}

		public List<SmsBean> getData() {
			return this.infos;
		}

		public List<SmsBean> getDataInfos() {
			return this.infos;
		}

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			System.out.println("����position = " + position);

			currSmsBean = infos.get(position);

			final String thread_id = currSmsBean.getThread_id();

			if (convertView == null) {
				holder = new ViewHolder();// ע��holderһ��Ҫд������ ����������Ŀ�ظ������
				convertView = View.inflate(Tab1_Session.this,
						R.layout.tab1_lv_item, null);
				holder.avantar = (ImageView) convertView
						.findViewById(R.id.imageView1);
				holder.title = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.body = (TextView) convertView
						.findViewById(R.id.textView2);
				holder.date = (TextView) convertView
						.findViewById(R.id.textView3);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			currPositionAddress = infos.get(position).getAddress();
			if (currPositionAddress == null || currPositionAddress.equals(""))
				currPositionAddress = "(��)";

			bitMapAvantar = currSmsBean.getBitmapAvantar();
			currContactName = currSmsBean.getName();

			if (currContactName == null || currContactName.equals(""))
				currContactName = currPositionAddress;

			// ��������
			currSmsBody = currSmsBean.getBody();

			// ��������
			date = currSmsBean.getDate();

			// ����ͷ��
			if (bitMapAvantar != null) {
				holder.avantar.setImageBitmap(bitMapAvantar);
			} else {
				holder.avantar
						.setImageResource(R.drawable.ic_unknow_contact_picture);
			}

			// ����name������
			if (currContactName != null && !currContactName.equals("")) {// name
				holder.title.setText(currContactName);
			} else {
				holder.title.setText(currPositionAddress);
			}

			// ����body������
			if (currSmsBody != null && !currSmsBody.equals("")) {// body
				holder.body.setText(currSmsBody);
			} else {
				holder.body.setText("null");
			}

			// ���� date������
			if (date != null && !date.equals("")) {// date
				date = "  " + date.substring(2, 10);
				holder.date.setText(date);
			} else {
				holder.date.setText("date-null");
			}

			// ��ʾ����checkbox
			if (EditMode) {// checkbox
				holder.checkBox.setVisibility(View.VISIBLE);

				if (checksSet.contains(thread_id)) {
					holder.checkBox.setChecked(true);
				} else {
					holder.checkBox.setChecked(false);
				}
			} else {
				holder.checkBox.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	class ViewHolder {
		ImageView avantar;
		TextView title;
		TextView body;
		TextView date;
		CheckBox checkBox;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (EditMode) {
			menu.findItem(R.id.search).setVisible(false);
			menu.findItem(R.id.edit).setVisible(false);
			menu.findItem(R.id.canceledit).setVisible(true);
		} else {
			menu.findItem(R.id.search).setVisible(true);
			menu.findItem(R.id.edit).setVisible(true);
			menu.findItem(R.id.canceledit).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.edit:
			EditMode = true;
			refreshUI();
			break;
		case R.id.search:
			EditMode = false;
			refreshUI();
			break;
		default:
			EditMode = false;
			refreshUI();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshUI() {
		if (EditMode) {
			bt_crete_info.setVisibility(View.GONE);
			bt_edit_select.setVisibility(View.GONE);
			bt_cancel_select.setVisibility(View.VISIBLE);
			bt_select_all.setVisibility(View.VISIBLE);
			bt_delete_info.setVisibility(View.VISIBLE);

			if (checksSet.size() == 0) {
				bt_cancel_select.setEnabled(false);
				bt_delete_info.setEnabled(false);
			} else {
				bt_cancel_select.setEnabled(true);
				bt_delete_info.setEnabled(true);
			}

			if (checksSet.size() == myAdapter.getCount()) {
				bt_select_all.setEnabled(false);
			} else {
				bt_select_all.setEnabled(true);
			}

		} else {
			bt_crete_info.setVisibility(View.VISIBLE);
			bt_edit_select.setVisibility(View.VISIBLE);
			bt_cancel_select.setVisibility(View.GONE);
			bt_delete_info.setVisibility(View.GONE);
			bt_select_all.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismisspopup();
		return super.onTouchEvent(event);
	}

	public void dismisspopup() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	// ѯ���Ƿ���Ҫ����
	private void willCreatGroupDialog() {
		delInfosAlertBuilder = new AlertDialog.Builder(this);
		delInfosAlertBuilder.setTitle("��ʾ:");
		delInfosAlertBuilder.setMessage("����δ�����κ�Ⱥ�飬�Ƿ���Ҫ����ȥ������");
		delInfosAlertBuilder.setIcon(R.drawable.appicon);
		delInfosAlertBuilder.setPositiveButton("ȷ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// ���ҵ���߼�����
						Toast.makeText(Tab1_Session.this, "ȷ��!!", 1).show();
						Intent intent = new Intent(Tab1_Session.this,
								Tab3_Group.class);
						startActivity(intent);
					}
				});

		delInfosAlertBuilder.setNegativeButton("ȡ��",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(Tab1_Session.this, "ȡ��!!", 1).show();
					}
				});
	}

}
