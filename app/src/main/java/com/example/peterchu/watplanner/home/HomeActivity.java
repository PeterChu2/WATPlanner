package com.example.peterchu.watplanner.home;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.peterchu.watplanner.Database.DatabaseHandler;
import com.example.peterchu.watplanner.Networking.ApiClient;
import com.example.peterchu.watplanner.Networking.ApiInterface;
import com.example.peterchu.watplanner.R;
import com.example.peterchu.watplanner.data.DataRepository;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class HomeActivity extends AppCompatActivity {

    HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        HomeFragment homeFragment =
                (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, homeFragment)
                .commit();

        homePresenter = new HomePresenter(homeFragment, DataRepository.getDataRepository(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
