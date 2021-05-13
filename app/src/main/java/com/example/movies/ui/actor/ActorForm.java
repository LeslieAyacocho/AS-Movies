package com.example.movies.ui.actor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActorForm extends AppCompatActivity {

    ImageView imgActorImage;
    EditText edtTxtActorFname, edtTxtActorLname, edtTxtActorNote;
    Button btnActorSave, btnActorSelect;
    String id, fname,lname,note,imgpath;
    Integer requestImage = 1;
    Bitmap imageMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_form);
        edtTxtActorFname = findViewById(R.id.editTextMovieTitle);
        edtTxtActorLname = findViewById(R.id.editTextActorLname);
        edtTxtActorNote = findViewById(R.id.editTextMovieDescription);
        imgActorImage = findViewById(R.id.imgActorImage);
        btnActorSave = findViewById(R.id.buttonActorSave);
        btnActorSelect = findViewById(R.id.buttonActorSelect);

        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            fname = getIntent().getStringExtra("fname");
            lname = getIntent().getStringExtra("lname");
            note = getIntent().getStringExtra("note");

            edtTxtActorFname.setText(fname);
            edtTxtActorLname.setText(lname);
            edtTxtActorNote.setText(note);
        }

        btnActorSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActorData();

            }
        });

        btnActorSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"imgpath"),requestImage);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        SessionClass.FRAGMENT = new ActorFragment();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == requestImage && resultCode == RESULT_OK && data != null && data.getData() !=null){
            Uri imageActors = data.getData();

                try {
                    imageMedia = MediaStore.Images.Media.getBitmap(getContentResolver(),imageActors);
                    Picasso.get().load(imageActors).into(imgActorImage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgToString(imageMedia);
        }
    }

    private String imgToString(Bitmap img){
        ByteArrayOutputStream arrayActors = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG,100,arrayActors);
        byte[] byteActors = arrayActors.toByteArray();
       imgpath = Base64.encodeToString(byteActors, Base64.DEFAULT);

       return imgpath;
    }


    private void sendActorData()  {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imgpath" , imgToString(imageMedia));
            jsonObject.put("fname" , edtTxtActorFname.getText().toString());
            jsonObject.put("lname" , edtTxtActorLname.getText().toString());
            jsonObject.put("note" , edtTxtActorNote.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getString(R.string.apiURL) + "actor";
        if(id != null){
            url += "/"+ id;
        }
        // (statement) ? true:false
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                (id != null) ? Request.Method.PUT:Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(ActorForm.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            SessionClass.FRAGMENT = new ActorFragment();
                            Intent intent = new Intent(ActorForm.this , MainActivity.class);
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
                })
        {
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