package com.example.facharztkatalog.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.facharztkatalog.R;

import org.apache.poi.ss.formula.functions.T;

import java.util.LinkedList;
import java.util.List;

/**
 *  ListViewAdapter without duplicates.
 * @param <T>
 */
public class ListAdapter<T> extends BaseAdapter {

    private List<T> items;
    protected Context context;

    public ListAdapter(Context context) {
        this.context = context;
        this.items = new LinkedList<>();
    }

    public void removeItem(int position) {
        items.remove(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = li.inflate(R.layout.layout_editcase_procedure_entry, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.editCase_procedureName))
                .setText(getItem(position).toString());

        return convertView;
    }

    public void addItem(T item) {
        if(items.contains(item)) {
            return;
        }
        items.add(item);
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return items;
    }
    public void setItems(List<T> items) {
        this.items = items;
    }
}
