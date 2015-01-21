package net.studymongolian.chimee;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
//import android.util.Log;

/**
 * Provides access to a database of user defined words for the Chimee app. Each
 * item has a word, a frequency, and a following word list.
 */
public class ChimeeUserDictionaryProvider extends ContentProvider {

	private static final String AUTHORITY = ChimeeUserDictionary.AUTHORITY;
	private static final String TAG = "ChimeeUserDictionaryProvider";
	private static final Uri CONTENT_URI = ChimeeUserDictionary.CONTENT_URI;
	private static final String DATABASE_NAME = "chimee_user_dict.db";
	private static final int DATABASE_VERSION = 1;
	private static final String USERDICT_TABLE_NAME = "words";
	private static HashMap<String, String> sDictProjectionMap;
	private static final UriMatcher sUriMatcher;
	private static final int WORDS = 1;
	private static final int WORD_ID = 2;


	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + USERDICT_TABLE_NAME + " ("
					+ ChimeeUserDictionary.Words._ID + " INTEGER PRIMARY KEY,"
					+ ChimeeUserDictionary.Words.WORD + " TEXT NOT NULL,"
					+ ChimeeUserDictionary.Words.FREQUENCY + " INTEGER,"
					+ ChimeeUserDictionary.Words.FOLLOWING + " TEXT NOT NULL"
					+ ");");
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			//Log.w(TAG, "Upgrading database from version " + oldVersion + " to "	+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + USERDICT_TABLE_NAME);
			onCreate(db);

		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {
		case WORDS:
			qb.setTables(USERDICT_TABLE_NAME);
			qb.setProjectionMap(sDictProjectionMap);
			break;
		case WORD_ID:
			qb.setTables(USERDICT_TABLE_NAME);
			qb.setProjectionMap(sDictProjectionMap);
			qb.appendWhere("_id" + "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ChimeeUserDictionary.Words.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case WORDS:
			return ChimeeUserDictionary.Words.CONTENT_TYPE;
		case WORD_ID:
			return ChimeeUserDictionary.Words.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != WORDS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		if (values.containsKey(ChimeeUserDictionary.Words.WORD) == false) {
			throw new SQLException("Word must be specified");
		}
		if (values.containsKey(ChimeeUserDictionary.Words.FREQUENCY) == false) {
			values.put(ChimeeUserDictionary.Words.FREQUENCY, "1");
		}
		if (values.containsKey(ChimeeUserDictionary.Words.FOLLOWING) == false) {
			values.put(ChimeeUserDictionary.Words.FOLLOWING, (String) "");
		}
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(USERDICT_TABLE_NAME,
				ChimeeUserDictionary.Words.WORD, values);
		if (rowId > 0) {
			Uri wordUri = ContentUris.withAppendedId(
					ChimeeUserDictionary.Words.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(wordUri, null);
			// mBackupManager.dataChanged();
			return wordUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case WORDS:
			count = db.delete(USERDICT_TABLE_NAME, where, whereArgs);
			break;
		case WORD_ID:
			String wordId = uri.getPathSegments().get(1);
			count = db.delete(
					USERDICT_TABLE_NAME,
					ChimeeUserDictionary.Words._ID
							+ "="
							+ wordId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case WORDS:
			count = db.update(USERDICT_TABLE_NAME, values, where, whereArgs);
			break;
		case WORD_ID:
			String wordId = uri.getPathSegments().get(1);
			count = db.update(
					USERDICT_TABLE_NAME,
					values,
					ChimeeUserDictionary.Words._ID
							+ "="
							+ wordId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		// mBackupManager.dataChanged();
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, "words", WORDS);
		sUriMatcher.addURI(AUTHORITY, "words/#", WORD_ID);
		sDictProjectionMap = new HashMap<String, String>();
		sDictProjectionMap.put(ChimeeUserDictionary.Words._ID,
				ChimeeUserDictionary.Words._ID);
		sDictProjectionMap.put(ChimeeUserDictionary.Words.WORD,
				ChimeeUserDictionary.Words.WORD);
		sDictProjectionMap.put(ChimeeUserDictionary.Words.FREQUENCY,
				ChimeeUserDictionary.Words.FREQUENCY);
		sDictProjectionMap.put(ChimeeUserDictionary.Words.FOLLOWING,
				ChimeeUserDictionary.Words.FOLLOWING);
	}
}
