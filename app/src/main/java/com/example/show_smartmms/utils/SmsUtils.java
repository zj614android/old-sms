package com.example.show_smartmms.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.PendingIntent;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.text.GetChars;
import android.util.Log;

import com.example.show_smartmms.MainActivity;
import com.example.show_smartmms.R;
import com.example.show_smartmms.bean.SmsBean;
import com.example.show_smartmms.common.CommonFields;

public class SmsUtils implements CommonFields {

	static SmsUtils smsUtils = null;

	private SmsUtils() {
	}

	public static SmsUtils instance() {
		if (smsUtils == null) {
			smsUtils = new SmsUtils();
		}
		return smsUtils;
	}

	/**
	 * 这个方法是根据时间先后顺序无差别的去查询手机内的所有短信(慎用)
	 * */
	public ArrayList<SmsBean> getAllSmsInfos(ContentResolver resolver) {
		ArrayList<SmsBean> arrayList = new ArrayList<SmsBean>();
		Uri uri = Uri.parse("content://sms/");
		// Uri uri = Uri.parse("content://sms/conversations");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String address = cursor.getString(cursor
						.getColumnIndex("address"));
				long date = cursor.getLong(cursor.getColumnIndex("date"));
				String body = cursor.getString(cursor.getColumnIndex("body"));
				String type = cursor.getString(cursor.getColumnIndex("type"));
				SmsBean smsBean = new SmsBean();
				smsBean.setAddress(address);
				smsBean.setBody(body);
				smsBean.setDate(date);
				smsBean.setType(type);
				arrayList.add(smsBean);
			}
		}
		cursor.close();
		return arrayList;
	}

	/**
	 * 通过电话号码找联系人姓名
	 * */
	public static String getContactName(ContentResolver resolver, String address) {
		String name = "";

		Uri path = Uri
				.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, address);// 根据电话号码找到联系人的uri
		Cursor cursor = resolver.query(path, new String[] { "display_name" },
				null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			name = cursor.getString(0);
			cursor.close();
			return name;
		}
		return null;
	}

	/**
	 * 通过电话号码找联系人头像
	 * */
	public static Bitmap getContactAvantar(ContentResolver resolver,
			String address) {

		// if (address != null && !address.equals("")) {
		// if (address.length() <= 4)
		// address = "1555521" + address; // 真实手机需要去除前缀,千万注意演示时候别搞混了
		// }

		Uri FILTER_URI = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				address);
		Cursor id_cursor = resolver.query(FILTER_URI, new String[] { "_id" },
				null, null, null);

		if (id_cursor != null && id_cursor.moveToFirst()) {
			String id = id_cursor.getString(0);
			Uri avantar_uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
			InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(resolver, avantar_uri);// 调用openContactPhotoInputStream把筛选出来的
			return BitmapUtils.instance().zoomImage(BitmapFactory.decodeStream(is), LV_ITEM_WIDTH,LV_ITEM_HEIGHT);
		}

		return null;
	}

	/**
	 * 打印游标
	 * */
	public static void printCursor(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			int columnCount;
			String columnName;
			String columnValue;
			int index = 0;
			while (cursor.moveToNext()) {
				columnCount = cursor.getColumnCount();
				for (int i = 0; i < columnCount; i++) {
					columnName = cursor.getColumnName(i);
					columnValue = cursor.getString(i);
					System.out.println("当前是第" + index + "行: " + columnName
							+ " = " + columnValue);
				}
				index++;
			}
		}
	}

	/**
	 * 发送短信
	 * 
	 * @param context
	 * @param address
	 * @param msg
	 */
	public void sendMessage(Context context, String address, String msg) {
		SmsManager smsManager = SmsManager.getDefault();
		ArrayList<String> divideMessage = smsManager.divideMessage(msg);// 分割短信,系统识别超过70个字的短信回报异常,如果超过70个字，这个返回的数组里面就会有2个
		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
		Intent intent = new Intent(
				"com.example.show_smartmms.broadcast.SendMessageBroadcast");
		for (String text : divideMessage) {
			sentIntents.add(PendingIntent.getBroadcast(context, 0, intent,
					PendingIntent.FLAG_ONE_SHOT));
		}
		// 添加到数据库
		writeMessage(context, address, msg);
		smsManager.sendMultipartTextMessage(address, null, divideMessage,
				sentIntents, null);
	}

	/**
	 * 写入到短信数据库
	 * 
	 * @param context
	 * @param address
	 * @param msg
	 */
	public static void writeMessage(Context context, String address, String msg) {
		ContentValues values = new ContentValues();
		values.put("address", address);
		values.put("body", msg);
		values.put("type", SEND_TYPE);
		context.getContentResolver().insert(SMS_URI, values);
	}

	/**
	 * 查询所有联系人
	 * */
	public ArrayList<SmsBean> queryAllContacters(ContentResolver resolver) {

		ArrayList<SmsBean> arrayList = new ArrayList<SmsBean>();

		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);

		if (cursor != null && cursor.getCount() > 0)
			while (cursor.moveToNext()) {
				SmsBean bean = new SmsBean();
				String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				Log.d("TAG", "Name is : " + name);
				bean.setName(name);
				int isHas = Integer.parseInt(cursor.getString(cursor.

				getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

				if (isHas > 0) {
					Cursor c = resolver.query(ContactsContract.

					CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + id, null, null);

					while (c.moveToNext()) {
						String number = c.getString(c
								.getColumnIndex(ContactsContract.

								CommonDataKinds.Phone.NUMBER));
						Log.d("TAG ", "Number is : " + number);
						bean.setAddress(number);
					}
					c.close();
				}
				arrayList.add(bean);
			}

		if (arrayList.size() > 0) {
			return arrayList;
		} else {
			return null;
		}
	}

	public Uri getFloderUri(int position) {

		switch (position) {
		case INBOX_POSITION:
			return INBOX_URI;
		case OUTBOX_POSITION:
			return OUTBOX_URI;
		case SENT_POSITION:
			return SENT_URI;
		case DRAFT_POSITION:
			return DRAFT_URI;
		default:
			break;
		}
		return null;
	}

	// ----数据填充----
	/**
	 * 文件夹页面的数据填充
	 * */
	public ArrayList<SmsBean> fillFloderDetail(Cursor cursor,
			ContentResolver resolver) {

		ArrayList<SmsBean> smsBeans = new ArrayList<SmsBean>();
		int version_sdk = Integer.parseInt(Build.VERSION.SDK); // 设备SDK版本

		int indexcount = 0;

		if (cursor != null) {
			while (cursor.moveToNext()) {
				SmsBean smsBean = new SmsBean();
				// date
				// type
				// body
				// address
				String type = String.valueOf(cursor.getInt(cursor
						.getColumnIndex("type")));
				String body = cursor.getString(cursor.getColumnIndex("body"));
				long date = Long.parseLong(cursor.getString(cursor
						.getColumnIndex("date")));
				String address = cursor.getString(cursor
						.getColumnIndex("address"));

				smsBean.setAddress(address);
				smsBean.setBody(body);
				smsBean.setDate(date);
				smsBean.setType(type);
				smsBean.setIndex(indexcount);
				smsBean.setViewtype(0);
				indexcount++;

				if (address != null && !address.equals("")) {
					String contactName = getContactName(resolver, address);
					if (contactName != null && !contactName.equals("")) {
						smsBean.setName(contactName);
					} else {
						smsBean.setName(address);
					}
				} else {
					smsBean.setName(address);
				}

				smsBeans.add(smsBean);
			}

		}

		if (version_sdk < 14)
			cursor.close();

		return smsBeans;
		// ArrayList<SmsBean> smsBeans = new ArrayList<SmsBean>();
		// String columnName;
		// String columnValue;
		// if (cursor != null && cursor.moveToFirst()) {
		// while (cursor.moveToNext()) {
		// int columnCount = cursor.getColumnCount();
		// SmsBean smsbean = new SmsBean();
		//
		// for (int i = 0; i < columnCount; i++) {
		// columnName = cursor.getColumnName(i);
		// columnValue = cursor.getString(i);
		//
		// if (columnName.equals("address")) {
		// smsbean.setAddress(columnValue);
		// if(columnValue!=null){
		// String contactName = getContactName(resolver, columnValue);
		// if(contactName != null && !contactName.equals("")){
		// smsbean.setName(contactName);
		// }
		// }
		// } else if (columnName.equals("date")) {
		// smsbean.setDate(Long.parseLong(columnValue));
		// } else if (columnName.equals("type")) {
		// smsbean.setType(columnValue);
		// } else if (columnName.equals("body")) {
		// smsbean.setBody(columnValue);
		// }
		//
		// smsBeans.add(smsbean);
		// }
		// }
		// }
		// return smsBeans;
	}

	/**
	 * 会话界面的数据填充 将游标结果集内的内容去除赋值 ,并且返回一个list,供给主要类进行数据填充
	 * */
	public static ArrayList<SmsBean> fillConversationData(Cursor cursor,
			ContentResolver cr) {
		ArrayList<SmsBean> list = new ArrayList<SmsBean>();

		int version_sdk = Integer.parseInt(Build.VERSION.SDK); // 设备SDK版本
		// String version_release = Build.VERSION.RELEASE; // 设备的系统版本
		// String device_model = Build.MODEL; // 设备型号

		if (cursor != null) {
			while (cursor.moveToNext()) {
				SmsBean smsBean = new SmsBean();
				// 当前是第0行: _id = 3
				// 当前是第0行: body = JMWTP
				// 当前是第0行: count = 2
				// 当前是第0行: address = 15555215560
				// 当前是第0行: date = 1428361090998
				String id = cursor.getString(0);
				String body = cursor.getString(1);
				int count = cursor.getInt(2);
				String address = cursor.getString(3);
				long date = cursor.getLong(4);

				smsBean.setThread_id(id);
				smsBean.setAddress(address);
				smsBean.setBody(body);
				smsBean.setDate(date);
				smsBean.setCount(count);
				smsBean.setBitmapAvantar(getContactAvantar(cr, address));
				smsBean.setName(getContactName(cr, address));

				list.add(smsBean);
			}

		}

		if (version_sdk < 14)
			cursor.close();

		return list;
	}

	/**
	 * 会话详情页面的数据填充
	 * */
	public List<SmsBean> fillDetailData(Cursor cursor) {
		List<SmsBean> list = new ArrayList<SmsBean>();
		int version_sdk = Integer.parseInt(Build.VERSION.SDK);

		if (cursor != null && cursor.getCount() > 0) {
			int columnCount;
			String columnName;
			String columnValue;
			while (cursor.moveToNext()) {
				columnCount = cursor.getColumnCount();
				SmsBean smsBean = new SmsBean();
				for (int i = 0; i < columnCount; i++) {
					columnName = cursor.getColumnName(i); // body || date
					columnValue = cursor.getString(i);

					if (columnName.equals("body")) {
						smsBean.setBody(columnValue);
					} else if (columnName.equals("date")) {
						smsBean.setDate(Long.parseLong(columnValue));
					} else if (columnName.equals("type")) {
						smsBean.setType(columnValue);
					} else if (columnName.equals("address")) {
						smsBean.setAddress(columnValue);
					}
				}
				list.add(smsBean);
			}
		}

		// if (cursor != null) {
		// while (cursor.moveToNext()) {
		// SmsBean smsBean = new SmsBean();
		// // String body = cursor.getString(1);
		// // long date = cursor.getLong(4);
		//
		// // smsBean.setBody(body);
		// // smsBean.setDate(date);
		// // list.add(smsBean);
		// }
		// }

		if (version_sdk < 14)
			cursor.close();

		return list;
	}

}
