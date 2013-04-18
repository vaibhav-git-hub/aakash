
package com.dummies.android.taskreminder;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeSlotsActivity extends Activity {

	// 
	// Dialog Constants
	//
	private static final int DATE_PICKER_DIALOG = 0;
	private static final int TIME_PICKER_DIALOG1 = 1;
	private static final int TIME_PICKER_DIALOG2 = 2;
	
	// 
	// Date Format 
	//
	private static final String DATE_FORMAT = "yyyy-MM-dd"; 
	private static final String TIME_FORMAT = "kk:mm";
	//public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
	
	
    private Button mDateButton;
    private Button mTimeButton1;
    private Button mTimeButton2;
    private Button mConfirmButton;
    private Long mRowId;
    private TimeDbAdapter mDbHelper;
    private Calendar mCalendar;
    private Calendar mCalendar1;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new TimeDbAdapter(this);
        
        setContentView(R.layout.timeslot_edit);
        
        mCalendar = Calendar.getInstance(); 
        mCalendar1 = Calendar.getInstance();
        mDateButton = (Button) findViewById(R.id.timeslot_date);
        mTimeButton1 = (Button) findViewById(R.id.start_time);
        mTimeButton2 = (Button) findViewById(R.id.end_time);
        mConfirmButton = (Button) findViewById(R.id.confirm);
       
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(TimeDbAdapter.KEY_ROWID) 
                							: null;
      
        registerButtonListenersAndSetDefaultText();
    }

	private void setRowIdFromIntent() {
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();            
			mRowId = extras != null ? extras.getLong(TimeDbAdapter.KEY_ROWID) 
									: null;
			
		}
	}
    
    @Override
    protected void onPause() {
        super.onPause();
        mDbHelper.close(); 
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mDbHelper.open(); 
    	setRowIdFromIntent();
		populateFields();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    		case DATE_PICKER_DIALOG: 
    			return showDatePicker();
    		case TIME_PICKER_DIALOG1: 
    			return showTimePicker1();
    		case TIME_PICKER_DIALOG2:
    			return showTimePicker2();
    	}
    	return super.onCreateDialog(id);
    }
    
 	private DatePickerDialog showDatePicker() {
		
		
		DatePickerDialog datePicker = new DatePickerDialog(TimeSlotsActivity.this, new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mCalendar.set(Calendar.YEAR, year);
				mCalendar.set(Calendar.MONTH, monthOfYear);
				mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateDateButtonText(); 
			}
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)); 
		return datePicker; 
	}

   private TimePickerDialog showTimePicker1() {
		
    	TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCalendar.set(Calendar.MINUTE, minute); 
				updateTimeButtonText1(); 
			}
		}, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true); 
		
    	return timePicker; 
	}
   private TimePickerDialog showTimePicker2() {
		
   	TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mCalendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mCalendar1.set(Calendar.MINUTE, minute); 
				updateTimeButtonText2(); 
			}
		}, mCalendar1.get(Calendar.HOUR_OF_DAY), mCalendar1.get(Calendar.MINUTE), true); 
		
   	return timePicker; 
	}
	private void registerButtonListenersAndSetDefaultText() {

		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DATE_PICKER_DIALOG);  
			}
		}); 
		
		
		mTimeButton1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TIME_PICKER_DIALOG1); 
			}
		});
		mTimeButton2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(TIME_PICKER_DIALOG2); 
			}
		});
		
		
		
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState(); 
        		setResult(RESULT_OK);
        		/* Change database entry table,query,etc */
        	    Toast.makeText(TimeSlotsActivity.this, getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();   		
        	    finish(); 
        	}
          
        });
		
		
		  updateDateButtonText(); 
	      updateTimeButtonText1();
	      updateTimeButtonText2();
	      
	}
   
    private void populateFields()  {
    	// Only populate the text boxes and change the calendar date
    	// if the row is not null from the database. 
        if (mRowId != null) {
            Cursor timeslot = mDbHelper.fetchTimeslot(mRowId);
            startManagingCursor(timeslot);
            
            //mBodyText.setText(reminder.getString(
              //      reminder.getColumnIndexOrThrow(RemindersDbAdapter.KEY_BODY)));
            

            // Get the date from the database and format it for our use.
        //edit from here......
            //SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
            //Date date = null;
            mDateButton.setText(timeslot.getString(timeslot.getColumnIndexOrThrow(TimeDbAdapter.KEY_DATE)));
            mTimeButton1.setText(timeslot.getString(timeslot.getColumnIndexOrThrow(TimeDbAdapter.KEY_STIME)));
            mTimeButton2.setText(timeslot.getString(timeslot.getColumnIndexOrThrow(TimeDbAdapter.KEY_ETIME)));
        } 	
    }
    
    private void updateTimeButtonText1() {
		// Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT); 
        String timeForButton = timeFormat.format(mCalendar.getTime()); 
        mTimeButton1.setText(timeForButton);
	}
    private void updateTimeButtonText2() {
		// Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT); 
        String timeForButton = timeFormat.format(mCalendar1.getTime()); 
        mTimeButton2.setText(timeForButton);
	}
	private void updateDateButtonText() {
		// Set the date button text based upon the value from the database 
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); 
        String dateForButton = dateFormat.format(mCalendar.getTime()); 
        mDateButton.setText(dateForButton);
	}
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TimeDbAdapter.KEY_ROWID, mRowId);
    }
    

    
    private void saveState() {

        //SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT); 
    	SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); 
        //String dateForButton = dateFormat.format(mCalendar.getTime()); 
    	String date = dateFormat.format(mCalendar.getTime());
    	SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);  
    	String stime = timeFormat.format(mCalendar.getTime());
    	String etime = timeFormat.format(mCalendar1.getTime());
        if (mRowId == null) {
        	
        	long id = mDbHelper.createTimeslot(date, stime, etime);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateTimeslot(mRowId, date, stime, etime);
        }
       //now add a button called schedule to invoke this method.
        //new ReminderManager(this).setReminder(mRowId, mCalendar); 
    }
    
}
