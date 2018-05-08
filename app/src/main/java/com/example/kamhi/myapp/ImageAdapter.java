package com.example.kamhi.myapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamhi on 26/2/2018.
 */

public class ImageAdapter extends BaseAdapter{

    ArrayList<FirebasePhoto> photos;
    private Context context;

    public ImageAdapter(Context context, ArrayList<FirebasePhoto> photos) {
        this.context = context;
        this.photos = photos;
    }

    public int getCount() {
        return photos.size();
    }

    public Object getItem(int position) {
        return photos.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,0,8,0);
            Glide.with(context).load(photos.get(position).getImage()).into(imageView);
        } else {
            imageView = (ImageView) convertView;
        }

      //  imageView.setImageResource();
        return imageView;
    }

}