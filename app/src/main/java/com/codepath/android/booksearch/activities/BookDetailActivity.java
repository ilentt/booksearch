package com.codepath.android.booksearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.models.Book;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private ShareActionProvider shareActionProvider;
    private Intent shareIntern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // Fetch views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);

        // Extract book object from intent extras
        Book book = getIntent().getParcelableExtra("book");

        // Use book object to populate data into views
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        Picasso.with(this).load(Uri.parse(book.getCoverUrl())).placeholder(R.drawable.ic_nocover).into(ivBookCover);

        // load image async from remote URL, setup share when completed
        Picasso.with(this).load(book.getCoverUrl()).into(ivBookCover, new Callback() {
            @Override
            public void onSuccess() {
                // setup share intern now that image that loaded
                onShareIntern();
            }

            @Override
            public void onError() {

            }
        });

    }

    public void onShareIntern() {
        // fetch Bitmap Uri locally
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        // get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivBookCover);
        // create share intern as describe above
        shareIntern = new Intent();
        shareIntern.setAction(Intent.ACTION_SEND);
        shareIntern.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntern.setType("image/*");
        /*
        if(bmpUri != null) {
            // construct a shareIntern with the link image
            Intent shareIntern = new Intent();
            shareIntern.setAction(Intent.ACTION_SEND);
            shareIntern.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntern.setType("image/*");
            // launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntern, "Share Image"));
        } else {
            // sharing failed, handle error
        }
        */
    }

    // return the URI path to the bitmap displayed in the specified ImageView
    public Uri getLocalBitmapUri(ImageView ivImage) {
        // extract Bitmap from the ImageView drawable
        Drawable drawable = ivImage.getDrawable();
        Bitmap bitmap;
        if(drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
        } else {
            return null;
        }

        // Store image to default external store directory
        Uri bmpUri = null;
        try {
            // use methods on context to access packed specific directories on external storage
            // this way, you don't need to request external read/write permission
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        }catch(IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        // Locate MenuItem with ShareActionProvide
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        // return true to display menu
        shareActionProvider.setShareIntent(shareIntern);

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

        return super.onOptionsItemSelected(item);
    }
}
