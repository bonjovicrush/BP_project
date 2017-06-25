package com.ysc.BookPreview0518_ysc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.ysc.BookPreview0518_ysc.R.id.isbn;

/**
 * listview와 customlistview 클래스
 *
 * Created by Do sin woock on 2017-06-06.
 */
public class listActivity extends AppCompatActivity {

    /**
     * 변수
     */
    CustomList adapter;                         // CustomList로 adapter생성
    ListView mList;                             // 리스트 뷰
    DBHelper mydb;                              // 데이터 베이스 객체
    private ArrayList<Book> book;               // Book 클래스 어레이 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mydb = new DBHelper(this);
        book = mydb.getData();

        // CustomList 클래스 adapter로 연결
        adapter = new CustomList(this, R.layout.customlist, book);

        // ListView 연결
        mList = (ListView)findViewById(R.id.list);
        mList.setAdapter(adapter);

        /**
         * ListView 길게 클릭시 해당 ROW 삭제
         */
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idchk = (TextView)view.findViewById(R.id.id);
                int id1 = Integer.valueOf(idchk.getText().toString());

                String item = parent.getItemAtPosition(position).toString();
                deleteItem(item, position, id1);
                return true;

            }
        });

        /**
         * ListView 짧게 클릭시 해당 웹페이지 이동
         */
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView mIsbn = (TextView)view.findViewById(isbn);

                String item = mIsbn.getText().toString();
                switch (item) {
                    // 평점 클릭후 가져온 메타데이터로 구분 웹페이지 이동
                    case "ISBN : 9788998756376":
                        Intent intent1 = new Intent(getApplicationContext(), WebAppActivity.class);
                        intent1.putExtra("INPUT_URL", "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=29704343&start=slayer");
                        startActivity(intent1);
                        break;
                    case "ISBN : 9791185553009":
                        Intent intent2 = new Intent(getApplicationContext(), WebAppActivity.class);
                        intent2.putExtra("INPUT_URL", "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=35920162&start=slayer");
                        startActivity(intent2);
                        break;
                    case "ISBN : 9788992649964":
                        Intent intent3 = new Intent(getApplicationContext(), WebAppActivity.class);
                        intent3.putExtra("INPUT_URL", "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=25477162");
                        startActivity(intent3);
                        break;
                }
            }
        });
    }

    /**
     *  리스트 뷰에서 롱키 선택하여 삭제
     * @param data - 삭제할 아이템
     * @param pos - 삭제할 아이템 인덱스
     */
    public AlertDialog deleteItem(String data, final int pos, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(listActivity.this);
        builder.setTitle("삭제 상자");
        builder.setMessage("선택한 목록을 삭제 하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mydb.deleteBook(id);
                    book.remove(pos);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

        })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    /**
     * 멤버클래스 CustomList
     *
     * 배열 어댑터의 속성을 확장하는 사용자 지정 어댑터를 만듭니다.
     * 이 어댑터는 각 텍스트 보기를 목록 보기에 표시 되어야하는 값과 연결합니다.
     * 이 경우 책의 평점, 제목, 저자, 출간일, ISBN, 책표지...를 표시하는 5개의 텍스트 뷰와 1개의 이미지 뷰가 있습니다.
     * 따라서 listview에서 각 행은 5개의 텍스트 뷰와 1개의 이미지 뷰를 포함합니다.
     * CustomList 덕분에 5개의 텍스트 뷰와 1개의 이미지 뷰를 데이터베이스에서 가져온 6개의 값과 연결할 수 있습니다.
     *
     */
    public class CustomList extends ArrayAdapter {
        private final Context context;
        private ArrayList<Book> book1;

        /**
         * 생성자.
         *
         * @param context
         */
        public CustomList(Context context, int textViewResourceID, ArrayList object) {
            super(context, textViewResourceID , object);
            this.context = context;
            book1 = object;
        }

        /**
         * 멤버클래스 ViewHolder
         * customlist layout 항목을 모아놓은 멤버클래스
         */
        private class ViewHolder {
            /**
             * 커스텀 리스트 항목
             */
            ImageView mBookimg;                          // 책표지
            TextView mId;                                // DB id
            TextView mWriter;                            // 작가, 저자
            TextView mYear;                              // 출간일
            TextView mIsbn;                              // ISBN 번호
            TextView mTitile;                            // 제목
            TextView mRating;                            // 평점
            TextView mTime;                              // 현재시간

        }



        /**
         * 데이터 베이스에서 받아온 값으로 커스텀리스트 각 항목에 연결한 다음
         * 리스트뷰로 출력하는 메서드
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.customlist, null);

                holder = new ViewHolder();
                holder.mId = (TextView) convertView.findViewById(R.id.id);
                holder.mRating = (TextView) convertView.findViewById(R.id.rating);
                holder.mTitile = (TextView) convertView.findViewById(R.id.title);
                holder.mWriter = (TextView) convertView.findViewById(R.id.writer);
                holder.mIsbn = (TextView) convertView.findViewById(isbn);
                holder.mYear = (TextView) convertView.findViewById(R.id.year);
                holder.mBookimg = (ImageView) convertView.findViewById(R.id.imageView);
                holder.mTime = (TextView) convertView.findViewById(R.id.thistime);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Book individualBook = book1.get(position);
            holder.mId.setText(individualBook.getId() + "");
            holder.mRating.setText(individualBook.getRating() + "");
            holder.mTitile.setText(individualBook.getTitle() + "");
            holder.mWriter.setText(individualBook.getWriter() + "");
            holder.mYear.setText(individualBook.getYear() + "");
            holder.mIsbn.setText(individualBook.getIsbn() + "");
            Glide.with(getApplicationContext()).load(individualBook.getBookimg() + "").into(holder.mBookimg);
            holder.mTime.setText(individualBook.getTime() + "");

            return convertView;
        }
    }
}
