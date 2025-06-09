package com.example.resepkita.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepkita.R;
import com.example.resepkita.model.Resep;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResepAdapter extends RecyclerView.Adapter<ResepAdapter.ResepViewHolder> {

    private static final String TAG = "ResepAdapter";

    private Context context;
    private List<Resep> resepList;
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteListener;
    private OnItemLongClickListener longClickListener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Resep resep);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Resep resep, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Resep resep, int position);
    }

    public interface OnEditClickListener {
        void onEditClick(Resep resep, int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Resep resep, int position);
    }

    public ResepAdapter(Context context, List<Resep> resepList) {
        this.context = context;
        this.resepList = resepList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setOnEditClickListener(OnEditClickListener editListener) {
        this.editListener = editListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ResepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resep, parent, false);
        return new ResepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResepViewHolder holder, int position) {
        Resep currentResep = resepList.get(position);

        holder.tvNamaResep.setText(currentResep.getNamaResep());
        holder.tvBahan.setText(currentResep.getBahan()); // Pastikan ini diset

        // Load image using Picasso
        String imageUrl = currentResep.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // Picasso can load from content://, file://, or http(s)://
                Picasso.get().load(Uri.parse(imageUrl))
                        .placeholder(R.drawable.ic_image_placeholder) // Gambar placeholder
                        .error(R.drawable.ic_broken_image) // Gambar error jika gagal
                        .into(holder.ivResep);
                Log.d(TAG, "Loading image for Resep: " + currentResep.getNamaResep() + " from URL: " + imageUrl);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image with Picasso for Resep: " + currentResep.getNamaResep() + ", URL: " + imageUrl, e);
                holder.ivResep.setImageResource(R.drawable.ic_broken_image); // Fallback to broken image
            }
        } else {
            holder.ivResep.setImageResource(R.drawable.ic_image_placeholder); // Default placeholder
            Log.d(TAG, "No image URL for Resep: " + currentResep.getNamaResep() + ". Using placeholder.");
        }

        // Set favorite icon
        if (currentResep.isFavorite()) {
            holder.ibFavorite.setImageResource(R.drawable.ic_favorite); // Filled heart
        } else {
            holder.ibFavorite.setImageResource(R.drawable.ic_favorite_border); // Border heart
        }

        // Set click listener for item view
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentResep);
                Log.d(TAG, "Item clicked: " + currentResep.getNamaResep());
            }
        });

        // Set long click listener for item view
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(currentResep, position);
                Log.d(TAG, "Item long clicked: " + currentResep.getNamaResep());
                return true; // Consume the long click
            }
            return false;
        });

        // Set click listener for favorite button
        holder.ibFavorite.setOnClickListener(v -> {
            if (favoriteListener != null) {
                favoriteListener.onFavoriteClick(currentResep, position);
                Log.d(TAG, "Favorite button clicked for item: " + currentResep.getNamaResep() + ", current favorite status: " + currentResep.isFavorite());
            }
        });

        // Set click listener for edit button
        holder.ibEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(currentResep, position);
                Log.d(TAG, "Edit button clicked for item: " + currentResep.getNamaResep());
            }
        });

        // Set click listener for delete button
        holder.ibDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(currentResep, position);
                Log.d(TAG, "Delete button clicked for item: " + currentResep.getNamaResep());
            }
        });
    }

    @Override
    public int getItemCount() {
        return resepList.size();
    }

    public static class ResepViewHolder extends RecyclerView.ViewHolder {
        ImageView ivResep;
        TextView tvNamaResep;
        TextView tvBahan; // Make sure this is in your item_resep.xml and initialized
        ImageButton ibFavorite;
        ImageButton ibEdit;
        ImageButton ibDelete;

        public ResepViewHolder(@NonNull View itemView) {
            super(itemView);
            ivResep = itemView.findViewById(R.id.iv_resep);
            tvNamaResep = itemView.findViewById(R.id.tv_nama_resep);
            tvBahan = itemView.findViewById(R.id.tv_bahan); // Inisialisasi tvBahan
            ibFavorite = itemView.findViewById(R.id.ib_favorite);
            ibEdit = itemView.findViewById(R.id.ib_edit);
            ibDelete = itemView.findViewById(R.id.ib_delete);
        }
    }

    public void updateResepList(List<Resep> newResepList) {
        this.resepList.clear();
        this.resepList.addAll(newResepList);
        notifyDataSetChanged();
        Log.d(TAG, "Resep list updated. New size: " + newResepList.size());
    }
}