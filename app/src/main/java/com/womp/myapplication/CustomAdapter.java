package com.womp.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private ArrayList<String> counteryList;
    private Context context;
    private boolean isSelected[];
    private CheckList check;

    public CustomAdapter(Context context, ArrayList<String> counteryList) {
        this.context = context;
        this.counteryList = counteryList;
        isSelected = new boolean[counteryList.size()];
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.row_listview, null);
            holder = new ViewHolder();
            holder.relativeLayout = (RelativeLayout) view.findViewById(R.id.row_relative_layout);
            holder.checkedTextView = (CheckedTextView) view.findViewById(R.id.row_list_checkedtextview);
            holder.checkedImage = (ImageView) view.findViewById(R.id.row_list_checkbox_image);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.checkedTextView.setText(counteryList.get(i));
        holder.relativeLayout.setOnClickListener(view1 ->  {
            boolean flag = holder.checkedTextView.isChecked();
            holder.checkedTextView.setChecked(!flag);
            isSelected[i] = !isSelected[i];


            if(holder.checkedTextView.isChecked()){
                holder.checkedImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checked));
                holder.relativeLayout.setBackgroundColor(Color.parseColor("#F16585"));



            }else{
                holder.relativeLayout.setBackgroundResource(0);
            }

        });

        return view;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return counteryList.size();
    }

    public boolean[] getSelectedFlags () {
        return isSelected;
    }

    private class ViewHolder {
        RelativeLayout relativeLayout;
        CheckedTextView checkedTextView;
        ImageView checkedImage;
    }
}
