package com.example.resepkita;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_CHEF_BIO = "chefBio";
    private static final String KEY_CHEF_SPECIALTY = "chefSpecialty";
    private static final String KEY_CONTACT_INFO = "contactInfo";
    private static final String KEY_PROFILE_IMAGE_URI = "profileImageUri";

    private ImageView ivProfilePicture;
    private TextInputEditText etUserName, etChefBio, etChefSpecialty, etContactInfo;
    private Button btnSelectProfilePicture, btnSaveProfile;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        etUserName = findViewById(R.id.et_user_name);
        etChefBio = findViewById(R.id.et_chef_bio);
        etChefSpecialty = findViewById(R.id.et_chef_specialty);
        etContactInfo = findViewById(R.id.et_contact_info);
        btnSelectProfilePicture = findViewById(R.id.btn_select_profile_picture);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        loadUserProfile();

        btnSelectProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT); // Ganti ACTION_GET_CONTENT menjadi ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Tambahkan kategori openable
        intent.setType("image/*");
        // Penting: Minta izin URI yang dapat dipertahankan untuk akses jangka panjang
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Pertahankan izin akses baca untuk URI
            try {
                // Ini mengambil izin dan menyimpannya dengan ContentResolver
                getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d(TAG, "Persisted URI permission for: " + selectedImageUri.toString());
            } catch (SecurityException e) {
                Log.e(TAG, "Failed to persist URI permission for profile image: " + e.getMessage(), e);
                Toast.makeText(this, "Gagal mempertahankan izin gambar profil. Silakan coba lagi.", Toast.LENGTH_LONG).show();
                selectedImageUri = null; // Hapus gambar jika izin gagal
            }

            Picasso.get().load(selectedImageUri).into(ivProfilePicture);
            Log.d(TAG, "Selected profile image URI: " + selectedImageUri.toString());
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_CANCELED) { // Tangani jika pemilihan gambar dibatalkan
            Log.d(TAG, "Image selection canceled.");
            // Tidak perlu mereset selectedImageUri di sini jika sebelumnya sudah ada gambar
            // IvProfilePicture akan tetap menampilkan gambar yang sudah ada atau placeholder
        } else {
            Log.w(TAG, "onActivityResult: Data or Data URI is null after image selection.");
            // Toast.makeText(this, "Tidak ada gambar yang dipilih.", Toast.LENGTH_SHORT).show(); // Mungkin terlalu sering muncul
            // selectedImageUri = null; // Pastikan tidak ada URL lama yang digunakan jika pemilihan gagal
            // ivProfilePicture.setImageResource(R.drawable.ic_profile_placeholder); // Reset preview jika tidak ada gambar dipilih
        }
    }

    private void saveUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_USER_NAME, etUserName.getText().toString().trim());
        editor.putString(KEY_CHEF_BIO, etChefBio.getText().toString().trim());
        editor.putString(KEY_CHEF_SPECIALTY, etChefSpecialty.getText().toString().trim());
        editor.putString(KEY_CONTACT_INFO, etContactInfo.getText().toString().trim());
        if (selectedImageUri != null) {
            editor.putString(KEY_PROFILE_IMAGE_URI, selectedImageUri.toString());
        } else {
            editor.remove(KEY_PROFILE_IMAGE_URI); // Hapus URI jika tidak ada gambar yang dipilih
        }

        editor.apply();
        Toast.makeText(this, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "User profile saved.");
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        etUserName.setText(sharedPreferences.getString(KEY_USER_NAME, ""));
        etChefBio.setText(sharedPreferences.getString(KEY_CHEF_BIO, ""));
        etChefSpecialty.setText(sharedPreferences.getString(KEY_CHEF_SPECIALTY, ""));
        etContactInfo.setText(sharedPreferences.getString(KEY_CONTACT_INFO, ""));

        String imageUriString = sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null);
        if (imageUriString != null) {
            selectedImageUri = Uri.parse(imageUriString);
            try {
                // Periksa apakah izin masih valid, jika tidak, mungkin perlu meminta ulang atau menampilkan placeholder
                Picasso.get().load(selectedImageUri)
                        .placeholder(R.drawable.ic_profile_placeholder) // Tambahkan placeholder
                        .error(R.drawable.ic_broken_image) // Tambahkan error image
                        .into(ivProfilePicture);
                Log.d(TAG, "Loaded profile image from URI: " + selectedImageUri.toString());
            } catch (Exception e) {
                Log.e(TAG, "Error loading persisted profile image: " + e.getMessage(), e);
                ivProfilePicture.setImageResource(R.drawable.ic_broken_image); // Tampilkan gambar rusak jika gagal dimuat
                selectedImageUri = null; // Reset URI jika gagal dimuat
                // Pertimbangkan untuk menghapus URI yang rusak dari SharedPreferences di sini
            }
        } else {
            ivProfilePicture.setImageResource(R.drawable.ic_profile_placeholder);
        }
        Log.d(TAG, "User profile loaded.");
    }
}