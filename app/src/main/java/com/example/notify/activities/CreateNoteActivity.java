package com.example.notify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notify.R;
import com.example.notify.database.NotesDatabase;
import com.example.notify.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    EditText inputNoteTitle ,inputNoteSubtitle, inputNoteText;
    TextView textDateTime;
    ImageView imageBack;
    ImageView imageSave;
    String selectNoteColor;
    View viewSubtitleIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        initializefields();
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        textDateTime.setText(new SimpleDateFormat(
                "EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date())
        );
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
        selectNoteColor="#333333";
        initiMiscellaneous();
        setSubtitleIndicatorColor();
    }

    private void saveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note title cannot be Empty!",Toast.LENGTH_SHORT).show();
            return;
        }else if (inputNoteSubtitle.getText().toString().trim().isEmpty()
                && inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note can't be empty!",Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note= new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectNoteColor);


        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void ,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().inserNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }
    private void initiMiscellaneous() {
        final LinearLayout layoutMiscellaneous=findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior= BottomSheetBehavior
                .from(layoutMiscellaneous);
        layoutMiscellaneous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(bottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
               }else{
                   bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
               }
            }
        });
        final ImageView imageColo1=layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColo2=layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColo3=layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColo4=layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColo5=layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              selectNoteColor="#333333";
              imageColo1.setImageResource(R.drawable.ic_done);
              imageColo2.setImageResource(0);
              imageColo3.setImageResource(0);
              imageColo4.setImageResource(0);
              imageColo5.setImageResource(0);
              setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor="#FDBE3B";
                imageColo1.setImageResource(0);
                imageColo2.setImageResource(R.drawable.ic_done);
                imageColo3.setImageResource(0);
                imageColo4.setImageResource(0);
                imageColo5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor="#FF4A52";
                imageColo1.setImageResource(0);
                imageColo2.setImageResource(0);
                imageColo3.setImageResource(R.drawable.ic_done);
                imageColo4.setImageResource(0);
                imageColo5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor="#3A52Fc";
                imageColo1.setImageResource(0);
                imageColo2.setImageResource(0);
                imageColo3.setImageResource(0);
                imageColo4.setImageResource(R.drawable.ic_done);
                imageColo5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor="#000000";
                imageColo1.setImageResource(0);
                imageColo2.setImageResource(0);
                imageColo3.setImageResource(0);
                imageColo4.setImageResource(0);
                imageColo5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });







    }
    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable=(GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectNoteColor));

    }
    private void initializefields() {
        imageBack=findViewById(R.id.image_back);
        inputNoteTitle=findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle=findViewById(R.id.inputNoteSubtitle);
        inputNoteText=findViewById(R.id.inputNote);
        textDateTime=findViewById(R.id.textDateTime);
        imageSave=findViewById(R.id.saveImage);
        viewSubtitleIndicator=findViewById(R.id.viewSubtitleIndicator);
    }
}