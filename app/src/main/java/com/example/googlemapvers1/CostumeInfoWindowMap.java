package com.example.googlemapvers1;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CostumeInfoWindowMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CostumeInfoWindowMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker){
        return null;
    }

    @Override
    public View getInfoContents(Marker marker){
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.mapmarker,null);
        TextView textView = view.findViewById(R.id.InfoString);
        ImageView image = view.findViewById(R.id.Image);

        InfoData infoData = (InfoData) marker.getTag();

        int imageId =  context.getResources().getIdentifier(infoData.getImage().toLowerCase(),"drawable",context.getPackageName());

        image.setImageResource(imageId);

        textView.setText(infoData.getInfoString());

        return view;


    }

}
