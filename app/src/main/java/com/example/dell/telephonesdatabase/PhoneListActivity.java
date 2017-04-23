package com.example.dell.telephonesdatabase;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.dell.telephonesdatabase.database.DatabaseContentProvider;
import com.example.dell.telephonesdatabase.database.DatabaseHelper;

public class PhoneListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter listAdapter;
    private ListView listView;
    private DatabaseContentProvider provider=new DatabaseContentProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_list);
        listView= (ListView) findViewById(R.id.listView);
        runLoader();
        handleTopMenuItemRequests();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_phone_menu, menu);
        return true;

    }

    @Override
    protected void onResume() {
        listAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_add){
            Intent intent = new Intent(getApplicationContext(), PhoneEditActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={DatabaseHelper.ID,DatabaseHelper.PRODUCER_COLUMN_NAME,DatabaseHelper.MODEL_COLUMN_NAME};
        CursorLoader cursorLoader=new CursorLoader(this, DatabaseContentProvider.CONTENT_URI,projection,null,null,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);

    }

    private void handleTopMenuItemRequests(){
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater=mode.getMenuInflater();
                menuInflater.inflate(R.menu.edit_phone_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_remove:
                        removeSelected();
                        finish();
                        startActivity(getIntent());
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editIntent=new Intent(getApplicationContext(),PhoneEditActivity.class);
                Bundle b=new Bundle();
                b.putLong("itemId",id);
                editIntent.putExtras(b);
                startActivity(editIntent);
                finish();
            }
        });
    }

    private void removeSelected(){
        long[] selected=listView.getCheckedItemIds();
        for(int i=0;i<selected.length;i++){
            getContentResolver().delete(ContentUris.withAppendedId(DatabaseContentProvider.CONTENT_URI,selected[i]),null,null);
        }

    }


    private void runLoader(){
        getLoaderManager().initLoader(0,null,this);
        String[] columnNames=new String[]{DatabaseHelper.MODEL_COLUMN_NAME,DatabaseHelper.PRODUCER_COLUMN_NAME};
        int[] viewsNames=new int[]{R.id.phoneModelRowLabel,R.id.producerRowLabel};

        listAdapter=new SimpleCursorAdapter(this,R.layout.row_of_list,null,columnNames,viewsNames,0);
        listView.setAdapter(listAdapter);
    }

}
