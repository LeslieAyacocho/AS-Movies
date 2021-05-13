package com.example.movies.ui.producer;

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
import com.example.movies.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProducerFragment extends Fragment {

    RecyclerView recViewProducer;
    ArrayList<Producer> producers = new ArrayList<Producer>();
    ProducerAdapter producersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_producer, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //recycler
        RecyclerView recViewProducer = view.findViewById(R.id.recViewProducer);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recViewProducer.setLayoutManager(layoutManager);
        //define an adapter
        producersAdapter = new ProducerAdapter(getActivity().getApplicationContext());
        recViewProducer.setAdapter(producersAdapter);

        FloatingActionButton fabProducers = view.findViewById(R.id.fabProducer);
        fabProducers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProducerForm.class);
                startActivity(intent);
            }
        });

        getProducers();
    }

    public void getProducers(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                                producers.add(producer);
                            }

                            producersAdapter.setProducers(producers);

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