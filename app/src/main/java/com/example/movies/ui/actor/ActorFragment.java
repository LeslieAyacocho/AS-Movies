package com.example.movies.ui.actor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorFragment extends Fragment {

    
    RecyclerView recViewActor;
    ArrayList<Actor> actors = new ArrayList<Actor>();
    ActorAdapter actorAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //recycler
        RecyclerView recViewActor = view.findViewById(R.id.recViewActor);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recViewActor.setLayoutManager(layoutManager);
        // define an adapter
        actorAdapter = new ActorAdapter((MainActivity) getActivity());
        recViewActor.setAdapter(actorAdapter);

        FloatingActionButton fabActors = view.findViewById(R.id.fabActor);
        fabActors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActorForm.class);
                startActivity(intent);
            }
        });

        getActors();
    }

    public void getActors(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.apiURL) + "actor",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray =  response.getJSONArray("actors");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                 Toast.makeText(getContext(), jsonObject.getString("fname"), Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getContext(), "test" + i, Toast.LENGTH_SHORT).show();
                                Actor actor = new Actor(
                                        jsonObject.getString("fname"),
                                        jsonObject.getString("lname"),
                                        jsonObject.getString("note"),
                                        String.valueOf(jsonObject.getInt("id")),
                                        jsonObject.getString("imgpath")
                                        );

                                actors.add(actor);
                            }

                            actorAdapter.setActors(actors);

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







