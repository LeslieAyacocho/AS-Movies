package com.example.movies.ui.producer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.SessionClass;
import com.example.movies.ui.actor.ActorFragment;
import com.example.movies.ui.movie.MovieFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProducerForm extends AppCompatActivity {

    EditText edtTxtProducerName,edtTxtProducerEmail;
    Button btnProducerSave;
    String id, name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_form);
        edtTxtProducerName = findViewById(R.id.editTextProducerName);
        edtTxtProducerEmail = findViewById(R.id.editTextProducerEmail);
        btnProducerSave = findViewById(R.id.buttonProducerSave);

        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");

            edtTxtProducerName.setText(name);
            edtTxtProducerEmail.setText(email);
        }

        btnProducerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendProducerData();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SessionClass.FRAGMENT = new ProducerFragment();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void sendProducerData() {
        Toast.makeText(getApplicationContext(),"SAVE PRODUCER",Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name" , edtTxtProducerName.getText().toString());
            jsonObject.put("email" , edtTxtProducerEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getString(R.string.apiURL) + "producer";
        if(id != null){
            url += "/"+id;
        }
//        // (statement) ? true:false
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                (id != null) ? Request.Method.PUT:Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(ProducerForm.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            SessionClass.FRAGMENT = new ProducerFragment();
                            Intent intent = new Intent(ProducerForm.this, MainActivity.class);
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
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}