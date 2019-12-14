package com.halo.ambulightadmin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_Home extends AppCompatActivity {

    ArrayList<String> locationsArray = new ArrayList<>();
    TextView tv_h1, tv_h2, tv_h3, tv_userCount;
    ProgressDialog progressDialog;
    EditText et_month;
    Button btn_search;
    private DatabaseReference fDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fDB = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        tv_h1 = findViewById(R.id.tv_h1);
        tv_h2 = findViewById(R.id.tv_h2);
        tv_h3 = findViewById(R.id.tv_h3);
        tv_userCount = findViewById(R.id.tv_userCount);
        et_month = findViewById(R.id.et_month);
        btn_search = findViewById(R.id.btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchUserCount();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        progressDialog.show();
        fetchUserCount();

        fDB.child("visits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationsArray.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String latlong = postSnapshot.getValue().toString();
                    locationsArray.add(latlong);

                    Log.d("asiouohdsad", locationsArray.toString());
                }

                Multiset<String> items = HashMultiset.create(locationsArray);
//                System.out.println(items.count(someItem));
                for (Multiset.Entry<String> entry : items.entrySet()) {
                    Log.d("asdsad", entry.getElement() + " - " + entry.getCount() + " times");
                }
                Iterable<Multiset.Entry<String>> entriesSortedByCount =
                        Multisets.copyHighestCountFirst(items).entrySet();

                for (Multiset.Entry<String> entry : entriesSortedByCount) {
                    Log.d("asdsad", entry.getElement() + " - " + entry.getCount() + " times");
                }

                List list = Lists.newArrayList(entriesSortedByCount);

                if (list.size() >= 3) {
//                    Geocoder geocoder;
//                    List<Address> addresses1 = null, addresses2 = null, addresses3 = null;
//                    geocoder = new Geocoder(Activity_Home.this, Locale.getDefault());
//
//                    String h1[] = Iterables.get(entriesSortedByCount, 0).getElement().split(", ");
//                    String h2[] = Iterables.get(entriesSortedByCount, 1).getElement().split(", ");
//                    String h3[] = Iterables.get(entriesSortedByCount, 2).getElement().split(", ");
//
//                    try {
//                        addresses1 = geocoder.getFromLocation(Double.valueOf(h1[0]), Double.valueOf(h1[1]), 1);
//                        addresses2 = geocoder.getFromLocation(Double.valueOf(h2[0]), Double.valueOf(h2[1]), 1);
//                        addresses3 = geocoder.getFromLocation(Double.valueOf(h3[0]), Double.valueOf(h3[1]), 1);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    tv_h1.setText("1. " + Iterables.get(entriesSortedByCount, 0).getElement());
                    tv_h2.setText("2. " + Iterables.get(entriesSortedByCount, 1).getElement());
                    tv_h3.setText("3. " + Iterables.get(entriesSortedByCount, 2).getElement());
                }

                progressDialog.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchUserCount() {
        progressDialog.show();
        fDB.child("users").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.hasChild("timestamp")) {
                        String timestamp = postSnapshot.child("timestamp").getValue().toString();
                        String month = "-" + et_month.getText().toString().trim() + "-";
                        if (timestamp.contains(month)) {
                            count++;
                        }
                    }
                }
                tv_userCount.setText(String.valueOf(count));
                progressDialog.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        progressDialog.hide();
        super.onPause();
    }
}
