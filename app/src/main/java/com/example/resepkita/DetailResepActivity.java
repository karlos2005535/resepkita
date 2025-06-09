package com.example.resepkita;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resepkita.database.DatabaseHelper;
import com.example.resepkita.model.Resep;
import com.squareup.picasso.Picasso;

public class DetailResepActivity extends AppCompatActivity {

    private static final String TAG = "DetailResepActivity";

    private ImageView ivResep;
    private TextView tvNamaResep, tvDeskripsi, tvBahan, tvCaraMembuat; // Added tvDeskripsi
    private ImageButton ibEdit, ibDelete;
    private DatabaseHelper db;
    private int resepId = -1;

    public static final int REQUEST_EDIT_RESEP_FROM_DETAIL = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resep);

        ivResep = findViewById(R.id.iv_resep);
        tvNamaResep = findViewById(R.id.tv_nama_resep);
        tvDeskripsi = findViewById(R.id.tv_deskripsi_resep); // Initialize tvDeskripsi
        tvBahan = findViewById(R.id.tv_bahan);
        tvCaraMembuat = findViewById(R.id.tv_cara_membuat);
        ibEdit = findViewById(R.id.ib_edit);
        ibDelete = findViewById(R.id.ib_delete);

        db = new DatabaseHelper(this);

        // Get resep ID from intent
        if (getIntent().hasExtra("RESEP_ID")) {
            resepId = getIntent().getIntExtra("RESEP_ID", -1);
            if (resepId != -1) {
                loadResepDetails(resepId);
            } else {
                Toast.makeText(this, "ID Resep tidak valid.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Tidak ada ID Resep yang diberikan.", Toast.LENGTH_SHORT).show();
            finish();
        }

        ibEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailResepActivity.this, AddEditResepActivity.class);
            intent.putExtra("RESEP_ID", resepId);
            startActivityForResult(intent, REQUEST_EDIT_RESEP_FROM_DETAIL);
            Log.d(TAG, "Edit button clicked. Starting AddEditResepActivity for ID: " + resepId);
        });

        ibDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadResepDetails(int id) {
        Resep resep = db.getResep(id);
        if (resep != null) {
            tvNamaResep.setText(resep.getNamaResep());
            tvDeskripsi.setText(resep.getDeskripsi()); // Set deskripsi
            tvBahan.setText(resep.getBahan());
            tvCaraMembuat.setText(resep.getCaraMembuat());

            String imageUrl = resep.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get().load(imageUrl)
                        .placeholder(R.drawable.ic_image_placeholder) // Placeholder if loading fails
                        .error(R.drawable.ic_broken_image) // Error image if loading fails
                        .into(ivResep);
                Log.d(TAG, "Loading image for ID " + id + ": " + imageUrl);
            } else {
                ivResep.setImageResource(R.drawable.ic_image_placeholder); // Default placeholder
                Log.d(TAG, "No image URL for ID " + id + ". Using placeholder.");
            }
        } else {
            Toast.makeText(this, "Resep tidak ditemukan.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Resep with ID " + id + " not found in database.");
            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Resep")
                .setMessage("Apakah Anda yakin ingin menghapus resep ini?")
                .setPositiveButton("Hapus", (dialog, which) -> performDeleteResep())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performDeleteResep() {
        int result = db.deleteResepById(resepId);
        if (result > 0) {
            Toast.makeText(this, "Resep berhasil dihapus", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Resep with ID " + resepId + " deleted successfully.");
            setResult(RESULT_OK); // Indicate success to the calling activity (MainActivity)
            finish();
        } else {
            Toast.makeText(this, "Gagal menghapus resep.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Failed to delete resep with ID " + resepId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_RESEP_FROM_DETAIL && resultCode == RESULT_OK) {
            if (resepId != -1) {
                loadResepDetails(resepId); // Reload details after editing
                setResult(RESULT_OK); // Propagate RESULT_OK to MainActivity as well
                Log.d(TAG, "Resep details reloaded after editing from AddEditResepActivity. Propagating RESULT_OK.");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload details on resume to ensure any changes (e.g., from editing in AddEditResepActivity) are reflected
        if (resepId != -1) {
            loadResepDetails(resepId);
            Log.d(TAG, "onResume: Reloading resep details for ID " + resepId);
        }
    }
}