package spcba.com.smartchatv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnChangePassword;
    private EditText inputUsername;
    private EditText inputPassword;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final String username = inputUsername.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!username.isEmpty() && !password.isEmpty()) {
                    checkLogin(username, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });


        // Link to Register Screen
        btnChangePassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                  //Code here
            }
        });
    }

    private void checkLogin(final String username, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("UserName", username);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, "http://teamagilevm.eastasia.cloudapp.azure.com:8080/smartchat/api/v1/loginuser.php", new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = response;

                            // user successfully logged in
                            String _password = jObj.getString("Password");
                            String firstname = jObj.getString("FirstName");

                            if(_password.equalsIgnoreCase(password)){
                                // login user check to database
                                // Create login session
                                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("firstname", firstname);
                                startActivity(intent);
                                finish();
                            } else {
                                // Prompt user to enter credentials
                                Toast.makeText(getApplicationContext(),
                                        "Please enter the right credentials!", Toast.LENGTH_LONG)
                                        .show();
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());

                        Toast.makeText(getApplicationContext(),
                                "Invalid Employee No. or Password", Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                })

        {
        };

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsObjRequest);
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
