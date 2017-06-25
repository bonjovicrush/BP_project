package com.ysc.BookPreview0518_ysc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

public class UnityPlayerActivity extends Activity {
    /**
     * SQLite 변수
     */
    private static final String DATABASE_NAME = "mylibrary.db";
    private static final String BOOKS_TABLE_NAME = "BOOK_INFO";
    private static final String BOOKS_COLUMN_TITLE = "title";
    private static final String BOOKS_COLUMN_AUTHOR = "author";
    private static final String BOOKS_COLUMN_YEAR = "year";
    private static final String BOOKS_COLUMN_ISBN = "isbn";
    private static final String BOOKS_COLUMN_RATING = "rating";
    private static final String BOOKS_COLUMN_BOOKIMG = "bookImg";
    private static final String BOOKS_COLUMN_THISTIME = "thistime";

    public boolean databaseCreated = false;
    public boolean tableCreated = false;
    public listActivity mylist;             // listActivity 객체 선언
    public SQLiteDatabase db;               // SQLiteDatabase 객체 선언
    protected UnityPlayer mUnityPlayer;     // don't change the name of this variable; referenced from native code
    public String bookURI;

    boolean introPopup = false;             // 인트로 팝업 플래그
    boolean wifiPopup = false;              //  와이파이 팝업 플래그
    boolean unityReady = false;             // 유니티 화면 준비상태


    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        /**
         * pyj_0531
         * 네트워크 상태체크 스레드
         */
        Thread netStateCheck = new Thread() {
            @Override
            public void run() {
                //super.run();
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (getNetAvailable(UnityPlayerActivity.this).equals("none")) {
                        Log.d("paranoid", "이용가능한 네트워크 없음");
                        introNetNotAvailable();
                    } else if (getNetAvailable(UnityPlayerActivity.this).equals("no_wifi")) {
                        Log.d("paranoid", "현재 모바일 데이터 이용");
                        UnityPlayer.UnitySendMessage("GameObject", "IntroReady", "true");
                        //wifiNotAvailable();
                        toastMessage();
                    } else {
                        UnityPlayer.UnitySendMessage("GameObject", "IntroReady", "true");
                    }
                }
            }
        };
        netStateCheck.setDaemon(true);
        netStateCheck.start();

        /**
         * DB 생성 코드
         * openOrCreateDatabase()는 db가 없으면 생성하고, db가 있으면 open만 한다.
         */
        try {
            db = openOrCreateDatabase(
                    DATABASE_NAME,
                    Activity.MODE_PRIVATE,
                    null);
            Log.d("paranoid :", "database 'mylibrary.db' 생성 성공!.");
            databaseCreated = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("paranoid :", "database 'mylibrary.db'  생성 되어있음.");
        }


        /**
         * TABLE 생성 코드
         */
        try {
            String CREATE_CONTACTS_TABLE = "CREATE TABLE " + BOOKS_TABLE_NAME +
                    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    BOOKS_COLUMN_RATING + " TEXT, " +
                    BOOKS_COLUMN_TITLE + " TEXT, " +
                    BOOKS_COLUMN_AUTHOR + " TEXT, " +
                    BOOKS_COLUMN_YEAR + " TEXT, " +
                    BOOKS_COLUMN_ISBN + " TEXT, " +
                    BOOKS_COLUMN_BOOKIMG + " TEXT, " +
                    BOOKS_COLUMN_THISTIME + " TEXT);";

            db.execSQL(CREATE_CONTACTS_TABLE);
            tableCreated = true;
            Log.d("paranoid", "Table 최초 생성 성공!.");
        } catch (Exception e) {
            e.printStackTrace();
            tableCreated = false;
            Log.d("paranoid", "Table 생성 되어있음!.");
        }


    }

    /**
     * pyj_0601
     *
     * @brief 이용가능한 네트워크가 없을 때 토스트 알림, 와이파이 설정 여부 다이얼로그 팝업
     */
    private void toastMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("paranoid", "이용가능한 네트워크 없음" + ": toastMessage()");
                //네트워크 연결이 되어있지 않음을 토스트로 띄움

                //와이파이 설정 여부 다이얼로그 팝업 빌드
                AlertDialog.Builder builder = new AlertDialog.Builder(UnityPlayerActivity.this);
                builder.setTitle("WIFI 설정").setMessage("WIFI 설정 하시겠습니까?").setCancelable(true)
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));//wifi 연결창 1
                                //startActivity(new Intent (Settings.ACTION_WIFI_SETTINGS));  //wifi 연결창 2
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                UnityPlayer.UnitySendMessage("GameObject", "IntroReady", "true");

                            }
                        });
                AlertDialog dialog = builder.create();
                if (wifiPopup == false && unityReady == true) {
                    wifiPopup = true;
                    dialog.show();  //다이얼로그 팝업
                }
            }
        });
    }

    /**
     * pyj_0601
     *
     * @brief 인트로에서 네트워크 연결 안 되있을 때 팝업메시지
     */
    private void introNetNotAvailable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(UnityPlayerActivity.this);
                builder.setTitle("안내").setMessage("데이터 설정 확인 후 다시 시도해주세요.").setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                AlertDialog dialog = builder.create();
                if (introPopup == false) {
                    introPopup = true;
                    dialog.show();  //다이얼로그 팝업
                }
            }
        });
    }

    /**
     * pyj_0531
     *
     * @param context
     * @return boolean
     * @brief network status check
     */
    public static String getNetAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        String netStatus;
        if (activeNetwork == null) {
            netStatus = "none";
            return netStatus;
        } else if (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            netStatus = "no_wifi";
            return netStatus;
        } else {
            return "";
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    /**
     * unity에서 Metadata을 받아서 switch문으로 분류
     * @param meta
     */
    public void targetMetadata(String meta) {
        switch (meta) {

            // 알라딘 API URL
            case "jsp":
                bookURI = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=ttbdbscjstk121356001&Query=9791185553009&isbn";
                new BookAsyncTask(bookURI, db);
                Log.d("paranoid : ", "jsp 인식 성공!");
                break;
            case "atm":
                bookURI = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=ttbdbscjstk121356001&Query=9788998756376&isbn";
                new BookAsyncTask(bookURI, db);
                Log.d("paranoid : ", "atm 인식 성공!");
                break;
            case "uml":
                bookURI = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=ttbdbscjstk121356001&Query=9788992649964&isbn";
                new BookAsyncTask(bookURI, db);
                Log.d("paranoid : ", "uml 인식 성공!");
                break;
        }
    }

    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // 유니티에서 값 받아서 Toast 띄우는 메서드

    /**
     * @brief 유니티 로딩완료 상태설정
     * @since 2017-06-13
     * @param msg
     */
    public void setUnityReady(String msg){
        if(msg.equals("true")){
            unityReady = true;
        }
    }

    /**
     * 유니티에서 값 받아서 Toast 띄우는 메서드
     */
    public void unityMessage(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 유니티에서 '평점', '나의 서재'를 누르면 해당 Activity로 이동하는 메서드
     * @param msg
     */
    public void unityIntent(String msg) {

        switch (msg) {
            // 유니티에서 '평점' 클릭후 가져온 메타데이터로 구분 웹페이지 이동
            case "atm":
                Intent intent1 = new Intent(getApplicationContext(), WebAppActivity.class);
                intent1.putExtra("INPUT_URL", "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=29704343&start=slayer");
                startActivity(intent1);
                break;
            case "jsp":
                Intent intent2 = new Intent(getApplicationContext(), WebAppActivity.class);
                intent2.putExtra("INPUT_URL", "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=35920162&start=slayer");
                startActivity(intent2);
                break;
            case "uml":
                Intent intent3 = new Intent(getApplicationContext(), WebAppActivity.class);
                intent3.putExtra("INPUT_URL", "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=25477162");
                startActivity(intent3);
                break;

            // 유니티에서 '나의 서재'를 누르면 listActivity로 이동
            case "lib":
                Intent intent4 = new Intent(getApplicationContext(), listActivity.class);
                startActivity(intent4);
                mylist = new listActivity();
                Log.d("paranoid : ", "나의 서재 호출 ");
                break;
        }
    }


    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }
}
