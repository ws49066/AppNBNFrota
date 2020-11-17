package com.womp.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class CustomAdapterEdit extends BaseAdapter {

    private ArrayList<String> counteryList;
    public   ArrayList<EditModel> editModelArrayList;
    public ArrayList<String> editList;
    private Context context;

    public CustomAdapterEdit(Context context, ArrayList<String> counteryList, ArrayList<EditModel> editModelArrayList) {
        this.context = context;
        this.counteryList = counteryList;
        this.editModelArrayList = editModelArrayList;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.row_edit, null);
            holder = new ViewHolder();
            holder.relativeLayout = (RelativeLayout) view.findViewById(R.id.row_relative_layout_edit);
            holder.TextView = (TextView) view.findViewById(R.id.rowedit);
            holder.editText = (EditText) view.findViewById(R.id.row_list_checkbox_image_edit);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.TextView.setText(counteryList.get(position));

        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editModelArrayList.get(position).setEditTextValue(holder.editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return editModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return editModelArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        RelativeLayout relativeLayout;
        TextView TextView;
        EditText editText;
    }


}
