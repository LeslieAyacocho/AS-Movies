package com.example.movies.ui.producer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movies.R;
import com.example.movies.ui.actor.ActorForm;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProducerAdapter extends RecyclerView.Adapter<ProducerAdapter.ViewHolder> {

    Context context;
    ArrayList<Producer> producers = new ArrayList<>();


    public ProducerAdapter(Context context){

        this.context = context;
    }

    public void setProducers(ArrayList<Producer> producers){
        this.producers = producers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProducerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producer, parent,false);
        return new ProducerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProducerAdapter.ViewHolder holder, int position) {
        holder.txtProducersName.setText(producers.get(position).getName());
        holder.txtProducersEmail.setText(producers.get(position).getEmail());

        holder.btnProducerEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProducerForm.class);
                intent.putExtra("id", producers.get(position).getId());
                intent.putExtra("name", producers.get(position).getName());
                intent.putExtra("email", producers.get(position).getEmail());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.btnProducerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProducer(producers.get(position).getId(), position);
            }
        });
    }

    private void deleteProducer(String id, final int index) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.apiURL) + "producer/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            producers.remove(index);
                            notifyDataSetChanged();
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
                SharedPreferences sharedPreferences = context.getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return producers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtProducersName,txtProducersEmail;
        Button btnProducerEdit, btnProducerDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProducersName = itemView.findViewById(R.id.textProducersName);
            txtProducersEmail = itemView.findViewById(R.id.textProducersEmail);
            btnProducerEdit = itemView.findViewById(R.id.buttonProducerEdit);
            btnProducerDelete = itemView.findViewById(R.id.buttonProducerDelete);

        }
    }

}
