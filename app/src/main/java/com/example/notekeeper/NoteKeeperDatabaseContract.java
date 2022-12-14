package com.example.notekeeper;

import android.provider.BaseColumns;

public final class NoteKeeperDatabaseContract {
    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_INTEGER = "INTEGER";
    public static final String NOT_NULL = "NOT NULL";
    public static final String UNIQUE = "UNIQUE";
    public static final String PRIMARY_KEY = "PRIMARY KEY";


    private NoteKeeperDatabaseContract(){}; //make non-creatable

    public static final class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

        // CREATE INDEX course_info_index1 ON course_info (course_title)
        public static final String SQL_CREATE_INDEX1 =
                String.format("CREATE INDEX %s ON %s (%s)",
                        INDEX1,
                        TABLE_NAME,
                        COLUMN_COURSE_TITLE);

        // CREATE TABLE course_info (course_id), course_title
        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s %s, %s %s, %s %s)",
                        TABLE_NAME,
                        _ID, TYPE_INTEGER + " " + PRIMARY_KEY,
                        COLUMN_COURSE_ID, TYPE_TEXT + " " + UNIQUE + " " + NOT_NULL,
                        COLUMN_COURSE_TITLE, TYPE_TEXT + " " + NOT_NULL);
    }

    public static final class NoteInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String INDEX1 = TABLE_NAME + "_index1";


        public static final String getQName(String columnName){
            return TABLE_NAME + "." + columnName;
        }

        public static final String SQL_CREATE_TABLE =
                String.format("CREATE TABLE %s (%s %s, %s %s, %s %s, %s %s)",
                        TABLE_NAME,
                        _ID, TYPE_INTEGER + " " + PRIMARY_KEY,
                        COLUMN_NOTE_TITLE, TYPE_TEXT + " " + NOT_NULL,
                        COLUMN_NOTE_TEXT, TYPE_TEXT,
                        COLUMN_COURSE_ID, TYPE_TEXT + " " + NOT_NULL);

        public static final String SQL_CREATE_INDEX1 =
                String.format("CREATE INDEX %s ON %s (%s)",
                        INDEX1,
                        TABLE_NAME,
                        COLUMN_NOTE_TITLE);


    }
}
