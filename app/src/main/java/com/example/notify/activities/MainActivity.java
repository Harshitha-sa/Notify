package com.example.notify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.notify.R;
import com.example.notify.adapters.NotesAdapter;
import com.example.notify.database.NotesDatabase;
import com.example.notify.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imageAddNoteMain;
    public static final int REQUEST_CODE_ADD_NOTE=1;
    RecyclerView notesRecyclerView;
    List<Note> noteList;
    NotesAdapter notesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize_fields();
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,
                        CreateNoteActivity.class),REQUEST_CODE_ADD_NOTE);
//                Intent intent=new Intent(MainActivity.this,CreateNoteActivity.class);
//                startActivity(intent,REQUEST_CODE_ADD_NOTE);
            }
        });
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );
        noteList=new ArrayList<>();
        notesAdapter=new NotesAdapter(noteList);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes();
    }

    private void getNotes(){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void ,Void , List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
//                Log.d("MY_NOTES",notes.toString());
                if(noteList.size()==0){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else{
                   noteList.add(0,notes.get(0));
                   notesAdapter.notifyItemInserted(0);

                }
                notesRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK){
             getNotes();
        }
    }

    private void initialize_fields() {
        imageAddNoteMain=findViewById(R.id.imageAddNoteMain);
        notesRecyclerView=findViewById(R.id.notesRecyclerView);
    }
}