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

public class Login extends AppCompatActivity {

    @BindView(R.id.numberField)
    EditText numberField;
    @BindView(R.id.passwordField)
    EditText passwordField;

    String number, password, TAG = Login.class.getName();
    Context mContext;
    ProgressDialog progressDialog;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

    }

    private void initUI() {

        mContext = this;
        ButterKnife.bind(this);

        user = new User(mContext);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.connecting));

    }

    public void login(View v) {

        if (!isInputValid()) {
            showToast(getString(R.string.invalid_input));
            return;
        }

        if (!user.isConnected()) {
            showToast(getString(R.string.network_problem));
            return;
        }

        progressDialog.show();
        Ion.with(mContext).load(Constants.LOGIN).setBodyParameter("phonefield", number)
                .setBodyParameter("password", password)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                progressDialog.dismiss();

                if (e != null) {
                    showToast(getString(R.string.server_problem));
                    Log.e(TAG, e.toString());
                    return;
                }

                Log.e(TAG, "Result: " + result);

                try {

                    JSONObject json = new JSONObject(Constants.formatResponse(result));
                    showToast(json.getString(Constants.MESSAGE));
                    if (json.getString(Constants.STATUS).equals("200")) {
                        user.setToken(json.getString(Constants.TOKEN));
                        Intent intent = new Intent(mContext, Maps.class);

                        intent.putExtra(Constants.SHOW_TUTORIAL, true);
                        startActivity(intent);
                    }
                } catch (JSONException e1) {
                    Log.e(TAG, e1.toString());
                }


                Log.e(TAG, result);
            }
        });
    }


    boolean isInputValid() {
        number = "+91" + numberField.getText().toString();
        password = passwordField.getText().toString();

        //  number = "+91" + "9910902830";
        //   password = "sharma";

        return number.length() == 13 && password.length() > 2;
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
