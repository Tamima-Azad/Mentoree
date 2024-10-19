package com.example.estudy;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {

    // Function to load and resize image using Bitmap
    public void loadImageIntoImageView(String imageUrl, ImageView imageView, int width, int height, int placeholderResId) {
        // Set a placeholder before the image is loaded
        imageView.setImageResource(placeholderResId);

        // Load the image in the background
        new LoadImageTask(imageView, width, height).execute(imageUrl);
    }

    // AsyncTask to handle image download and resizing in the background
    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        int width, height;

        public LoadImageTask(ImageView imageView, int width, int height) {
            this.imageView = imageView;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);

                // Resize the bitmap to the specified width and height
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);  // Set the resized bitmap to ImageView
            }
        }
    }
}
