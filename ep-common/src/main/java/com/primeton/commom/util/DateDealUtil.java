package com.primeton.commom.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateDealUtil {


    /**
     * 将传入的时间转换成没有时分秒的时间，即（2022-06-17：00：00：00）
     * @param date
     * @return
     */
    public static Date paresDate(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = format.format(date);
        try {
            return format.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseDateString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = simpleDateFormat.format(date);
        return stringDate;
    }

    /**
     * 获取前几天 的日期
     * @param dayCount 传入获取的前 几 天数（如 现在 2022/6/18 输入1 则获取 2022/6/17）
     * @return
     */
    public static Date getDayBefore(Integer dayCount,Date date){

        //将时间格式化成的格式

       // yyyy-MM-dd HH:mm:ss
        SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
        //创建实例

        //Calendar
        Calendar cal = Calendar.getInstance();
        //设置当前时间

        cal.setTime(date);

        /*//在当前时间基础上减⼀年
        cal.add(Calendar.YEAR,-1);
        System.out.println(format.format(cal.getTime()));
        //在当前时间基础上减⼀⽉
        cal.add(Calendar.MONTH,-1);
        System.out.println(format.format(cal.getTime()));
        */
        //同理增加⼀天的⽅法：
        cal.add(Calendar.DATE,-dayCount);
        System.out.println(format.format(cal.getTime()));
        date.setTime(cal.getTimeInMillis());
        return date;
    }
}
