package vandy.mooc.services;

import java.util.List;


import vandy.mooc.aidl.WeatherData;
import vandy.mooc.aidl.WeatherRequest;
import vandy.mooc.aidl.WeatherResults;
import vandy.mooc.jsonweather.Weather;
import vandy.mooc.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;

/**
 * @class AcronymServiceAsync
 * 
 * @brief This class uses asynchronous AIDL interactions to expand
 *        acronyms via an Acronym Web service.  The AcronymActivity
 *        that binds to this Service will receive an IBinder that's an
 *        instance of AcronymRequest, which extends IBinder.  The
 *        Activity can then interact with this Service by making
 *        one-way method calls on the AcronymRequest object asking
 *        this Service to lookup the Acronym's meaning, passing in an
 *        AcronymResults object and the Acronym string.  After the
 *        lookup is finished, this Service sends the Acronym results
 *        back to the Activity by calling sendResults() on the
 *        AcronymResults object.
 * 
 *        AIDL is an example of the Broker Pattern, in which all
 *        interprocess communication details are hidden behind the
 *        AIDL interfaces.
 */
public class AcronymServiceAsync extends LifecycleLoggingService {
    /**
     * Factory method that makes an Intent used to start the
     * AcronymServiceAsync when passed to bindService().
     * 
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                          AcronymServiceAsync.class);
    }

    /**
     * Called when a client (e.g., AcronymActivity) calls
     * bindService() with the proper Intent.  Returns the
     * implementation of AcronymRequest, which is implicitly cast as
     * an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAcronymRequestImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface
     * AcronymRequest, which extends the Stub class that implements
     * AcronymRequest, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     * 
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final WeatherRequest.Stub mAcronymRequestImpl =
        new WeatherRequest.Stub() {
            @Override
            public void getCurrentWeather(String weather, WeatherResults results)
                    throws RemoteException {
                List<WeatherData> acronymResults;
                try {
                    acronymResults = Utils.getResults(weather);
                } catch (JSONException e) {
                    acronymResults = null;
                    e.printStackTrace();
                }

                if (acronymResults != null) {
                    Log.d(TAG, ""
                            + acronymResults.size()
                            + " results for acronym: "
                            + weather);
                    // Invoke a one-way callback to send list of
                    // acronym expansions back to the AcronymActivity.
                    results.sendResults(acronymResults);
                } //else
                    // Invoke a one-way callback to send an error
                    // message back to the AcronymActivity.
                    //results.sendError("No expansions for "
                     //       + acronym
                     //       + " found");

            }

	};
}
