package net.studymongolian.chimee;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
//import android.util.Log;

/**
 * A provider of user defined words for predictive text input. Words can have
 * associated frequency information and following words.
 */
public class ChimeeUserDictionary {

	/** Authority string for this provider. */
	public static final String AUTHORITY = "net.studymongolian.chimee.user_dictionary";

	/**
	 * The content:// style URL for this provider
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	private static final int FREQUENCY_MIN = 0;
	private static final int FREQUENCY_MAX = 2147483646; // max int value -1
	private static final int MAX_FOLLOWING_WORDS = 10;

	// If this number is ever reached then someone is using their phone way too
	// much.

	/**
	 * Contains the user defined words.
	 */
	public static class Words implements BaseColumns {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/words");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of words.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.net.studymongolian.chimee.userword";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * word.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.net.studymongolian.chimee.userword";

		public static final String _ID = BaseColumns._ID;

		/**
		 * The word column.
		 * <p>
		 * TYPE: TEXT
		 * </p>
		 */
		public static final String WORD = "word";

		/**
		 * The frequency column. A value between 1 and 255. Higher values imply
		 * higher frequency.
		 * <p>
		 * TYPE: INTEGER
		 * </p>
		 */
		public static final String FREQUENCY = "frequency";

		/**
		 * The following words column. A comma delimited list of words that
		 * follow the row word. The list is in order of use, the first being the
		 * most recently used.
		 * <p>
		 * TYPE: TEXT
		 * </p>
		 */
		public static final String FOLLOWING = "following";

		/**
		 * Sort by descending order of frequency.
		 */
		public static final String DEFAULT_SORT_ORDER = FREQUENCY + " DESC";

		/**
		 * Queries the dictionary and returns all the words and values words.
		 * This is for testing so only return a concatinated string
		 * 
		 * @param context
		 *            the current application context
		 */
		public static String getAllWords(Context context) {

			// General purpose
			final ContentResolver resolver = context.getContentResolver();
			Cursor cursor = resolver.query(CONTENT_URI, null, null, null, null);

			// If for non-testing purposes then do this:
			// return cursor;

			// For Testing:
			StringBuilder builder = new StringBuilder();
			while (cursor.moveToNext()) {
				builder.append(cursor.getLong(cursor.getColumnIndex(_ID)) + " ");
				builder.append(cursor.getString(cursor.getColumnIndex(WORD))
						+ " ");
				builder.append(cursor.getInt(cursor.getColumnIndex(FREQUENCY))
						+ " ");
				builder.append(cursor.getString(cursor
						.getColumnIndex(FOLLOWING)) + " ");
				builder.append('\n');
			}
			cursor.close();
			return builder.toString();
		}

		/**
		 * Queries the dictionary and returns a cursor with all matches. But
		 * there should be no more than one match.
		 * 
		 * @param context
		 *            the current application context
		 * @param word
		 *            the word to search
		 */
		public static Cursor queryWord(Context context, String word) {

			// error checking
			if (TextUtils.isEmpty(word)) {
				return null;
			}

			// General purpose
			final ContentResolver resolver = context.getContentResolver();
			String[] projection = new String[] { _ID, WORD, FREQUENCY,
					FOLLOWING };
			String selection = WORD + "=?";
			String[] selectionArgs = { word };
			Cursor cursor = resolver.query(CONTENT_URI, projection, selection,
					selectionArgs, null);

			return cursor;

		}

		/**
		 * Adds a word to the dictionary, with the given frequency and following
		 * words.
		 * 
		 * @param context
		 *            the current application context
		 * @param word
		 *            the word to add to the dictionary. This should not be null
		 *            or empty.
		 * @param frequency
		 *            the frequency of the word.
		 * @param following
		 *            the word sugestions for following words
		 */
		public static Uri addWord(Context context, String word, int frequency,
				String following) {

			final ContentResolver resolver = context.getContentResolver();

			if (TextUtils.isEmpty(word)) {
				return null;
			}

			if (frequency < FREQUENCY_MIN)
				frequency = FREQUENCY_MIN;
			if (frequency > FREQUENCY_MAX)
				frequency = FREQUENCY_MAX;

			if (following == null) {
				following = "";
			}

			final int COLUMN_COUNT = 3;
			ContentValues values = new ContentValues(COLUMN_COUNT);

			values.put(WORD, word);
			values.put(FREQUENCY, frequency);
			values.put(FOLLOWING, following);

			Uri result = resolver.insert(CONTENT_URI, values);
			// It's ok if the insert doesn't succeed because the word
			// already exists.
			return result;

		}

		/**
		 * Changes the following suggestions of a word. Adds the following word
		 * if it doesn't exist or makes sure it is first in the list if it does
		 * exist.
		 * 
		 * @param context
		 *            the current application context
		 * @param word
		 *            the word whose following list needs updating
		 * @param followingWord
		 *            the following word to add
		 * 
		 */
		public static int addFollowing(Context context, String word,
				String followingWord) {

			// do error checking on input params
			if (TextUtils.isEmpty(word) || TextUtils.isEmpty(followingWord)) {
				//Log.e("ChimeeUserDictionary", "Empty word");
			}

			// Get following words string
			long wordId = -1;
			String followingString = "";
			final ContentResolver resolver = context.getContentResolver();
			String[] projection = new String[] { _ID, FOLLOWING };
			String selection = WORD + "=?";
			String[] selectionArgs = { word };
			Cursor cursor = null;
			try {
				cursor = resolver.query(CONTENT_URI, projection, selection,
						selectionArgs, null);
				if (cursor.moveToNext()) {
					wordId = cursor.getLong(cursor.getColumnIndex(_ID));
					followingString = cursor.getString(cursor
							.getColumnIndex(FOLLOWING));
				}
			} catch (Exception e) {
				//Log.e("ChimeeUserDictionary", e.toString());
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			if (wordId == -1) {
				return 0;
			}

			// if followingWord is already first then quit
			if (followingString.equals(followingWord)
					|| followingString.startsWith(followingWord + ",")) {

				return 0;
			}

			// put followingWord first in the list
			followingString = reorderFollowing(followingWord, followingString);

			// update word with new following list
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, wordId);
			ContentValues values = new ContentValues(1);
			values.put(FOLLOWING, followingString);
			int count = resolver.update(uri, values, null, null);

			return count;
		}

		/**
		 * Updates the frequency or following words of a word.
		 * 
		 * @param context
		 *            the current application context
		 * @param wordId
		 *            the id of the word to update
		 * @param frequency
		 *            the frequency of the word.
		 * @param following
		 *            the word sugestions for following words
		 */
		public static int updateWord(Context context, long wordId,
				int frequency, String following) {

			final ContentResolver resolver = context.getContentResolver();

			if (wordId < 0) {
				return -1;
			}

			// if (frequency < FREQUENCY_MIN) frequency = FREQUENCY_MIN;
			if (frequency > FREQUENCY_MAX)
				frequency = FREQUENCY_MAX;

			if (following == null) {
				following = "";
			}

			// final int COLUMN_COUNT = 3;
			ContentValues values = new ContentValues();
			// set content values
			if (frequency >= 0) {
				values.put(FREQUENCY, frequency);
				if (!TextUtils.isEmpty(following)) {
					values.put(FOLLOWING, following);
				}
			} else {
				if (!TextUtils.isEmpty(following)) {
					values.put(FOLLOWING, following);
				} else {
					// both are null/empty
					return -1;
				}
			}

			Uri uri = ContentUris.withAppendedId(CONTENT_URI, wordId);
			int count = resolver.update(uri, values, null, null);
			return count;
		}

		/**
		 * Changes the frequency number of a word. Usually for incrementing.
		 * 
		 * @param context
		 *            the current application context
		 * @param wordId
		 *            the row id of the word whose frequency to increment
		 * @param newFrequency
		 *            the new value for the word frequency
		 * 
		 */
		public static int updateFrequency(Context context, long wordId,
				int newFrequency) {

			if (wordId < 0) {
				return -1;
			}
			if (newFrequency < FREQUENCY_MIN)
				newFrequency = FREQUENCY_MIN;
			if (newFrequency > FREQUENCY_MAX)
				newFrequency = FREQUENCY_MAX;

			final ContentResolver resolver = context.getContentResolver();
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, wordId);
			ContentValues values = new ContentValues(1);
			values.put(FREQUENCY, newFrequency);
			int count = resolver.update(uri, values, null, null);

			return count;
		}

		/**
		 * Changes the following string of a word. Usually for updating after
		 * delete. Other types of updates can use addFollowing().
		 * 
		 * @param context
		 *            the current application context
		 * @param word
		 *            the word whose following words to change
		 * @param newFollowingList
		 *            a comma delimited list of following words
		 * 
		 */
		public static int updateFollowing(Context context, String word,
				String newFollowingList) {

			if (TextUtils.isEmpty(word)) {
				return -1;
			}

			final ContentResolver resolver = context.getContentResolver();
			ContentValues values = new ContentValues(1);
			values.put(FOLLOWING, newFollowingList);
			String where = WORD + "=?";
			String[] selectionArgs = { word };
			int count = resolver.update(CONTENT_URI, values, where, selectionArgs);

			return count;
		}

		/**
		 * Deletes a word from the dictionary database
		 * 
		 * @param context
		 *            the current application context
		 * @param word
		 *            the word to delete from the dictionary. This should not be
		 *            null or empty.
		 */
		public static int deleteWord(Context context, String word) {

			final ContentResolver resolver = context.getContentResolver();

			if (TextUtils.isEmpty(word)) {
				return -1;
			}

			String where = WORD + "=?";
			String[] selectionArgs = { word };

			int count = resolver.delete(CONTENT_URI, where, selectionArgs);
			return count;
		}

		/**
		 * Deletes a word from the dictionary database
		 * 
		 * @param context
		 *            the current application context
		 * @param wordId
		 *            the table row id of the word to delete from the
		 *            dictionary.
		 */
		public static int deleteWord(Context context, long wordId) {

			final ContentResolver resolver = context.getContentResolver();

			if (wordId < 0) {
				return -1;
			}

			Uri uri = ContentUris.withAppendedId(CONTENT_URI, wordId);

			int count = resolver.delete(uri, null, null);
			return count;
		}

		private static String reorderFollowing(String wordToAdd,
				String followingList) {

			if (TextUtils.isEmpty(followingList)) {
				return wordToAdd;
			} else {
				String[] followingSplit = followingList.split(",");
				StringBuilder builder = new StringBuilder();
				builder.append(wordToAdd);
				int counter = 0;
				for (String item : followingSplit) {
					if (!item.equals(wordToAdd)) {
						builder.append("," + item);
					}
					counter++;
					if (counter >= MAX_FOLLOWING_WORDS)
						break;
				}
				return builder.toString();
			}
		}
	}
}
