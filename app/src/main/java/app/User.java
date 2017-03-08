package app;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class User {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context mContext;

    public String getMobile() {
        return preferences.getString(Constants.MOBILE, Constants.NULL);
    }

    public void setMobile(String mobile) {
        editor.putString(Constants.MOBILE, mobile);
        editor.apply();
    }


    public String getToken() {
        return preferences.getString(Constants.TOKEN, Constants.NULL);
    }

    public void setToken(String token) {
        editor.putString(Constants.TOKEN, token);
        editor.apply();
    }


    public User(Context context) {
        mContext = context;
        preferences = mContext.getSharedPreferences(Constants.APPNAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void logOut() {
        editor.clear();
        editor.commit();
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isLoggedIn() {
        return !getToken().equals(Constants.NULL);
    }
}
