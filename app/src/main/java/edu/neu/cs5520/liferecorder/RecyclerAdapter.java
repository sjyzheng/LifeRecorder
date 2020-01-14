package edu.neu.cs5520.liferecorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<VenueItem> pictures;
    private SimpleDateFormat newFormat;
    private static int thumbnailSize = 64;



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView createdTime;
        public TextView createdLocation;
        public View layout;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            createdTime = (TextView) v.findViewById(R.id.firstLine);
            createdLocation = (TextView) v.findViewById(R.id.secondLine);
            imageView = (ImageView) v.findViewById(R.id.image);
        }

        public View getLayout() {
            return this.layout;
        }
    }

    public void add(int position, VenueItem item) {
        pictures.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        pictures.remove(position);
        notifyItemRemoved(position);
    }

    public RecyclerAdapter(List<VenueItem> data) {
        pictures = data.size() == 0 ? new ArrayList<VenueItem>() : data;
        newFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext()
        );
        View v = inflater.inflate(R.layout.activity_row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Log.e("photoActivity", "position: " + position);
        final VenueItem venueItem = pictures.get(position);
        holder.createdTime.setText(newFormat.format(venueItem.created));
        holder.getLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri apkURI = venueItem.photoUri;
                intent.setDataAndType(apkURI, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                v.getContext().startActivity(intent);
            }
        });
        holder.createdLocation.setText("Location: " + venueItem.address);
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(getPathFromUri(venueItem.photoUri)),
                thumbnailSize, thumbnailSize);
        holder.imageView.setImageBitmap(thumbImage);

    }

    private String getPathFromUri(Uri uri) {
        String prefix = "/storage/emulated/0/Android/data/edu.neu.cs5520.liferecorder/files/Pictures/"; // TODO: Remove Hardcoded Path
        String fileName = uri.getPath().split("/")[2];
        return prefix + fileName;
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

}
