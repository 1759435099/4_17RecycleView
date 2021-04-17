package com.example.txl03;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Person> list;
    public CustomAdapter(Context context, List<Person> list){
        this.context = context;
        this.list = list;
    }
    @Override
    //列表的长度
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    //获取每一行的view
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewItems viewItems = null;
        if (convertView == null){
            viewItems = new ViewItems();
            //获取自定义的布局
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_contact_person,null);
            viewItems.imageView = convertView.findViewById(R.id.imageView);
            viewItems.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(viewItems);
        }else {
            //如果布局不为空，直接使用getTag()获取
            viewItems = (ViewItems) convertView.getTag();
        }
        //为控件设置内容
        viewItems.textView.setText((String) list.get(position).getName());
        return convertView;
    }
    //每一行所使用的控件
    public static class ViewItems{
        private ImageView imageView;
        private TextView textView;
    }
}

