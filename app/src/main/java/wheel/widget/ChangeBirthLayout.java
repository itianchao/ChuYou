package wheel.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.jueda.ndian.R;

import java.util.ArrayList;
import java.util.Calendar;

import wheel.widget.adapters.AbstractWheelTextAdapter;
import wheel.widget.views.OnWheelChangedListener;
import wheel.widget.views.OnWheelScrollListener;
import wheel.widget.views.WheelView;

/**
 * 日期选择对话框
 * 
 * @author ywl
 * 
 */
public class ChangeBirthLayout extends LinearLayout {

    private Context mContext;
    private WheelView wvYear;
    private WheelView wvMonth;
    private WheelView wvDay;

    private ArrayList<String> arry_years = new ArrayList<String>();
    private ArrayList<String> arry_months = new ArrayList<String>();
    private ArrayList<String> arry_days = new ArrayList<String>();
    private CalendarTextAdapter mYearAdapter;
    private CalendarTextAdapter mMonthAdapter;
    private CalendarTextAdapter mDaydapter;

    private int month;
    private int day;

    private int currentYear = getYear();
    private int currentMonth = 1;
    private int currentDay = 1;

    private int maxTextSize = 18;
    private int minTextSize = 15;

    private boolean issetdata = false;

    private String selectYear;
    private String selectMonth;
    private String selectDay;

    public String  selectedyear=getYear()+"";
    public String  selectedmonth="1";
    public String  selectedday="1";

    private OnBirthListener onBirthListener;


    public ChangeBirthLayout(Context context) {
        this(context, null);
    }

    public ChangeBirthLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChangeBirthLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;

        View convertView =
                LayoutInflater.from(mContext).inflate(R.layout.dialog_myinfo_changebirth, this);
        wvYear = (WheelView) convertView.findViewById(R.id.wv_birth_year);
        wvMonth = (WheelView) convertView.findViewById(R.id.wv_birth_month);
        wvDay = (WheelView) convertView.findViewById(R.id.wv_birth_day);
        Init();

    }
    public void Init(){
        if (!issetdata) {
            initData(currentMonth,currentDay);
        }
        initYears();
        mYearAdapter =
                new CalendarTextAdapter(mContext, arry_years, setYear(currentYear), maxTextSize,
                        minTextSize);
        wvYear.setVisibleItems(3);
        wvYear.setViewAdapter(mYearAdapter);
        wvYear.setCurrentItem(setYear(currentYear));

        initMonths(month);
        mMonthAdapter =
                new CalendarTextAdapter(mContext, arry_months, setMonth(currentMonth), maxTextSize,
                        minTextSize);
        wvMonth.setVisibleItems(3);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(setMonth(currentMonth));

        initDays(day);
        mDaydapter =
                new CalendarTextAdapter(mContext, arry_days, currentDay - 1, maxTextSize,
                        minTextSize);
        wvDay.setVisibleItems(3);
        wvDay.setViewAdapter(mDaydapter);
        wvDay.setCurrentItem(currentDay - 1);

        wvYear.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                selectYear = currentText;
                setTextviewSize(currentText, mYearAdapter);
                currentYear = Integer.parseInt(currentText.substring(0, currentText.length() - 1));
                setYear(currentYear);
                initMonths(month);
                mMonthAdapter =
                        new CalendarTextAdapter(mContext, arry_months, 0, maxTextSize, minTextSize);
                wvMonth.setVisibleItems(3);
                wvMonth.setViewAdapter(mMonthAdapter);
                wvMonth.setCurrentItem(0);
                selectedmonth="1";
            }
        });

        wvYear.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {


            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                selectedyear=currentText.substring(0,currentText.length()-1);
                setTextviewSize(currentText, mYearAdapter);
            }
        });

        wvMonth.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                selectMonth = currentText;
                setTextviewSize(currentText, mMonthAdapter);
                setMonth(Integer.parseInt(currentText.substring(0, currentText.length() - 1)));
                initDays(day);
                mDaydapter =
                        new CalendarTextAdapter(mContext, arry_days, 0, maxTextSize, minTextSize);
                wvDay.setVisibleItems(3);
                wvDay.setViewAdapter(mDaydapter);
                wvDay.setCurrentItem(0);
                selectedday="1";
            }
        });

        wvMonth.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {


            }

            @Override
            public void onScrollingFinished(WheelView wheel) {

                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                selectedmonth=currentText.substring(0,currentText.length()-1);
                setTextviewSize(currentText, mMonthAdapter);
            }
        });

        wvDay.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {

                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mDaydapter);
                selectDay = currentText;
            }
        });

        wvDay.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {


            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                selectedday=currentText.substring(0,currentText.length()-1);
                setTextviewSize(currentText, mDaydapter);
            }
        });
    }

    /**
     * \设置默认时间
     * @param year
     * @param months
     * @param day
     */
    public void setTime(int year,int months,int day){
        selectedyear=year+"";
        selectedmonth=months+"";
        selectedday=day+"";
        currentYear=year;
        currentMonth=months;
        currentDay=day;
        Init();
    }

    public void initYears() {
        for (int i = getYear(); i > 1940; i--) {
            arry_years.add(i + "年");
        }
    }

    public void initMonths(int months) {
        arry_months.clear();
        for (int i = 1; i <= months; i++) {
            arry_months.add(i + "月");
        }
    }

    public void initDays(int days) {
        arry_days.clear();
        for (int i = 1; i <= days; i++) {
            arry_days.add(i + "日");
        }
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem,
                int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    public void setBirthdayListener(OnBirthListener onBirthListener) {
        this.onBirthListener = onBirthListener;
    }

    public interface OnBirthListener {
        public void onClick(String year, String month, String day);
    }

    /**
     * 设置字体大小
     * 
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(maxTextSize);
                textvew.setTextColor(Color.parseColor("#30b0f0"));
            } else {
                textvew.setTextSize(minTextSize);
                textvew.setTextColor(Color.parseColor("#999999"));
            }
        }
    }

    /**
     * 获取选中时间
     * @return
     */
    public String getTime(){
        if(selectedmonth.length()==1){
            selectedmonth="0"+selectedmonth;
        }
        if(selectedday.length()==1){
            selectedday="0"+selectedday;
        }
        String time=selectedyear+"."+selectedmonth+"."+selectedday;
     return time;
    }
    public int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE);
    }

    public void initData(int month,int day) {
        setDate(getYear(), getMonth(), getDay());
        this.currentDay = day;
        this.currentMonth = month;
    }

    /**
     * 设置年月日
     * 
     * @param year
     * @param month
     * @param day
     */
    public void setDate(int year, int month, int day) {
        selectYear = year + "";
        selectMonth = month + "";
        selectDay = day + "";
        issetdata = true;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentDay = day;
        if (year == getYear()) {
            this.month = getMonth();
        } else {
            this.month = 12;
        }
        calDays(year, month);
    }

    /**
     * 设置年份
     * 
     * @param year
     */
    public int setYear(int year) {
        int yearIndex = 0;
        if (year != getYear()) {
            this.month = 12;
        } else {
            this.month = getMonth();
        }
        for (int i = getYear(); i > 1950; i--) {
            if (i == year) {
                return yearIndex;
            }
            yearIndex++;
        }
        return yearIndex;
    }

    /**
     * 设置月份
     * 
     * @param year
     * @param month
     * @return
     */
    public int setMonth(int month) {
        int monthIndex = 0;
        calDays(currentYear, month);
        for (int i = 1; i < this.month; i++) {
            if (month == i) {
                return monthIndex;
            } else {
                monthIndex++;
            }
        }
        return monthIndex;
    }

    /**
     * 计算每月多少天
     * 
     * @param month
     * @param leayyear
     */
    public void calDays(int year, int month) {
        boolean leayyear = false;
        if (year % 4 == 0 && year % 100 != 0) {
            leayyear = true;
        } else {
            leayyear = false;
        }
        for (int i = 1; i <= 12; i++) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    this.day = 31;
                    break;
                case 2:
                    if (leayyear) {
                        this.day = 29;
                    } else {
                        this.day = 28;
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    this.day = 30;
                    break;
            }
        }
        if (year == getYear() && month == getMonth()) {
            this.day = getDay();
        }
    }
}
