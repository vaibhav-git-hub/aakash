
package com.dummies.android.taskreminder;

import com.dummies.android.taskreminder.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TimeSlotListActivity extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    
    private TimeDbAdapter mDbHelper;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeslotlist);
        mDbHelper = new TimeDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    

	private void fillData() {
        Cursor timeCursor = mDbHelper.fetchAllTimeslots();
        startManagingCursor(timeCursor);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{TimeDbAdapter.KEY_DATE};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter timeslots = 
        	    new SimpleCursorAdapter(this, R.layout.reminder_row, timeCursor, from, to);
        setListAdapter(timeslots);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.timeslot_menu, menu); 
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_insert: 
            createTimeslot();
            return true; 
        case R.id.menu_settings: 
        	Intent i = new Intent(this, TaskPreferences.class); 
        	startActivity(i); 
            return true;
        }
       
        return super.onMenuItemSelected(featureId, item);
    }
	
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater mi = getMenuInflater(); 
		mi.inflate(R.menu.timeslot_menu_item_longpress, menu); 
	}

    @Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()) {
    	case R.id.menu_delete:
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        mDbHelper.deleteTimeslot(info.id);
	        fillData();
	        return true;
		}
		return super.onContextItemSelected(item);
	}
	
    private void createTimeslot() {
        Intent i = new Intent(this, TimeSlotsActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, TimeSlotsActivity.class);
        i.putExtra(TimeDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT); 
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
