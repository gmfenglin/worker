package com.feng.lin.worker.ui;

import android.content.Intent;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.feng.lin.worker.R;
import com.feng.lin.worker.service.ApiService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class MonthActivity extends AppCompatActivity {
    private CalendarAdapter mAdapter;
    private GridView mIdGvSelectItem;
    private TextView tvMonth;
    private String accountId;
   private List<Map<String,Object>> monthList=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_layout);
        Intent intent=getIntent();
         accountId= intent.getStringExtra("accountId");
        Calendar cal = Calendar.getInstance();
       int year= cal.get(Calendar.YEAR);
       int month=cal.get(Calendar.MONTH)+1;
        tvMonth=(TextView) findViewById(R.id.rtv_month);
        mAdapter = new CalendarAdapter(this,year, month, cal.get(Calendar.DAY_OF_MONTH)+"",monthList);
        tvMonth.setText(mAdapter.getShowYear()+"-"+mAdapter.getShowMonth());
        mIdGvSelectItem=(GridView)findViewById(R.id.gl_month);
        mIdGvSelectItem.setAdapter(mAdapter);
        searchMoth();

    }
    public void searchMoth(){
        Map<String, List<Map<String,Object>>> result= ApiService.getInstance().searchByAccountAndMonth(mAdapter.getShowYear()+"-"+mAdapter.getShowMonth(),accountId);
        List<Map<String,Object>> monthListTemp=result.get("monthList");
        monthList.clear();
        for (Map<String,Object> map:monthListTemp){
            map.put("did",0);
            monthList.add(map);
        }
        List<Map<String,Object>> countList=result.get("countList");
        TextView baoGong=(TextView)findViewById(R.id.ltv_bao_gong);
        baoGong.setText(countList.get(0).get("payTypeName")+": "+countList.get(0).get("workCount")+" 天");
        TextView dianGong=(TextView)findViewById(R.id.ltv_dian_gong);
        dianGong.setText(countList.get(1).get("payTypeName")+": "+countList.get(1).get("workCount")+" 天");
    }
    public void preMonth(View v){
        mAdapter.lessMonth();
        mAdapter.notifyDataSetChanged();
        tvMonth.setText(mAdapter.getShowYear()+"-"+mAdapter.getShowMonth());
        searchMoth();
    }
    public  void nextMoth(View v){
        mAdapter.addMonth();
        mAdapter.notifyDataSetChanged();
        tvMonth.setText(mAdapter.getShowYear()+"-"+mAdapter.getShowMonth());
        searchMoth();
    }
    public void back(View v){
        finish();
    }

}
