package net.paccoin.wallet.rates;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.paccoin.wallet.AppDatabase;
import net.paccoin.wallet.rates.restclient.APIClient;
import net.paccoin.wallet.rates.restclient.model.APIInterface;
import net.paccoin.wallet.rates.restclient.model.PACRateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Samuel Barbosa
 */
public class ExchangeRatesRepository {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRatesRepository.class);
    private static ExchangeRatesRepository instance;

    private AppDatabase appDatabase;
    private Executor executor;
    private Deque<ExchangeRatesClient> exchangeRatesClients = new ArrayDeque<>();

    private static final long UPDATE_FREQ_MS = TimeUnit.SECONDS.toMillis(30);
    private long lastUpdated;

    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasError = new MutableLiveData<>();

    private boolean isRefreshing = false;


    public APIInterface apiInterface;
    public PACRateResponse pacRateResponse;

    private ExchangeRatesRepository() {
        appDatabase = AppDatabase.getAppDatabase();
        executor = Executors.newSingleThreadExecutor();

        populateExchangeRatesStack();


        apiInterface = APIClient.getRetrofit().create(APIInterface.class);
        makeRestCall();
    }

    private void makeRestCall() {

        Call<PACRateResponse> call = apiInterface.getPACRate();

        call.enqueue(new Callback<PACRateResponse>() {
            @Override
            public void onResponse(Call<PACRateResponse> call, Response<PACRateResponse> response) {
                Log.w("SERVICE_RETROFIT_EXCHAN", "[REPOSITORY] It Answered -> \n"+response.body().toString());
                pacRateResponse = response.body();
            }

            @Override
            public void onFailure(Call<PACRateResponse> call, Throwable t) {
                call.cancel();
            }
        });


    }




    public static ExchangeRatesRepository getInstance() {
        if (instance == null) {
            instance = new ExchangeRatesRepository();
        }
        return instance;
    }

    private void populateExchangeRatesStack() {
        if (!exchangeRatesClients.isEmpty()) {
            exchangeRatesClients.clear();
        }

        exchangeRatesClients.push(DashRatesSecondFallback.getInstance());
        exchangeRatesClients.push(DashRatesFirstFallback.getInstance());
        exchangeRatesClients.push(DashRatesClient.getInstance());
    }

    private void refreshRates() {
        this.refreshRates(false);
    }

    private void refreshRates(boolean forceRefresh) {
        if (!shouldRefresh()) {
            return;
        }
        if (exchangeRatesClients.isEmpty()) {
            populateExchangeRatesStack();
        }
        if (!forceRefresh && isRefreshing) {
            return;
        }
        isRefreshing = true;
        final ExchangeRatesClient exchangeRatesClient = exchangeRatesClients.pop();
        isLoading.postValue(true);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<ExchangeRate> rates;

                try {
                    rates = exchangeRatesClient.getRates();
                    if (rates != null && !rates.isEmpty()) {




                        //Log.w("SERVICE_RETROFIT2", "This is PacRateResponse repository "+pacRateResponse.data.currency +" :::: "+pacRateResponse.data.pac_price);

                        appDatabase.exchangeRatesDao().insert(new ExchangeRate("USD", "" + pacRateResponse.data.pac_price + ""));

                        //appDatabase.exchangeRatesDao().insert(rates.get(147));




                        lastUpdated = System.currentTimeMillis();
                        populateExchangeRatesStack();
                        hasError.postValue(false);
                        isRefreshing = false;
                        log.info("exchange rates updated successfully with {}", exchangeRatesClient);
                    } else if (!exchangeRatesClients.isEmpty()) {
                        refreshRates(true);
                    } else {
                        handleRefreshError();
                    }
                } catch (Exception e) {
                    log.error("failed to fetch exchange rates with {}", exchangeRatesClient, e);
                    if (!exchangeRatesClients.isEmpty()) {
                        refreshRates(true);
                    } else {
                        handleRefreshError();
                    }
                } finally {
                    isLoading.postValue(false);
                }
            }
        });
    }

    private void handleRefreshError() {
        isRefreshing = false;
        if (appDatabase.exchangeRatesDao().count() == 0) {
            hasError.postValue(true);
        }
    }

    private boolean shouldRefresh() {
        long now = System.currentTimeMillis();
        return lastUpdated == 0 || now - lastUpdated > UPDATE_FREQ_MS;
    }

    public LiveData<List<ExchangeRate>> getRates() {
        if (shouldRefresh()) {
            refreshRates();
        }
        return appDatabase.exchangeRatesDao().getAll();
    }

    public LiveData<ExchangeRate> getRate(String currencyCode) {
        if (shouldRefresh()) {
            refreshRates();
        }
        return appDatabase.exchangeRatesDao().getRate(currencyCode);
    }

    public LiveData<List<ExchangeRate>> searchRates(String query) {
        return appDatabase.exchangeRatesDao().searchRates(query);
    }


}
