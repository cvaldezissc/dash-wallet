package net.paccoin.wallet;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import net.paccoin.wallet.rates.ExchangeRate;
import net.paccoin.wallet.rates.ExchangeRatesDao;

/**
 * @author Samuel Barbosa
 */
@Database(entities = {ExchangeRate.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract ExchangeRatesDao exchangeRatesDao();

    public static AppDatabase getAppDatabase() {
        if (instance == null) {
            instance = Room.databaseBuilder(WalletApplication.getInstance(),
                    AppDatabase.class, "dash-wallet-database").build();
        }
        return instance;
    }

}
