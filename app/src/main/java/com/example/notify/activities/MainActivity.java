package com.example.notify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.notify.R;

public class MainActivity extends AppCompatActivity {
    ImageView imageAddNoteMain;
    public static final int REQUEST_CODE_ADD_NOTE=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize_fields();
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));
            }
        });
    }

    private void initialize_fields() {
        imageAddNoteMain=findViewById(R.id.imageAddNoteMain);

    }
}