package activity;


import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_infor_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text  =(TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode))
		{
			//有县级代号就去查询
			publishText.setText("同步中");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
			
		}else {
			//没有县级代号就直接显示本地天气
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	switch (arg0.getId()) {
	case R.id.switch_city:
		Intent intent = new Intent(this,ChooseAreaActivity.class);
		intent.putExtra("from_weather_activity", true);
		startActivity(intent);
		finish();
		break;
	case R.id.refresh_weather:
		publishText.setText("同步中");
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		String weatherCode = preferences.getString("weather_code","");
		if(!TextUtils.isEmpty(weatherCode)){
			queryWeatherInfo(weatherCode);
		}
				
		break;
	default:
		break;
	}
		
	}
	
	/**
	 * 查询县级代号对应的天气
	 */
	private void queryWeatherCode(String countyCode)
	{
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
		
	}
	
	/**
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode)
	{
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
		
	}
	
	/**
	 * 根据传入的地址和类型区服务器查询天气代号或者天气信息
	 */
	private void queryFromServer(final String address,final String type)
	{
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				/**
				 * 从服务器返回的天气代号
				 */
				Log.d("onFinish", response);
				Log.d("type", type);
				if("countyCode".equals(type))
				{
					Log.d("countyCode", type+"");
					if(!TextUtils.isEmpty(response))
					{
						String[] array  = response.split("\\|");
						if(array !=null && array.length ==2)
						{
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
							
						}
					}
					}
				else if ("weatherCode".equals(type)) {
					Log.d("weatherCode", type+"");
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					
					runOnUiThread(new  Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
				
				
			}
			
			@Override
			public void onError(Exception e) {
				Log.d("fail", "同步失败");
				// TODO Auto-generated method stub
				runOnUiThread(new  Runnable() {
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	
	/**
	 * 有天气代号就查询天气，没有天气代号就在本地读取
	 */
	private void showWeather()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(preferences.getString("city_name", ""));
		temp1Text.setText(preferences.getString("temp1", ""));
		temp2Text.setText(preferences.getString("temp2", ""));
		weatherDespText.setText(preferences.getString("weather_desp", ""));
		publishText.setText(preferences.getString("publish_time", ""));
		currentDateText.setText(preferences.getString("current_date", ""));
		Log.d("temp1Text", temp1Text.getText().toString());
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		
	}
	
	
	
}
