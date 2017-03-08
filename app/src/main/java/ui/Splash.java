package ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import app.Constants;
import app.User;
import dudic.accieo.R;

public class Splash extends AppCompatActivity {
    Context mContext;
    String TAG = Splash.class.getName();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;
        user = new User(mContext);

        if (!user.isConnected()) {
            Toast.makeText(mContext, getString(R.string.network_problem), Toast.LENGTH_LONG).show();
            return;
        }

        if (user.isLoggedIn()) {
            Intent intent = new Intent(mContext, Maps.class);
            intent.putExtra(Constants.SHOW_TUTORIAL, false);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (user.isLoggedIn()){
                        Intent intent = new Intent(mContext, Maps.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }

            }
        }).start();


    }

    public void openRegister(View v) {
        startActivity(new Intent(mContext, Register.class));
    }


    public void openLogin(View v) {
        startActivity(new Intent(mContext, Login.class));
    }
}
