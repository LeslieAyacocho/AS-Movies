package com.example.movies.ui.movie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    ArrayList<Movie> movies = new ArrayList<Movie>();
    ArrayList<Role> roles = new ArrayList<Role>();

    public MovieAdapter(Context context){

        this.context = context;
    }

    public void setMovies(ArrayList<Movie> movies){
        this.movies = movies;
        notifyDataSetChanged();
    }


    public void setRoles(ArrayList<Role> roles){
        this.roles = roles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent,false);
        return new MovieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        if(movies.get(position).getImgpath() != ""){
            Picasso.get().load("http://192.168.0.36:8000/" + movies.get(position).getImgpath()).into(holder.imgVwMovieItem);
        }

        holder.txtMovieTitle.setText(movies.get(position).getTitle());
        holder.txtMovieRelease.setText(movies.get(position).getRelease());
//        holder.txtMovieDescription.setText(movies.get(position).getDescription());
//        holder.txtMovieGenre.setText(movies.get(position).getGenre());
//        holder.txtMovieProducer.setText(movies.get(position).getProducer());

        holder.btnMovieEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieForm.class);
                intent.putExtra("id", movies.get(position).getId());
                intent.putExtra("title", movies.get(position).getTitle());
                intent.putExtra("release", movies.get(position).getRelease());
                intent.putExtra("description", movies.get(position).getDescription());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.crdMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build = new AlertDialog.Builder( (MainActivity) context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_movie,null);
                View viewCast = LayoutInflater.from(context).inflate(R.layout.movie_cast,null);

                RecyclerView recViewMovieCast = view.findViewById(R.id.recViewMovieCast);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                recViewMovieCast.setLayoutManager(layoutManager);

                MovieCastAdapter rolesAdapter = new MovieCastAdapter((MainActivity) context);
                recViewMovieCast.setAdapter(rolesAdapter);



                ImageView imgDialogMovie = view.findViewById(R.id.imageViewDialogMovie);
                TextView txtDialogMovieTitle = view.findViewById(R.id.textViewDialogMovieTitle);
                TextView txtDialogMovieRelease = view.findViewById(R.id.textViewDialogMovieRelease);
                TextView txtDialogMovieGenre = view.findViewById(R.id.textViewDialogMovieGenre);
                TextView txtDialogMovieProducer = view.findViewById(R.id.textViewDialogMovieProducer);
                TextView txtDialogMovieDescription = view.findViewById(R.id.textViewDialogMovieDescription);

                Picasso.get().load("http://192.168.0.36:8000/" + movies.get(position).getImgpath()).into(imgDialogMovie);
                txtDialogMovieTitle.setText(movies.get(position).getTitle());
                txtDialogMovieRelease.setText(movies.get(position).getRelease());
                txtDialogMovieDescription.setText(movies.get(position).getDescription());
                txtDialogMovieGenre.setText(movies.get(position).getGenre());
                txtDialogMovieProducer.setText(movies.get(position).getProducer());
//                txtMovieCastName.setText(roles.get(position).getFname() +" "+ roles.get(position).getLname());


                build.setView(view);

                build.setNegativeButton("back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                build.create().show();
            }
        });

        holder.btnMovieDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMovie(movies.get(position).getId(), position);
            }
        });
    }

    private void deleteMovie(String id, final int index) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.apiURL) + "movie/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            movies.remove(index);
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
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtMovieTitle,txtMovieRelease, txtMovieDescription, txtMovieGenre, txtMovieProducer;
        Button btnMovieEdit, btnMovieDelete;
        ImageView imgVwMovieItem;
        CardView crdMovie;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMovieTitle = itemView.findViewById(R.id.textMovieTitle);
            txtMovieRelease = itemView.findViewById(R.id.textMovieRelease);
//            txtMovieDescription = itemView.findViewById(R.id.textDescription);
//            txtMovieGenre = itemView.findViewById(R.id.textMovieGenre);
//            txtMovieProducer = itemView.findViewById(R.id.textMovieProducer);
            btnMovieEdit = itemView.findViewById(R.id.buttonMovieEdit);
            btnMovieDelete = itemView.findViewById(R.id.buttonMovieDelete);
            imgVwMovieItem = itemView.findViewById(R.id.imgViewMovieItem);
            crdMovie = itemView.findViewById(R.id.cardMovie);
        }
    }
}
