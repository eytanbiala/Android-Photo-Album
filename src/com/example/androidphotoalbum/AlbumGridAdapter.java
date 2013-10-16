package com.example.androidphotoalbum;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Eytan Biala
 */
public class AlbumGridAdapter extends BaseAdapter implements View.OnCreateContextMenuListener {

	Context ctx;
	private HashMap<String, Album> albums;
	private List<Photo> albumCovers;
	private Controller ctrl;
	private LayoutInflater inflater;
	private ViewHolder holder;
	private List<Album> albumList;

	private int placeholderWidth;
	private int placeholderHeight;
	BitmapFactory.Options options;
	private Drawable d;

	public AlbumGridAdapter(Context c) {
		super();
		this.ctx = c;
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ctrl = Controller.getInstance(ctx);
		albums = ctrl.getAlbums();
		
		options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;

		d = this.ctx.getResources().getDrawable(R.drawable.no_image);
		
		placeholderWidth = (int) (1.1 * ctx.getResources().getDrawable(R.drawable.no_image)
				.getIntrinsicWidth());
		placeholderHeight = (int) (1.1 * ctx.getResources().getDrawable(R.drawable.no_image)
				.getIntrinsicHeight());

		albumList = new ArrayList<Album>(albums.values());
		albumCovers = new ArrayList<Photo>();
		Photo p = new Photo("");
		if (albums.size() > 0) {
			for (Map.Entry<String, Album> album : albums.entrySet()) {
				HashMap<String, Photo> photos = album.getValue().getPhotos();
				if (photos != null && !photos.isEmpty()) {
					for (Map.Entry<String, Photo> photo : photos.entrySet()) {
						File file = new File(ctx.getFilesDir() + File.separator
								+ photo.getValue().getFilename());
						if (file.exists()) {
							albumCovers.add(photo.getValue());
						} else {
							Log.e("File does not exist", Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
									+ photo.getValue().getFilename());
						}
						break;
					}
				} else {
					albumCovers.add(p);
				}
			}
		} else {
			albumCovers.add(p);

		}
	}

	@Override
	public int getCount() {
		return albumList.size();
	}

	@Override
	public Object getItem(int position) {
		return albumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		ImageView image;
		TextView caption;
		LinearLayout container;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.grid_square, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.caption = (TextView) convertView.findViewById(R.id.caption);
			holder.container = (LinearLayout) convertView.findViewById(R.id.grid_cell);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (albumList.isEmpty() || albumCovers.isEmpty()) {
			return null;
		}

		String fname = albumCovers.get(position).getFilename();

		if (!fname.isEmpty()) {
			File file = new File(ctx.getFilesDir() + File.separator + fname);
			holder.image.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath(), options));
			holder.image.setLayoutParams(new LinearLayout.LayoutParams(placeholderWidth, placeholderHeight));
		} else {
			holder.image.setImageDrawable(d);
		}

		holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		holder.image.setId(position);

		Album al = albumList.get(position);
		String a = al.getAlbumName();
		holder.caption.setText(a);

		holder.container.setTag(R.string.album_title, a);

		holder.caption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((TextView) v.findViewById(R.id.caption)).getText().toString();
				showAlbumIntent(name);
			}
		});

		holder.container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlbumIntent(v.getTag(R.string.album_title).toString());
			}
		});

		return convertView;
	}

	public void showAlbumIntent(String name) {
		Intent i = new Intent(ctx, PhotoGridActivity.class);
		i.putExtra(AppConstants.ALBUM_TITLE, name);
		ctx.startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

	}

}
