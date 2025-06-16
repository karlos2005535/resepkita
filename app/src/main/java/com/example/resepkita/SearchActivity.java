package com.example.resepkita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resepkita.adapter.ResepAdapter;
import com.example.resepkita.database.DatabaseHelper;
import com.example.resepkita.model.Resep;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ResepAdapter.OnItemClickListener {

    private EditText etSearchQuery;
    private ImageButton btnSearch;
    private RecyclerView rvSearchResults;
    private ResepAdapter resepAdapter;
    private List<Resep> searchResultsList;
    private DatabaseHelper db;
//komponen yang di gunakan dalam pencarian resep

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearchQuery = findViewById(R.id.et_search_query);
        btnSearch = findViewById(R.id.btn_search);
        rvSearchResults = findViewById(R.id.rv_search_results);
//menghubungkan data base ke pencarian
        db = new DatabaseHelper(this);
        searchResultsList = new ArrayList<>();
        resepAdapter = new ResepAdapter(this, searchResultsList);
        resepAdapter.setOnItemClickListener(this); // Set the click listener
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(resepAdapter);

        // memanggil pencariah sat tombol pencarian di pencet
        btnSearch.setOnClickListener(v -> performSearch());

        // Mengganti Anonymous new TextView.OnEditorActionListener() dengan lambda
        etSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // tidak digunakan tapi kebutuhan interface
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 // Ini akan memicu pencarian setiap kali teks berubah
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // digunakan untuk menangani aksi setelah teks diubah sepenuhnya
            }
        });
    }

    private void performSearch() {
        String query = etSearchQuery.getText().toString().trim();
        if (query.isEmpty()) {
            searchResultsList.clear();
            resepAdapter.updateResepList(searchResultsList); // Clear results if query is empty
            return;
        }

        // Panggil metode searchResep dari DatabaseHelper
        List<Resep> results = db.searchResep(query);

        // Perbarui data di adapter
        // searchResultsList.clear(); // Baris ini dan berikutnya sudah ditangani oleh updateResepList
        // searchResultsList.addAll(results); // (Jika updateResepList di ResepAdapter sudah benar)
        resepAdapter.updateResepList(results); // Memanggil metode updateResepList di adapter

        if (results.isEmpty()) {
            Toast.makeText(this, "Tidak ada resep yang ditemukan.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(Resep resep) {
        // Handle item click, e.g., open DetailResepActivity
        Intent intent = new Intent(SearchActivity.this, DetailResepActivity.class);
        intent.putExtra("resep_id", resep.getId());
        startActivity(intent);
    }
}