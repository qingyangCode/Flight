package com.uandme.flight.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.text.TextUtils;

public class NetBase extends AsyncTask<Void, Void, String>{
	private String mUrl;
	private String mParam;
	private ResponseListner mListner;
	public NetBase(String url, String xmlParam,ResponseListner responseListner) {
		this.mUrl = url;
		this.mParam = xmlParam;
		this.mListner = responseListner;
	}

	@Override protected void onPreExecute() {
		super.onPreExecute();
		//TODO check network
	}

	@Override
	protected String doInBackground(Void... arg0) {
		if(TextUtils.isEmpty(mUrl) || TextUtils.isEmpty(mParam)){
			return null;
		}
		try {
			byte[] data = mParam.getBytes("UTF-8");
			URL url22 = new URL(mUrl);
			HttpURLConnection conn = (HttpURLConnection) url22.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			conn.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(data);
			outStream.flush();
			outStream.close();
			InputStream inputStream = conn.getInputStream();
			String resbond = readInputStream(inputStream);
			if(TextUtils.isEmpty(resbond)){
				return null;
			}else{
				return resbond;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return new String(outSteam.toByteArray());
	}
	
	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(TextUtils.isEmpty(result)){
			if(mListner != null){
				mListner.onEmptyOrError(result);
			}
		}else{
		    if(mListner != null){
		    	mListner.onResponse(result);
		    }
		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

}
