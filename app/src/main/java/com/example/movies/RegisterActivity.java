package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movies.ui.actor.ActorForm;
import com.example.movies.ui.actor.ActorFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextView txtVwLogin;
    EditText edtTxtRegisterName, edtTxtRegisterEmail, edtTxtRegisterPassword;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtVwLogin = findViewById(R.id.textViewLogin);
        edtTxtRegisterName = findViewById(R.id.editTextRegisterName);
        edtTxtRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        edtTxtRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        btnRegister = findViewById(R.id.buttonRegister);

        txtVwLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterAccount();
            }
        });
    }

    private void RegisterAccount()  {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name" , edtTxtRegisterName.getText().toString());
            jsonObject.put("email" , edtTxtRegisterEmail.getText().toString());
            jsonObject.put("password" , edtTxtRegisterPassword.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getString(R.string.apiURL) + "auth/register";

        // (statement) ? true:false
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(RegisterActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
                            startActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volleyError", error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}