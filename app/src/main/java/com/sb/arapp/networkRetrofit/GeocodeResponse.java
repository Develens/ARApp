
package com.sb.arapp.networkRetrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sb.arapp.networkRetrofit.poi.Result;


import java.util.List;

public class GeocodeResponse {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
