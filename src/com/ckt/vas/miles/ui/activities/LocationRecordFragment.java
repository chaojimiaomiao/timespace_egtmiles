package com.ckt.vas.miles.ui.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.juhe.locate.JHLocateClient;
import cn.juhe.locate.JHLocateListener;
import cn.juhe.locate.JHLocateOption;
import cn.juhe.locate.JHLocation;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.baixing.widgets.CommonDlg;
import com.ckt.vas.miles.R;
import com.ckt.vas.miles.dto.Constants;

public class LocationRecordFragment extends Fragment {
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
        Button fabuButton = (Button) rootView.findViewById(R.id.id_fabu);
        fabuButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(editText.getText().toString())) {
					new CommonDlg(getActivity(), "沉默有时也是最好的回忆", "你确定不说话吗？", new CommonDlg.DialogAction(){
						@Override
						public void doAction(CommonDlg dialog){
							dialog.cancel();
							createAVObject();
						}
					}, true).show();
				} else {
					createAVObject();
				}
			}
        	
        });
        locateButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				jhLocateClient.stop();
				jhLocateClient.start();
			}
        	
        });
        return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AVOSCloud.initialize(getActivity(), Constants.App_ID, Constants.App_KEY);
		getLocation();
	}
	
	private void getLocation() {
		jhLocateClient = JHLocateClient.getClientInstance(getActivity(), Constants.JH_LOCATION_APPKEY);
		jhLocateClient.registerLocateListener(new MyJHBaseStaListener());

		setLocateOption();
	}
	
	/**
	 * 设置基站定位选项
	 */
	private void setLocateOption() {
		JHLocateOption option = new JHLocateOption();
		option.setCoorType(JHLocateOption.COOR_GOOGLE); // 设置放回的坐标体系，需要在谷歌地图上显示位置，请选择JHLocateOption.COOR_GOOGLE,其它类似
		option.setInterval(0); // 间隔多长时间定位一次，单位：毫秒
																				// 小于等于1000时，只定位一次，不循环
		option.setPriority(JHLocateOption.GPS_FIRST); // 设置优先定位模式，
		jhLocateClient.setJhOption(option);
	}
	
	private void createAVObject() {
	    
	    Log.e("", "what?");
	    
	    AVAnalytics.trackAppOpened(getActivity().getIntent());
		AVObject placeObject = new AVObject("LocationRecord");
		if (point == null) {
			point = new AVGeoPoint(31.1979581757, 121.4350390538);
		}
		placeObject.put("location", point);
		placeObject.put("record", editText.getText().toString());
		placeObject.saveInBackground();
	}
	
	AVGeoPoint point;
	
	private class MyJHBaseStaListener implements JHLocateListener {

		@Override
		public void onReceiveLocation(JHLocation location) {
			locationTextView.setText(location.getAddress());
			point = new AVGeoPoint(location.getLat(), location.getLng());
			
			parseLocate(location);
		}

		@Override
		public void onReceivePoi(JHLocation location) {
			locationTextView.setText(location.getAddress());
			point = new AVGeoPoint(location.getLat(), location.getLng());
			
			parseLocate(location);
		}
		
		private void parseLocate(JHLocation jhl) {
			StringBuffer sb = new StringBuffer();
			sb.append("responseCode:" + jhl.getResponseCode() + "\n");
			sb.append("response： " + jhl.getResponse() + "\n");
			sb.append("lat: " + jhl.getLat() + "\n");
			sb.append("lng: " + jhl.getLng() + "\n");		
			sb.append("省： " + jhl.getProvince() + "\n");
			sb.append("市： " + jhl.getCity() + "\n");
			sb.append("区/县： " + jhl.getDistrict() + "\n");			
			sb.append("误差半径： " + jhl.getRadius() + "米\n");
			sb.append("地理位置： " + jhl.getAddress() + "\n");
		}
		
	}
}
