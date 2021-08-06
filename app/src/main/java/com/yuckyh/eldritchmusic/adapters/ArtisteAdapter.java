package com.yuckyh.eldritchmusic.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.models.Artiste;
import com.yuckyh.eldritchmusic.utils.BitmapUtil;

import java.util.ArrayList;

public class ArtisteAdapter extends RecyclerView.Adapter<ArtisteAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<Artiste> mArtistes;

    public ArtisteAdapter(Context context, ArrayList<Artiste> artistes) {
        mContext = context;
        mArtistes = artistes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_artiste_card, parent, false);
        return new ArtisteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artiste artiste = mArtistes.get(position);

        BitmapUtil util = new BitmapUtil();
        Log.d("AAA", "onBindViewHolder: " + artiste.getProfileUrl());
        util.downloadImageBitmap(artiste.getProfileUrl(),
                () -> holder.mImgViewArtisteItemPic.setImageBitmap(util.getBitmap()));

        holder.mTxtViewArtisteItemName.setText(artiste.getName());
    }

    @Override
    public int getItemCount() {
        return mArtistes.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTxtViewArtisteItemName;
        private final ImageView mImgViewArtisteItemPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtViewArtisteItemName = itemView.findViewById(R.id.txtViewArtisteItemName);
            mImgViewArtisteItemPic = itemView.findViewById(R.id.imgViewArtisteItemPic);
        }
    }
}
