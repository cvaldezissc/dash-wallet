/*
 * Copyright 2011-2015 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.paccoin.wallet.ui;

import net.paccoin.wallet.rates.restclient.APIClient;
import net.paccoin.wallet.rates.restclient.model.APIInterface;
import net.paccoin.wallet.rates.restclient.model.PACRateResponse;
import net.paccoin.wallet_test.R;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Andreas Schildbach
 */
public final class RequestCoinsActivity extends AbstractBindServiceActivity {


    APIInterface apiInterface;

    public PACRateResponse pacRateResponse = null;


    public PACRateResponse getPacRateResponse() {
        return pacRateResponse;
    }

    public void setPacRateResponse(PACRateResponse pacRateResponse) {
        this.pacRateResponse = pacRateResponse;
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.request_coins_content);


        apiInterface = APIClient.getRetrofit().create(APIInterface.class);
        makeRestCall();


    }


    void makeRestCall(){
        Call<PACRateResponse> call = apiInterface.getPACRate();

        call.enqueue(new Callback<PACRateResponse>() {
            @Override
            public void onResponse(Call<PACRateResponse> call, Response<PACRateResponse> response) {
                Log.w("SERVICE_RETROFIT", "[ACTIVITY] It Answered -> \n"+response.body().toString());
                setPacRateResponse(response.body());

            }

            @Override
            public void onFailure(Call<PACRateResponse> call, Throwable t) {
                Log.w("SERVICE_RETROFIT", "[ACTIVITY] there was an error "+t.getCause());
                call.cancel();
            }
        });
    }




    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.request_coins_activity_options, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;

        case R.id.request_coins_options_help:
            HelpDialogFragment.page(getSupportFragmentManager(), R.string.help_request_coins);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
