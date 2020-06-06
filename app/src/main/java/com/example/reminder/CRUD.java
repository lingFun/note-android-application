package com.example.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

//create-read-update-delete
public class CRUD {
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private static final String[] columns = {
            NoteDatabase.ID,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME,
            NoteDatabase.MODE
    };

    public  CRUD(Context context) {
        dbHandler = new NoteDatabase(context);
    }

    public void open() {
        db = dbHandler.getWritableDatabase();
    }

    public void close() {
        dbHandler.close();
    }

    //add Note object to NoteDatabase  *important*
    public Note addNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
        contentValues.put(NoteDatabase.MODE, note.getTag());
        int insertId = (int) db.insert(NoteDatabase.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        return note;
    }

    //get Note from NoteDatabase using cursor index
    public Note getNote(long id) {
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, NoteDatabase.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if(cursor != null) cursor.moveToFirst();
        Note e = new Note(cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        return e;
    }

    //get all note from NoteDatabase by List<Note>
    public List<Note> getAllNotes() {
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, null, null, null, null, null);

        List<Note> notes = new ArrayList<>();
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME)));
                note.setTag(cursor.getInt(cursor.getColumnIndex(NoteDatabase.MODE)));
                notes.add(note);
            }
        }
        return notes;
    }

    //update the info of an existing Note
    public int updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.CONTENT, note.getContent());
        values.put(NoteDatabase.TIME, note.getTime());
        values.put(NoteDatabase.MODE, note.getTag());

        return db.update(NoteDatabase.TABLE_NAME, values, NoteDatabase.ID + "=?",
                new String[] { String.valueOf(note.getId())});
    }

    //remove a Note according to ID value
    public void deleteNote(Note note) {
        db.delete(NoteDatabase.TABLE_NAME, NoteDatabase.ID + "=" + note.getId(), null);
    }
}
