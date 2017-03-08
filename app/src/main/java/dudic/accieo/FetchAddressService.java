package dudic.accieo;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.Constants;


public class FetchAddressService extends IntentService {

    List<Address> addresses;
    ResultReceiver mReceiver;

    public FetchAddressService() {
        super("FetchAddressService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;
        double lat = intent.getDoubleExtra(Constants.LATITUDE, 0);
        double lang = intent.getDoubleExtra(Constants.LONGITUDE, 0);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lang, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addresses.get(0);
        ArrayList<String> addressString = new ArrayList<String>();

        for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
            addressString.add(address.getAddressLine(i));

        deliverResultToReceiver(0, addressString.toString());

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ADDRESS, message);
        mReceiver.send(resultCode, bundle);
    }
}
