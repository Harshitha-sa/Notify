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
import com.example.notify.listners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {
    ImageView imageAddNoteMain;
    public static final int REQUEST_CODE_ADD_NOTE=1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTES=3;
    RecyclerView notesRecyclerView;
    List<Note> noteList;
    NotesAdapter notesAdapter;
    private int noteClickedPosition=-1;
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
        notesAdapter=new NotesAdapter(noteList,this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTES);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition=position;
        Intent intent=new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode){
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
                  if(requestCode==REQUEST_CODE_SHOW_NOTES){
                      noteList.addAll(notes);
                      notesAdapter.notifyDataSetChanged();
                  }else if(requestCode==REQUEST_CODE_ADD_NOTE){
                      noteList.add(0,notes.get(0));
                      notesAdapter.notifyItemInserted(0);
                      notesRecyclerView.smoothScrollToPosition(0);
                  }else if(requestCode==REQUEST_CODE_UPDATE_NOTE){
                      noteList.remove(noteClickedPosition);
                      noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                      notesAdapter.notifyItemChanged(noteClickedPosition);
                  }
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK){
             getNotes(REQUEST_CODE_ADD_NOTE);
        }else if(requestCode==REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
                if(data!=null){
                    getNotes(REQUEST_CODE_UPDATE_NOTE);
                }
        }
    }

    private void initialize_fields() {
        imageAddNoteMain=findViewById(R.id.imageAddNoteMain);
        notesRecyclerView=findViewById(R.id.notesRecyclerView);
    }
}