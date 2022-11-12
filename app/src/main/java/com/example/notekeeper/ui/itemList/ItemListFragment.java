package com.example.notekeeper.ui.itemList;

import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notekeeper.CourseInfo;
import com.example.notekeeper.CourseRecyclerAdapter;
import com.example.notekeeper.DataManager;
import com.example.notekeeper.MainActivity;
import com.example.notekeeper.NoteInfo;
import com.example.notekeeper.NoteKeeperDatabaseContract;
import com.example.notekeeper.NoteKeeperOpenHelper;
import com.example.notekeeper.NoteRecyclerAdapter;
import com.example.notekeeper.R;

import java.util.List;

public class ItemListFragment extends Fragment {
    private ItemListViewModel mViewModel;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private CourseRecyclerAdapter mCourseRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    public static final int NOTES_MODE = 0;
    public static final int COURSES_MODE = 1;
    private LinearLayoutManager mNotesLayoutManager;
    private GridLayoutManager mCoursesLayoutManager;
    private int mMode;
    private NoteKeeperOpenHelper mDbOpenHelper;

    public static ItemListFragment newInstance() {
        return new ItemListFragment();
    }

    @Override
    public void onDestroyView() {
        mDbOpenHelper.close();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        container.removeAllViews();
        mDbOpenHelper = new NoteKeeperOpenHelper(getActivity());
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        mMode = 0;
        if (getArguments() != null) {
            mMode = getArguments().getInt("mode");
        }
        mRecyclerItems = view.findViewById(R.id.list_items);
        mDbOpenHelper.getReadableDatabase();
        initializeDisplayContent();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ItemListViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] noteColumns = {
                NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_COURSE_ID,
                NoteKeeperDatabaseContract.NoteInfoEntry._ID};
        String noteOrderBy = NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteKeeperDatabaseContract.NoteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor noteCursor = db.query(NoteKeeperDatabaseContract.NoteInfoEntry.TABLE_NAME, noteColumns,
                null, null, null, null, noteOrderBy);
        mNoteRecyclerAdapter.changeCursor(noteCursor);
    }

    private void initializeDisplayContent() {
        DataManager.loadFromDatabase(mDbOpenHelper);
        mNotesLayoutManager = new LinearLayoutManager(getContext());
        mCoursesLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.course_grid_span));

        mNoteRecyclerAdapter = new NoteRecyclerAdapter(getContext(), null);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        mCourseRecyclerAdapter = new CourseRecyclerAdapter(getContext(), courses);

        switch(mMode){
            case NOTES_MODE:
                displayNotes();
                break;
            case COURSES_MODE:
                displayCourses();
                break;
        }
    }

    private void displayNotes() {
        mRecyclerItems.setLayoutManager(mNotesLayoutManager);
        mRecyclerItems.setAdapter(mNoteRecyclerAdapter);

        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

    }

    private void displayCourses(){
        mRecyclerItems.setLayoutManager(mCoursesLayoutManager);
        mRecyclerItems.setAdapter(mCourseRecyclerAdapter);
    }
}