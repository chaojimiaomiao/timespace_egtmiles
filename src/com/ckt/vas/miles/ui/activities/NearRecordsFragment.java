package com.ckt.vas.miles.ui.activities;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.juhe.locate.JHLocateClient;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.ckt.vas.miles.R;
import com.ckt.vas.miles.dto.Constants;

public class NearRecordsFragment extends Fragment {
	private JHLocateClient jhLocateClient;
	private TextView locationTextView;
	private EditText editText;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mylocate, container, false);
        locationTextView = (TextView) rootView.findViewById(R.id.id_location);
        editText = (EditText) rootView.findViewById(R.id.id_record);
        Button locateButton = (Button) rootView.findViewById(R.id.id_locate_button);
        locateButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				jhLocateClient.stop();
				jhLocateClient.start();
			}
        	
        });
        return rootView;
	}
	
	String nearInfos = "";
	private void findNearPeople() {
		//定位
		AVGeoPoint userLocation = new AVGeoPoint(31.1982322, 121.4348722);
		AVQuery<AVObject> query = new AVQuery<AVObject>("LocationRecord");
		query.whereNear("location", userLocation);
		query.setLimit(10);            //获取最接近用户地点的10条数据
		try {
			List<AVObject> nearPlaces = query.find();
			for (int i = 0; i < nearPlaces.size(); i++) {
				AVObject object = nearPlaces.get(i);
				nearInfos += object.getString("record");
			}
			locationTextView.setText(nearInfos);
		} catch (AVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AVOSCloud.initialize(getActivity(), Constants.App_ID, Constants.App_KEY);
		findNearPeople();
	}
}
