package com.henshin.smart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class InfAdapter extends ArrayAdapter<InfModel> {

    //resourceID指定ListView的布局方式
    private int resourceID;

    //重写BrowserAdapter的构造器
    public InfAdapter(Context context, int textViewResourceID , List<InfModel> objects){
        super(context,textViewResourceID,objects);
        resourceID = textViewResourceID;
    }

    //自定义item资源的解析方式
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取当前Browser实例
        InfModel infModel = getItem(position);
        //使用LayoutInfater为子项加载传入的布局
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceID,null);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.list_name);
            viewHolder.title =view.findViewById(R.id.list_title);
            //将ViewHolder存储在View中
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.title.setText(infModel.getTitle());
        viewHolder.name.setText(infModel.getName());
        return view;
    }

    class ViewHolder{
        TextView title;
        TextView name;
    }

}
