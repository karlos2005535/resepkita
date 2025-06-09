package com.example.resepkita;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.resepkita.database.DatabaseHelper;
import com.example.resepkita.model.Resep;
import com.squareup.picasso.Picasso;

public class AddEditResepActivity extends AppCompatActivity {

    private static final String TAG = "AddEditResepActivity";

    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText etNamaResep, etBahanResep, etCaraMembuatResep, etDeskripsiResep;
    private Button btnSaveResep, btnSelectImage;
    private ImageView ivResepImagePreview;
    private DatabaseHelper db;
    private int resepId = -1;
    private String selectedImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_resep);

        etNamaResep = findViewById(R.id.et_nama_resep);
        etDeskripsiResep = findViewById(R.id.et_deskripsi_resep); // Initialize deskripsi EditText
        etBahanResep = findViewById(R.id.et_bahan_resep);
        etCaraMembuatResep = findViewById(R.id.et_cara_membuat_resep);
        btnSaveResep = findViewById(R.id.btn_save_resep);
        btnSelectImage = findViewById(R.id.btn_select_image);
        ivResepImagePreview = findViewById(R.id.iv_resep_image_preview);

        db = new DatabaseHelper(this);

        // Get intent data for edit mode
        if (getIntent().hasExtra("RESEP_ID")) {
            resepId = getIntent().getIntExtra("RESEP_ID", -1);
            if (resepId != -1) {
                loadResepDataForEdit(resepId);
            }
        }

        btnSelectImage.setOnClickListener(v -> openImageChooser());
        btnSaveResep.setOnClickListener(v -> saveResep());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar Resep"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            selectedImageUrl = imageUri.toString();
            Picasso.get().load(imageUri).into(ivResepImagePreview);
            ivResepImagePreview.setVisibility(View.VISIBLE);
            Log.d(TAG, "Image selected: " + selectedImageUrl);
        }
    }

    private void loadResepDataForEdit(int id) {
        Resep resep = db.getResep(id);
        if (resep != null) {
            etNamaResep.setText(resep.getNamaResep());
            etDeskripsiResep.setText(resep.getDeskripsi()); // Set deskripsi
            etBahanResep.setText(resep.getBahan());
            etCaraMembuatResep.setText(resep.getCaraMembuat());
            selectedImageUrl = resep.getImageUrl();
            if (selectedImageUrl != null && !selectedImageUrl.isEmpty()) {
                Picasso.get().load(selectedImageUrl).into(ivResepImagePreview);
                ivResepImagePreview.setVisibility(View.VISIBLE);
            } else {
                ivResepImagePreview.setVisibility(View.GONE);
            }
            btnSaveResep.setText("Perbarui Resep");
            Log.d(TAG, "Loaded recipe for edit: " + resep.getNamaResep());
        } else {
            Toast.makeText(this, "Gagal memuat resep untuk diedit.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Failed to load recipe with ID " + id + " for editing.");
            finish();
        }
    }

    private void saveResep() {
        String nama = etNamaResep.getText().toString().trim();
        String deskripsi = etDeskripsiResep.getText().toString().trim(); // Get deskripsi
        String bahan = etBahanResep.getText().toString().trim();
        String caraMembuat = etCaraMembuatResep.getText().toString().trim();

        if (nama.isEmpty() || deskripsi.isEmpty() || bahan.isEmpty() || caraMembuat.isEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        if (resepId == -1) {
            // Add new recipe mode
            Resep newResep = new Resep(nama, deskripsi, bahan, caraMembuat, selectedImageUrl);
            db.addResep(newResep);
            Toast.makeText(this, "Resep berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "New recipe added: " + nama + ", Image URL: " + selectedImageUrl);
        } else {
            // Edit recipe mode
            Resep oldResep = db.getResep(resepId);
            // Pastikan oldResep tidak null sebelum mencoba mengakses isFavorite()
            if (oldResep != null) {
                // Menggunakan konstruktor yang sesuai dengan 7 argumen (id, nama, deskripsi, bahan, cara_membuat, image_url, favorite)
                Resep updatedResep = new Resep(resepId, nama, deskripsi, bahan, caraMembuat, selectedImageUrl, oldResep.isFavorite());
                db.updateResep(updatedResep);
                Toast.makeText(this, "Resep berhasil diperbarui", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Recipe updated: " + nama + ", ID: " + resepId + ", Image URL: " + selectedImageUrl);
            } else {
                Toast.makeText(this, "Gagal memperbarui resep. Resep tidak ditemukan.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to find old recipe with ID " + resepId + " for updating.");
            }
        }
        setResult(RESULT_OK); // Indicate success to the calling activity (MainActivity or DetailResepActivity)
        finish();
    }
}