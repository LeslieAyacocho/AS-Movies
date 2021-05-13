package com.example.movies.ui.actor;


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
import androidx.appcompat.view.menu.MenuView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ViewHolder> {

    Context context;
    ArrayList<Actor> actors = new ArrayList<Actor>();

    public ActorAdapter(Context context) {
        this.context = context;
    }

    public void setActors(ArrayList<Actor> actors) {
        this.actors = actors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_actor, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorAdapter.ViewHolder holder, int position) {

        if(actors.get(position).getImgpath() != ""){
            Picasso.get().load("http://192.168.0.36:8000/" + actors.get(position).getImgpath()).into(holder.imgVwActorItem);
        }

        holder.txtActorsName.setText(actors.get(position).getFname() + " " + actors.get(position).getLname());
//        holder.txtActorsLname.setText(actors.get(position).getLname());
//        holder.txtActorsNote.setText(actors.get(position).getNote());

        holder.btnActorEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActorForm.class);
                intent.putExtra("id", actors.get(position).getId());
                intent.putExtra("fname", actors.get(position).getFname());
                intent.putExtra("lname", actors.get(position).getLname());
                intent.putExtra("note", actors.get(position).getNote());
                intent.putExtra("imgpath", actors.get(position).getImgpath());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.crdActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity) context);
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_actor,null);

                ImageView imgDialogActor = view.findViewById(R.id.imageViewDialogActor);
                TextView txtDialogActorName = view.findViewById(R.id.textViewDialogActorName);
                TextView txtDialogActorNote = view.findViewById(R.id.textViewDialogActorNote);

                Picasso.get().load("http://192.168.0.36:8000/" + actors.get(position).getImgpath()).into(imgDialogActor);
                txtDialogActorName.setText(actors.get(position).getFname() + " " + actors.get(position).getLname());
                txtDialogActorNote.setText(actors.get(position).getNote());

                builder.setView(view);

                builder.setNegativeButton("back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        holder.btnActorDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteActor(actors.get(position).getId(), position);
            }
        });

    }

    private void deleteActor(String id, final int index) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.apiURL) + "actor/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            actors.remove(index);
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
        return actors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtActorsName, txtActorsLname, txtActorsNote;
        Button btnActorEdit,btnActorDelete;
        ImageView imgVwActorItem;
        CardView crdActor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtActorsName = itemView.findViewById(R.id.textActorsName);
//            txtActorsLname = itemView.findViewById(R.id.textActorsLname);
//            txtActorsNote = itemView.findViewById(R.id.textActorsNote);
            btnActorEdit = itemView.findViewById(R.id.buttonActorEdit);
            btnActorDelete = itemView.findViewById(R.id.buttonActorDelete);
            imgVwActorItem = itemView.findViewById(R.id.imgViewActorItem);
            crdActor = itemView.findViewById(R.id.cardActor);
        }
    }
}