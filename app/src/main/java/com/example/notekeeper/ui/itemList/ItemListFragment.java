package com.example.notekeeper.ui.itemList;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notekeeper.DataManager;
import com.example.notekeeper.NoteInfo;
import com.example.notekeeper.NoteRecyclerAdapter;
import com.example.notekeeper.R;

import java.util.List;

public class ItemListFragment extends Fragment {
    private ItemListViewModel mViewModel;
    private NoteRecyclerAdapter mNoteRecyclerAdapter;
    private RecyclerView mRecyclerNotes;
    public static final int NOTES_MODE = 0;
    public static final int COURSES_MODE = 1;

    public static ItemListFragment newInstance() {
        return new ItemListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        int mode = 0;
        if (getArguments() != null) {
            mode = getArguments().getInt("mode");
        }
        switch(mode){
            case NOTES_MODE:
                mRecyclerNotes = view.findViewById(R.id.list_items);
                break;
            case COURSES_MODE:
                mRecyclerNotes = view.findViewById(R.id.list_items);
                break;
        }
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
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
        final LinearLayoutManager notesLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerNotes.setLayoutManager(notesLayoutManager);

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(getContext(), notes);
        mRecyclerNotes.setAdapter(mNoteRecyclerAdapter);
    }
}