package net.studymongolian.chimee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SuffixDatabaseAdapter {

    private MyDatabaseHelper helper;
    Context context;

    private final int masculine = Suffix.WordGender.Masculine.getValue();
    private final int feminine = Suffix.WordGender.Feminine.getValue();

    // Constructor
    SuffixDatabaseAdapter(Context context) {

        helper = new MyDatabaseHelper(context);
        this.context = context;
    }

    public void updateFrequencyForSuffix(String suffixToUpdate) {

        SQLiteDatabase db = helper.getWritableDatabase();
        String valueToIncrementBy = "1";
        String[] bindingArgs = new String[]{ valueToIncrementBy, suffixToUpdate };

        db.execSQL("UPDATE " + MyDatabaseHelper.SUFFIX_TABLE_NAME + " SET " +
                MyDatabaseHelper.FREQUENCY + " = " + MyDatabaseHelper.FREQUENCY + " + ? WHERE " +
                MyDatabaseHelper.SUFFIX + " = ?", bindingArgs);
        db.close();

    }

    public ArrayList<String> findSuffixesBeginningWith(String suffixStart, Suffix.WordGender gender, Suffix.WordEnding ending) {

        SQLiteDatabase db = helper.getReadableDatabase();
        String selection;

        switch (ending) {
            case Vowel:

                if (gender == Suffix.WordGender.Masculine) {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + feminine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.VowelOnly.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NotBigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                } else {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + masculine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.VowelOnly.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NotBigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                }
                break;

            case N:

                if (gender == Suffix.WordGender.Masculine) {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + feminine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NOnly.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantsAll.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NotBigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                } else {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + masculine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NOnly.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantsAll.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NotBigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                }

                break;
            case BigDress:

                if (gender == Suffix.WordGender.Masculine) {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + feminine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantNonN.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantsAll.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.BigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                } else {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + masculine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantNonN.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantsAll.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.BigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                }

                break;
            case OtherConsonant: // besides N or BGDRS

                if (gender == Suffix.WordGender.Masculine) {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + feminine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantNonN.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantsAll.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NotBigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                } else {

                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + masculine + " AND (" +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantNonN.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.ConsonantsAll.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.NotBigDress.getValue() + " OR " +
                            MyDatabaseHelper.ENDING_TYPE + "=" + Suffix.SuffixType.All.getValue() + ")";
                }
                break;

            case Nil:

                if (gender == Suffix.WordGender.Masculine) {
                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + feminine;
                }else {
                    selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                            MyDatabaseHelper.GENDER + "!=" + masculine;
                }


                break;
            default:

                selection = MyDatabaseHelper.SUFFIX + " LIKE ? AND " +
                        MyDatabaseHelper.GENDER + "!=" + masculine;

        }

        String table = MyDatabaseHelper.SUFFIX_TABLE_NAME;
        String[] columns = {MyDatabaseHelper.SUFFIX};
        String[] selectionArgs = { suffixStart + "%"};
        String orderBy = MyDatabaseHelper.FREQUENCY + " DESC";

        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, orderBy);

        int columnIndex = cursor.getColumnIndex(MyDatabaseHelper.SUFFIX);
        ArrayList<String> suffixList = new ArrayList<>();
        while (cursor.moveToNext()) {
            suffixList.add(cursor.getString(columnIndex));
        }

        cursor.close();
        db.close();

        return suffixList;

    }


    // Making this an inner class rather than a separate class so that outer
    // class can securely refer to private variables in this class
    static class MyDatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "chimee_suffixes.db";
        private static final int DATABASE_VERSION = 1;

        // //////////// Favorite Table //////////////////
        private static final String SUFFIX_TABLE_NAME = "SUFFIXLIST";
        // Column names
        private static final String ID = "_id";
        private static final String SUFFIX = "suffix";
        private static final String GENDER = "gender";
        private static final String ENDING_TYPE = "type";
        private static final String FREQUENCY = "frequency";
        // SQL statements
        private static final String CREATE_SUFFIX_TABLE = "CREATE TABLE " + SUFFIX_TABLE_NAME
                + " (" + ID + " INTEGER PRIMARY KEY, " + SUFFIX + " TEXT UNIQUE, " + GENDER
                + " INTEGER, " + ENDING_TYPE + " INTEGER, " + FREQUENCY + " INTEGER)";
        private static final String DROP_SUFFIX_TABLE = "DROP TABLE IF EXISTS "
                + SUFFIX_TABLE_NAME;

        MyDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_SUFFIX_TABLE);
                insertInitialData(db);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try {
                db.execSQL(DROP_SUFFIX_TABLE);
                onCreate(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void insertInitialData(SQLiteDatabase db) {

            int DEFAULT_FREQUENCY = 1;

            // Default messages to add to Favorite table
            List<Suffix> list = new ArrayList<>();
            list.add(new Suffix(" ᠶᠢᠨ", Suffix.WordGender.Neutral, Suffix.SuffixType.VowelOnly)); // yin
            list.add(new Suffix(" ᠤᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.ConsonantNonN)); // on
            list.add(new Suffix(" ᠦᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.ConsonantNonN)); // un
            list.add(new Suffix(" ᠤ", Suffix.WordGender.Masculine, Suffix.SuffixType.NOnly)); //o
            list.add(new Suffix(" ᠦ", Suffix.WordGender.Feminine, Suffix.SuffixType.NOnly)); //u
            list.add(new Suffix(" ᠢ", Suffix.WordGender.Neutral, Suffix.SuffixType.ConsonantsAll)); //i
            list.add(new Suffix(" ᠶᠢ", Suffix.WordGender.Neutral, Suffix.SuffixType.VowelOnly)); //yi
            list.add(new Suffix(" ᠳᠤ", Suffix.WordGender.Masculine, Suffix.SuffixType.NotBigDress)); //do
            list.add(new Suffix(" ᠳᠦ", Suffix.WordGender.Feminine, Suffix.SuffixType.NotBigDress)); //du
            list.add(new Suffix(" ᠲᠤ", Suffix.WordGender.Masculine, Suffix.SuffixType.BigDress)); //to
            list.add(new Suffix(" ᠲᠦ", Suffix.WordGender.Feminine, Suffix.SuffixType.BigDress)); //tu
            list.add(new Suffix(" ᠠᠴᠠ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //acha
            list.add(new Suffix(" ᠡᠴᠡ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //eche
            list.add(new Suffix(" ᠪᠠᠷ", Suffix.WordGender.Masculine, Suffix.SuffixType.VowelOnly)); //bar
            list.add(new Suffix(" ᠪᠡᠷ", Suffix.WordGender.Feminine, Suffix.SuffixType.VowelOnly)); //ber
            list.add(new Suffix(" ᠢᠶᠠᠷ", Suffix.WordGender.Masculine, Suffix.SuffixType.ConsonantsAll)); //iyar
            list.add(new Suffix(" ᠢᠶᠡᠷ", Suffix.WordGender.Feminine, Suffix.SuffixType.ConsonantsAll)); //iyer
            list.add(new Suffix(" ᠲᠠᠶ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //tai
            list.add(new Suffix(" ᠲᠡᠶ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //tei
            list.add(new Suffix(" ᠢᠶᠠᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.ConsonantsAll)); //iyan
            list.add(new Suffix(" ᠢᠶᠡᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.ConsonantsAll)); //iyen
            list.add(new Suffix(" ᠪᠠᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.VowelOnly)); //ban
            list.add(new Suffix(" ᠪᠡᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.VowelOnly)); //ben
            list.add(new Suffix(" ᠤᠤ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //oo
            list.add(new Suffix(" ᠦᠦ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //uu
            list.add(new Suffix(" ᠶᠤᠭᠠᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //yogan
            list.add(new Suffix(" ᠶᠦᠭᠡᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //yugen
            list.add(new Suffix(" ᠳᠠᠭᠠᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.NotBigDress)); //dagan
            list.add(new Suffix(" ᠳᠡᠭᠡᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.NotBigDress)); //degen
            list.add(new Suffix(" ᠲᠠᠭᠠᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.BigDress)); //tagan
            list.add(new Suffix(" ᠲᠡᠭᠡᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.BigDress)); //tegen
            list.add(new Suffix(" ᠠᠴᠠᠭᠠᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //achagan
            list.add(new Suffix(" ᠡᠴᠡᠭᠡᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //echegen
            list.add(new Suffix(" ᠲᠠᠶᠢᠭᠠᠨ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //taigan
            list.add(new Suffix(" ᠲᠡᠶᠢᠭᠡᠨ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //teigen
            list.add(new Suffix(" ᠤᠳ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //od
            list.add(new Suffix(" ᠦᠳ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //ud
            list.add(new Suffix(" ᠨᠤᠭᠤᠳ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //nogod
            list.add(new Suffix(" ᠨᠦᠭᠦᠳ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //nugud
            list.add(new Suffix(" ᠨᠠᠷ", Suffix.WordGender.Masculine, Suffix.SuffixType.All)); //nar
            list.add(new Suffix(" ᠨᠡᠷ", Suffix.WordGender.Feminine, Suffix.SuffixType.All)); //ner

            ContentValues contentValues = new ContentValues();

            try {
                db.beginTransaction();

                for (Suffix item: list) {

                    contentValues.put(SUFFIX, item.getSuffix());
                    contentValues.put(GENDER, item.getWordGender().getValue());
                    contentValues.put(ENDING_TYPE, item.getSuffixType().getValue());
                    contentValues.put(FREQUENCY, DEFAULT_FREQUENCY);

                    db.insert(SUFFIX_TABLE_NAME, null, contentValues);
                }

                db.setTransactionSuccessful();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        }
    }

}