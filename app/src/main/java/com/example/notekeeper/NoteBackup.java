package com.example.notekeeper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class NoteBackup {
    public static final String ALL_COURSES = "ALL_COURSES";
    private static final String TAG = NoteBackup.class.getSimpleName();

    public static void doBackup(Context context, String backupCourseId) {
        String[] columns = {
                NoteKeeperProviderContract.Notes.COLUMN_COURSE_ID,
                NoteKeeperProviderContract.Notes.COLUMN_NOTE_TITLE,
                NoteKeeperProviderContract.Notes.COLUMN_NOTE_TEXT,
        };

        String selection = null;
        String[] selectionArgs = null;
        if(!backupCourseId.equals(ALL_COURSES)) {
            selection = NoteKeeperProviderContract.Notes.COLUMN_COURSE_ID + " = ?";
            selectionArgs = new String[] {backupCourseId};
        }

        Cursor cursor = context.getContentResolver().query(NoteKeeperProviderContract.Notes.CONTENT_URI, columns, selection, selectionArgs, null);
        int courseIdPos = cursor.getColumnIndex(NoteKeeperProviderContract.Notes.COLUMN_COURSE_ID);
        int noteTitlePos = cursor.getColumnIndex(NoteKeeperProviderContract.Notes.COLUMN_NOTE_TITLE);
        int noteTextPos = cursor.getColumnIndex(NoteKeeperProviderContract.Notes.COLUMN_NOTE_TEXT);

        Log.i(TAG, ">>>***   BACKUP START - Thread: " + Thread.currentThread().getId() + "   ***<<<");
        while(cursor.moveToNext()) {
            String courseId = cursor.getString(courseIdPos);
            String noteTitle = cursor.getString(noteTitlePos);
            String noteText = cursor.getString(noteTextPos);

            if(!noteTitle.equals("")) {
                Log.i(TAG, ">>>Backing Up Note<<< " + courseId + "|" + noteTitle + "|" + noteText);
                simulateLongRunningWork();
            }
        }
        Log.i(TAG, ">>>***   BACKUP COMPLETE   ***<<<");
        cursor.close();
    }


    private static void simulateLongRunningWork() {
        try {
            Thread.sleep(1000);
        } catch(Exception ex) {}
    }

}
