package com.example.root.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {
    private EditText urlLink;
    private String stringURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlLink = (EditText) findViewById(R.id.url);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * Called by the Android Activity framework when the user clicks
     * the "Download Image" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
            stringURL = urlLink.getText().toString();
            if (!isValidUrl(stringURL)){
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.url_invalid), Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                new DownloadImageAsync().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        if(m.matches()) {
            return true;
        }
        else{
            return false;

        }
    }


    private class DownloadImageAsync extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        Uri imageSavedURI;
        Uri imageGreyURI;

        @Override
        protected String doInBackground(String... params) {
            imageSavedURI = Utils.downloadImage(getApplicationContext(), getURL());
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (imageSavedURI == null) {
                Toast toast = Toast.makeText(getApplicationContext(), "error al descargar imagen", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                new ConvertGreyImageAsync().execute(imageSavedURI);
            }

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("wait to save image");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    private class ConvertGreyImageAsync extends AsyncTask<Uri, Void, Uri> {
        Uri imageGreyURI;
        ProgressDialog progressDialog = null;

        @Override
        protected void onPostExecute(Uri result) {
            if (progressDialog.isShowing() && progressDialog != null) {
                progressDialog.dismiss();
            }
            MainActivity.this.openImage(imageGreyURI);

        }

        @Override
        protected Uri doInBackground(Uri... params) {
            imageGreyURI = Utils.grayScaleFilter(getApplicationContext(), params[0]);
            return imageGreyURI;
        }

        @Override
        protected void onPreExecute() {
             if (progressDialog == null) {
                 progressDialog = new ProgressDialog(MainActivity.this);
                 progressDialog.setMessage("converting to grey scale");
             }
                progressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }



    private Uri getURL(){
        return Uri.parse(stringURL);
    }

    private void openImage(Uri imageGreyUri){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://"+imageGreyUri), "image/*");
        startActivity(intent);
    }

}
