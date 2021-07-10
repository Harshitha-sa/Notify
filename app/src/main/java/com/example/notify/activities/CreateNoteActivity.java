package com.example.notify.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notify.R;
import com.example.notify.database.NotesDatabase;
import com.example.notify.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class CreateNoteActivity extends AppCompatActivity {

    EditText inputNoteTitle ,inputNoteSubtitle, inputNoteText;
    TextView textDateTime;
    ImageView imageBack;
    ImageView imageSave;
    String selectNoteColor;
    View viewSubtitleIndicator;
    ImageView imageNote;
    String selectedImagePath;
    TextView textWebUrl;
    LinearLayout layoutWebUrl;

    AlertDialog dialogAddUrl;
    AlertDialog dialogDeleteNote;

    private Note alreadyAvailableNote;
    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE=2;
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
        selectedImagePath="";

        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote=(Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemoveWebURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWebUrl.setText(null);
                layoutWebUrl.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectedImagePath="";
            }
        });

        initiMiscellaneous();
        setSubtitleIndicatorColor();
    }

    private void setViewOrUpdateNote(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if(alreadyAvailableNote.getImagePath()!=null&&!alreadyAvailableNote.getImagePath().trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath=alreadyAvailableNote.getImagePath();
        }

        if(alreadyAvailableNote.getWebLink()!=null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
            textWebUrl.setText(alreadyAvailableNote.getWebLink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }
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
        note.setImagePath(selectedImagePath);

        if(layoutWebUrl.getVisibility()==View.VISIBLE){
            note.setWebLink(textWebUrl.getText().toString());
        }

        if(alreadyAvailableNote!=null){
            note.setId(alreadyAvailableNote.getId());
        }



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

        if(alreadyAvailableNote!=null && alreadyAvailableNote.getColor()!=null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#FDBE3B":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4A52":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52Fc":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
            }

            
        }

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else {
                    selectImage();
                }
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddUrlDialog();
            }
        });

        if(alreadyAvailableNote!=null){
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }

    }

    private void showDeleteNoteDialog(){
        if(dialogDeleteNote==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(CreateNoteActivity.this);
            View view=LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote=builder.create();
            if(dialogDeleteNote.getWindow()!=null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void,Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent=new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }

                   new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
    }
    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable=(GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectNoteColor));

    }

    private void selectImage() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION&&grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_SELECT_IMAGE&&resultCode==RESULT_OK){
            if (data!=null){
                Uri selectedImageUri =data.getData();
                if(selectedImageUri!=null){
                    try {
                        InputStream inputStream=getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

                        selectedImagePath=getPathFromUri(selectedImageUri);

                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor=getContentResolver()
                .query(contentUri,null,null,null,null);
        if (cursor==null){
            filePath=contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int index=cursor.getColumnIndex("_data");
            filePath=cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void showAddUrlDialog(){
        if (dialogAddUrl==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(CreateNoteActivity.this);
            View view=LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
            (ViewGroup)findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogAddUrl=builder.create();
            if(dialogAddUrl.getWindow()!=null){
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable((0)));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                        Toast.makeText(CreateNoteActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    }else {
                        textWebUrl.setText(inputURL.getText().toString());
                        layoutWebUrl.setVisibility(View.VISIBLE);
                        dialogAddUrl.dismiss();
                    }
                }
            });
         view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialogAddUrl.dismiss();
             }
         });
        }
        dialogAddUrl.show();
    }
    private void initializefields() {
        imageBack=findViewById(R.id.image_back);
        inputNoteTitle=findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle=findViewById(R.id.inputNoteSubtitle);
        inputNoteText=findViewById(R.id.inputNote);
        textDateTime=findViewById(R.id.textDateTime);
        imageSave=findViewById(R.id.saveImage);
        viewSubtitleIndicator=findViewById(R.id.viewSubtitleIndicator);
        imageNote=findViewById(R.id.imageNote);
        textWebUrl=findViewById(R.id.textWenURL);
        layoutWebUrl=findViewById(R.id.layoutWebUrl);

    }
}