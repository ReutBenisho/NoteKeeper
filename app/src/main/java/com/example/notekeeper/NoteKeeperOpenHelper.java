package com.example.notekeeper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
    public static final String DATABSE_NAME = "NoteKeeper.db";
    public static final int DATABASE_VERSION = 2;
    public NoteKeeperOpenHelper(@Nullable Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(NoteInfoEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        sqLiteDatabase.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);

        DatabaseDataWorker worker = new DatabaseDataWorker(sqLiteDatabase);
        worker.insertCourses();
        worker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(oldVersion < 2){
            sqLiteDatabase.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
            sqLiteDatabase.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);
        }
    }
}
