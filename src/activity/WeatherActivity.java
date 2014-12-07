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
	 * ������ʾ������
	 */
	private TextView cityNameText;
	
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	
	/**
	 * ������ʾ��ǰ����
	 */
	private TextView currentDateText;
	
	/**
	 * �л����а�ť
	 */
	private Button switchCity;
	
	/**
	 * ����������ť
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
			//���ؼ����ž�ȥ��ѯ
			publishText.setText("ͬ����");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
			
		}else {
			//û���ؼ����ž�ֱ����ʾ��������
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
		publishText.setText("ͬ����");
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
	 * ��ѯ�ؼ����Ŷ�Ӧ������
	 */
	private void queryWeatherCode(String countyCode)
	{
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
		
	}
	
	/**
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode)
	{
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
		
	}
	
	/**
	 * ���ݴ���ĵ�ַ����������������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(final String address,final String type)
	{
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				/**
				 * �ӷ��������ص���������
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
					//������������ص�������Ϣ
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
				Log.d("fail", "ͬ��ʧ��");
				// TODO Auto-generated method stub
				runOnUiThread(new  Runnable() {
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	/**
	 * ���������žͲ�ѯ������û���������ž��ڱ��ض�ȡ
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
