package net.paccoin.wallet.rates.restclient.model;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("api/currency/usd")
    Call<PACRateResponse> getPACRate();

}
