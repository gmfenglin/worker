package com.feng.lin.worker.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.feng.lin.worker.R;
import com.feng.lin.worker.utils.SimpleLogger;
import com.feng.lin.worker.utils.SpecialCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CalendarAdapter extends BaseAdapter {
 
  private static String TAG = "CalendarAdapter";
  private boolean isLeapyear = false; //是否为闰年 
  private int daysOfMonth = 0;   //某月的天数 
  private int dayOfWeek = 0;    //具体某一天是星期几 
  private int lastDaysOfMonth = 0; //上一个月的总天数 
  private Context context;
  private String[] dayNumber = new String[42]; //一个gridview中的日期存入此数组中 
  private SpecialCalendar sc = null;
  private int currentYear = 0;
  private int currentMonth = 0;
  /**
   * 当前选中的日期位置
   */
  private int currentFlag = -1;
  /**
   * 当前选中天的字符串 例:20170830
   */
  private String currentDayStr;
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
  private Set<Integer> schDateTagFlag = new ArraySet<>(); //存储当月所有的日程日期(标签)
  private String showYear = "";  //用于在头部显示的年份
  private String showMonth = ""; //用于在头部显示的月份 
  private String animalsYear = "";
  private String leapMonth = "";  //闰哪一个月 
  private Set<String> mSet = null;
  private List<Map<String,Object>> monthList;
  private List<String> monthDayList=new ArrayList<>();
  private boolean dFlag;
  /**
   * 距离当前月的差(上一个月-1,当前月0,下一个月+1)
   */
  private int jumpMonth = 0;
 
 
  public CalendarAdapter(Context context, int year, int month, String currentDayStr, List<Map<String,Object>> monthList) {
    this.context = context;
    sc = new SpecialCalendar();
    currentYear = year;
    currentMonth = month; //得到跳转到的月份
    this.currentDayStr = currentDayStr;
    this.monthList=monthList;
    getCalendar(currentYear, currentMonth);
  }

  @Override
  public void notifyDataSetChanged() {
    dFlag=false;
    monthDayList.clear();
    super.notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return dayNumber.length;
  }
 
  @Override
  public Object getItem(int position) {
    return position;
  }
 
  @Override
  public long getItemId(int position) {
    return position;
  }
 
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder myViewHolder = null;
    if (convertView == null || convertView.getTag() == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.item_select_time, null);
      myViewHolder = new ViewHolder();

      myViewHolder.mIdTvItemSelectTimeDay=convertView.findViewById(R.id.id_tv_item_select_time_day);
      convertView.setTag(myViewHolder);
    } else {
      myViewHolder = (ViewHolder) convertView.getTag();
    }
    myViewHolder.mIdTvItemSelectTimeDay.setText(dayNumber[position]);
    myViewHolder.mIdTvItemSelectTimeDay.setTextColor(Color.GRAY);//不是当前月为灰

    if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
      // 当前月信息显示 
      myViewHolder.mIdTvItemSelectTimeDay.setTextColor(Color.BLACK);// 当月字体设黑
      myViewHolder.mIdTvItemSelectTimeDay.setTag(true);// 当月字体设黑
    }else {
      myViewHolder.mIdTvItemSelectTimeDay.setTag(false);// 当月字体设黑
    }

    myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(0);
    if(!dFlag&&Integer.parseInt(dayNumber[position])==1&&monthDayList.size()==0){
      dFlag=true;
    }
    if(dFlag&&!monthDayList.contains(dayNumber[position])){
      monthDayList.add(dayNumber[position]);
      if (currentFlag != -1 && currentFlag == position) {
        //设置当天的背景
        myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.BLACK);
        myViewHolder.mIdTvItemSelectTimeDay.setTextColor(Color.WHITE);
        for (Map<String,Object> map:monthList){
          if(map.get("work_day").equals(""+currentYear+"-"+getStr(String.valueOf(currentMonth),2)+"-"+getStr(currentDayStr,2))){
            map.put("did",1);
            switch (Integer.parseInt(map.get("time_type_id").toString())){
              case 1:
              {
                myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.GREEN);
                break;
              }
              case 2:
              {
                myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.YELLOW);
                break;
              }
              case 3:
              {
                myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.BLUE);
                break;
              }

            }
          }
        }
      } else {
        String dayString=getShowYear()+"-"+getShowMonth()+"-"+getStr(dayNumber[position],2);
        boolean tFlag=false;
        for (Map<String,Object> map:monthList){
          if(map.get("work_day").equals(dayString) && map.get("did").equals(0)){
            map.put("did",1);
            tFlag=true;
            SimpleLogger.getInstance().log(dayString+":timeType:"+map.get("time_type_id"));
            switch (Integer.parseInt(map.get("time_type_id").toString())){
              case 1:
              {
                myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.GREEN);
                break;
              }
              case 2:
              {
                myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.YELLOW);
                break;
              }
              case 3:
              {

                myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.BLUE);
                break;
              }

            }
          }
        }
        SimpleLogger.getInstance().log(dayString+":day:"+tFlag);
        if(!tFlag){
          try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date today=format.parse(""+currentYear+"-"+getStr(String.valueOf(currentMonth),2)+"-"+getStr(currentDayStr,2));
            Date currentDay=format.parse(dayString);

            if(today.compareTo(currentDay)>0){
              myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(Color.RED);
            }else {
              myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(0);
            }
          }catch (Exception e){
            e.printStackTrace();
            myViewHolder.mIdTvItemSelectTimeDay.setBackgroundColor(0);
          }


        }


      }
    }

 

 
 
    return convertView;
  }
 
 
  /**
   * 下一个月
   */
  public void addMonth() {
    jumpMonth++;
    upDataMonth();
  }
 
  /**
   * 上一个月
   */
  public void lessMonth() {
    jumpMonth--;
    upDataMonth();
  }
 
 
  /**
   * 更新日历数据
   */
  public void upDataMonth() {
    int stepYear;
    int stepMonth = currentMonth + jumpMonth;
    if (stepMonth > 0) {
      //下一个月
      if (stepMonth % 12 == 0) {
        stepYear = currentYear + stepMonth / 12 - 1;
        stepMonth = 12;
      } else {
        stepYear = currentYear + stepMonth / 12;
        stepMonth = stepMonth % 12;
      }
    } else {
      //上一个月
      stepYear = currentYear - 1 + stepMonth / 12;
      stepMonth = stepMonth % 12 + 12;
    }
    getCalendar(stepYear, stepMonth);
  }
 
  /**
   * 得到某年的某月的天数且这月的第一天是星期几
   *
   * @param year
   * @param month
   */
  private void getCalendar(int year, int month) {
    isLeapyear = sc.isLeapYear(year);       //是否为闰年 
    daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); //某月的总天数 
    dayOfWeek = sc.getWeekdayOfMonth(year, month);   //某月第一天为星期几 
    lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); //上一个月的总天数
    getWeek(year, month);
  }
 
  /**
   * 将一个月中的每一天的值添加入数组dayNuber中
   *
   * @param year
   * @param month
   */
  private void getWeek(int year, int month) {
    schDateTagFlag.clear();
    currentFlag = -1;
    int j = 1;
    //得到当前月的所有日程日期(这些日期需要标记)
    for (int i = 0; i < dayNumber.length; i++) {
      if (i < dayOfWeek) { //前一个月
        int temp = lastDaysOfMonth - dayOfWeek + 1;
        dayNumber[i] = (temp + i) + "";
      } else if (i < daysOfMonth + dayOfWeek) {//本月
        int day = i - dayOfWeek + 1;  //得到的日期
        dayNumber[i] = i - dayOfWeek + 1 + "";
        //对于当前月才去标记当前日期 
        String yearStr = String.valueOf(year);
        String monthStr =getStr(String.valueOf(month),2);
        String dayStr =getStr(String.valueOf(day),2);
        String timeAll = yearStr + monthStr + dayStr;
        if (timeAll.equals(""+currentYear+getStr(String.valueOf(currentMonth),2)+getStr(currentDayStr,2))) {//判断选中的位置
          currentFlag = i;
        }
        if (mSet != null && mSet.size() > 0) {
          for (String s : mSet) {//迭代器遍历判断是否需要带标签
            if (timeAll.equals(s)) {
              schDateTagFlag.add(i);
            }
          }
        }
        setShowYear(yearStr);
        setShowMonth((month));
      } else {  //下一个月
        dayNumber[i] = j + "";
        j++;
      }
    }
  }
 
 
  /**
   * 获取当前时间 样式:20170830
   * @param position
   * @return
   */
  public String getItemTime(int position) {
    String month = getStr(getShowMonth(), 2);
    String day = getStr(getDateByClickItem(position), 2);
    return getShowYear() + month + day;
 
  }
 
  /**
   * 保留N位整数,不足前面补0
   *
   * @param file String
   * @param bit 位数
   * @return
   */
  public static String getStr(String file,int bit) {
    while (file.length() < bit)
      file = "0" + file;
    return file;
  }
 
  /**
   * 点击每一个item时返回item中的日期
   *
   * @param position
   * @return
   */
  public String getDateByClickItem(int position) {
    return dayNumber[position];
  }
 
  /**
   * 在点击gridView时，得到这个月中第一天的位置
   *
   * @return
   */
  public int getStartPositon() {
    return dayOfWeek + 7;
  }
 
  /**
   * 在点击gridView时，得到这个月中最后一天的位置
   *
   * @return
   */
  public int getEndPosition() {
    return (dayOfWeek + daysOfMonth + 7) - 1;
  }
 
  public String getShowYear() {
    return showYear;
  }
 
  public void setShowYear(String showYear) {
    this.showYear = showYear;
  }
 
  public String getShowMonth() {
    return showMonth;
  }
 
  public void setShowMonth(int showMonth) {
    if(showMonth>=10){
      this.showMonth = showMonth+"";
    }else{
      this.showMonth ="0"+showMonth;
    }

  }
 
  public String getAnimalsYear() {
    return animalsYear;
  }
 
  public void setAnimalsYear(String animalsYear) {
    this.animalsYear = animalsYear;
  }
 
  public String getLeapMonth() {
    return leapMonth;
  }
 
  public void setLeapMonth(String leapMonth) {
    this.leapMonth = leapMonth;
  }
 
 
  public Set<String> getSet() {
    return mSet;
  }
 
  public void setSet(Set<String> set) {
    mSet = set;
  }



  static class ViewHolder {



    TextView mIdTvItemSelectTimeDay;
 

  }
}