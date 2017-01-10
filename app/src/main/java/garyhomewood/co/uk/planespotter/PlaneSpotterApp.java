package garyhomewood.co.uk.planespotter;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 *
 */

public class PlaneSpotterApp extends Application {

    private static PlaneSpotterApp instance;
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Timber.i("Creating Application...");

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("planespotters.realm")
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        return realm;
    }

    public static PlaneSpotterApp getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.isConnected();
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
