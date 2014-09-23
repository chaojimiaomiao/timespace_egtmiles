package com.ckt.vas.miles.ui.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.ckt.vas.miles.R;
import com.ckt.vas.miles.dto.ActivityMessage;
import com.ckt.vas.miles.dto.Constants;
import com.ckt.vas.miles.ui.adapters.PublicActivityAdapter;
import com.ckt.vas.miles.ui.views.ExtendedListView;
import com.ckt.vas.miles.ui.views.ExtendedListView.OnPositionChangedListener;

public class NearRecordsFragment extends Fragment implements OnPositionChangedListener {
	private ExtendedListView dataListView;
	private FrameLayout clockLayout;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.feed_activity2, container, false);
        dataListView = (ExtendedListView) rootView.findViewById(R.id.list_view);
        clockLayout = (FrameLayout) rootView.findViewById(R.id.clock);
        dataListView.setCacheColorHint(Color.TRANSPARENT);
        dataListView.setOnPositionChangedListener(this);
        return rootView;
	}
	
	PublicActivityAdapter chatHistoryAdapter;

    private void setAdapterForThis() {
        chatHistoryAdapter = new PublicActivityAdapter(getActivity(), messages);
        dataListView.setAdapter(chatHistoryAdapter);
    }
	
	private List<ActivityMessage> messages = new ArrayList<ActivityMessage>();
	private class findNearPeopleTask extends AsyncTask<Void, Void, List<AVObject>> {

		@Override
		protected List<AVObject> doInBackground(Void... params) {
			//定位
			AVGeoPoint userLocation = new AVGeoPoint(31.1982322, 121.4348722);
			AVQuery<AVObject> query = new AVQuery<AVObject>("LocationRecord");
			query.whereNear("location", userLocation);
			query.setLimit(10);            //获取最接近用户地点的10条数据
			try {
				List<AVObject> nearPlaces = query.find();
				return nearPlaces;
			} catch (AVException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(List<AVObject> result) {
			if (result == null) {
				return;
			}
			for (int i = 0; i < result.size(); i++) {
				AVObject object = result.get(i);
				nearInfos += object.getString("record");
				messages.add(new ActivityMessage(R.drawable.icy, "浩然高科技大厦", 
						object.getString("record"), 1333153510605l));
			}
			setAdapterForThis();
			//editText.setText(nearInfos);
		}
	}
	String nearInfos = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AVOSCloud.initialize(getActivity(), Constants.App_ID, Constants.App_KEY);
		new findNearPeopleTask().execute();
	}
	
	@Override
	public void onPositionChanged(ExtendedListView listView, int firstVisiblePosition, View scrollBarPanel) {
		Log.e("", "padding top" + scrollBarPanel.getPaddingTop());
		TextView datestr = ((TextView) getView().findViewById(R.id.clock_digital_date));
        datestr.setText("上午");
        ActivityMessage msg = messages.get(firstVisiblePosition);
        int hour = msg.getHour();
        String tmpstr = "";
        if (hour > 12) {
            hour = hour - 12;
            datestr.setText("下午");
            tmpstr += " ";
        } else if (0 < hour && hour < 10) {
            tmpstr += " ";
        }
        tmpstr += hour + ":" + msg.getMin();
        ((TextView) getView().findViewById(R.id.clock_digital_time)).setText(tmpstr);
        RotateAnimation[] tmp = computeAni(msg.getMin(),hour);
        ImageView minView = (ImageView) getView().findViewById(R.id.clock_face_minute);
        minView.startAnimation(tmp[0]);
        ImageView hourView = (ImageView) getView().findViewById(R.id.clock_face_hour);
        hourView.setImageResource(R.drawable.clock_hour_rotatable);
        hourView.startAnimation(tmp[1]);
	}

	@Override
	public void onScollPositionChanged(View scrollBarPanel, int top) {
		MarginLayoutParams layoutParams = (MarginLayoutParams)clockLayout.getLayoutParams();
        System.out.println("left=="+layoutParams.leftMargin+" top=="+layoutParams.topMargin+" bottom=="+layoutParams.bottomMargin+" right=="+layoutParams.rightMargin);
        layoutParams.setMargins(0, top, 0, 0);
        clockLayout.setLayoutParams(layoutParams);
	}
	
	private float[] computMinAndHour(int currentMinute, int currentHour) {
        float minuteRadian = 6f * currentMinute;

        float hourRadian = 360f / 12f * currentHour;

        float[] rtn = new float[2];
        rtn[0] = minuteRadian;
        rtn[1] = hourRadian;
        return rtn;
    }
	private float[] lastTime = {
            0f, 0f
    };
	private RotateAnimation[] computeAni(int min, int hour) {

        RotateAnimation[] rtnAni = new RotateAnimation[2];
        float[] timef = computMinAndHour(min, hour);
        RotateAnimation ra = new RotateAnimation(lastTime[0], timef[0], Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true); 
        ra.setFillBefore(true);
        ra.setDuration(800);
        rtnAni[0] = ra;

        lastTime[0] = timef[0];
        RotateAnimation ra2 = new RotateAnimation(lastTime[1], timef[1], Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // 设置动画的执行时间
        ra2.setFillAfter(true);
        ra2.setFillBefore(true);
        ra2.setDuration(800);
        rtnAni[1] = ra2;
        lastTime[1] = timef[1];
        return rtnAni;
    }
}
