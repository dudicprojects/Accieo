package ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import app.Constants;
import app.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import dudic.accieo.R;

public class OTP extends AppCompatActivity {

    @BindView(R.id.otpField)
    EditText otpField;

    ProgressDialog progressDialog;
    Context mContext;
    User user;
    String otp, TAG = OTP.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        initUI();
    }

    private void initUI() {
        mContext = this;
        ButterKnife.bind(this);

        user = new User(this);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.connecting));
    }

    public void verifyOTP(View v) {
        if (!isInputValid()) {
            showToast(getString(R.string.invalid_input));
            return;
        }

        Ion.with(mContext).load(Constants.OTP).setBodyParameter("token_given", otp).setBodyParameter("id", user.getToken())
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if (e != null) {
                    showToast(getString(R.string.server_problem));
                    Log.e(TAG, e.toString());
                    return;
                }
                Log.e(TAG, result);

                try {
                    JSONObject json = new JSONObject(Constants.formatResponse(result));
                    showToast(json.getString(Constants.MESSAGE));
                    if (json.getString(Constants.STATUS).equals("200")) {
                        Intent intent = new Intent(mContext, Maps.class);
                        intent.putExtra(Constants.SHOW_TUTORIAL, true);
                        startActivity(intent);
                    }


                } catch (JSONException e1) {
                    Log.e(TAG, e1.toString());
                    showToast(getString(R.string.server_problem));
                }
            }
        });

    }

    boolean isInputValid() {
        otp = otpField.getText().toString();
        return otp.length() == 4;
    }

    void showToast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
