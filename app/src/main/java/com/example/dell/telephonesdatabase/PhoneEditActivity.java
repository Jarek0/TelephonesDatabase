package com.example.dell.telephonesdatabase;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.telephonesdatabase.database.DatabaseContentProvider;
import com.example.dell.telephonesdatabase.database.DatabaseHelper;

import java.net.URL;

/**
 * Created by Dell on 2017-04-23.
 */

public class PhoneEditActivity extends AppCompatActivity {
    private long rowId;
    private EditText producerInput;
    private EditText modelInput;
    private EditText androidInput;
    private EditText uriInput;
    private Button wwwButton;
    private Button cancelButton;
    private Button saveButton;
    private final int MODE_ADD=0;
    private final int MODE_EDIT=1;
    private int MODE=MODE_ADD;
    private long editingItemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_edit);
        producerInput= (EditText) findViewById(R.id.producerInput);
        modelInput= (EditText) findViewById(R.id.phoneModelInput);
        androidInput= (EditText) findViewById(R.id.androidVersionInput);
        uriInput= (EditText) findViewById(R.id.urlInput);
        wwwButton= (Button) findViewById(R.id.urlButton);
        cancelButton= (Button) findViewById(R.id.cancelButton);
        saveButton= (Button) findViewById(R.id.saveButton);
        attachClickListeners();
        setMode();

        if(MODE==MODE_EDIT) loadEditingPhoneInformation();
    }

    private void loadEditingPhoneInformation(){
        String[] columnNames=new String[]{DatabaseHelper.ID,DatabaseHelper.ANDROID_VERSION_COLUMN_NAME,DatabaseHelper.MODEL_COLUMN_NAME,DatabaseHelper.PRODUCER_COLUMN_NAME
                ,DatabaseHelper.URL_COLUMN_NAME};
        Cursor c=getContentResolver().query(ContentUris.withAppendedId(DatabaseContentProvider.CONTENT_URI,editingItemId),columnNames,null,null,null);
        c.moveToFirst();
        producerInput.setText(c.getString(c.getColumnIndex(DatabaseHelper.PRODUCER_COLUMN_NAME)));
        modelInput.setText(c.getString(c.getColumnIndex(DatabaseHelper.MODEL_COLUMN_NAME)));
        androidInput.setText(c.getString(c.getColumnIndex(DatabaseHelper.ANDROID_VERSION_COLUMN_NAME)));
        uriInput.setText(c.getString(c.getColumnIndex(DatabaseHelper.URL_COLUMN_NAME)));
    }

    private void setMode(){
        Bundle b=getIntent().getExtras();
        if(b!=null){
            editingItemId=b.getLong("itemId");
            MODE=MODE_EDIT;
        }else
            MODE=MODE_ADD;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DatabaseHelper.ID,rowId);
    }

    private boolean validateElements(){
        return producerInput.getText().toString().length()>0&&producerInput.getText().toString().matches("[a-zA-Z]+")&&
                modelInput.getText().toString().length()>0&&
                androidInput.getText().toString().length()>0;
    }

    private boolean validateUri(){
        return uriInput.getText().toString().length()>0&&validateHttpUri(uriInput.getText().toString());
    }

    private void attachClickListeners(){

        saveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(validateElements()){
                    ContentValues newPhoneContentValues=new ContentValues();
                    newPhoneContentValues.put(DatabaseHelper.PRODUCER_COLUMN_NAME,producerInput.getText().toString());
                    newPhoneContentValues.put(DatabaseHelper.MODEL_COLUMN_NAME,modelInput.getText().toString());
                    newPhoneContentValues.put(DatabaseHelper.ANDROID_VERSION_COLUMN_NAME,androidInput.getText().toString());
                    newPhoneContentValues.put(DatabaseHelper.URL_COLUMN_NAME,uriInput.getText().toString());
                    if(MODE==MODE_ADD) getContentResolver().insert(DatabaseContentProvider.CONTENT_URI,newPhoneContentValues);
                    else getContentResolver().update(ContentUris.withAppendedId(DatabaseContentProvider.CONTENT_URI,editingItemId),newPhoneContentValues,null,null);

                    Intent comeBackIntent=new Intent(getApplicationContext(),PhoneListActivity.class);
                    startActivity(comeBackIntent);
                    finish();
                }else{
                    Toast toast=Toast.makeText(PhoneEditActivity.this,getString(R.string.formIncorrect).toString(),Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });
        wwwButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(validateUri()){
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(uriInput.getText().toString()));
                    startActivity(browserIntent);
                }else{
                    Toast toast=Toast.makeText(PhoneEditActivity.this,getString(R.string.wrongUri).toString(),Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent comeBackIntent=new Intent(getApplicationContext(),PhoneListActivity.class);
                startActivity(comeBackIntent);
                finish();
            }
        });

    }

    private boolean validateHttpUri(String uri){
        final URL url;
        try{
            url=new URL(uri);
        }
        catch(Exception e){
            return false;
        }
        return "http".equals(url.getProtocol()) || "https".equals(url.getProtocol());
    }
}
