package com.ysc.BookPreview0518_ysc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * 데이터 베이스 관리 클래스
 *
 * Created by Do sin woock on 2017-06-05.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mylibrary.db";
    private static final String BOOKS_TABLE_NAME = "BOOK_INFO";
    private static final String BOOKS_COLUMN_ID = "_id";
    private static final String BOOKS_COLUMN_TITLE = "title";
    private static final String BOOKS_COLUMN_AUTHOR = "author";
    private static final String BOOKS_COLUMN_YEAR = "year";
    private static final String BOOKS_COLUMN_ISBN = "isbn";
    private static final String BOOKS_COLUMN_RATING = "rating";
    private static final String BOOKS_COLUMN_BOOKIMG = "bookImg";
    private static final String BOOKS_COLUMN_THISTIME = "thistime";

    /**
     * DB 생성자
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 테이블 생성 메서드
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
    }

    /**
     * 신규 도서정보 등록 메서드
     * @param rating
     * @param title
     * @param author
     * @param year
     * @param isbn
     * @param bookUrl
     * @return
     */
    public boolean insertBook(String rating, String title, String author, String year, String isbn, String bookUrl, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOKS_COLUMN_RATING, rating);
        contentValues.put(BOOKS_COLUMN_TITLE, title);
        contentValues.put(BOOKS_COLUMN_AUTHOR, author);
        contentValues.put(BOOKS_COLUMN_YEAR, year);
        contentValues.put(BOOKS_COLUMN_ISBN, isbn);
        contentValues.put(BOOKS_COLUMN_BOOKIMG, bookUrl);
        contentValues.put(BOOKS_COLUMN_THISTIME, time);


        db.insert(BOOKS_TABLE_NAME, null, contentValues);
        return true;
    }

    /**
     * CustomlistView를 위한 커서 메서드
     * @return
     */
    public ArrayList<Book> getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Book> bookslist = new ArrayList<Book>();
        String selectQuery = "SELECT * FROM " + BOOKS_TABLE_NAME;
        Cursor res = db.rawQuery(selectQuery, null);

        while (res.moveToNext()) {
            bookslist.add(new Book(
                    res.getInt(res.getColumnIndex(BOOKS_COLUMN_ID)),
                    res.getString(res.getColumnIndex(BOOKS_COLUMN_RATING)),
                    res.getString(res.getColumnIndex(BOOKS_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(BOOKS_COLUMN_AUTHOR)),
                    res.getString(res.getColumnIndex(BOOKS_COLUMN_YEAR)),
                    res.getString(res.getColumnIndex(BOOKS_COLUMN_ISBN)),
                    res.getString(res.getColumnIndex(BOOKS_COLUMN_BOOKIMG)),
                    res.getString(res.getColumnIndex(BOOKS_COLUMN_THISTIME))));
        }
        res.close();
        return bookslist;
    }

    /**
     * @return
     */
    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, BOOKS_TABLE_NAME);
        return numRows;
    }

    /**
     * 기존 도서 정보 수정 메서드
     * @param id
     * @param rating
     * @param title
     * @param author
     * @param year
     * @param isbn
     * @param bookUrl
     * @param time
     * @return
     */
    public boolean updateBook(Integer id, String rating, String title, String author, String year, String isbn, String bookUrl, String time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOKS_COLUMN_RATING, rating);
        contentValues.put(BOOKS_COLUMN_TITLE, title);
        contentValues.put(BOOKS_COLUMN_AUTHOR, author);
        contentValues.put(BOOKS_COLUMN_YEAR, year);
        contentValues.put(BOOKS_COLUMN_ISBN, isbn);
        contentValues.put(BOOKS_COLUMN_BOOKIMG, bookUrl);
        contentValues.put(BOOKS_COLUMN_THISTIME, time);

        db.update(BOOKS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    /**
     * 기존 도서 삭제 메서드
     * @param id
     * @return
     */
    public Integer deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(BOOKS_TABLE_NAME, "_id = ? ", new String[]{Integer.toString(id)});
    }





}
