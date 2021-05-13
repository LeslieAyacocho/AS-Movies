package com.example.movies.ui.genre;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    Context context;
    ArrayList<Genre> genres = new ArrayList<>();

    public GenreAdapter(Context context){

        this.context = context;
    }

    public void setGenres(ArrayList<Genre> genres){
        this.genres = genres;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent,false);
        return new GenreAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.ViewHolder holder, int position) {
        holder.txtGenresName.setText(genres.get(position).getName());

        holder.btnGenreEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GenreForm.class);
                intent.putExtra("id", genres.get(position).getId());
                intent.putExtra("name", genres.get(position).getName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.btnGenreDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGenre(genres.get(position).getId(), position);
            }
        });
    }

    private void deleteGenre(String id, final int index) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.apiURL) + "genre/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            genres.remove(index);
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
                })
        {
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
        return genres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtGenresName;
        Button btnGenreEdit,btnGenreDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGenresName = itemView.findViewById(R.id.textGenreName);
            btnGenreEdit = itemView.findViewById(R.id.buttonGenreEdit);
            btnGenreDelete = itemView.findViewById(R.id.buttonGenreDelete);

        }
    }
}
