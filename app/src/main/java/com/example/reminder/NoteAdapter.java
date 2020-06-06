package com.example.reminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends BaseAdapter implements Filterable {
    private Context mContext;

    private List<Note> backupList;
    private List<Note> noteList;

    public NoteAdapter(Context mContext, List<Note> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;
        backupList = noteList;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        View v = View.inflate(mContext, R.layout.note_layout, null);
        TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
        TextView tv_time = (TextView) v.findViewById(R.id.tv_time);

        //set text for TextView
        String allText = noteList.get(position).getContent();
        // theme
/*        if(sharedPreferences.getBoolean("noteTitle", true))
            tv_content.setText(allText.split("\n")[0]);
        else */
        tv_content.setText(allText);
        tv_time.setText(noteList.get(position).getTime());

//        //save note id to tag
        v.setTag(noteList.get(position).getId());

        return v;
    }



    @Override
    public Filter getFilter() {
        return new MyFilter();
    }

    class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    }

}
