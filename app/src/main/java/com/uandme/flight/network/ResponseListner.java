package com.uandme.flight.network;


public interface ResponseListner<T>  {
	public void onResponse(T response);
	public void onEmptyOrError(String message);
}
