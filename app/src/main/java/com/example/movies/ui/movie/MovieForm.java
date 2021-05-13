package com.example.movies.ui.movie;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.movies.ui.genre.Genre;
import com.example.movies.ui.producer.Producer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieForm extends AppCompatActivity {

    Spinner spnrMovieGenre, spnrMovieProducer;
    EditText edtTxtMovieTitle, edtTxtMovieRelease,edtTxtMovieDescription;
    Button btnMovieSave , btnMovieSelect;
    String id, title, release, description, imgpath;
    ArrayList<Genre> genreList = new ArrayList<>();
    ArrayList<Producer> producerList = new ArrayList<>();
    ArrayList<String> stringGenre = new ArrayList<>();
    ArrayList<String> stringProducer = new ArrayList<>();
    ArrayAdapter<String> stringAdapterGenre;
    ArrayAdapter<String> stringAdapterProducer;
    Integer genreid, producerid;
    Integer requestImage = 1;
    Bitmap imageMedia;
    ImageView imgMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_form);

        edtTxtMovieTitle = findViewById(R.id.editTextMovieTitle);
        edtTxtMovieRelease = findViewById(R.id.editTextMovieRelease);
        edtTxtMovieDescription = findViewById(R.id.editTextMovieDescription);
        btnMovieSave = findViewById(R.id.buttonMovieSave);
        spnrMovieGenre = findViewById(R.id.spinnerMovieGenre);
        spnrMovieProducer = findViewById(R.id.spinnerMovieProducer);
        imgMoviePoster = findViewById(R.id.imgMoviePoster);
        btnMovieSelect = findViewById(R.id.buttonMovieSelect);

        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            release = getIntent().getStringExtra("release");
            description = getIntent().getStringExtra("description");

            edtTxtMovieTitle.setText(title);
            edtTxtMovieRelease.setText(release);
            edtTxtMovieDescription.setText(description);
        }

        spnrMovieGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genreid = Integer.parseInt(genreList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genreid = Integer.parseInt(genreList.get(0).getId());
            }
        });

        spnrMovieProducer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                producerid = Integer.parseInt(producerList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                producerid = Integer.parseInt(producerList.get(0).getId());
            }
        });

        btnMovieSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMovieData();

            }
        });
        getAllProducers();
        getAllGenres();


        btnMovieSelect.setOnClickListener(new View.OnClickListener() {
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
        SessionClass.FRAGMENT = new MovieFragment();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == requestImage && resultCode == RESULT_OK && data != null && data.getData() !=null){
            Uri imageMovies = data.getData();

            try {
                imageMedia = MediaStore.Images.Media.getBitmap(getContentResolver(),imageMovies);
                Picasso.get().load(imageMovies).into(imgMoviePoster);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imgToString(imageMedia);
        }
    }

    private String imgToString(Bitmap img){
        ByteArrayOutputStream arrayMovies = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG,100,arrayMovies);
        byte[] byteMovies = arrayMovies.toByteArray();
        imgpath = Base64.encodeToString(byteMovies, Base64.DEFAULT);

        return imgpath;
    }

    private void getAllGenres(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.apiURL) + "genre",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray =  response.getJSONArray("genres");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Genre genre = new Genre(
                                        jsonObject.getString("name"),
                                        String.valueOf(jsonObject.getInt("id"))
                                );

                                genreList.add(genre);
                                stringGenre.add(genre.getName());
                            }

                            stringAdapterGenre = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item, stringGenre);
                            stringAdapterGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnrMovieGenre.setAdapter(stringAdapterGenre);
                        } catch (JSONException e) {
                            Log.e("ERROR", e.toString());
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

    public void getAllProducers(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.apiURL) + "producer",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray =  response.getJSONArray("producers");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                Toast.makeText(getContext(), jsonObject.getString("email"), Toast.LENGTH_SHORT).show();
                                Producer producer = new Producer(
                                        jsonObject.getString("name"),
                                        jsonObject.getString("email"),
                                        String.valueOf(jsonObject.getInt("id"))
                                );

                                producerList.add(producer);
                                stringProducer.add(producer.getName());
                            }

                            stringAdapterProducer = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item, stringProducer);
                            stringAdapterProducer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnrMovieProducer.setAdapter(stringAdapterProducer);

                        } catch (JSONException e) {
                            Log.e("ERROR", e.toString());
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

    private void sendMovieData()  {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title" , edtTxtMovieTitle.getText().toString());
            jsonObject.put("release" , edtTxtMovieRelease.getText().toString());
            jsonObject.put("description" , edtTxtMovieDescription.getText().toString());
            jsonObject.put("genre_id" , genreid);
            jsonObject.put("producer_id" , producerid);
            jsonObject.put("imgpath" , imgToString(imageMedia));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = getString(R.string.apiURL) + "movie";
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
                            Toast.makeText(MovieForm.this, response.getString("message"), Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(ActorForm.this, MainActivity.class);
                            SessionClass.FRAGMENT = new MovieFragment();
                            Intent intent = new Intent(MovieForm.this , MainActivity.class);
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