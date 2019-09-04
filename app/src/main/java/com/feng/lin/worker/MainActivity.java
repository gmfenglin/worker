package com.feng.lin.worker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.lin.worker.service.ApiService;
import com.feng.lin.worker.ui.AccountActivity;
import com.feng.lin.worker.ui.LandActivity;
import com.feng.lin.worker.ui.MonthActivity;
import com.feng.lin.worker.ui.WorkRecordActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView lvAccount;
    private static MainActivity instance;
    private String[] menuItems = new String[]{"工人管理","工地管理",};
   private List<Map<String,Object>> dataSearch=new ArrayList<>();
    private AccountAdatper accountAdatper;
    private String currentAccountId;

    @Override
    protected void onResume() {
        super.onResume();
        searchData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance=this;
        ApiService.getInstance().init(this);
        lvAccount=this.findViewById(R.id.account_list);
        accountAdatper= new AccountAdatper(this,dataSearch);
        lvAccount.setAdapter(accountAdatper);
        TextView textView=findViewById(R.id.tv_today);
        String today= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        textView.setText("今天："+today);
        searchData();
    }
    public void searchData(){
        dataSearch.clear();

        List<Map<String,Object>> dataResult= ApiService.getInstance().searchAccounts();
        for (Map<String,Object> map :dataResult){
            if(map.get("id").equals(currentAccountId)){
                map.put("last","yes");
            }
            dataSearch.add(map);
        }
        accountAdatper.notifyDataSetChanged();
    }

    public void showMenu(View v){
        showAlertDialog();
    }
    private void showAlertDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setItems(menuItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i==0){
                    Intent intent=new Intent(instance, AccountActivity.class);
                    intent.putExtra("title",menuItems[i]);
                    instance.startActivity(intent);
                }else{
                    Intent intent=new Intent(instance, LandActivity.class);
                    intent.putExtra("title",menuItems[i]);
                    instance.startActivity(intent);
                }
            }
        });
        alertDialog.show();
    }
    public final class ViewHolder {
        public TextView title;
        public TextView text;
        public TextView textBG;
        public TextView textDG;
        public Button bt;
    }
private class AccountAdatper extends BaseAdapter{
    private LayoutInflater mInflater;
    private  AccountAdatper instanceAdapter;
    private List<Map<String,Object>> data;
        public AccountAdatper(Context context,List<Map<String,Object>> data){
            this.mInflater = LayoutInflater.from(context);
            this.data=data;
            instanceAdapter=this;
        }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.account_item, null);
            holder = new ViewHolder();
            /*得到各个控件的对象*/
            holder.title = (TextView) convertView.findViewById(R.id.tv_account_name);
            holder.text = (TextView) convertView.findViewById(R.id.tv_records_year_month);
            holder.textBG = (TextView) convertView.findViewById(R.id.tv_records_bao_gong);
            holder.textDG = (TextView) convertView.findViewById(R.id.tv_records_dian_gong);
            holder.bt = (Button) convertView.findViewById(R.id.btn_record); // to ItemButton


            convertView.setTag(holder); //绑定ViewHolder对象
        }
        else {
            holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAccountId=data.get(position).get("id").toString();
                for (Map<String,Object> map:data){
                    map.put("last","no");
                }
                data.get(position).put("last","yes");
                instanceAdapter.notifyDataSetChanged();
                Intent intent=new Intent(instance, MonthActivity.class);
                intent.putExtra("accountId",data.get(position).get("id").toString());
                intent.putExtra("accountName",data.get(position).get("name").toString());
                instance.startActivity(intent);
            }
        });
        /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
        holder.title.setText(data.get(position).get("name").toString());
        holder.text.setText(data.get(position).get("yearMonth").toString());
        holder.textBG.setText(data.get(position).get("payTypeName").toString()+"   "+data.get(position).get("workCount").toString()+" 小时 ");
        holder.textDG.setText(data.get(position).get("spayTypeName").toString()+"    "+data.get(position).get("sworkCount").toString()+" 小时");
        if("yes".equals(data.get(position).get("last"))){
            holder.title.setTextColor(Color.RED);
        }else{
            holder.title.setTextColor(Color.BLACK);
        }
        /*为Button添加点击事件*/
        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAccountId=data.get(position).get("id").toString();
                for (Map<String,Object> map:data){
                    map.put("last","no");
                }
                data.get(position).put("last","yes");
                instanceAdapter.notifyDataSetChanged();
                Intent intent=new Intent(instance, WorkRecordActivity.class);
                intent.putExtra("accountId",data.get(position).get("id").toString());
                intent.putExtra("accountName",data.get(position).get("name").toString());
                instance.startActivity(intent);
            }
        });

        return convertView;
    }
}

}
