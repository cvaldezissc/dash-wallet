package net.paccoin.wallet.rates.restclient.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;


public class PACRate {


    @SerializedName("pac_price")
    public BigDecimal pac_price;

    @SerializedName("btc_price")
    public BigDecimal btc_price;

    @SerializedName("btc_pac")
    public BigDecimal btc_pac;

    @SerializedName("currency")
    public String currency;



    public BigDecimal getPac_price() {
        return pac_price;
    }

    public void setPac_price(BigDecimal pac_price) {
        this.pac_price = pac_price;
    }

    public BigDecimal getBtc_price() {
        return btc_price;
    }

    public void setBtc_price(BigDecimal btc_price) {
        this.btc_price = btc_price;
    }

    public BigDecimal getBtc_pac() {
        return btc_pac;
    }

    public void setBtc_pac(BigDecimal btc_pac) {
        this.btc_pac = btc_pac;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
