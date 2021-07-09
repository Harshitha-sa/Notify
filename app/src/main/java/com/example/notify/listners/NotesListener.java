package com.example.notify.listners;

import com.example.notify.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note,int position);
}
