package com.ckt.vas.miles.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.ckt.vas.miles.dto.Constants;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
	    AVOSCloud.initialize(this, Constants.App_ID, Constants.App_KEY);
	    
	    Log.e("", "what?");
	    
	    AVAnalytics.trackAppOpened(getIntent());
	    
	    AVObject testObject = new AVObject("TestObject");
	    testObject.put("foo", "bar");
	    testObject.saveInBackground();
	}
}
