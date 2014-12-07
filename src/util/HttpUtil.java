package util;

import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String response =null;
				HttpGet httpGet = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();
					 httpGet = new HttpGet(address);
					 Log.d("address", address);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode()==200)
					{
						HttpEntity entity = httpResponse.getEntity();
						 response = EntityUtils.toString(entity, "utf-8");
						 Log.d("response", response);
						 
						 
					}
					if(listener!=null)
					{
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					// TODO: handle exception
					if(listener!=null)
					{
						listener.onError(e);
					}
				}
				
			}
		}).start();
	}
	
}
