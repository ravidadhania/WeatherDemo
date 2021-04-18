package com.weatherdemo.callbacks;


import com.weatherdemo.network.RestResponse;

public interface APIResponseCallback<T> {

    void onResponse(RestResponse<T> response);
}
