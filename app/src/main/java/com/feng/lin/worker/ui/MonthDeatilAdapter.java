package com.feng.lin.worker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.feng.lin.worker.R;
import com.feng.lin.worker.utils.SimpleLogger;

import java.util.List;
import java.util.Map;

public class MonthDeatilAdapter  extends BaseAdapter {
    private List<Map<String,Object>> data;
    private Context context;

    public MonthDeatilAdapter(List<Map<String, Object>> data, Context context) {
        this.data = data;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder myViewHolder = null;
        if (convertView == null || convertView.getTag() == null) {
            myViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.month_detail_item, null);
            myViewHolder.tvLand=convertView.findViewById(R.id.tv_month_detail_item_time_land);
            myViewHolder.tvPayType=convertView.findViewById(R.id.tv_month_detail_item_pay_type);
            myViewHolder.tvTimeType=convertView.findViewById(R.id.tv_month_detail_item_time_type);
            myViewHolder.tvWorkCount=convertView.findViewById(R.id.tv_month_detail_item_work_count);
            myViewHolder.tvZone=convertView.findViewById(R.id.tv_month_detail_item_time_zone);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }

        myViewHolder.tvLand.setText("工地："+data.get(position).get("landName").toString());
        myViewHolder.tvPayType.setText("工资类型："+data.get(position).get("payTypeName").toString());
        myViewHolder.tvTimeType.setText("时间："+data.get(position).get("timeTypeName").toString());
        myViewHolder.tvWorkCount.setText("工时："+data.get(position).get("work_count").toString()+" 小时");
        myViewHolder.tvZone.setText("时段："+data.get(position).get("timeZoneName").toString());
        return convertView;
    }
    static class ViewHolder {



        TextView tvZone;
        TextView tvTimeType;
        TextView tvPayType;
        TextView tvLand;
        TextView tvWorkCount;


    }
}
