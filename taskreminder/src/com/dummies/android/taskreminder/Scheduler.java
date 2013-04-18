package com.dummies.android.taskreminder;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

public class Scheduler extends Activity {
	//data structures for time slot...
	char [] stime = new char[10];
	char [] etime = new char[10];
	int [] slot = new int[100];
	String[] date = new String[100];
	private int index = 0;
	
	//data structures for tasks....
	String [] title = new String[100];
	float [] rtime = new float[100];
	int [] type = new int[100];
	String [] deadline = new String[100];
	float [] priority = new float[100];
	
	int radiotype1 = 2131165193;
	int radiotype2 = 2131165194;
	int radiotype3 = 2131165195;
	
	TimeDbAdapter mDbHelper = new TimeDbAdapter(this);
	RemindersDbAdapter taskdbhelper = new RemindersDbAdapter(this);
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduler_layout);
        mDbHelper.open();
        taskdbhelper.open();
		int i = 0;
		while(i < 100)
		{
			slot[i] = 0;
			i++;
		}
		while(i < 100)
		{
			rtime[i] = 0;
			i++;
		}
		
        getInfofunction_time();
        getInfofunction_task();
        priorityassignment();
         i = 0;
        float sum = 0, req_sum = 0;
        while(i < 100)
        {
        	sum+= slot[i];
        	i++;
        }
        i = 0;
        while(i < 100)
        {
        	req_sum+= rtime[i];
        	i++;
        }
        makeTimeSlotSame(sum, req_sum);
        i = 0;
        while(i < index)
        {
        	Toast.makeText(Scheduler.this, (String.valueOf(rtime[i])), Toast.LENGTH_SHORT).show();
        	i++;
        }
        //Toast.makeText(Scheduler.this, ("sum = "+req_sum), Toast.LENGTH_SHORT).show();
        mDbHelper.close();
        taskdbhelper.close();
	}
	
	public void getInfofunction_time()
	{
		Cursor timeCursor = mDbHelper.fetchAllTimeslots();	
		int i = 0;
		int sindex = 0;
		int eindex = 0;
		
		if (timeCursor != null) {
            timeCursor.moveToFirst();
        }
		sindex = timeCursor.getColumnIndexOrThrow(TimeDbAdapter.KEY_STIME);
		eindex = timeCursor.getColumnIndexOrThrow(TimeDbAdapter.KEY_ETIME);
		while(!timeCursor.isAfterLast()){
			stime = timeCursor.getString(sindex).toCharArray();
			etime = timeCursor.getString(eindex).toCharArray();
			slot[i] = (((etime[0]*10 - '0' ) + (etime[1] - '0'))*60 + (etime[3] - '0')*10 + (etime[4]- '0')) - (((stime[0]*10 - '0' ) + (stime[1] - '0'))*60 + (stime[3] - '0')*10 + (stime[4]- '0'));     
			date[i] = timeCursor.getString(timeCursor.getColumnIndexOrThrow(TimeDbAdapter.KEY_DATE));
			timeCursor.moveToNext();
			i++;
		}
	}
	public void getInfofunction_task()
	{
		Cursor reminderCursor = taskdbhelper.fetchAllReminders();
		if (reminderCursor != null){
			reminderCursor.moveToFirst();
		}
		int tindex = 0;
		int tyindex = 0;
		int dindex = 0;
		int reqindex = 0;
		tindex = reminderCursor.getColumnIndexOrThrow(RemindersDbAdapter.KEY_TITLE);
		tyindex = reminderCursor.getColumnIndexOrThrow(RemindersDbAdapter.KEY_TYPE);
		dindex = reminderCursor.getColumnIndexOrThrow(RemindersDbAdapter.KEY_DATE_TIME);
		reqindex = reminderCursor.getColumnIndexOrThrow(RemindersDbAdapter.KEY_RTIME);
		while(!reminderCursor.isAfterLast())
		{
			title[index] = reminderCursor.getString(tindex);
			type[index] = reminderCursor.getInt(tyindex);
			deadline[index] = reminderCursor.getString(dindex);
			rtime[index] = reminderCursor.getFloat(reqindex);
			reminderCursor.moveToNext();
			//Toast.makeText(Scheduler.this,deadline[index].toString() , Toast.LENGTH_SHORT).show();
			index++;
		}
		
	}
	public void priorityassignment()
	{
		int i = 0;
		int k = 0;
		int j = 0;
		float pri = 1;
		int temp = 0;
		//Toast.makeText(Scheduler.this, "index = "+index, Toast.LENGTH_SHORT).show();
		for (i = 0; i < ( index-1); i++) {  
		    temp = i;
		    for ( j = i+1; j < index; j++) {
		    	k = comparefunction(deadline[temp],deadline[j]);
		    	//Toast.makeText(Scheduler.this, "k = "+k, Toast.LENGTH_SHORT).show();
		        if ( k == -1 )
		        {
		            temp = j;
		        }
		    }
		    if ( temp != i ) {
		        swap(i, temp);
		    }
		}
		//Toast.makeText(Scheduler.this, title[0]+" "+title[1]+" "+title[2]+" "+title[3]+" "+title[4], Toast.LENGTH_SHORT).show();
		
		i = 1;
		if( type[0] == radiotype1 )
		{
			priority[0] = pri;
		}
		else if ( type[0] == radiotype2 )
		{
			priority[0] = pri + (float)0.1;
		}
		else
		{
			priority[0] = pri + (float)0.2;
		}
		
		while ( i < index )
		{
			k = comparefunction(deadline[i],deadline[i-1]);
			
			if ( k == -1 )
			{
				pri++;
			}
			if( type[i] == radiotype1 )
			{
				priority[i] = pri;
			}
			else if ( type[i] == radiotype2 )
			{
				priority[i] = pri + (float)0.1;
			}
			else
			{
				priority[i] = pri + (float)0.2;
			}
			i++;
		}
		//Toast.makeText(Scheduler.this, priority[0]+" "+priority[1]+" "+priority[2]+" "+priority[3]+" "+priority[4]+" "+priority[5], Toast.LENGTH_SHORT).show();
		//Toast.makeText(Scheduler.this, type[0]+" "+type[1]+" "+type[2], Toast.LENGTH_SHORT).show();
	}
	public int comparefunction(String one, String two )
	{
		
		//Toast.makeText(Scheduler.this, "one = "+one + "two = "+two, Toast.LENGTH_SHORT).show();
		
		int year1 = (one.charAt(0) - '0')*1000 + (one.charAt(1) - '0')*100 + (one.charAt(2) - '0')*10 + (one.charAt(3) - '0');
		int month1 = (one.charAt(5) - '0')*10 + (one.charAt(6) - '0');
		int day1 = (one.charAt(8) - '0')*10 + (one.charAt(9) - '0');
		
		int year2 = (two.charAt(0) - '0')*1000 + (two.charAt(1) - '0')*100 + (two.charAt(2) - '0')*10 + (two.charAt(3) - '0');
		int month2 = (two.charAt(5) - '0')*10 + (two.charAt(6) - '0');
		int day2 = (two.charAt(8) - '0')*10 + (two.charAt(9) - '0');
		
		//Toast.makeText(Scheduler.this, "y = "+ year2 + " m "+ month2+ " d "+day2, Toast.LENGTH_SHORT).show();
		
		if (year1 > year2)
		{
			//Toast.makeText(Scheduler.this, "year = "+year1, Toast.LENGTH_SHORT).show();
			return -1;
		}
		else if(year1 == year2)
		{
			if(month1 > month2)
			{
				return -1;
			}
			else if(month1 == month2)
			{
				if(day1 > day2)
				{
					return -1;
				}
				else 
				{
					return 0;
				}
			}
		}
		return 0;
	}
	
	public void swap(int one, int two)
	{
		String temp = new String(title[one]);
		String temp3 = new String(title[two]);
		title[one] = temp3;
		title[two] = temp;
		
		int temp1;
		temp1 = type[one];
		type[one] = type[two];
		type[two] = temp1;
		
		temp = new String(deadline[one]);
		temp3 = new String(deadline[two]);
		deadline[one] = temp3;
		deadline[two] = temp;
		
		float temp2;
		temp2 = rtime[one];
		rtime[one] = rtime[two];
		rtime[two] = temp2;
		//Toast.makeText(Scheduler.this, ("swapped = "+title[one]+" & "+title[two]), Toast.LENGTH_SHORT).show();
	}
	public void makeTimeSlotSame(float sum, float req_sum)
	{
		float decrement;
		int i, ratio = 0;
		if(req_sum > sum)
		{
			for (i = 0; i < index; i++)
			{
				ratio += i;
			}
			decrement = ( req_sum - sum ) / ratio;
			for (i = 0; i < index; i++)
			{
				rtime[i] = rtime[i] - (decrement * i);
			}
		}
		/*else if (sum > req_sum)
		{
			for (i = 0; i < index; i++)
			{
				ratio += i;
			}
			decrement = ( sum - req_sum ) / ratio;
			for (i = (index - 1) ; i > -1; i++)
			{
				temp = rtime[i];
				rtime[i] = temp + (decrement * i);
			}
		}*/
	}
}