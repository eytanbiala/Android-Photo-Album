package com.example.androidphotoalbum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Eytan Biala
 */
public class Searchable extends Activity {

	private Controller ctrl;
	protected Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results);
		ctx = this;
		ctrl = Controller.getInstance(this);

		String query = getIntent().getExtras().getString(AppConstants.QUERY);
		doSearch(query);

		getActionBar().setTitle("Search Results for " + "\"" + query + "\"");
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.search_result_menu, menu);
		return true;
	}

	private void doSearch(String query) {
		final List<Photo> matchingPhotos = new ArrayList<Photo>();

		query = query.toLowerCase(Locale.getDefault()).trim();

		final HashMap<String, Album> albums = ctrl.getAlbums();
		for (Album album : albums.values()) {
			Collection<Photo> photos = album.getPhotos().values();
			for (Photo photo : photos) {
				for (PhotoTag tag : photo.getTags()) {
					boolean value = tag.getTagValue().toLowerCase(Locale.getDefault()).trim()
							.contains(query);
					if (value) {
						if (!matchingPhotos.contains(photo)) {
							matchingPhotos.add(photo);
						}
					}
				}
			}
		}

		matchingPhotos.removeAll(Arrays.asList("", null));

		int resultSize = matchingPhotos.size();
		String notifyText = "";

		if (resultSize == 0) {
			notifyText = "No results found for \"" + query + "\".";
		} else if (resultSize > 0) {
			notifyText = "Found 1 result";
			if (resultSize > 1) {
				notifyText = "Found " + resultSize + " results";
			}

			ListView lv = (ListView) findViewById(R.id.list);
			lv.setAdapter(new ArrayAdapter<Photo>(this, R.layout.list_item_1, matchingPhotos));
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
					Photo p = matchingPhotos.get(pos);
					Intent i = new Intent(ctx, FullImageActivity.class);
					i.putExtra(AppConstants.PHOTO_FILENAME, p.getFilename());
					i.putExtra(AppConstants.ALBUM_TITLE, p.getParentAlbum());
					startActivity(i);
				}
			});

		}

		Toast.makeText(getApplicationContext(), notifyText, Toast.LENGTH_SHORT).show();
	}
}
