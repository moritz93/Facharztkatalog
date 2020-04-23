package com.example.facharztkatalog;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    public static final int EXCEPTION_CODE = 42;
    public static final int NOTIFICATION_CODE = 20;
    public static final int LOADING_DONE_CODE = 10;
    public static final int SEARCH_DONE_CODE = 11;

    private AtomicInteger cc =  new AtomicInteger(0);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        askForPremissions();
    }

    public void clicked(View view) {
        if(view.getId() != R.id.editCase_noteLabel) return;
        if(cc.get() == 0) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> cc.set(0), 5000);
        }
        if ( cc.incrementAndGet() == 7) {
            Toast.makeText(this, "Morulia MMVII rox :-*", Toast.LENGTH_LONG).show();
        }
    }

    private void askForPremissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    public static void goToDestination(Fragment fragment, int fromId, int actionId) {

        NavController navController = NavHostFragment.findNavController(fragment);
        if(navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == fromId) {
            navController.navigate(actionId);
        }
    }

    public static void goToDestination(Fragment fragment, int fromId, NavDirections d) {

        NavController navController = NavHostFragment.findNavController(fragment);
        if(navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == fromId) {
            navController.navigate(d);
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            //Toast.makeText(this, "Suche eingegangen", Toast.LENGTH_SHORT).show();
//        }
//    }


}
