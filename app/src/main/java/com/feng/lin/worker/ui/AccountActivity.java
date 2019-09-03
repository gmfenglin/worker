package com.feng.lin.worker.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.feng.lin.worker.MainActivity;
import com.feng.lin.worker.R;
import com.feng.lin.worker.entry.Account;
import com.feng.lin.worker.service.ApiService;
import com.feng.lin.worker.utils.SimpleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountActivity  extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private List<String> hitList=new ArrayList<>();
    private List<Account> accountListResult=new ArrayList<>();
    private  ArrayAdapter<String> adapter;
    private static AccountActivity instance;
    private ListView lvAccount;
    private AccountAdatper accountAdatper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
        setContentView(R.layout.base_data);
        lvAccount=this.findViewById(R.id.base_data_list);
        accountAdatper=new AccountAdatper(this,accountListResult);
        lvAccount.setAdapter(accountAdatper);
        autoCompleteTextView=this.findViewById(R.id.actv_base_data);
       adapter = new ArrayAdapter<String>(this,

               android.R.layout.simple_list_item_1, hitList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textViewSearch=(TextView) view;
                autoCompleteTextView.setText(textViewSearch.getText());
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hitList.clear();
                SimpleLogger.getInstance().log("hitsearch:");
              List<String>   hitListResult=ApiService.getInstance().hitSearch(s.toString());
              for(String hit:hitListResult){
                  hitList.add(hit);
              }
                SimpleLogger.getInstance().log("hitsearch:"+hitList.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        List<Account> accountList= ApiService.getInstance().search(autoCompleteTextView.getText().toString());
        accountListResult.clear();
        for (Account account:accountList){
            accountListResult.add(account);
        }
        accountAdatper.notifyDataSetChanged();
    }
    public void showAdd(View v){
       final View view = LayoutInflater.from(this).inflate(R.layout.base_data_add_dilog,null,false);
         AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        builder.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        EditText editText=   view.findViewById(R.id.base_data_add_et_name);
                       String text= editText.getText().toString().trim();
                       if(text.length()>0){
                           if(ApiService.getInstance().saveAccount(text)){
                               dialog.dismiss();
                               Toast.makeText(instance,"添加成功", Toast.LENGTH_SHORT).show();
                               List<Account> accountList= ApiService.getInstance().search(autoCompleteTextView.getText().toString());
                               accountListResult.clear();
                               for (Account account:accountList){
                                   accountListResult.add(account);
                               }
                               accountAdatper.notifyDataSetChanged();
                           }else{
                               Toast.makeText(instance,"添加失败", Toast.LENGTH_SHORT).show();
                           }
                       }else {
                           Toast.makeText(instance,"名称不能为空", Toast.LENGTH_SHORT).show();
                       }

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
    public void back(View v){
        this.finish();
    }
    public void showEdit(int id,String name,int status){
        final View view = LayoutInflater.from(this).inflate(R.layout.base_data_add_dilog,null,false);
        EditText editText=(EditText) view.findViewById(R.id.base_data_add_et_name);
        editText.setText(name);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
       final Account account=new Account();
        account.setStatus(status);
        account.setId(id);

        builder.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        EditText editText=   view.findViewById(R.id.base_data_add_et_name);
                        String text= editText.getText().toString().trim();
                        if(text.length()>0){
                            account.setName(text);
                            if(ApiService.getInstance().modifyAccount(account)){
                                dialog.dismiss();
                                Toast.makeText(instance,"修改成功", Toast.LENGTH_SHORT).show();
                                List<Account> accountList= ApiService.getInstance().search(autoCompleteTextView.getText().toString());
                                accountListResult.clear();
                                for (Account account:accountList){
                                    accountListResult.add(account);
                                }
                                accountAdatper.notifyDataSetChanged();
                            }else{
                                Toast.makeText(instance,"修改失败", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(instance,"名称不能为空", Toast.LENGTH_SHORT).show();
                        }

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
    public void search(View v){
       List<Account> accountList= ApiService.getInstance().search(autoCompleteTextView.getText().toString());
        accountListResult.clear();
        for (Account account:accountList){
            accountListResult.add(account);
        }
        accountAdatper.notifyDataSetChanged();
    }
    public final class ViewHolder {
        public TextView name;
        public ImageButton imageButton;
        public Switch status;

    }
    private class AccountAdatper extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<Account> data;
        public AccountAdatper(Context context, List<Account> data){
            this.mInflater = LayoutInflater.from(context);
            this.data=data;
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
                convertView = mInflater.inflate(R.layout.base_data_item, null);
                holder = new ViewHolder();
                /*得到各个控件的对象*/
                holder.name = (TextView) convertView.findViewById(R.id.base_data_item_tv_name);
                holder.status = (Switch) convertView.findViewById(R.id.base_data_item_switch_status);
                holder.imageButton=(ImageButton)convertView.findViewById(R.id.base_data_item_ib_edit);

                convertView.setTag(holder); //绑定ViewHolder对象
            }
            else {
                holder = (ViewHolder) convertView.getTag(); //取出ViewHolder对象
            }

            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.name.setText(data.get(position).getName());

            holder.status.setChecked(data.get(position).getStatus()==0);
            holder.imageButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    showEdit(data.get(position).getId(),data.get(position).getName(),data.get(position).getStatus());
                }
            });
            holder.status.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(data.get(position).getStatus()==0){
                        data.get(position).setStatus(1);
                    }else{
                        data.get(position).setStatus(0);
                    }
                    ApiService.getInstance().modifyAccount(data.get(position));
                    List<Account> accountList= ApiService.getInstance().search(autoCompleteTextView.getText().toString());
                    accountListResult.clear();
                    for (Account account:accountList){
                        accountListResult.add(account);
                    }
                    accountAdatper.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
}
