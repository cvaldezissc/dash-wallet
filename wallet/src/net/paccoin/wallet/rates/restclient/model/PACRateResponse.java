package net.paccoin.wallet.rates.restclient.model;

import com.google.gson.annotations.SerializedName;

public class PACRateResponse {


    @SerializedName("status")
    public int status;

    @SerializedName("data")
    public PACRate data;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public PACRate getData() {
        return data;
    }

    public void setData(PACRate data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PACRateResponse \n{ \nstatus="+status+", \ndata=\n{\npac_price:"+data.pac_price+", \nbtc_price: "+data.btc_price+", \nbtc_pac: "+data.btc_pac+", \ncurrency: "+data.currency+" \n}\n}}";
    }
}
