package com.jonathanlouis.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener {

    private static final String TAG = "MainActivity";

    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolbar(false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        flickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(this, new ArrayList<Photo>());
        recyclerView.setAdapter(flickrRecyclerViewAdapter);

        Log.d(TAG, "onCreate: finished");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(FLICKR_QUERY, "");

        if(queryResult.length() > 0) {
            GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this, "https://www.flickr.com/services/feeds/photos_public.gne", "en-us", true);
            getFlickrJsonData.execute(queryResult);
        } else {
            GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this, "https://www.flickr.com/services/feeds/photos_public.gne", "en-us", true);
            getFlickrJsonData.execute("android", "nougat");
        }

        Log.d(TAG, "onResume: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void  onDataAvailable(List<Photo> data, DownloadStatus status){
        Log.d(TAG, "onDataAvailable: starts");

        if(status == DownloadStatus.OK){
            flickrRecyclerViewAdapter.loadNewData(data);
        } else {
            Log.e(TAG, "onDataAvailable: failed with status: " + status);
        }

        Log.d(TAG, "onDataAvailable: finished");
    }

    @Override
    public void onItemClick(View view, int pos) {
        Log.d(TAG, "onItemClick: starts");

    }

    @Override
    public void onItemLongClick(View view, int pos) {
        Log.d(TAG, "onItemLongClick: starts");
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER, flickrRecyclerViewAdapter.getPhoto(pos));
        startActivity(intent);
    }
}