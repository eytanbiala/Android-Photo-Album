package com.example.androidphotoalbum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
public class PhotoGridAdapter extends BaseAdapter implements View.OnCreateContextMenuListener {

	private Context ctx;
	List<Photo> photoList;
	Controller ctrl;
	LayoutInflater inflater;
	ViewHolder holder;
	String album;
	BitmapFactory.Options options;
	private int placeholderWidth;
	private int placeholderHeight;
	
	public PhotoGridAdapter(Context c, String a) {
		super();
		this.ctx = c;
		this.album = a;
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ctrl = Controller.getInstance(ctx);

		HashMap<String, Album> albums = ctrl.getAlbums();
		photoList = new ArrayList<Photo>(albums.get(this.album).getPhotos().values());
		
		
		options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		
		placeholderWidth = (int) (1.1 * ctx.getResources().getDrawable(R.drawable.no_image)
				.getIntrinsicWidth());
		placeholderHeight = (int) (1.1 * ctx.getResources().getDrawable(R.drawable.no_image)
				.getIntrinsicHeight());
	}

	@Override
	public int getCount() {
		return photoList.size();
	}

	@Override
	public Object getItem(int position) {
		return photoList.get(position);
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

		String filepath = new File(ctx.getFilesDir() + File.separator
				+ photoList.get(position).getFilename()).getAbsolutePath();
		
		holder.image.setLayoutParams(new LinearLayout.LayoutParams(placeholderWidth, placeholderHeight));
		
//		Bitmap image = BitmapFactory.decodeFile(filepath, options);
//		Bitmap resized = Bitmap.createScaledBitmap(image, placeholderWidth, placeholderHeight, true);
//		holder.image.setImageBitmap(resized);
//		image.recycle();
		
		loadBitmap(filepath, holder.image);
		 
		holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		holder.image.setId(position);

		holder.image.setLayoutParams(new LinearLayout.LayoutParams(150, 150));

		String a = photoList.get(position).getPhotoFilenameWithoutExtension();
		holder.caption.setText(a);

		holder.container.setTag(R.string.photo_filename, photoList.get(position).getPhotoFilename());

		holder.caption.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = ((TextView) v.findViewById(R.id.caption)).getText().toString();
				showPhotoIntent(name);
			}
		});

		holder.container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPhotoIntent(v.getTag(R.string.photo_filename).toString());
			}
		});

		return convertView;
	}
	public void showPhotoIntent(String name) {

		Intent i = new Intent(ctx, FullImageActivity.class);
		i.putExtra(AppConstants.MODE, 0);
		i.putExtra(AppConstants.PHOTO_FILENAME, name);
		i.putExtra(AppConstants.ALBUM_TITLE, this.album);
		ctx.startActivity(i);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

	}
	
	public static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }
            catch (FileNotFoundException e) {}
        return null;
    }
	
	public void loadBitmap(String filepath, ImageView imageView) {
		BitMapWorkerTask task = new BitMapWorkerTask(imageView);
		task.execute(filepath);
	}
	
	class BitMapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		private WeakReference<ImageView> imageViewReference = null;
	    private String data = null;

	    public BitMapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	    	imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... params) {
	        data = params[0];
	        Bitmap image = BitmapFactory.decodeFile(data, options);
	        Bitmap resized = Bitmap.createScaledBitmap(image, placeholderWidth, placeholderHeight, true);
	        image.recycle();
	        image = null;
	        return resized;
//	        return decodeSampledBitmapFromResource(getResources(), data, 100, 100));
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	                bitmap = null;
	                imageView = null;
	            } else {
	            	bitmap.recycle();
	            }
	        }
	    }
		
	}
}
