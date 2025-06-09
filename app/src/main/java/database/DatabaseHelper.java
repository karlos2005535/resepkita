package com.example.resepkita.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.resepkita.model.Resep;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Tingkatkan versi database setiap kali Anda mengubah skema.
    // Ini akan memicu metode onUpgrade().
    private static final int DATABASE_VERSION = 7; // DIPERBARUI: Versi database ditingkatkan
    private static final String DATABASE_NAME = "ResepKitaDB";
    private static final String TABLE_RESEP = "resep";

    // Kolom tabel
    private static final String KEY_ID = "id";
    private static final String KEY_NAMA_RESEP = "nama_resep";
    private static final String KEY_DESKRIPSI = "deskripsi";
    private static final String KEY_BAHAN = "bahan";
    private static final String KEY_CARA_MEMBUAT = "cara_membuat";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_FAVORITE = "favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESEP_TABLE = "CREATE TABLE " + TABLE_RESEP + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAMA_RESEP + " TEXT,"
                + KEY_DESKRIPSI + " TEXT,"
                + KEY_BAHAN + " TEXT," // PERBAIKAN: Memastikan ini adalah KEY_BAHAN
                + KEY_CARA_MEMBUAT + " TEXT,"
                + KEY_IMAGE_URL + " TEXT,"
                + KEY_FAVORITE + " INTEGER DEFAULT 0" // Default 0 (false)
                + ")";
        db.execSQL(CREATE_RESEP_TABLE);
        Log.d(TAG, "Database created with table: " + TABLE_RESEP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESEP);
        // Create tables again
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    // Menambahkan resep baru
    public long addResep(Resep resep) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAMA_RESEP, resep.getNamaResep());
        values.put(KEY_DESKRIPSI, resep.getDeskripsi());
        values.put(KEY_BAHAN, resep.getBahan());
        values.put(KEY_CARA_MEMBUAT, resep.getCaraMembuat());
        values.put(KEY_IMAGE_URL, resep.getImageUrl());
        values.put(KEY_FAVORITE, resep.isFavorite() ? 1 : 0);

        long id = db.insert(TABLE_RESEP, null, values);
        db.close();
        if (id != -1) {
            Log.d(TAG, "Resep added: " + resep.getNamaResep() + " with ID: " + id);
        } else {
            Log.e(TAG, "Failed to add resep: " + resep.getNamaResep());
        }
        return id;
    }

    // Mendapatkan satu resep berdasarkan ID
    public Resep getResep(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Resep resep = null;
        try {
            cursor = db.query(TABLE_RESEP,
                    new String[]{KEY_ID, KEY_NAMA_RESEP, KEY_DESKRIPSI, KEY_BAHAN, KEY_CARA_MEMBUAT, KEY_IMAGE_URL, KEY_FAVORITE},
                    KEY_ID + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int namaResepIndex = cursor.getColumnIndex(KEY_NAMA_RESEP);
                int deskripsiIndex = cursor.getColumnIndex(KEY_DESKRIPSI);
                int bahanIndex = cursor.getColumnIndex(KEY_BAHAN);
                int caraMembuatIndex = cursor.getColumnIndex(KEY_CARA_MEMBUAT);
                int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);
                int favoriteIndex = cursor.getColumnIndex(KEY_FAVORITE);

                if (idIndex != -1 && namaResepIndex != -1 && deskripsiIndex != -1 && bahanIndex != -1 &&
                        caraMembuatIndex != -1 && imageUrlIndex != -1 && favoriteIndex != -1) {
                    resep = new Resep(
                            cursor.getInt(idIndex),
                            cursor.getString(namaResepIndex),
                            cursor.getString(deskripsiIndex),
                            cursor.getString(bahanIndex),
                            cursor.getString(caraMembuatIndex),
                            cursor.getString(imageUrlIndex),
                            cursor.getInt(favoriteIndex) == 1
                    );
                    Log.d(TAG, "Resep retrieved: " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
                } else {
                    Log.e(TAG, "getResep: One or more columns not found for ID: " + id);
                }
            } else {
                Log.w(TAG, "No resep found with ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting resep: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return resep;
    }

    // Mendapatkan semua resep
    public List<Resep> getAllResep() {
        List<Resep> resepList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESEP;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int namaResepIndex = cursor.getColumnIndex(KEY_NAMA_RESEP);
                    int deskripsiIndex = cursor.getColumnIndex(KEY_DESKRIPSI);
                    int bahanIndex = cursor.getColumnIndex(KEY_BAHAN);
                    int caraMembuatIndex = cursor.getColumnIndex(KEY_CARA_MEMBUAT);
                    int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);
                    int favoriteIndex = cursor.getColumnIndex(KEY_FAVORITE);

                    if (idIndex != -1 && namaResepIndex != -1 && deskripsiIndex != -1 && bahanIndex != -1 &&
                            caraMembuatIndex != -1 && imageUrlIndex != -1 && favoriteIndex != -1) {
                        Resep resep = new Resep(
                                cursor.getInt(idIndex),
                                cursor.getString(namaResepIndex),
                                cursor.getString(deskripsiIndex),
                                cursor.getString(bahanIndex),
                                cursor.getString(caraMembuatIndex),
                                cursor.getString(imageUrlIndex),
                                cursor.getInt(favoriteIndex) == 1
                        );
                        resepList.add(resep);
                    } else {
                        Log.e(TAG, "getAllResep: One or more columns not found. Skipping row.");
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all resep: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        Log.d(TAG, "getAllResep: Total recipes: " + resepList.size());
        return resepList;
    }

    // Memperbarui resep
    public int updateResep(Resep resep) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAMA_RESEP, resep.getNamaResep());
        values.put(KEY_DESKRIPSI, resep.getDeskripsi());
        values.put(KEY_BAHAN, resep.getBahan());
        values.put(KEY_CARA_MEMBUAT, resep.getCaraMembuat());
        values.put(KEY_IMAGE_URL, resep.getImageUrl());
        values.put(KEY_FAVORITE, resep.isFavorite() ? 1 : 0); // Update favorite status

        int rowsAffected = db.update(TABLE_RESEP, values, KEY_ID + " = ?",
                new String[]{String.valueOf(resep.getId())});
        db.close();
        if (rowsAffected > 0) {
            Log.d(TAG, "Resep updated: " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
        } else {
            Log.w(TAG, "Failed to update resep: " + resep.getNamaResep() + " (ID: " + resep.getId() + ")");
        }
        return rowsAffected;
    }

    // Menghapus resep
    public int deleteResepById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RESEP, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        if (rowsAffected > 0) {
            Log.d(TAG, "Resep deleted with ID: " + id);
        } else {
            Log.w(TAG, "Failed to delete resep with ID: " + id);
        }
        return rowsAffected;
    }

    // Mendapatkan resep favorit
    public List<Resep> getFavoriteResep() {
        List<Resep> resepList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESEP + " WHERE " + KEY_FAVORITE + " = 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int namaResepIndex = cursor.getColumnIndex(KEY_NAMA_RESEP);
                    int deskripsiIndex = cursor.getColumnIndex(KEY_DESKRIPSI);
                    int bahanIndex = cursor.getColumnIndex(KEY_BAHAN);
                    int caraMembuatIndex = cursor.getColumnIndex(KEY_CARA_MEMBUAT);
                    int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);
                    int favoriteIndex = cursor.getColumnIndex(KEY_FAVORITE);

                    if (idIndex != -1 && namaResepIndex != -1 && deskripsiIndex != -1 && bahanIndex != -1 &&
                            caraMembuatIndex != -1 && imageUrlIndex != -1 && favoriteIndex != -1) {
                        Resep resep = new Resep(
                                cursor.getInt(idIndex),
                                cursor.getString(namaResepIndex),
                                cursor.getString(deskripsiIndex),
                                cursor.getString(bahanIndex),
                                cursor.getString(caraMembuatIndex),
                                cursor.getString(imageUrlIndex),
                                cursor.getInt(favoriteIndex) == 1
                        );
                        resepList.add(resep);
                    } else {
                        Log.e(TAG, "getFavoriteResep: One or more columns not found. Skipping row.");
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting favorite resep: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        Log.d(TAG, "getFavoriteResep: Total favorite recipes: " + resepList.size());
        return resepList;
    }

    // Update favorite status (sudah ada, tapi pastikan konsisten)
    public int updateFavoriteStatus(int resepId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FAVORITE, isFavorite ? 1 : 0);

        int rowsAffected = db.update(TABLE_RESEP, values, KEY_ID + " = ?",
                new String[]{String.valueOf(resepId)});
        db.close();
        if (rowsAffected > 0) {
            Log.d(TAG, "updateFavoriteStatus: Resep with ID " + resepId + " favorite status updated to " + isFavorite);
        } else {
            Log.w(TAG, "updateFavoriteStatus: No resep found with ID " + resepId);
        }
        return rowsAffected;
    }

    // MENAMBAHKAN METHOD searchResep() UNTUK PENCARIAN
    public List<Resep> searchResep(String query) {
        List<Resep> resepList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Tambahkan wildcard % di awal dan akhir query untuk pencarian sebagian
        String formattedQuery = "%" + query + "%";

        // Query untuk mencari di nama resep, deskripsi, bahan, dan cara membuat
        // Menggunakan LIKE untuk pencarian sebagian dan COLLATE NOCASE untuk pencarian case-insensitive
        String searchQuery = "SELECT * FROM " + TABLE_RESEP +
                " WHERE " + KEY_NAMA_RESEP + " LIKE ? COLLATE NOCASE OR " +
                KEY_DESKRIPSI + " LIKE ? COLLATE NOCASE OR " +
                KEY_BAHAN + " LIKE ? COLLATE NOCASE OR " +
                KEY_CARA_MEMBUAT + " LIKE ? COLLATE NOCASE";

        String[] selectionArgs = new String[]{formattedQuery, formattedQuery, formattedQuery, formattedQuery};

        try {
            Log.d(TAG, "searchResep: Executing query: " + searchQuery + " with args: " + formattedQuery);
            cursor = db.rawQuery(searchQuery, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int namaResepIndex = cursor.getColumnIndex(KEY_NAMA_RESEP);
                    int deskripsiIndex = cursor.getColumnIndex(KEY_DESKRIPSI);
                    int bahanIndex = cursor.getColumnIndex(KEY_BAHAN);
                    int caraMembuatIndex = cursor.getColumnIndex(KEY_CARA_MEMBUAT);
                    int imageUrlIndex = cursor.getColumnIndex(KEY_IMAGE_URL);
                    int favoriteIndex = cursor.getColumnIndex(KEY_FAVORITE);

                    if (idIndex != -1 && namaResepIndex != -1 && deskripsiIndex != -1 && bahanIndex != -1 &&
                            caraMembuatIndex != -1 && imageUrlIndex != -1 && favoriteIndex != -1) {
                        Resep resep = new Resep(
                                cursor.getInt(idIndex),
                                cursor.getString(namaResepIndex),
                                cursor.getString(deskripsiIndex),
                                cursor.getString(bahanIndex),
                                cursor.getString(caraMembuatIndex),
                                cursor.getString(imageUrlIndex),
                                cursor.getInt(favoriteIndex) == 1
                        );
                        resepList.add(resep);
                        Log.d(TAG, "searchResep: Found resep: " + resep.getNamaResep());
                    } else {
                        Log.e(TAG, "searchResep: One or more columns not found. Skipping row.");
                        // Tidak perlu break di sini, lanjutkan ke baris berikutnya
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "searchResep: No results found for query: " + query);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching resep: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        Log.d(TAG, "searchResep: Total found " + resepList.size() + " recipes for query: " + query);
        return resepList;
    }
}