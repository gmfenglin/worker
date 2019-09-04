package com.feng.lin.worker.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.lin.worker.R;

import com.feng.lin.worker.entry.Account;
import com.feng.lin.worker.entry.Land;
import com.feng.lin.worker.entry.PayType;
import com.feng.lin.worker.entry.TimeType;
import com.feng.lin.worker.entry.TimeZone;
import com.feng.lin.worker.entry.WorkRecord;
import com.feng.lin.worker.entry.WorkTime;
import com.feng.lin.worker.service.ApiService;
import com.feng.lin.worker.utils.SimpleLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkRecordActivity extends AppCompatActivity {
    private static WorkRecordActivity instance;
    private WorkRecord workRecord=new WorkRecord();
    private TimeType timeType=new TimeType();
    private TextView workDayView;
    private View [] panels=new View[3];
    private Spinner oneACTV;
    private ArrayAdapter<String> oneAdapter;
    private List<String> oneList=new ArrayList<>();
    private Spinner part_SW_ACTV;
    private List<String> sList=new ArrayList<>();
    private Spinner part_XW_ACTV;
    private List<String> xList=new ArrayList<>();
    private Spinner part_WS_ACTV;
    private List<String> wList=new ArrayList<>();
    private Map<String,Integer> onemap=new HashMap<>();



    private CheckBox [] timeZoneCheckBoxs=new CheckBox[3];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_record);
        instance=this;
        Intent intent=getIntent();
       String accountId= intent.getStringExtra("accountId");
        String accountName= intent.getStringExtra("accountName");
       TextView tvAccountName=(TextView) findViewById(R.id.tv_record_accountName);
        tvAccountName.setText(accountName);
        Account account=new Account();
        account.setId(Integer.valueOf(accountId));

        workRecord.setAccount(account);
        SimpleLogger.getInstance().log(accountId+":accountId");
        workDayView= (TextView) findViewById(R.id.tv_tv_day);
       String today= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        workDayView.setText(today);
        workRecord.setTimeType(timeType);

        timeType.setId(3);
        timeType.setName("休息");
        panels[0]=findViewById(R.id.fll_one);
        panels[1]=findViewById(R.id.fill_part);
        panels[2]=findViewById(R.id.fill_0);
        timeZoneCheckBoxs[0]=findViewById(R.id.cb_sw);
        timeZoneCheckBoxs[1]=findViewById(R.id.cb_xw);
        timeZoneCheckBoxs[2]=findViewById(R.id.cb_ws);
        RadioGroup timeTypeGroup=(RadioGroup) findViewById(R.id.rg_time_type);
        timeTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SimpleLogger.getInstance().log(checkedId+":checkId");
                for (View v:panels){
                    v.setVisibility(View.GONE);
                }
                switch (checkedId){
                    case R.id.rb_one:{
                        timeType.setId(1);
                        timeType.setName("整天");
                        panels[0].setVisibility(View.VISIBLE);
                        break;
                    }
                    case R.id.rb_part:{
                        timeType.setId(2);
                        timeType.setName("时段");
                        panels[1].setVisibility(View.VISIBLE);
                        break;
                    }
                    case R.id.rb_none:{
                        timeType.setId(3);
                        timeType.setName("休息");
                        panels[2].setVisibility(View.VISIBLE);
                        break;
                    }
                }

            }
        });
        oneACTV=(Spinner)findViewById(R.id.fr_actv_land);
        oneAdapter= new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, oneList);
        oneACTV.setAdapter(oneAdapter);
        part_SW_ACTV=(Spinner)findViewById(R.id.s_ractv_land);
        part_SW_ACTV.setAdapter(oneAdapter);
        part_XW_ACTV=(Spinner)findViewById(R.id.x_ractv_land);
        part_XW_ACTV.setAdapter(oneAdapter);
        part_WS_ACTV=(Spinner)findViewById(R.id.w_ractv_land);
        part_WS_ACTV.setAdapter(oneAdapter);
        hitLand();

}
    public void back(View v){
        this.finish();
    }

    public void hitLand(){
        Map<String,Integer> dataMap= ApiService.getInstance().hitSearchEnableLand();

        oneList.clear();
       onemap.clear();
       Set<String> keySet= dataMap.keySet();
       for (String name:keySet){
           onemap.put(name,dataMap.get(name));
           oneList.add(name);
       }
        oneAdapter.notifyDataSetChanged();
    }
    public void showChooseDay(View v){

        final View view = LayoutInflater.from(this).inflate(R.layout.choose_day_dilog,null,false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        final DatePicker datePicker=(DatePicker) view.findViewById(R.id.dp_choose_day);
        builder.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        int month=(datePicker.getMonth()+1);
                        String day=    ""+datePicker.getYear();
                        if(month<10){
                            day=day+"-0"+month;
                        }else{
                            day=day+"-"+month;
                        }
                        int n=datePicker.getDayOfMonth();
                        if(n<10){
                            day=day+"-0"+n;
                        }else{
                            day=day+"-"+n;
                        }
                        workDayView.setText(day);
                    }
                });
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
    public void saveRecord(View v){
       List<WorkTime> workTimeList=new ArrayList<>();
        workRecord.setId(System.currentTimeMillis());
       workTimeList.clear();
       workRecord.setWorkTimeList(workTimeList);
        workRecord.setWorkDay(workDayView.getText().toString());
        switch (workRecord.getTimeType().getId()){
            case 1:{

                String landName=oneACTV.getSelectedItem().toString().trim();
                if(landName.length()<=0){
                    Toast.makeText(instance,"请输入选择工地", Toast.LENGTH_SHORT).show();
                    return;
                }
                TimeZone timeZone=new TimeZone();
                timeZone.setId(4);
                timeZone.setName("全天");

                WorkTime workTime=new WorkTime();
                RadioGroup payTypeRG=(RadioGroup) findViewById(R.id.rg_one_pay_type);
                int checkedId=  payTypeRG.getCheckedRadioButtonId();
                PayType payType=new PayType();

                if(checkedId==R.id.rg_one_rb_bg){
                    payType.setId(1);
                    payType.setName("包工");
                }else if(checkedId==R.id.rg_one_rb_dg){
                    payType.setId(2);
                    payType.setName("点工");
                }else{
                    Toast.makeText(instance,"请选择（点工/包工）", Toast.LENGTH_SHORT).show();
                    return;
                }
                workTime.setPayType(payType);
                Land land=new Land();

                land.setId(onemap.get(landName));
                land.setName(landName);
                land.setStatus(0);
                workTime.setLand(land);
                workTime.setTimeZone(timeZone);
                workTime.setWorkCount(1);
                workTime.setWorkRecord(workRecord);
                workTimeList.add(workTime);

                int flag=ApiService.getInstance().recordWork(workRecord);
                if(flag==1){
                    Toast.makeText(instance,"打卡成功", Toast.LENGTH_SHORT).show();
                }else if(flag==0){
                    Toast.makeText(instance,"打卡失败", Toast.LENGTH_SHORT).show();
                }else if(flag==-1){
                    Toast.makeText(instance,"今天已经打过卡", Toast.LENGTH_SHORT).show();
                }
                break;}
            case 2:{
                boolean flag=false;
                for (int i=0;i<timeZoneCheckBoxs.length;i++){
                    CheckBox cb=timeZoneCheckBoxs[i];
                    if(cb.isChecked()){
                        flag=true;
                       if(i==0){
                           WorkTime workTime=new WorkTime();
                           workTimeList.add(workTime);
                           workTime.setWorkRecord(workRecord);
                           TimeZone timeZone=new TimeZone();
                           timeZone.setId(1);
                           timeZone.setName("上午");
                           workTime.setTimeZone(timeZone);
                           String count=((EditText)findViewById(R.id.s_ret_count)).getText().toString().trim();
                           try{
                               double workCount=Double.valueOf(count);
                               workTime.setWorkCount(workCount);
                           }catch (Exception e){
                               Toast.makeText(instance,"请输入工天）", Toast.LENGTH_SHORT).show();
                               return;
                           }

                           RadioGroup payTypeRG=(RadioGroup) findViewById(R.id.s_rg_pay_type);
                           int checkedId=  payTypeRG.getCheckedRadioButtonId();
                           PayType payType=new PayType();
                           if(checkedId==R.id.s_rg_rb_bg){
                               payType.setId(1);
                               payType.setName("包工");
                           }else if(checkedId==R.id.s_rg_rb_dg){
                               payType.setId(2);
                               payType.setName("点工");
                           }else{
                               Toast.makeText(instance,"请选择（点工/包工）", Toast.LENGTH_SHORT).show();
                               return;
                           }
                           workTime.setPayType(payType);
                           Land land=new Land();
                           String landName=part_SW_ACTV.getSelectedItem().toString().trim();
                           if(landName.length()<=0){
                               Toast.makeText(instance,"请输入选择工地", Toast.LENGTH_SHORT).show();
                               return;
                           }
                           land.setId(onemap.get(landName));
                           land.setName(landName);
                           land.setStatus(0);
                           workTime.setLand(land);
                       }else if(i==1){
                           WorkTime workTime=new WorkTime();
                           workTimeList.add(workTime);
                           workTime.setWorkRecord(workRecord);
                           TimeZone timeZone=new TimeZone();
                           timeZone.setId(2);
                           timeZone.setName("下午");
                           workTime.setTimeZone(timeZone);
                           String count=((EditText)findViewById(R.id.x_ret_count)).getText().toString().trim();
                           try{
                               double workCount=Double.valueOf(count);
                               workTime.setWorkCount(workCount);
                           }catch (Exception e){
                               Toast.makeText(instance,"请输入工天）", Toast.LENGTH_SHORT).show();
                               return;
                           }

                           RadioGroup payTypeRG=(RadioGroup) findViewById(R.id.x_rg_pay_type);
                           int checkedId=  payTypeRG.getCheckedRadioButtonId();
                           PayType payType=new PayType();
                           if(checkedId==R.id.x_rg_rb_bg){
                               payType.setId(1);
                               payType.setName("包工");
                           }else if(checkedId==R.id.x_rg_rb_dg){
                               payType.setId(2);
                               payType.setName("点工");
                           }else{
                               Toast.makeText(instance,"请选择（点工/包工）", Toast.LENGTH_SHORT).show();
                               return;
                           }
                           workTime.setPayType(payType);
                           Land land=new Land();

                           String landName=part_XW_ACTV.getSelectedItem().toString().trim();
                           if(landName.length()<=0){
                               Toast.makeText(instance,"请输入选择工地", Toast.LENGTH_SHORT).show();
                               return;
                           }
                           land.setId(onemap.get(landName));
                           land.setName(landName);
                           land.setStatus(0);
                           workTime.setLand(land);
                       }else if(i==2){
                           WorkTime workTime=new WorkTime();
                           workTimeList.add(workTime);
                           workTime.setWorkRecord(workRecord);
                           TimeZone timeZone=new TimeZone();
                           timeZone.setId(3);
                           timeZone.setName("晚上");
                           workTime.setTimeZone(timeZone);
                           String count=((EditText)findViewById(R.id.w_ret_count)).getText().toString().trim();
                           try{
                               double workCount=Double.valueOf(count);
                               workTime.setWorkCount(workCount);
                           }catch (Exception e){
                               Toast.makeText(instance,"请输入工天）", Toast.LENGTH_SHORT).show();
                               return;
                           }
                           RadioGroup payTypeRG=(RadioGroup) findViewById(R.id.w_rg_pay_type);
                           int checkedId=  payTypeRG.getCheckedRadioButtonId();
                           PayType payType=new PayType();
                           if(checkedId==R.id.w_rg_rb_bg){
                               payType.setId(1);
                               payType.setName("包工");
                           }else if(checkedId==R.id.w_rg_rb_dg){
                               payType.setId(2);
                               payType.setName("点工");
                           }else{
                               Toast.makeText(instance,"请选择（点工/包工）", Toast.LENGTH_SHORT).show();
                               return;
                           }
                           workTime.setPayType(payType);
                           Land land=new Land();
                           String landName=part_WS_ACTV.getSelectedItem().toString().trim();
                           if(landName.length()<=0){
                               Toast.makeText(instance,"请输入选择工地", Toast.LENGTH_SHORT).show();
                               return;
                           }
                           land.setId(onemap.get(landName));
                           land.setName(landName);
                           land.setStatus(0);
                           workTime.setLand(land);
                       }
                    }
                }
                if(!flag){
                    Toast.makeText(instance,"请选择（上午/下午/晚上）", Toast.LENGTH_SHORT).show();
                    return;
                }

                int flagI=ApiService.getInstance().recordWork(workRecord);
                if(flagI==1){
                    Toast.makeText(instance,"打卡成功", Toast.LENGTH_SHORT).show();
                }else if(flagI==0){
                    Toast.makeText(instance,"打卡失败", Toast.LENGTH_SHORT).show();
                }else if(flagI==-1){
                    Toast.makeText(instance,"今天已经打过卡", Toast.LENGTH_SHORT).show();
                }
                break;}
            case 3:{

                int flagI=ApiService.getInstance().recordWork(workRecord);
                if(flagI==1){
                    Toast.makeText(instance,"打卡成功", Toast.LENGTH_SHORT).show();
                }else if(flagI==0){
                    Toast.makeText(instance,"打卡失败", Toast.LENGTH_SHORT).show();
                }else if(flagI==-1){
                    Toast.makeText(instance,"今天已经打过卡", Toast.LENGTH_SHORT).show();
                }
                break;}
        }

    }
}
