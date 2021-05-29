package com.example.proj1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.proj1.controllers.UserController;
import com.example.proj1.utils.Database;
import com.example.proj1.utils.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    private EditText emailField;
    private EditText passwordField;
    private Button loginBtn;
    public final static String TAG_ID = "_id";
    public final static String TAG_EMAIL = "email";
    public final static String TAG_NAME = "name";
    public final static String TAG_PHONE = "phone";
    public final static String TAG_PASSWORD = "password";
    Boolean session = false;
    String id, email, name;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn = findViewById(R.id.login_btn);
        Button btn1 = findViewById(R.id.signup_btn);
        emailField = findViewById(R.id.email_Login);
        passwordField = findViewById(R.id.password_Login);

        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0){
                attemptLogin();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        ((ActionBar) actionBar).hide();

        initiateCoreApp();
        checkSession();
    }

    private void initiateCoreApp() {
        Database database = new DatabaseHelper(this);
        UserController.setDatabase(database);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
// Reset errors.
        emailField.setError(null);
        passwordField.setError(null);
// Store values at the time of the login attempt.
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        boolean cancel = false;
        View focusView = null;
// Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordField.setError(getString(R.string.error_invalid_password));
            focusView = passwordField;
            cancel = true;
        }
// Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.error_field_required));
            focusView = emailField;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailField.setError(getString(R.string.error_invalid_email));
            focusView = emailField;
            cancel = true;
        }
        if (cancel) {
// There was an error; don't attempt login and focus the first
// form field with an error.
            focusView.requestFocus();
        } else {
// Show a progress spinner, and kick off a background task to
// perform the user login attempt.
//            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void checkSession() {
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);

        email = sharedpreferences.getString(TAG_EMAIL, null);
        name = sharedpreferences.getString(TAG_NAME, null);
        if (session) {
            Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_EMAIL, email);
            intent.putExtra(TAG_NAME, name);
            finish();
            startActivity(intent);
        }
    }

    public static boolean isEmailValid(String email) {
//TODO: Replace this with your own logic
        return email.contains("@");
    }
    public static boolean isPasswordValid(String password) {
//TODO: Replace this with your own logic
        return password.length() > 2;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private ContentValues admin;
        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
// TODO: attempt authentication against a network service.
            admin = UserController.getInstance().getDataByEmail(mEmail);

            if (admin != null) {
                if (mPassword.equals(admin.getAsString("password"))) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(session_status, true);
                    editor.putString(TAG_ID, admin.getAsString("_id"));
                    editor.putString(TAG_EMAIL, admin.getAsString("email"));
                    editor.putString(TAG_NAME, admin.getAsString("name"));
                    editor.putString(TAG_PHONE, admin.getAsString("phone"));
                    editor.commit();
                } else {
                    return false;
                }
                return true;
            }
            return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
//            showProgress(false);
            if (success) {
                Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                intent.putExtra(TAG_ID, id);
                intent.putExtra(TAG_EMAIL, email);
                if (admin != null) {
                    intent.putExtra(TAG_NAME, admin.getAsString("name"));
                }
                finish();
                startActivity(intent);
            } else {
                passwordField.setError(getString(R.string.error_incorrect_password));
                passwordField.requestFocus();
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
//            showProgress(false);
        }
    }

}
