package org.technocracy.app.kleos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.technocracy.app.kleos.App;
import org.technocracy.app.kleos.helper.AppController;
import org.technocracy.app.kleos.helper.DatabaseHandler;
import org.technocracy.app.kleos.R;
import org.technocracy.app.kleos.helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText registerName;
    private EditText registerUsername;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerPhone;
    private EditText registerBranch;
    private EditText registerSemester;
    private EditText registerCollege;
    private EditText registerRollNo;
    private Button registerButton;
    private Button toLoginButton;
    private ProgressDialog pDialog;
    private SessionManager session;
    private DatabaseHandler db;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        registerName = (EditText) findViewById(R.id.register_name_editText);
        registerUsername = (EditText) findViewById(R.id.register_username_editText);
        registerEmail = (EditText) findViewById(R.id.register_email_editText);
        registerPassword = (EditText) findViewById(R.id.register_password_editText);
        registerPhone = (EditText) findViewById(R.id.register_phone_editText);
        registerBranch = (EditText) findViewById(R.id.register_branch_editText);
        registerSemester = (EditText) findViewById(R.id.register_semester_editText);
        registerCollege = (EditText) findViewById(R.id.register_college_editText);
        registerRollNo = (EditText) findViewById(R.id.register_rollno_editText);
        registerButton = (Button) findViewById(R.id.register_button);
        toLoginButton = (Button) findViewById(R.id.login_button);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());
        db = new DatabaseHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    HomeActivity.class);
            startActivity(intent);
            finish();
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = registerName.getText().toString().trim();
                String username = registerUsername.getText().toString().trim();
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                String phone = registerPhone.getText().toString().trim();
                String branch = registerBranch.getText().toString().trim();
                String semester = registerSemester.getText().toString().trim();
                String college = registerCollege.getText().toString().trim();
                String rollno = registerRollNo.getText().toString().trim();

                if (!name.isEmpty() && !username.isEmpty() && !email.isEmpty() && !password.isEmpty()
                        && !phone.isEmpty() && !branch.isEmpty() && !semester.isEmpty()
                        && !college.isEmpty() && !rollno.isEmpty()) {
                    //TODO: Validation
                    registerUser(name, username, email, password, phone, branch, semester, college, rollno);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(toLogin);
                finish();
            }
        });
    }
    private void registerUser(final String name, final String username, final String email,
                              final String password, final String phone, final String branch,
                              final String semester, final String college, final String rollno) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                App.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("phone", phone);
                params.put("branch", branch);
                params.put("semester", semester);
                params.put("college", college);
                params.put("rollno", rollno);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
