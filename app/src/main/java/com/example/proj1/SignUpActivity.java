package com.example.proj1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.proj1.utils.Database;
import com.example.proj1.utils.DatabaseHelper;
import com.example.proj1.controllers.UserController;

public class SignUpActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;
    //    Komponen UI
    private EditText emailField;
    private EditText passwordField;
    private EditText nameField;
    private EditText phoneField;
    private Button signupButton;
    private View mProgressView;
    private View mLoginFormView;
    public final static String TAG_ID = "id";
    public final static String TAG_EMAIL = "email";
    public final static String TAG_NAME = "name";
    public final static String TAG_PHONE = "phone";
    public final static String TAG_PASSWORD = "password";
    SharedPreferences sharedpreferences;
    Boolean session = false;
    String id, email, name;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        ((ActionBar) actionBar).hide();

        initiateCoreApp();
//        checkSession();

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        nameField = findViewById(R.id.nameBox);
        phoneField = findViewById(R.id.phone_num);
        signupButton = findViewById(R.id.register_btn);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }
// Reset errors.
        emailField.setError(null);
        passwordField.setError(null);
// Store values at the time of the login attempt.
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String full_name = nameField.getText().toString();
        String phone = phoneField.getText().toString();
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
        } else {
            ContentValues adminData = UserController.getInstance().getDataByEmail(email);
            if (adminData != null) {
                emailField.setError(getString(R.string.error_unavailable_email));
                focusView = emailField;
                cancel = true;
            }
        }
        if (TextUtils.isEmpty(full_name)) {
            nameField.setError(getString(R.string.error_field_required));
            focusView = nameField;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneField.setError(getString(R.string.error_field_required));
            focusView = phoneField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            ContentValues content = new ContentValues();
            content.put("email", email);
            content.put("password", password);
            content.put("name", full_name);
            content.put("phone", phone);
            int id = UserController.getInstance().register(content);
            if (id > 0) {
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow

// for very easy animations. If available, use these APIs to fade-in
// the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime =

                    getResources().getInteger(android.R.integer.config_shortAnimTime);

//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
        } else {
// The ViewPropertyAnimator APIs are not available, so simply show
// and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
//                if (mPassword.equals(admin.getAsString("password"))) {
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                    editor.putBoolean(session_status, true);
//                    editor.putString(TAG_ID, admin.getAsString("_id"));
//                    editor.putString(TAG_EMAIL, admin.getAsString("email"));
//                    editor.putString(TAG_NAME, admin.getAsString("name"));
//                    editor.putString(TAG_PHONE, admin.getAsString("phone"));
//                    editor.commit();
//                } else {
//                    return false;
//                }
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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
            showProgress(false);
        }
    }

    private void initiateCoreApp() {
        Database database = new DatabaseHelper(this);
        UserController.setDatabase(database);
    }

//    private void checkSession() {
//        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
//        session = sharedpreferences.getBoolean(session_status, false);
//        id = sharedpreferences.getString(TAG_ID, null);
//
//        email = sharedpreferences.getString(TAG_EMAIL, null);
//        name = sharedpreferences.getString(TAG_NAME, null);
//        if (session) {
//            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//            intent.putExtra(TAG_ID, id);
//            intent.putExtra(TAG_EMAIL, email);
//            intent.putExtra(TAG_NAME, name);
//            finish();
//            startActivity(intent);
//        }
//    }

    public static boolean isEmailValid(String email) {
//TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
//TODO: Replace this with your own logic
        return password.length() > 2;
    }
}
