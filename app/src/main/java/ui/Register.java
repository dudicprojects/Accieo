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

public class Register extends AppCompatActivity {

    @BindView(R.id.numberField)
    EditText numberField;

    @BindView(R.id.nameField)
    EditText nameField;

    @BindView(R.id.emailField)
    EditText emailField;

    @BindView(R.id.passwordField)
    EditText passwordField;


    String number, name, email, password, TAG = Register.class.getName();
    Context mContext;
    User user;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

    public void register(View v) {

        if (!isInputValid()) {
            showToast(getString(R.string.invalid_input));
            return;
        }

        if (!user.isConnected()) {
            showToast(getString(R.string.network_problem));
            return;
        }

        progressDialog.show();
        Ion.with(mContext).load(Constants.SIGNUP).setBodyParameter("name", name).setBodyParameter("phonefield", number)
                .setBodyParameter("password", password).setBodyParameter("email", email)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                progressDialog.dismiss();
                if (e != null) {
                    showToast(getString(R.string.server_problem));
                    Log.e(TAG, e.toString());
                    return;
                }

                try {
                    JSONObject json = new JSONObject(Constants.formatResponse(result));
                    user.setMobile(number);
                    showToast(json.getString(Constants.MESSAGE));
                    if (json.getString(Constants.STATUS).equals("200")) {
                        user.setToken(json.getString(Constants.TOKEN_ID));
                        startActivity(new Intent(mContext, OTP.class));
                        finish();
                    }
                } catch (JSONException e1) {
                    Log.e(TAG, e1.toString());
                    showToast(getString(R.string.server_problem));
                }


                Log.e(TAG, result);
            }
        });
    }


    boolean isInputValid() {
        number = "+91" + numberField.getText().toString();
        name = nameField.getText().toString();
        email = emailField.getText().toString();
        password = passwordField.getText().toString();
        // Log.e(TAG, number + name + email + password);
        return number.length() == 13 && name.length() > 1 && email.length() > 3 && password.length() > 2;
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
