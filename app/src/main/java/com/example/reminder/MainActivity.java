package com.example.reminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.print.PrinterId;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private  NoteDatabase dbHelper;

    private Context context = this;
    final String TAG = "tag";
    FloatingActionButton btn;
    FloatingActionButton deleteAll;
//    TextView tv;
    private ListView lv;
    private NoteAdapter adapter;
    private List<Note> noteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (FloatingActionButton) findViewById(R.id.fab);
        deleteAll = (FloatingActionButton) findViewById(R.id.deleteAll);
//        tv = findViewById(R.id.tv);
        lv = findViewById(R.id.lv);
        adapter = new NoteAdapter(getApplicationContext(), noteList);
        refreshListView();
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode",4); //create new note mode
                startActivityForResult(intent,0);
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Sure to delete ALL?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper = new NoteDatabase(context);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.delete("notes",null,null);
                                db.execSQL("update sqlite_sequence set seq = 0 where name = 'notes'");
                                refreshListView();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
    }

    //accept startActivityForResult result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        int returnMode;  //-1 nothing happen, 0 create new note, 1 edit content
        long noteId;
        returnMode = data.getExtras().getInt("mode",-1);
        noteId = data.getExtras().getLong("id", 0);

/*        String content = data.getStringExtra("content");
        String time = data.getStringExtra("time");
        int tag = data.getExtras().getInt("tag",1);
        Note note = new Note(content, time, tag);
        CRUD op = new CRUD(context);
        op.open();
        op.addNote(note);
        op.close();
        refreshListView();*/

        if(returnMode == 1) { //update current node
            String content = data.getStringExtra("content");
            String time = data.getStringExtra("time");
            int tag = data.getExtras().getInt("tag",1);

            Note newNote = new Note(content, time, tag);
            newNote.setId(noteId);
            CRUD op = new CRUD(context);
            op.open();
            op.updateNote(newNote);
            op.close();
        } else if (returnMode == 0){ // create new note
            String content = data.getStringExtra("content");
            String time = data.getStringExtra("time");
            int tag = data.getExtras().getInt("tag",1);

            Note newNote = new Note(content, time, tag);
            CRUD op = new CRUD(context);
            op.open();
            op.addNote(newNote);
            op.close();
        } else if (returnMode == 2) { //delete
            Note curNote = new Note();
            curNote.setId(noteId);
            CRUD op = new CRUD(context);
            op.open();
            op.deleteNote(curNote);
            op.close();
        }
        refreshListView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshListView() {
        CRUD op = new CRUD(context);
        op.open();
        if(noteList.size() > 0) noteList.clear();
        noteList.addAll(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.lv:
                Note curNote = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("content", curNote.getContent());
                intent.putExtra("id", curNote.getId());
                intent.putExtra("time", curNote.getTime());
                intent.putExtra("mode", 3); //Mode of "click to edit"
                intent.putExtra("tag", curNote.getTag());
                startActivityForResult(intent, 1); //collect data from edit
                Log.d(TAG,"onItemClick: " + position);
                break;
        }
    }
}
