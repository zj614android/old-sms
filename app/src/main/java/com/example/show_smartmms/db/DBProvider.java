package com.example.show_smartmms.db;

import com.example.show_smartmms.common.CommonFields;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DBProvider extends ContentProvider implements CommonFields {

	private static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private DBopenhelper helper;
	private static final int QUERY_GROUPNAME_ALL = 1; // ��ѯ����Ⱥ��
	private static final int INSERT_ONE = 2; // ������һ��Ⱥ��
	private static final int INSERT_THREAD = 3; // ��ӻỰ��Ⱥ��
	private static final int QUERY_GROUPTHREAD_ALL = 4; // ��ѯ����Ⱥ��
	private static final int DELETE_GP = 5; // ��ӻỰ��Ⱥ��
	private static final int DELETE_THREAD_WHERE_GPID = 6; // ��ӻỰ��Ⱥ��
	private static final int DELETE_THREAD_WHERE_THREAD_ID = 8; // ��ӻỰ��Ⱥ��
	private static final int UPDATE_GP = 7;

	static {
		mUriMatcher.addURI("db_groups", null, QUERY_GROUPNAME_ALL);
		mUriMatcher.addURI("db_groups", "group_thread", QUERY_GROUPTHREAD_ALL);
		mUriMatcher.addURI("db_groups", "insertone", INSERT_ONE);
		mUriMatcher.addURI("db_groups", "insertone_threads", INSERT_THREAD);
		mUriMatcher.addURI("db_groups", "delete_threads",
				DELETE_THREAD_WHERE_GPID);
		mUriMatcher.addURI("db_groups", "delete_threads_where_thread_id",
				DELETE_THREAD_WHERE_THREAD_ID);
		mUriMatcher.addURI("db_groups", "delete_group", DELETE_GP);
		mUriMatcher.addURI("db_groups", "update_group", UPDATE_GP);
	}

	@Override
	public boolean onCreate() {
		helper = new DBopenhelper(getContext(), DB_NAME, null, 10);
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = helper.getReadableDatabase();
		if (mUriMatcher.match(uri) == QUERY_GROUPNAME_ALL) {
			Cursor cursor = db.query("groupname_table", projection, selection,
					selectionArgs, null, null, sortOrder);
			// ��Ҫ :Ϊ����Ⱥ������Զ����� ��������� �� ע�����uri
			cursor.setNotificationUri(getContext().getContentResolver(),
					Uri.parse("content://db_groups/"));
			return cursor;
		} else if (mUriMatcher.match(uri) == QUERY_GROUPTHREAD_ALL) {
			Cursor cursor = db.query("thread_and_group_table", projection,
					selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(),
					Uri.parse("content://db_groups/"));
			return cursor;
		} else {
			throw new IllegalArgumentException("·����ƥ��,�������...by dbprovider");
		}

	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (mUriMatcher.match(uri) == INSERT_ONE) {
			db.insert("groupname_table", null, values);
			getContext().getContentResolver().notifyChange(Uri.parse("content://db_groups/"), null);
		} else if (mUriMatcher.match(uri) == INSERT_THREAD) {
			db.insert("thread_and_group_table", null, values);
			getContext().getContentResolver().notifyChange(
					Uri.parse("content://db_groups/group_thread"), null);
		} else {
			throw new IllegalArgumentException("·����ƥ��,�������...");
		}

		return uri;
	}

	@Override
	public int delete(Uri uri, String gpid_or_thread_id, String[] whereArgs) {

		// mUriMatcher.addURI("db_groups", "delete_threads", DELETE_THREAD);
		// mUriMatcher.addURI("db_groups", "delete_group", DELETE_GP);
		SQLiteDatabase db = helper.getWritableDatabase();
		if (mUriMatcher.match(uri) == DELETE_GP) {
			delGp(db, Integer.parseInt(gpid_or_thread_id));
			getContext().getContentResolver().notifyChange(
					Uri.parse("content://db_groups/"), null);
		} else if (mUriMatcher.match(uri) == DELETE_THREAD_WHERE_GPID) {
			delthread_where_gpid(db, Integer.parseInt(gpid_or_thread_id));
			getContext().getContentResolver().notifyChange(
					Uri.parse("content://db_groups/"), null);
		} else if (mUriMatcher.match(uri) == DELETE_THREAD_WHERE_THREAD_ID) {
			delthread_where_thread_id(db, Integer.parseInt(gpid_or_thread_id));
			getContext().getContentResolver().notifyChange(
					Uri.parse("content://db_groups/"), null);
		}

		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String old_gp_name,
			String[] new_gp_name) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (mUriMatcher.match(uri) == UPDATE_GP) {
			updateGP_name(db, old_gp_name, new_gp_name[0]);
			getContext().getContentResolver().notifyChange(
					Uri.parse("content://db_groups/"), null);
		}
		return 0;
	}

	/**
	 * ɾ��Ⱥ���
	 * @param albuminfo
	 */
	public void delGp(SQLiteDatabase db, int gpid) {
		db.beginTransaction();
		try {
			db.execSQL("DELETE FROM groupname_table WHERE group_id = ?",
					new Object[] { gpid });
			db.setTransactionSuccessful(); // ��������ɹ����
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // ��������
		}
	}

	/**
	 * ɾ��������where_gpid
	 * @param albuminfo
	 */
	public void delthread_where_gpid(SQLiteDatabase db, int gpid) {
		db.beginTransaction();
		try {
			db.execSQL("DELETE FROM thread_and_group_table WHERE group_id = ?",
					new Object[] { gpid });
			db.setTransactionSuccessful(); // ��������ɹ����
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // ��������
		}
	}

	/**
	 * ɾ��������where_thread_id
	 * @param albuminfo
	 */
	public void delthread_where_thread_id(SQLiteDatabase db, int thread_id) {
		db.beginTransaction();
		System.out.println("�� ������Ц 2");
		try {
			db.execSQL(
					"DELETE FROM thread_and_group_table WHERE thread_id = ?",
					new Object[] { thread_id });
			db.setTransactionSuccessful(); // ��������ɹ����
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // ��������
		}
	}

	/**
	 * ����Ⱥ������
	 */
	public void updateGP_name(SQLiteDatabase db, String old_gp_name,
			String new_gp_name) {
		//update groupname_table set group_name='hahagha' where group_name = '����ckua'
		db.beginTransaction();
		try {
			db.execSQL(
					"update groupname_table set group_name=? where group_name = '"
							+ old_gp_name+"'", new Object[] { new_gp_name });
			db.setTransactionSuccessful(); // ��������ɹ����
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // ��������
		}
	}
}

///**
	// * ɾ��һ��ר����Ϣfrom DB,��¼���ڴ������ʱ(50mb)��ui����֮ǰ��Ҫ�����ݿ���߰�ĳ��ר����Ϣ��ɾ��
	// * @param albuminfo
	// */
	// public void delHeadAlbumInfo(ISAlbumInfoData albuminfo) {
	// String albumID = albuminfo.albumID;
	// db.beginTransaction();
	// try {
	// db.execSQL("DELETE FROM albumList WHERE albumID=?",new
	// Object[]{albumID});
	// db.setTransactionSuccessful(); // ��������ɹ����
	// } catch (Exception e) {
	// }finally{
	// db.endTransaction(); // ��������
	// }
	// }
	//
	// //--------------------�����û����ݿ���� 2014��10��16��19:29:30 ---------------
	// /**
	// * ��ǰ����id����
	// * */
	// public void updateCurrLocalUserThemeId(SingSettingBean ssb){
	// db.beginTransaction();
	// try {/*,songRecLen=?*/
	// Log.i("jay",
	// "dbop  curr id == "+settings.instance().currLocalUserId+"_________ssb.getStyleID() == "+ssb.getStyleID());
	// db.execSQL("update localusers set SSstyleID=? where UserId="+settings.instance().currLocalUserId,new
	// Object[]{ssb.getStyleID()});
	// db.setTransactionSuccessful(); // ��������ɹ����
	// } catch (Exception e) {
	// Log.i("jay", "dbop Transaction  false");
	// }finally{
	// db.endTransaction(); // ��������
	// }
	// }
	
	
//	adb pull /data/data/com.example.show_smartmms/databases/smartsms.db C:\Users\Administrator.PC-20130630LRVK\Desktop\aft.db
//	C:\Users\Administrator.PC-20130630LRVK\Desktop\pre.db
//	C:\Users\Administrator.PC-20130630LRVK\Desktop\mid.db
//	C:\Users\Administrator.PC-20130630LRVK\Desktop\aft.db
//
//	su
//	chmod 777 /data/data/com.example.show_smartmms/databases/smartsms.db

