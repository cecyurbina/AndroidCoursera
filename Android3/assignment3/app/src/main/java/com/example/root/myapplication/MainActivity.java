package com.example.root.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
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


public class MainActivity extends Activity implements TaskFragment.TaskCallbacks {
    private EditText urlLink;
    private String stringURL;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";

    private TaskFragment mTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlLink = (EditText) findViewById(R.id.url);

        FragmentManager fm = getFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }


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
                mTaskFragment.executeAsync();
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





   public Uri getURL(){
        return Uri.parse(stringURL);
    }

    public void openImage(Uri imageGreyUri){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://"+imageGreyUri), "image/*");
        startActivity(intent);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute() {

    }
}
