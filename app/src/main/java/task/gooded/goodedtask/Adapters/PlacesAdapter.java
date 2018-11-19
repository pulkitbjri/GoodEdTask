package task.gooded.goodedtask.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import task.gooded.goodedtask.Activities.DetailsActivity;
import task.gooded.goodedtask.Place;
import task.gooded.goodedtask.R;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

    Context context;
    ArrayList<Place> places;

    public PlacesAdapter(Context context, ArrayList<Place> places)
    {
        this.places=places;
        this.context=context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.place, viewGroup, false);

        return new MyViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(places.get(i).getName());
        myViewHolder.desc.setText(places.get(i).getCategory());


    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            desc=itemView.findViewById(R.id.desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,DetailsActivity.class)
                            .putExtra("id",places.get(getAdapterPosition()).getId()));
                }
            });
        }
    }
}
