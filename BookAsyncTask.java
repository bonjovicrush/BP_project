package com.ysc.BookPreview0518_ysc;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 알라딘API 정보 파싱, 파싱한 정보 DB저장, 중복 검사
 *
 *
 */
public class BookAsyncTask extends AppCompatActivity {

    /**
     * SQLite 변수
     */
    private static final String BOOKS_TABLE_NAME = "BOOK_INFO";
    private static final String BOOKS_COLUMN_TITLE = "title";
    private static final String BOOKS_COLUMN_AUTHOR = "author";
    private static final String BOOKS_COLUMN_YEAR = "year";
    private static final String BOOKS_COLUMN_ISBN = "isbn";
    private static final String BOOKS_COLUMN_RATING = "rating";
    private static final String BOOKS_COLUMN_BOOKIMG = "bookImg";
    private static final String BOOKS_COLUMN_THISTIME = "thistime"; //2017-06-10 추가

    /**
     * 멤버 변수
     * 알라딘API AsynkTask 변수
     */
    private SQLiteDatabase db;                      // SQLiteDatabase 객체
    private String htmlPageUrl = "";                // 책 UML 주소
    private String mAuthor = "";                    // 작가, 저자 멤버변수
    private String mYear = "";                      // 출간일 멤버변수
    private String mIsbn = "";                      // ISBN 번호 멤버변수
    private String mTitile = "";                    // 책제목 멤버변수
    private String mRating = "";                    // 책평점 멤버변수
    private String mBookimg = "";                   // 책표지 멤버변수
    private String mTime = "";                      // 현재시간 멤버변수 //2017-06-10 추가

    boolean dbinsert = false;                       // DB 데이터 입력 체크

    /**
     * 생성자
     * 책 URL 초기화, SQLiteDatabase 객체 초기화
     * JsoupAsyncTask객체 생성 및 가동
     * @param bookURI
     * @param db
     */
    public BookAsyncTask(String bookURI, SQLiteDatabase db) {
        this.htmlPageUrl = bookURI;
        this.db = db;

        // JsoupAsyncTask 객체 생성
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();

        // doInBackground 가동
        jsoupAsyncTask.execute(htmlPageUrl);
    }

    /**
     * 신규 도서정보 DB 등록 메서드
     * @param rating
     * @param title
     * @param author
     * @param year
     * @param isbn
     * @param bookUrl
     * @return
     */
    public boolean insertBook(String rating, String title, String author, String year, String isbn, String bookUrl, String time) {
        try {
            String CONTACTS_INSERT_TABLE = "INSERT INTO " + BOOKS_TABLE_NAME +
                    "(" +
                    BOOKS_COLUMN_RATING + ", " +
                    BOOKS_COLUMN_TITLE + ", " +
                    BOOKS_COLUMN_AUTHOR + ", " +
                    BOOKS_COLUMN_YEAR + ", " +
                    BOOKS_COLUMN_ISBN + ", " +
                    BOOKS_COLUMN_BOOKIMG + ", " +
                    BOOKS_COLUMN_THISTIME + ") values ('" +
                    rating + "', '" +
                    title + "', '" +
                    author + "', '" +
                    year + "', '" +
                    isbn + "', '" +
                    bookUrl + "', '" +
                    time + "');";
            db.execSQL(CONTACTS_INSERT_TABLE);

            dbinsert = true;
        } catch (Exception e) {
            e.printStackTrace();
            dbinsert = false;
        }
        return dbinsert;
    }

    /**
     * DB 중복 검사 메서드
     * @param isbn
     * @return
     */
    public boolean getResult(String isbn) {
        String result = "select * from " + BOOKS_TABLE_NAME + " where isbn= '" + isbn + "';";
        try {
            db.execSQL(result);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update(String time, String isbn) {
        String update = "update " + BOOKS_TABLE_NAME + " set " + BOOKS_COLUMN_THISTIME
                + " = '" + time + "', " + "where " + BOOKS_COLUMN_ISBN + " = " +  isbn + ";";
        db.execSQL(update);
    }


    /**
     * AsyncTask 이너 클래스
     *
     */
    private class JsoupAsyncTask extends AsyncTask<String, Void, Document> {

        Document doc = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 알라딘API XML 파싱 AsynkTask
         * @param urls
         * @return doc
         */
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db;

                db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc;
        }


        /**
         * Unity로 별점 정보 보냄
         * DBHelp로 데이터 저장
         */
        @Override
        protected void onPostExecute(Document doc) {


            // 알라딘API item 하부 태그 파싱
            NodeList nodeList = doc.getElementsByTagName("item");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;

                // 평점
                NodeList RankNode = element.getElementsByTagName("customerReviewRank");
                Element rankElement = (Element) RankNode.item(0);
                RankNode = rankElement.getChildNodes();
                mRating += "평점 : " + RankNode.item(0).getNodeValue();

                // 출간일
                NodeList pubDatenNode = element.getElementsByTagName("pubDate");
                Element pubDateElement = (Element) pubDatenNode.item(0);
                pubDatenNode = pubDateElement.getChildNodes();
                mYear += "출간일 : " + FileDateUtil.getParseDate(pubDatenNode.item(0).getNodeValue());

                // 책 표지 URL
                NodeList coverNode = element.getElementsByTagName("cover");
                Element coverElement = (Element) coverNode.item(0);
                coverNode = coverElement.getChildNodes();
                mBookimg += coverNode.item(0).getNodeValue();

                // 책제목
                NodeList titleNode = element.getElementsByTagName("title");
                Element titleElement = (Element) titleNode.item(0);
                titleNode = titleElement.getChildNodes();
                mTitile += "제목 : " + titleNode.item(0).getNodeValue();

                // ISBN
                NodeList isbnNode = element.getElementsByTagName("isbn13");
                Element isbnElement = (Element) isbnNode.item(0);
                isbnNode = isbnElement.getChildNodes();
                mIsbn += "ISBN : " + isbnNode.item(0).getNodeValue();

                // 저자
                NodeList authorNode = element.getElementsByTagName("author");
                Element authorElement = (Element) authorNode.item(0);
                authorNode = authorElement.getChildNodes();
                mAuthor += "저자 : " + authorNode.item(0).getNodeValue();

                /**
                 * 현재 시간 확인
                 */
                mTime = "확인 시간 : " + FileDateUtil.getModifiedDate(Locale.KOREA, System.currentTimeMillis());

                /**
                 * 유니티로 파싱한 평점을 전송
                 */
                UnityPlayer.UnitySendMessage("CloudRecognition", "GradeStar", mRating);
                Log.d("paranoid : ", "Unity GradeStar()로 평점 전송 완료!");


                /**
                 * DB로 값 전송
                 */
                if (true == getResult(mIsbn)){
                    insertBook(mRating, mTitile, mAuthor, mYear, mIsbn, mBookimg, mTime);
                    Log.d("paranoid : ", "insertBook()로 전송 완료!");
                } else {
                    //update(mTime, mIsbn);
                    Log.d("paranoid : ", "중복된 데이터");
                }

            }
        }
    }
}