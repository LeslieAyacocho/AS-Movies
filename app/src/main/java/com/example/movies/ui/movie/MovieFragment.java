package com.example.movies.ui.movie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.ui.actor.ActorForm;
import com.example.movies.ui.roles.Role;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieFragment extends Fragment {

    RecyclerView recViewMovie;
    ArrayList<Movie> movies = new ArrayList<Movie>();
    ArrayList<Role> roles = new ArrayList<Role>();
    MovieAdapter moviesAdapter;
    MovieCastAdapter moviesCastAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //recycler
        RecyclerView recViewMovie = view.findViewById(R.id.recViewMovie);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recViewMovie.setLayoutManager(layoutManager);
        //define an adapter
        moviesAdapter = new MovieAdapter((MainActivity) getActivity());
        recViewMovie.setAdapter(moviesAdapter);

        FloatingActionButton fabMovies = view.findViewById(R.id.fabMovie);
        fabMovies.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MovieForm.class);
                startActivity(intent);
            }
        });
        getMovies();
    }

    public void getMovies(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.apiURL) + "movie",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray =  response.getJSONArray("movies");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject jsonGenre =  jsonObject.getJSONObject("genre");
                                JSONObject jsonProducer =  jsonObject.getJSONObject("producer");
                                JSONArray jsonRoles =  jsonObject.getJSONArray("roles");
//                                Toast.makeText(getContext(), jsonObject.getString("roles"), Toast.LENGTH_SHORT).show();
                                Movie movie = new Movie(
                                        jsonObject.getString("title"),
                                        jsonObject.getString("release"),
                                        jsonObject.getString("description"),
                                        jsonGenre.getString("name"),
                                        jsonProducer.getString("name"),
                                        String.valueOf(jsonObject.getInt("id")),
                                        jsonObject.getString("imgpath")
                                );

                                for (int j = 0; j < jsonRoles.length(); j++) {
                                    JSONObject jsonRolesObj = jsonRoles.getJSONObject(j);
                                    JSONObject jsonActorRole =  jsonRolesObj.getJSONObject("actor");
//                                    Toast.makeText(getContext(), jsonRolesObj.getString("name"), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getContext(), jsonActorRole.getString("fname"), Toast.LENGTH_SHORT).show();
                                    Role role = new Role(
                                            jsonRolesObj.getString("name"),
                                            jsonActorRole.getString("fname"),
                                            jsonActorRole.getString("lname"),
                                            jsonActorRole.getString("note"),
                                            jsonActorRole.getString("imgpath")
                                    );

                                    roles.add(role);
                                }

                                moviesAdapter.setRoles(roles);


                                movies.add(movie);
                            }

                            moviesAdapter.setMovies(movies);

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
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}