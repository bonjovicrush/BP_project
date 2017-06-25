package com.ysc.BookPreview0518_ysc;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 현재 시간 정보 수집 클래스
 *
 * Created by Do sin woock on 2017-06-10.
 */

public class FileDateUtil {
    // 싱글턴
    public static String getModifiedDate(long modified) {
        return getModifiedDate(Locale.getDefault(), modified);
    }

    /**
     * 현재 시간을 처리하는 메서드
     * @param locale
     * @param modified
     * @return
     */
    public static String getModifiedDate(Locale locale, long modified) {
        SimpleDateFormat dateFormat = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dateFormat = new SimpleDateFormat(getDateFormat(locale));
        } else {
            dateFormat = new SimpleDateFormat("MMM/dd/yyyy hh:mm:ss");
        }

        return dateFormat.format(new Date(modified));
    }

    /**
     * 알라딘API 에서 받아온 pubDate RSS를 '~년~월~일'로 가공처리하는 메서드
     * @param pubDate
     * @return
     */
    public static String getParseDate(String pubDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
            try {
                Date date = dateFormat.parse(pubDate);
                pubDate = new SimpleDateFormat("yyyy년 MM월 dd일").format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        return pubDate;
    }

    /**
     * JELLY_BEAN 이하 일때 처리하는 메서드
     * @param locale
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getDateFormat(Locale locale) {
        String formatString = DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy hh:mm:ss aa");
        return formatString;
    }
}
