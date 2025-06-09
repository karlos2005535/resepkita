package com.example.resepkita.model;

public class Resep {
    private int id;
    private String namaResep;
    private String deskripsi;
    private String bahan;
    private String caraMembuat;
    private String imageUrl;
    private boolean favorite;

    // Konstruktor default (dibutuhkan untuk beberapa operasi database atau deserialisasi)
    public Resep() {
    }

    // Constructor dengan semua field, termasuk deskripsi dan favorite
    public Resep(int id, String namaResep, String deskripsi, String bahan, String caraMembuat, String imageUrl, boolean favorite) {
        this.id = id;
        this.namaResep = namaResep;
        this.deskripsi = deskripsi;
        this.bahan = bahan;
        this.caraMembuat = caraMembuat;
        this.imageUrl = imageUrl;
        this.favorite = favorite;
    }

    // Constructor untuk menambahkan resep baru tanpa ID atau status favorite (default false)
    public Resep(String namaResep, String deskripsi, String bahan, String caraMembuat, String imageUrl) {
        this.namaResep = namaResep;
        this.deskripsi = deskripsi;
        this.bahan = bahan;
        this.caraMembuat = caraMembuat;
        this.imageUrl = imageUrl;
        this.favorite = false; // Default ke tidak favorit
    }

    // Getter dan Setter untuk semua field

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaResep() {
        return namaResep;
    }

    public void setNamaResep(String namaResep) {
        this.namaResep = namaResep;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getBahan() {
        return bahan;
    }

    public void setBahan(String bahan) {
        this.bahan = bahan;
    }

    public String getCaraMembuat() {
        return caraMembuat;
    }

    public void setCaraMembuat(String caraMembuat) {
        this.caraMembuat = caraMembuat;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}