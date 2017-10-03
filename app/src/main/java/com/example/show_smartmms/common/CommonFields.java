package com.example.show_smartmms.common;

import android.net.Uri;

public interface CommonFields {
	public final int LV_ITEM_WIDTH = 52;
	public final int LV_ITEM_HEIGHT = 47;
	
	public final String CONTENT_URI_SMS_CONVERSATIONS=  "content://sms/conversations";
	public final String [] SMS_PROJECTION = {
			"sms.thread_id AS _id",
			"sms.body AS body",
			"groups.msg_count AS count",
			"sms.address AS address",
			"sms.date AS date",
	};
	
	//---handler标识
	public final int SET_ADAPTER = 0;
	public final int NOTIFY_DATA_CHANGED = 1;
	public final int UI_REFRESH = 2;
	
	//详情页面查询参数
	public final String CONTENT_URI_SMS=  "content://sms";
	public final String [] CONTENT_URI_SMS_DETAIL_PROJECTION = {
			"sms.body AS body",
			"sms.date AS date",
			"sms.type AS type",
			"sms.address",
	};
	
	public static final int RECEIVE_TYPE = 1;	// 接受类型
	public static final int SEND_TYPE = 2;		// 发送类型
	public static final Uri SMS_URI = Uri.parse("content://sms/");
	
	
	//文件及其群组
	// 收件箱的uri
	public static final Uri INBOX_URI = Uri.parse("content://sms/inbox");
	// 发件箱的uri
	public static final Uri OUTBOX_URI = Uri.parse("content://sms/outbox");
	// 已发送的uri
	public static final Uri SENT_URI = Uri.parse("content://sms/sent");
	// 草稿箱的uri
	public static final Uri DRAFT_URI = Uri.parse("content://sms/draft");
	// 添加群组uri
	public static final Uri GROUPS_INSERT_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/insert");
	// 查询所有的群组uri
	public static final Uri GROUPS_QUERY_ALL_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/");
	// 查询thread_group关联表所有的内容uri
	public static final Uri THREAD_GROUP_QUERY_ALL_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/thread_group");
	// 插入thread_group关联表的uri
	public static final Uri THREAD_GROUP_INSERT_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/thread_group/insert");
	// 删除群组的uri
	public static final Uri GROUPS_DELETE_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/delete");
	// 修改群组的uri
	public static final Uri GROUPS_UPDATE_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/update");
	
	
	public static final int INBOX_POSITION = 0;
	public static final int OUTBOX_POSITION = 1;
	public static final int SENT_POSITION = 2;
	public static final int DRAFT_POSITION = 3;
	
	public static final int VIEWTYPE_DATE = 100;
	public static final int VIEWTYPE_COMMON = 200;
	
	public static final String DB_NAME = "smartsms.db";
	public static final int DB_VERSION = 1;
	
	//群组URI Uri.parse("content://db_groups/")
	
	
}
