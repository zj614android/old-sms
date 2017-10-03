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
	
	//---handler��ʶ
	public final int SET_ADAPTER = 0;
	public final int NOTIFY_DATA_CHANGED = 1;
	public final int UI_REFRESH = 2;
	
	//����ҳ���ѯ����
	public final String CONTENT_URI_SMS=  "content://sms";
	public final String [] CONTENT_URI_SMS_DETAIL_PROJECTION = {
			"sms.body AS body",
			"sms.date AS date",
			"sms.type AS type",
			"sms.address",
	};
	
	public static final int RECEIVE_TYPE = 1;	// ��������
	public static final int SEND_TYPE = 2;		// ��������
	public static final Uri SMS_URI = Uri.parse("content://sms/");
	
	
	//�ļ�����Ⱥ��
	// �ռ����uri
	public static final Uri INBOX_URI = Uri.parse("content://sms/inbox");
	// �������uri
	public static final Uri OUTBOX_URI = Uri.parse("content://sms/outbox");
	// �ѷ��͵�uri
	public static final Uri SENT_URI = Uri.parse("content://sms/sent");
	// �ݸ����uri
	public static final Uri DRAFT_URI = Uri.parse("content://sms/draft");
	// ���Ⱥ��uri
	public static final Uri GROUPS_INSERT_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/insert");
	// ��ѯ���е�Ⱥ��uri
	public static final Uri GROUPS_QUERY_ALL_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/");
	// ��ѯthread_group���������е�����uri
	public static final Uri THREAD_GROUP_QUERY_ALL_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/thread_group");
	// ����thread_group�������uri
	public static final Uri THREAD_GROUP_INSERT_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/thread_group/insert");
	// ɾ��Ⱥ���uri
	public static final Uri GROUPS_DELETE_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/delete");
	// �޸�Ⱥ���uri
	public static final Uri GROUPS_UPDATE_URI = Uri.parse("content://com.itheima22.smsmanager.provider.MyContentProvider/groups/update");
	
	
	public static final int INBOX_POSITION = 0;
	public static final int OUTBOX_POSITION = 1;
	public static final int SENT_POSITION = 2;
	public static final int DRAFT_POSITION = 3;
	
	public static final int VIEWTYPE_DATE = 100;
	public static final int VIEWTYPE_COMMON = 200;
	
	public static final String DB_NAME = "smartsms.db";
	public static final int DB_VERSION = 1;
	
	//Ⱥ��URI Uri.parse("content://db_groups/")
	
	
}
