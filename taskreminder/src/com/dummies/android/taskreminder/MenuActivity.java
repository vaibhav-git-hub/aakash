package com.dummies.android.taskreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {
	public Button button1,button2,button3,button4;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
         button1 = (Button)findViewById(R.id.task_list);
         button2 = (Button)findViewById(R.id.time_slots);
         button3 = (Button)findViewById(R.id.schedule);
         button4 = (Button)findViewById(R.id.exit);
         registerButtonListenersAndSetDefaultText();
    }


private void registerButtonListenersAndSetDefaultText() {

	button1.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(MenuActivity.this,ReminderListActivity.class); 
        	startActivity(i);   
		}
	}); 
button2.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(MenuActivity.this,TimeSlotListActivity.class); 
        	startActivity(i);   
		}
	});
button3.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		Intent i = new Intent(MenuActivity.this,Scheduler.class); 
    	startActivity(i);
	}
});
button4.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		  
	}
});
	
	
}
}