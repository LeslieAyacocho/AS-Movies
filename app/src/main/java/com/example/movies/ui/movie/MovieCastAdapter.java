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
import com.example.movies.ui.actor.Actor;
import com.example.movies.ui.actor.ActorForm;
import com.example.movies.ui.roles.Role;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieCastAdapter extends RecyclerView.Adapter<MovieCastAdapter.ViewHolder> {

    Context context;
    ArrayList<Role> roles = new ArrayList<Role>();

    public MovieCastAdapter(Context context){
        this.context = context;
    }

    public void setRoles(ArrayList<Role> roles){
        this.roles = roles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieCastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_cast, parent,false);
        return new MovieCastAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.txtMovieCastName.setText(roles.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return roles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView  txtMovieCastName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMovieCastName = itemView.findViewById(R.id.textMovieCastName);

        }
    }
}