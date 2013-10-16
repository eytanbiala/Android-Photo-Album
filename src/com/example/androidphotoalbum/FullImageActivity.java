package com.example.androidphotoalbum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

/**
 * @author Eytan Biala
 */
public class FullImageActivity extends Activity {

	Controller ctrl;
	String album;
	String photo;
	private AlertDialog.Builder builder;
	private List<String> albumList;
	private Object[] photoList;
	Photo currentPhoto;
	int currentIndex;
	private Dialog dlg;
	private ImageView imageView;
	private BitmapFactory.Options options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);

		ctrl = Controller.getInstance(this);
		imageView = (ImageView) findViewById(R.id.full_image_view);
		options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;

		this.album = getIntent().getExtras().getString(AppConstants.ALBUM_TITLE);
		switch (getIntent().getExtras().getInt(AppConstants.MODE)) {
			case 0 :
				imageFromPath(getIntent().getExtras().getString(AppConstants.PHOTO_FILENAME));
				photo = getIntent().getExtras().getString(AppConstants.PHOTO_FILENAME);
				getActionBar().setTitle(getIntent().getExtras()
						.getString(AppConstants.PHOTO_FILENAME));
				break;
			case 1 :
				imageFromUri(getIntent().getExtras().get(AppConstants.URI));
				break;
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);

		Collection<Photo> vals = ctrl.getAlbums().get(album).getPhotos().values();
		this.photoList = vals.toArray();
		for (int i = 0; i < photoList.length; i++) {
			if (((Photo) photoList[i]).equals(photo)) {
				currentIndex = i;
			}
		}

		final ImageButton next = (ImageButton) findViewById(R.id.next);
		final ImageButton previous = (ImageButton) findViewById(R.id.previous);

		final int length = photoList.length;
		if (length <= 1) {
			next.setVisibility(View.GONE);
			previous.setVisibility(View.GONE);
		}

		if (currentIndex == 0) {
			previous.setEnabled(false);
		} else if (currentIndex == photoList.length) {
			next.setEnabled(false);
		}

		previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				currentIndex--;
				imageFromPath((Photo) photoList[currentIndex]);

				if (currentIndex != length - 1) {
					next.setEnabled(true);
				}

				if (currentIndex == 0) {
					previous.setEnabled(false);
					next.setEnabled(true);
				}
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				currentIndex++;
				imageFromPath((Photo) photoList[currentIndex]);

				if (currentIndex != 0) {
					previous.setEnabled(true);
				}

				if (currentIndex == length - 1) {
					previous.setEnabled(true);
					next.setEnabled(false);
				}
			}
		});

	}
	
	@Override
	public void onPause() {
		super.onPause();
		ctrl = Controller.getInstance(getApplicationContext());
		ctrl.write();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.photo_detail, menu);

		final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent i = new Intent(getApplicationContext(), Searchable.class);
				i.putExtra(AppConstants.QUERY, searchView.getQuery().toString());
				startActivity(i);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case android.R.id.home :
				finish();
				break;
			case R.id.add_tag :
				addTag();
				break;
			case R.id.move_photo :
				movePhoto();
				break;
			case R.id.tags :
				if (!ctrl.getTagsForPhoto(album, photo).isEmpty()) {
					tags();
				} else {
					Toast.makeText(this, "No tags for this photo", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.delete_photo :
				deletePhoto();
				break;
		}
		return false;
	}

	private void deletePhoto() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Delete Photo");
		alertDialogBuilder.setMessage("Are you sure you want to delete this photo?")
				.setCancelable(true)
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (ctrl.removePhotoFromAlbum(FullImageActivity.this.photo, FullImageActivity.this.album)) {
							finish();
						} else {
							Toast.makeText(getApplicationContext(), "Could not be deleted", Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void tags() {
		Intent i = new Intent(this, TagListActivity.class);
		i.putExtra(AppConstants.ALBUM_TITLE, this.album);
		i.putExtra(AppConstants.PHOTO_FILENAME, this.photo);
		startActivity(i);
	}

	private void movePhoto() {
		HashMap<String, Album> albums = ctrl.getAlbums();
		if (albums.size() <= 1) {
			Toast.makeText(getApplicationContext(), "This is the only album in your collection, please create another to move photos to.", Toast.LENGTH_LONG)
					.show();
			return;
		}
		albumList = new ArrayList<String>(albums.keySet());
		albumList.remove(this.album);

		builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose Album");
		ListView albumListView = new ListView(this);
		ArrayAdapter<String> albumAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, albumList);
		albumListView.setAdapter(albumAdapter);

		builder.setView(albumListView);
		dlg = builder.create();
		dlg.show();
		albumListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				if (ctrl.movePhoto(FullImageActivity.this.photo, FullImageActivity.this.album, albumList
						.get(position))) {
					Toast.makeText(getApplicationContext(), "Moved photo from "
							+ FullImageActivity.this.album + " to " + albumList.get(position), Toast.LENGTH_SHORT)
							.show();
					dlg.dismiss();
					Intent i = new Intent(getApplicationContext(), PhotoGridActivity.class);
					i.putExtra(AppConstants.ALBUM_TITLE, albumList.get(position));
					startActivity(i);
					finish();
				} else {
					Log.e("Move error - Selected album", albumList.get(position));
				}

			}

		});

	}

	private void addTag() {
		TagDialogFragment df = new TagDialogFragment();
		Bundle b = new Bundle();
		b.putString(AppConstants.ALBUM_TITLE, this.album);
		b.putString(AppConstants.PHOTO_FILENAME, this.photo);
		b.putInt(AppConstants.MODE, 1);
		df.setArguments(b);
		df.show(getFragmentManager(), "TagDialogFragment");
	}

	private void imageFromPath(String filepath) {
		String path = this.getFilesDir() + File.separator + filepath;
		Bitmap b = BitmapFactory.decodeFile(path, options);
		imageView.setImageBitmap(b);

		getActionBar().setTitle(photo);
		getActionBar().setSubtitle("Album: " + album);
	}

	private void imageFromPath(Photo p) {
		String path = this.getFilesDir() + File.separator + p.getFilename();
		Bitmap b = BitmapFactory.decodeFile(path, options);
		imageView.setImageBitmap(b);
		imageView.invalidate();
		imageView.refreshDrawableState();

		this.photo = p.getFilename();
		this.album = p.getParentAlbum();
		getActionBar().setTitle(this.photo);
		getActionBar().setSubtitle("Album: " + this.album);
	}

	private void imageFromUri(Object uri) {
		String[] filePathColumn = {MediaStore.Images.Media.DATA};
		Cursor cursor = getContentResolver().query((Uri) uri, filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		photo = picturePath;
		cursor.close();
		ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
		imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

	}
}