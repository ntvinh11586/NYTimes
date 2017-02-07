package com.coderschool.vinh.nytimes.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vinh on 10/23/2016.
 */

public class ApiResponse {

    @SerializedName("response")
    private JsonObject response;

    public JsonObject getResponse() {
        if (response == null) {
            return new JsonObject();
        }
        return response;
    }

}
