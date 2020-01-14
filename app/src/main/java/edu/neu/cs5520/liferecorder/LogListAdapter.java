package edu.neu.cs5520.liferecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.neu.cs5520.liferecorder.MainActivity.resize;

public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.ViewHolder> {
    private Context context;
    private Map<Date, List<VenueItem>> itemsMap;
    private List<Date> dates;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public Date getIthDate(int position){
        return dates.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }


    public LogListAdapter(Context context, Map<Date, List<VenueItem>> itemsMap ) {
        this.context = context;
        this.itemsMap = itemsMap;
        this.dates = new ArrayList<>(itemsMap.keySet());
        Collections.reverse(this.dates);
    }

    @NonNull
    @Override
    public LogListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_log_history, parent,false);
        ViewHolder viewHolder = new ViewHolder(view, mlistener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogListAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return itemsMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, locationTextView, placeTextView;
        HorizontalScrollView imageScrollView;
        LinearLayout imageLayout;



        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            dateTextView = (TextView) itemView.findViewById(R.id.logDate);
            locationTextView = (TextView) itemView.findViewById(R.id.logLocation);
            placeTextView = (TextView) itemView.findViewById(R.id.logPlace);
            imageScrollView = (HorizontalScrollView) itemView.findViewById(R.id.imageScrollView);
            imageLayout = (LinearLayout) itemView.findViewById(R.id.imageGallery);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }

        void bind(final int position) {
            Date date = dates.get(position);
            List<VenueItem> items = itemsMap.get(date);
            dateTextView.setText(Constants.DATE_FORMAT.format(date));


            StringBuilder locationBuilder = new StringBuilder("Location: In ");
            StringBuilder placeBuilder = new StringBuilder("At ");

            Set<String> locations = new HashSet<>();
            Set<String> places = new HashSet<>();
            List<Uri> photos = new ArrayList<>();

            for(VenueItem item: items){
                String[] addressLines = item.address.split(",");
                if(addressLines.length >= 3) {
                    System.out.println(addressLines[addressLines.length-2]);
                    locations.add(addressLines[addressLines.length - 3]+", "+addressLines[addressLines.length-2].split(" ")[1]);
                }
                places.add(item.name);
                if(item.photoUri != null) {
                    photos.add(item.photoUri);
                }
            }

            for(String l: locations){
                locationBuilder.append(l+"; ");
            }
            for(String p: places){
                placeBuilder.append(p+", ");
            }

            locationTextView.setText(locationBuilder.toString());
            placeTextView.setText(placeBuilder.toString());

            if(photos.size() == 0){
                imageScrollView.setVisibility(View.GONE);
            }else{
                LayoutInflater inflater = LayoutInflater.from(context);
                for(Uri uri : photos) {
                    View photoView = inflater.inflate(R.layout.layout_image_gallery,imageLayout,false);
                    ImageView imageView = photoView.findViewById(R.id.imageView);

                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(getPathFromUri(uri)),
                            128, 128);
                    imageView.setImageBitmap(thumbImage);
                    imageView.setPadding(10,0,10,0);
                    imageLayout.addView(photoView);
                }
            }



            /*
            if (venueItem != null) {
                date.setText(new SimpleDateFormat("MM/dd/yyyy").format(venueItem.getCreated()));
                locationTextView.setText("Description:"+venueItem.getDescription());
                placeTextView.setText(venueItem.getAddress());
                if(venueItem.photoUri == null) {
                    imageScrollView.setVisibility(View.GONE);
                    System.out.println("imagescrollView gone!!!!!!!!!!!!!!!!!!!!");
                }else{
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), venueItem.photoUri);
                        Bitmap resizemap = resize(bitmap,142,142);
                        imageView4.setImageBitmap(bitmap);
                        imageView5.setImageBitmap(bitmap);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

             */
        }


        private String getPathFromUri(Uri uri) {
            String prefix = "/storage/emulated/0/Android/data/edu.neu.cs5520.liferecorder/files/Pictures/"; // TODO: Remove Hardcoded Path
            String fileName = uri.getPath().split("/")[2];
            return prefix + fileName;
        }


    }

}
