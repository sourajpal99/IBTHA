package com.freelearners.ibtha.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.freelearners.ibtha.model.ProductModel;
import com.freelearners.ibtha.server.Constants;
import com.freelearners.ibtha.server.data.ServerClass;
import com.freelearners.ibtha.server.data.ServerResponseCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProductViewModel extends ViewModel {
    private static final String TAG = "ProductViewModel";
    private final MutableLiveData<ArrayList<ProductModel>> productList;

    public ProductViewModel() {
        productList = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<ProductModel>> getProductListObserver() {
        return productList;
    }

    public void makeApiCall(Context context) {

        new ServerClass().sendPOSTArrayRequestToServer(context,
                Constants.BASE_URL + "/api/product/getProducts",
                new ServerResponseCallback() {
                    @Override
                    public void onJSONResponse(JSONObject jsonObject) {
                        Log.d(TAG, "onJSONResponse: " + jsonObject.toString());
                    }

                    @Override
                    public void onJSONArrayResponse(JSONArray jsonArray) {

                        Log.d(TAG, "onJSONArrayResponse: " + jsonArray.toString());
                        Type productType = new TypeToken<ArrayList<ProductModel>>() {
                        }.getType();
                        ArrayList<ProductModel> productModels = new Gson().fromJson(String.valueOf(jsonArray), productType);
                        productList.postValue(productModels);

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError: ", e);
                        productList.postValue(null);
                    }
                });
    }
}