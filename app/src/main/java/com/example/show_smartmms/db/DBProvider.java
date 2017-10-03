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
	private static final int QUERY_GROUPNAME_ALL = 1; // 查询所有群组
	private static final int INSERT_ONE = 2; // 增添创建一个群组
	private static final int INSERT_THREAD = 3; // 添加会话到群组
	private static final int QUERY_GROUPTHREAD_ALL = 4; // 查询所有群组
	private static final int DELETE_GP = 5; // 添加会话到群组
	private static final int DELETE_THREAD_WHERE_GPID = 6; // 添加会话到群组
	private static final int DELETE_THREAD_WHERE_THREAD_ID = 8; // 添加会话到群组
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
			// 重要 :为了让群组界面自动更新 这条必须加 ① 注册这个uri
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
			throw new IllegalArgumentException("路径不匹配,口令错误...by dbprovider");
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
			throw new IllegalArgumentException("路径不匹配,口令错误...");
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
	 * 删除群组表
	 * @param albuminfo
	 */
	public void delGp(SQLiteDatabase db, int gpid) {
		db.beginTransaction();
		try {
			db.execSQL("DELETE FROM groupname_table WHERE group_id = ?",
					new Object[] { gpid });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 删除关联表where_gpid
	 * @param albuminfo
	 */
	public void delthread_where_gpid(SQLiteDatabase db, int gpid) {
		db.beginTransaction();
		try {
			db.execSQL("DELETE FROM thread_and_group_table WHERE group_id = ?",
					new Object[] { gpid });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 删除关联表where_thread_id
	 * @param albuminfo
	 */
	public void delthread_where_thread_id(SQLiteDatabase db, int thread_id) {
		db.beginTransaction();
		System.out.println("囧 哈哈大笑 2");
		try {
			db.execSQL(
					"DELETE FROM thread_and_group_table WHERE thread_id = ?",
					new Object[] { thread_id });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * 更新群组名称
	 */
	public void updateGP_name(SQLiteDatabase db, String old_gp_name,
			String new_gp_name) {
		//update groupname_table set group_name='hahagha' where group_name = '足球ckua'
		db.beginTransaction();
		try {
			db.execSQL(
					"update groupname_table set group_name=? where group_name = '"
							+ old_gp_name+"'", new Object[] { new_gp_name });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} catch (Exception e) {
		} finally {
			db.endTransaction(); // 结束事务
		}
	}
}

///**
	// * 删除一个专辑信息from DB,当录音内存溢出的时(50mb)，ui更新之前需要在数据库里边把某个专辑信息给删掉
	// * @param albuminfo
	// */
	// public void delHeadAlbumInfo(ISAlbumInfoData albuminfo) {
	// String albumID = albuminfo.albumID;
	// db.beginTransaction();
	// try {
	// db.execSQL("DELETE FROM albumList WHERE albumID=?",new
	// Object[]{albumID});
	// db.setTransactionSuccessful(); // 设置事务成功完成
	// } catch (Exception e) {
	// }finally{
	// db.endTransaction(); // 结束事务
	// }
	// }
	//
	// //--------------------本地用户数据库操作 2014年10月16日19:29:30 ---------------
	// /**
	// * 当前主题id更新
	// * */
	// public void updateCurrLocalUserThemeId(SingSettingBean ssb){
	// db.beginTransaction();
	// try {/*,songRecLen=?*/
	// Log.i("jay",
	// "dbop  curr id == "+settings.instance().currLocalUserId+"_________ssb.getStyleID() == "+ssb.getStyleID());
	// db.execSQL("update localusers set SSstyleID=? where UserId="+settings.instance().currLocalUserId,new
	// Object[]{ssb.getStyleID()});
	// db.setTransactionSuccessful(); // 设置事务成功完成
	// } catch (Exception e) {
	// Log.i("jay", "dbop Transaction  false");
	// }finally{
	// db.endTransaction(); // 结束事务
	// }
	// }
	
	
//	adb pull /data/data/com.example.show_smartmms/databases/smartsms.db C:\Users\Administrator.PC-20130630LRVK\Desktop\aft.db
//	C:\Users\Administrator.PC-20130630LRVK\Desktop\pre.db
//	C:\Users\Administrator.PC-20130630LRVK\Desktop\mid.db
//	C:\Users\Administrator.PC-20130630LRVK\Desktop\aft.db
//
//	su
//	chmod 777 /data/data/com.example.show_smartmms/databases/smartsms.db

