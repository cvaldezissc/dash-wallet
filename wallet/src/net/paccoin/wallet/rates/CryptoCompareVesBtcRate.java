package net.paccoin.wallet.rates;

import com.squareup.moshi.Json;

import java.math.BigDecimal;

/**
 * @author Samuel Barbosa
 */
public class CryptoCompareVesBtcRate {

    @Json(name = "USD")
    private final BigDecimal rate;

    public CryptoCompareVesBtcRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

}
