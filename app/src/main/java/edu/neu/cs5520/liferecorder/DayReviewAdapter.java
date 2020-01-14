package edu.neu.cs5520.liferecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DayReviewAdapter extends RecyclerView.Adapter<DayReviewAdapter.ViewHolder>  {
    private Context context;
    private List<VenueItem> logList;

    public DayReviewAdapter(Context context, List<VenueItem> logList) {
        this.context = context;
        this.logList = logList;
        System.out.println("loglist in dayreviewadapter");
        System.out.println(logList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_day_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(logList.get(position));
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, location, place;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.timeCreated);
            location = (TextView) itemView.findViewById(R.id.eventLocation);
            place = (TextView) itemView.findViewById(R.id.eventPlace);
            image = (ImageView) itemView.findViewById(R.id.imageView3);

        }

        void bind(final VenueItem venueItem) {
            if (venueItem != null) {
                date.setText(new SimpleDateFormat("HH:mm").format(venueItem.getCreated()));
                String[] addressLines = venueItem.address.split(",");
                if(addressLines.length >= 3) {
                    location.setText(addressLines[addressLines.length-3]+", "+addressLines[addressLines.length-2].split(" ")[1]);
                }else location.setText("("+ Double.toString(venueItem.lat)+","+Double.toString(venueItem.lng)+")");
                place.setText("At " + venueItem.name);
                System.out.println(venueItem.photoUri);
                if(venueItem.photoUri != null){
                    image.setImageURI(venueItem.photoUri);
                }else{
                }
            }
        }
    }
}
