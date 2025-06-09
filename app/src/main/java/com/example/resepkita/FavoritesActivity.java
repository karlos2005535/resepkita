package com.example.resepkita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.resepkita.adapter.ResepAdapter;
import com.example.resepkita.database.DatabaseHelper;
import com.example.resepkita.model.Resep;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private TextView tvEmpty;
    private ResepAdapter resepAdapter;
    private List<Resep> favoriteList;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rvFavorites = findViewById(R.id.rv_favorites);
        tvEmpty = findViewById(R.id.tv_empty);

        db = new DatabaseHelper(this);
        favoriteList = new ArrayList<>();
        // loadFavoriteData(); // Initial load will be done in onResume

        resepAdapter = new ResepAdapter(this, favoriteList);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(resepAdapter);

        resepAdapter.setOnFavoriteClickListener((resep, position) -> {
            boolean newFavoriteStatus = !resep.isFavorite(); // Toggle the favorite status
            db.updateFavoriteStatus(resep.getId(), newFavoriteStatus); // Use the correct method
            resep.setFavorite(newFavoriteStatus); // Update model immediately
            resepAdapter.notifyItemChanged(position); // Notify adapter for specific item
            loadFavoriteData(); // Reload data after toggle to remove/add from list
            checkEmptyState();  // Re-check empty state after data changes
        });

        // checkEmptyState(); // Initial check for empty state will be done in onResume
    }

    private void loadFavoriteData() {
        favoriteList.clear();
        favoriteList.addAll(db.getFavoriteResep());
        if (resepAdapter != null) {
            resepAdapter.notifyDataSetChanged();
        }
    }

    private void checkEmptyState() {
        if (favoriteList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvFavorites.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteData(); // Reload data when returning to the activity
        checkEmptyState();
    }
}