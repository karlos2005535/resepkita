package com.example.resepkita;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.resepkita.adapter.ResepAdapter;
import com.example.resepkita.database.DatabaseHelper;
import com.example.resepkita.model.Resep;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ResepAdapter.OnItemClickListener,
        ResepAdapter.OnFavoriteClickListener, ResepAdapter.OnItemLongClickListener,
        ResepAdapter.OnEditClickListener, ResepAdapter.OnDeleteClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView rvResep;
    private ResepAdapter resepAdapter;
    private List<Resep> resepList;
    private DatabaseHelper db;
    private ImageButton btnAddResep;
    private ImageButton btnFavorites;
    private ImageButton btnSearch;
    private ImageButton btnProfile;
    private ImageButton btnHome;

    public static final int REQUEST_ADD_RESEP = 1;
    public static final int REQUEST_EDIT_RESEP = 2;
    public static final int REQUEST_DETAIL_RESEP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvResep = findViewById(R.id.rv_resep);
        btnAddResep = findViewById(R.id.btn_add_resep);
        btnFavorites = findViewById(R.id.btn_favorites);
        btnSearch = findViewById(R.id.btn_search_main); // Make sure this ID matches your XML
        btnProfile = findViewById(R.id.btn_profile);
        btnHome = findViewById(R.id.btn_home);

        db = new DatabaseHelper(this);
        resepList = new ArrayList<>();
        resepAdapter = new ResepAdapter(this, resepList);

        rvResep.setLayoutManager(new LinearLayoutManager(this));
        rvResep.setAdapter(resepAdapter);

        // Set listeners for the adapter
        resepAdapter.setOnItemClickListener(this);
        resepAdapter.setOnFavoriteClickListener(this);
        resepAdapter.setOnItemLongClickListener(this);
        resepAdapter.setOnEditClickListener(this);
        resepAdapter.setOnDeleteClickListener(this);

        // Load initial data
        loadResepData();

        btnAddResep.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditResepActivity.class);
            startActivityForResult(intent, REQUEST_ADD_RESEP);
            Log.d(TAG, "Add recipe button clicked. Starting AddEditResepActivity.");
        });

        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
            Log.d(TAG, "Favorites button clicked. Starting FavoritesActivity.");
        });

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            Log.d(TAG, "Search button clicked. Starting SearchActivity.");
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
            Log.d(TAG, "Profile button clicked. Starting UserProfileActivity.");
        });

        btnHome.setOnClickListener(v -> {
            // Already on home, maybe refresh or do nothing
            Toast.makeText(this, "Anda sudah di halaman Beranda", Toast.LENGTH_SHORT).show();
            loadResepData(); // Refresh data if already on home
        });
    }

    private void loadResepData() {
        Log.d(TAG, "Attempting to load all recipes from database...");
        List<Resep> loadedResep = db.getAllResep();
        if (loadedResep != null && !loadedResep.isEmpty()) {
            resepList.clear();
            resepList.addAll(loadedResep);
            resepAdapter.notifyDataSetChanged();
            Log.d(TAG, "Recipes loaded successfully. Total: " + resepList.size());
        } else {
            resepList.clear(); // Clear existing data if no recipes found
            resepAdapter.notifyDataSetChanged();
            Log.d(TAG, "No recipes found in database.");
            Toast.makeText(this, "Belum ada resep. Tambahkan resep baru!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(Resep resep) {
        Intent intent = new Intent(MainActivity.this, DetailResepActivity.class);
        intent.putExtra("RESEP_ID", resep.getId());
        startActivityForResult(intent, REQUEST_DETAIL_RESEP);
        Log.d(TAG, "Resep item clicked: " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
    }

    @Override
    public void onFavoriteClick(Resep resep, int position) {
        boolean newFavoriteStatus = !resep.isFavorite();
        resep.setFavorite(newFavoriteStatus);
        db.updateFavoriteStatus(resep.getId(), newFavoriteStatus);
        resepAdapter.notifyItemChanged(position); // Update just the clicked item
        Log.d(TAG, "Favorite status changed for " + resep.getNamaResep() + " to: " + newFavoriteStatus);
        Toast.makeText(this, resep.getNamaResep() + (newFavoriteStatus ? " ditambahkan ke favorit" : " dihapus dari favorit"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(Resep resep, int position) {
        // Implement long click action here if needed, e.g., show a context menu
        Log.d(TAG, "Resep item long clicked: " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
        Toast.makeText(this, "Long clicked: " + resep.getNamaResep(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(Resep resep, int position) {
        Intent intent = new Intent(MainActivity.this, AddEditResepActivity.class);
        intent.putExtra("RESEP_ID", resep.getId());
        startActivityForResult(intent, REQUEST_EDIT_RESEP);
        Log.d(TAG, "Edit button clicked for resep: " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
    }

    @Override
    public void onDeleteClick(Resep resep, int position) {
        showDeleteConfirmationDialog(resep, position);
        Log.d(TAG, "Delete button clicked for resep: " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
    }

    private void showDeleteConfirmationDialog(Resep resep, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Resep")
                .setMessage("Apakah Anda yakin ingin menghapus resep '" + resep.getNamaResep() + "'?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performDeleteResep(resep, position);
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performDeleteResep(Resep resep, int position) {
        int result = db.deleteResepById(resep.getId());
        if (result > 0) {
            resepList.remove(position);
            resepAdapter.notifyItemRemoved(position);
            // Setelah menghapus item, beri tahu tentang perubahan rentang untuk memperbarui posisi dengan benar
            resepAdapter.notifyItemRangeChanged(position, resepList.size());
            Toast.makeText(this, "Resep berhasil dihapus", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Resep " + resep.getNamaResep() + " (ID: " + resep.getId() + ") deleted successfully.");
        } else {
            Toast.makeText(this, "Gagal menghapus resep. Resep tidak ditemukan.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Failed to delete resep " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: Refreshing data due to RESULT_OK from request code: " + requestCode);
            loadResepData(); // Ini akan menyegarkan seluruh daftar
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Reloading data.");
        loadResepData(); // Ensure data is fresh when returning to MainActivity
    }
}