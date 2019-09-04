package com.feng.lin.worker.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.lin.worker.R;
import com.feng.lin.worker.entry.Account;
import com.feng.lin.worker.service.ApiService;
import com.feng.lin.worker.utils.SimpleLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class MonthActivity extends AppCompatActivity {
    private CalendarAdapter mAdapter;
    private GridView mIdGvSelectItem;
    private TextView tvMonth;
    private String accountId;
    private static MonthActivity instance;
    private  List<Map<String,Object>> adapterData=new ArrayList<>();
   private List<Map<String,Object>> monthList=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_layout);
        instance=this;
        Intent intent=getIntent();
         accountId= intent.getStringExtra("accountId");
         String accountName=  intent.getStringExtra("accountName");
        TextView tvAccountName= (TextView) findViewById(R.id.tv_month_account_name);
        tvAccountName.setText(accountName);
        Calendar cal = Calendar.getInstance();
       int year= cal.get(Calendar.YEAR);
       int month=cal.get(Calendar.MONTH)+1;
        tvMonth=(TextView) findViewById(R.id.rtv_month);
        mAdapter = new CalendarAdapter(this,year, month, cal.get(Calendar.DAY_OF_MONTH)+"",monthList);
        tvMonth.setText(mAdapter.getShowYear()+"-"+mAdapter.getShowMonth());
        mIdGvSelectItem=(GridView)findViewById(R.id.gl_month);
        mIdGvSelectItem.setAdapter(mAdapter);
        mIdGvSelectItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv=(TextView)view.findViewById(R.id.id_tv_item_select_time_day);
                int day=Integer.valueOf(tv.getText().toString());
                String dayStr=""+day;
                if(day<10){
                    dayStr="0"+day;
                }
               String getDay= mAdapter.getShowYear()+"-"+ mAdapter.getShowMonth()+"-"+dayStr;
                List<Map<String,Object>> data=ApiService.getInstance().searchDay(getDay,accountId);
                if(data.size()>0){
                    showDetail(data,getDay);
                }

            }
        });
        searchMoth();

    }
    public void showDetail(List<Map<String,Object>> data,String day){
        final View view = LayoutInflater.from(this).inflate(R.layout.month_detai,null,false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        ListView listView=(ListView)view.findViewById(R.id.lv_month_detail);
        MonthDeatilAdapter adapter=new MonthDeatilAdapter(adapterData,instance);
        listView.setAdapter(adapter);
        adapterData.clear();
        for (Map<String,Object> map:data){
            adapterData.add(map);
        }
        adapter.notifyDataSetChanged();
        TextView tvDay=(TextView) view.findViewById(R.id.tv_month_detail_day);
        tvDay.setText(day);
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        dialog.dismiss();
                    }
                });
        builder.show();

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
        baoGong.setText(countList.get(0).get("payTypeName")+": "+countList.get(0).get("workCount")+" 小时");
        TextView dianGong=(TextView)findViewById(R.id.ltv_dian_gong);
        dianGong.setText(countList.get(1).get("payTypeName")+": "+countList.get(1).get("workCount")+" 小时");
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
