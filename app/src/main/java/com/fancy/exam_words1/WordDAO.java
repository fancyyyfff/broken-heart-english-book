package com.fancy.exam_words1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WordDAO {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public WordDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    // 打开数据库连接
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // 关闭数据库连接
    public void close() {
        dbHelper.close();
    }

    // 插入单词
    public long insertWord(String english, String chinese, int type) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_ENGLISH, english);
        values.put(SQLiteHelper.COLUMN_CHINESE, chinese);
        values.put(SQLiteHelper.COLUMN_TYPE, type);
        return database.insert(SQLiteHelper.TABLE_NAME, null, values);
    }

    // 查询所有单词
    public List<Words> getAllWords() {
        List<Words> wordsList = new ArrayList<>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Words word = cursorToWord(cursor);
                wordsList.add(word);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return wordsList;
    }

    // 根据熟悉度筛选单词
    public List<Words> getWordsByType(int type) {
        List<Words> wordsList = new ArrayList<>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME, null, SQLiteHelper.COLUMN_TYPE + " = ?",
                new String[]{String.valueOf(type)}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Words word = cursorToWord(cursor);
                wordsList.add(word);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return wordsList;
    }

    // 将Cursor中的数据转化为Words对象
    private Words cursorToWord(Cursor cursor) {
        Words word = new Words();
        word.setId(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID)));
        word.setEnglish(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_ENGLISH)));
        word.setChinese(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CHINESE)));
        word.setType(cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_TYPE)));
        return word;
    }

    //    判断单词在数据库中是否存在
    public boolean isWordExist(String english) {
        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME, null, "english = ?", new String[]{english}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // 根据单词的 id 更新其熟悉度 (type)
    public int updateWordType(int id, int type) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TYPE, type);
        return database.update(SQLiteHelper.TABLE_NAME, values, SQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 根据单词的 id 删除单词
    public int deleteWordById(int id) {
        return database.delete(SQLiteHelper.TABLE_NAME, SQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

}
