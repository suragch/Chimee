package net.studymongolian.chimee;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDatabaseAdapter {

	private MyDatabaseHelper helper;
	Context context;

	// Constructor
	MessageDatabaseAdapter(Context context) {

		helper = new MyDatabaseHelper(context);
		this.context = context;
	}

	public Message getFavoriteMessage(long messageId) {

		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = { MyDatabaseHelper.ID, MyDatabaseHelper.DATE_TIME,
				MyDatabaseHelper.MESSAGE };
        String selection = MyDatabaseHelper.ID + " = ?";
        String[] selectionArgs = {String.valueOf(messageId)};
		Cursor cursor = db.query(MyDatabaseHelper.FAVORITE_TABLE_NAME, columns,
                selection, selectionArgs, null,null, null, null);
		int indexId = cursor.getColumnIndex(MyDatabaseHelper.ID);
		int indexDate = cursor.getColumnIndex(MyDatabaseHelper.DATE_TIME);
		int indexMessage = cursor.getColumnIndex(MyDatabaseHelper.MESSAGE);

        Message message = null;
		if (cursor.moveToNext()) {
            message = new Message(
                    cursor.getLong(indexId),
                    cursor.getLong(indexDate),
                    cursor.getString(indexMessage));
		}

		cursor.close();
		db.close();

		return message;
	}

	public ArrayList<Message> getAllFavoriteMessages() {

		ArrayList<Message> allMessages = new ArrayList<Message>();

		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = { MyDatabaseHelper.ID, MyDatabaseHelper.DATE_TIME,
				MyDatabaseHelper.MESSAGE };
		String orderBy = MyDatabaseHelper.DATE_TIME + " DESC";
		Cursor cursor = db.query(MyDatabaseHelper.FAVORITE_TABLE_NAME, columns, null, null, null,
				null, orderBy, null);
		int indexId = cursor.getColumnIndex(MyDatabaseHelper.ID);
		int indexDate = cursor.getColumnIndex(MyDatabaseHelper.DATE_TIME);
		int indexMessage = cursor.getColumnIndex(MyDatabaseHelper.MESSAGE);

		while (cursor.moveToNext()) {
			Message message = new Message(
			        cursor.getLong(indexId),
                    cursor.getLong(indexDate),
                    cursor.getString(indexMessage));
			allMessages.add(message);
		}

		cursor.close();
		db.close();

		return allMessages;
	}

	public ArrayList<Message> getAllHistoryMessages() {

		ArrayList<Message> allMessages = new ArrayList<Message>();

		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = { MyDatabaseHelper.ID, MyDatabaseHelper.DATE_TIME,
				MyDatabaseHelper.MESSAGE };
		String orderBy = MyDatabaseHelper.DATE_TIME + " DESC";
		Cursor cursor = db.query(MyDatabaseHelper.HISTORY_TABLE_NAME, columns, null, null, null,
				null, orderBy, null);
		int indexId = cursor.getColumnIndex(MyDatabaseHelper.ID);
		int indexDate = cursor.getColumnIndex(MyDatabaseHelper.DATE_TIME);
		int indexMessage = cursor.getColumnIndex(MyDatabaseHelper.MESSAGE);

		while (cursor.moveToNext()) {
			Message message = new Message(
                    cursor.getLong(indexId),
                    cursor.getLong(indexDate),
                    cursor.getString(indexMessage));
			allMessages.add(message);
		}

		cursor.close();
		db.close();

		return allMessages;
	}

	public ArrayList<Message> getHistoryMessages(int limit, int offset) {
		ArrayList<Message> messages = new ArrayList<>();

		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = { MyDatabaseHelper.ID, MyDatabaseHelper.DATE_TIME,
				MyDatabaseHelper.MESSAGE };
		String orderBy = MyDatabaseHelper.DATE_TIME + " DESC";
		String limitStr = "" + offset + "," + limit; // same as LIMIT limit OFFSET offset
		Cursor cursor = db.query(MyDatabaseHelper.HISTORY_TABLE_NAME, columns,
                null, null, null,
				null, orderBy, limitStr);
		int indexId = cursor.getColumnIndex(MyDatabaseHelper.ID);
		int indexDate = cursor.getColumnIndex(MyDatabaseHelper.DATE_TIME);
		int indexMessage = cursor.getColumnIndex(MyDatabaseHelper.MESSAGE);

		while (cursor.moveToNext()) {
			Message message = new Message(
					cursor.getLong(indexId),
					cursor.getLong(indexDate),
					cursor.getString(indexMessage));
			messages.add(message);
		}

		cursor.close();
		db.close();

		return messages;
	}

	public ArrayList<Message> getRecentHistoryMessages(int numberOfMessages) {

		ArrayList<Message> allMessages = new ArrayList<Message>();

		SQLiteDatabase db = helper.getWritableDatabase();
		String[] columns = { MyDatabaseHelper.ID, MyDatabaseHelper.DATE_TIME,
				MyDatabaseHelper.MESSAGE };
		String orderBy = MyDatabaseHelper.DATE_TIME + " DESC";
		String limit = Integer.toString(numberOfMessages);
		Cursor cursor = db.query(MyDatabaseHelper.HISTORY_TABLE_NAME, columns, null, null, null,
				null, orderBy, limit);
		int indexId = cursor.getColumnIndex(MyDatabaseHelper.ID);
		int indexDate = cursor.getColumnIndex(MyDatabaseHelper.DATE_TIME);
		int indexMessage = cursor.getColumnIndex(MyDatabaseHelper.MESSAGE);

		while (cursor.moveToNext()) {
            Message message = new Message(
                    cursor.getLong(indexId),
                    cursor.getLong(indexDate),
                    cursor.getString(indexMessage));
			allMessages.add(message);
		}

		cursor.close();
		db.close();

		return allMessages;
	}

	public long addFavoriteMessage(String message) {

		// get current Unix epoc time in milliseconds
		long date = System.currentTimeMillis();

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MyDatabaseHelper.DATE_TIME, date);
		contentValues.put(MyDatabaseHelper.MESSAGE, message);
		long id = db.insert(MyDatabaseHelper.FAVORITE_TABLE_NAME, null, contentValues);
		db.close();
		return id;
	}

	public long addHistoryMessage(String message) {

		// get current Unix epoc time in milliseconds
		long date = System.currentTimeMillis();

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MyDatabaseHelper.DATE_TIME, date);
		contentValues.put(MyDatabaseHelper.MESSAGE, message);
		long id = db.insert(MyDatabaseHelper.HISTORY_TABLE_NAME, null, contentValues);
		db.close();
		return id;
	}

    public int updateFavoriteMessage(Message item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDatabaseHelper.MESSAGE, item.getMessage());
        contentValues.put(MyDatabaseHelper.DATE_TIME, System.currentTimeMillis());

        String selection = MyDatabaseHelper.ID + " = ?";
        String[] selectionArgs = {String.valueOf(item.getId())};

        SQLiteDatabase db = helper.getWritableDatabase();
        int id = db.update(MyDatabaseHelper.FAVORITE_TABLE_NAME, contentValues, selection,
                selectionArgs);
        db.close();
        return id;
    }

	public int updateFavoriteMessageTime(long rowId) {

		// get current Unix epoc time in milliseconds
		long date = System.currentTimeMillis();

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MyDatabaseHelper.DATE_TIME, date);
		String selection = MyDatabaseHelper.ID + " = ?";
		String[] selectionArgs = { String.valueOf(rowId) };

		int id = db.update(MyDatabaseHelper.FAVORITE_TABLE_NAME, contentValues, selection,
				selectionArgs);
		db.close();
		return id;
	}

	public int deleteFavoriteMessage(long rowId) {

		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = MyDatabaseHelper.ID + " =?";
		String[] whereArgs = { Long.toString(rowId) };
		int count = db.delete(MyDatabaseHelper.FAVORITE_TABLE_NAME, whereClause, whereArgs);
		db.close();
		return count;
	}

	public int deleteHistoryMessage(long rowId) {

		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = MyDatabaseHelper.ID + " =?";
		String[] whereArgs = { Long.toString(rowId) };
		int count = db.delete(MyDatabaseHelper.HISTORY_TABLE_NAME, whereClause, whereArgs);
		db.close();
		return count;
	}

	public int deleteHistoryAllMessages() {

		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = null; // delete all rows
		int count = db.delete(MyDatabaseHelper.HISTORY_TABLE_NAME, whereClause, null);
		db.close();
		return count;
	}

    // Making this an inner class rather than a separate class so that outer
	// class can securely refer to private variables in this class
	static class MyDatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "chimee_user_messages.db";
		private static final int DATABASE_VERSION = 1;

		// //////////// Favorite Table //////////////////
		private static final String FAVORITE_TABLE_NAME = "FAVORITE";
		// Column names
		private static final String ID = "_id";
		private static final String DATE_TIME = "date_time";
		private static final String MESSAGE = "message";
		// SQL statements
		private static final String CREATE_FAVORITE_TABLE = "CREATE TABLE " + FAVORITE_TABLE_NAME
				+ " (" + ID + " INTEGER PRIMARY KEY," + DATE_TIME + " INTEGER," + MESSAGE
				+ " TEXT NOT NULL)";
		private static final String DROP_FAVORITE_TABLE = "DROP TABLE IF EXISTS "
				+ FAVORITE_TABLE_NAME;

		// //////////// History Table //////////////////
		private static final String HISTORY_TABLE_NAME = "HISTORY";
		// SQL statements
		private static final String CREATE_HISTORY_TABLE = "CREATE TABLE " + HISTORY_TABLE_NAME
				+ " (" + ID + " INTEGER PRIMARY KEY," + DATE_TIME + " INTEGER," + MESSAGE
				+ " TEXT NOT NULL)";
		private static final String DROP_HISTORY_TABLE = "DROP TABLE IF EXISTS "
				+ HISTORY_TABLE_NAME;

		private Context context;

		public MyDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			try {
				db.execSQL(CREATE_FAVORITE_TABLE);
				db.execSQL(CREATE_HISTORY_TABLE);
				insertDefaultInitialData(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			try {
				db.execSQL(DROP_FAVORITE_TABLE);
				db.execSQL(DROP_HISTORY_TABLE);
				onCreate(db);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void insertDefaultInitialData(SQLiteDatabase db) {

			// Default messages to add to Favorite table
			String message1 = context.getResources().getString(R.string.favorite_db_message1);
			String message2 = context.getResources().getString(R.string.favorite_db_message2);
			String message3 = context.getResources().getString(R.string.favorite_db_message3);

			long date = System.currentTimeMillis();
			ContentValues contentValues = new ContentValues();

			// Message 3 (sorted by time, newest first, so this will be last)
			contentValues.put(DATE_TIME, date);
			contentValues.put(MESSAGE, message3);
			db.insert(FAVORITE_TABLE_NAME, null, contentValues);

			// Message 2
			date = System.currentTimeMillis() + 1; // +1 to ensure that it is different
			contentValues.put(DATE_TIME, date);
			contentValues.put(MESSAGE, message2);
			db.insert(FAVORITE_TABLE_NAME, null, contentValues);

			// Message 1
			date = System.currentTimeMillis() + 2; // +2 to ensure that it is different
			contentValues.put(DATE_TIME, date);
			contentValues.put(MESSAGE, message1);
			db.insert(FAVORITE_TABLE_NAME, null, contentValues);
		}
	}

}