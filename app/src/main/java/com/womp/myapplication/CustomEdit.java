package com.womp.myapplication;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.ArrayList;

public class CustomEdit extends BaseAdapter {
    private Context context;
    public static ArrayList<EditModel> editModelArrayList;

    public CustomEdit(Context context) {
        this.context = context;

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

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {



        return view;
    }

    private class ViewHolder {
        protected EditText editText;
    }
}
