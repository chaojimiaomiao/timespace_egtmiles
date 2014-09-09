package com.ckt.vas.miles.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.ckt.vas.miles.R;
import com.ckt.vas.miles.ui.activities.MainActivity;

public class RecordProvider extends AppWidgetProvider {

	//定义我们要发送的事件  
    private final String broadCastString = "com.wd.appWidgetUpdate";
    
    @Override  
    public void onDeleted(Context context, int[] appWidgetIds)  
    {
        super.onDeleted(context, appWidgetIds);
        Log.e("provider", "onDeleted");
    }
    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,  
            int[] appWidgetIds) {
    	Log.e("provider", "onUpdate");
    	Timer timer = new Timer();     
        timer.scheduleAtFixedRate(new MyTime(context,appWidgetManager), 1, 60000);     
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    
    @Override  
    public void onReceive(Context context, Intent intent)  
    {
    	Log.e("provider", "onReceive");
        //当判断到是该事件发过来时， 我们就获取插件的界面， 然后将index自加后传入到textview中
        //if(intent.getAction().equals(broadCastString)) {
        	/*RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);  
            //将该界面显示到插件中  
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);  
            ComponentName componentName = new ComponentName(context, RecordProvider.class);  
            appWidgetManager.updateAppWidget(componentName, remoteViews);  */
        //}     
        // TODO Auto-generated method stub  
        super.onReceive(context, intent);  
    }
    
    private class MyTime extends TimerTask{     
        RemoteViews remoteViews;     
        AppWidgetManager appWidgetManager;     
        ComponentName thisWidget;     
             
        public MyTime(Context context, AppWidgetManager appWidgetManager){     
            this.appWidgetManager = appWidgetManager;     
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.widget_record, "位置变了，写点什么？");
            Intent fullIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, fullIntent, 0);
            remoteViews.setPendingIntentTemplate(R.id.id_widget_layout, pendingIntent);
            thisWidget = new ComponentName(context, RecordProvider.class);    
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
        public void run() {
            Date date = new Date();     
            Calendar calendar = new GregorianCalendar(2010,06,11);     
            long days = (((calendar.getTimeInMillis()-date.getTime())/1000))/86400;     
            //remoteViews.setTextViewText(R.id.wordcup, "距离南非世界杯还有" + days+"天");     
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);     
        }
    }
}
