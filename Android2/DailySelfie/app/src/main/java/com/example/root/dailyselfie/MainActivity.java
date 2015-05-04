package com.example.root.dailyselfie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private ListView mainListView ;
    private List<RowData> listSelfies = new ArrayList<>();
    private CustomAdapter customAdapter;
    private String nameFile;
    Calendar calendar;
    private PendingIntent alarmIntent;
    AlarmManager alarmManager = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find the ListView resource.
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }


        calendar = Calendar.getInstance();
        Long time = new GregorianCalendar().getTimeInMillis()+60*06*24*1000;

        Intent intentAlarm = new Intent(this, AlarmReceiver.class);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent=PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP,time, alarmIntent);

        long minute = 60000;
        long twominutes = 120000;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),minute, alarmIntent);



        mainListView = (ListView) findViewById( R.id.list_row);
        getDataInList();
        customAdapter = new CustomAdapter(getApplicationContext(), listSelfies);
        mainListView.setAdapter(customAdapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                String selected = ((TextView) view.findViewById(R.id.secondLine)).getText().toString();
                showImage(selected);


            }
        });

    }

    public void showImage(String aSelected){
        String selected = aSelected.replace("Thumb","");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + selected), "image/*");
        startActivity(intent);
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
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        if (id == R.id.action_search){
            showCameraIntent();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }



    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DailySelfie");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }
        nameFile = "IMG_"+ timeStamp + ".jpg";
        return mediaFile;
    }

    public class CustomAdapter extends BaseAdapter {

        List<RowData> myList;
        LayoutInflater inflater;
        Context context;


        public CustomAdapter(Context context, List<RowData> myList) {
            this.myList = myList;
            this.context = context;
            inflater = LayoutInflater.from(this.context);        // only context can also be used
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public RowData getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder mViewHolder;

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_list, null);
                mViewHolder = new MyViewHolder();
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            mViewHolder.tvTitle = detail(convertView, R.id.firstLine, myList.get(position).getTitle());
            mViewHolder.tvDesc  = detail(convertView, R.id.secondLine,  myList.get(position).getDescription());
            mViewHolder.ivIcon  = detailI(convertView, R.id.icon,  myList.get(position).getImgResId());

            return convertView;
        }

        // or you can try better way
        private TextView detail(View v, int resId, String text) {
            TextView tv = (TextView) v.findViewById(resId);
            tv.setText(text);
            return tv;
        }

        private ImageView detailI(View v, int resId, String icon) {
            ImageView iv = (ImageView) v.findViewById(resId);
            //iv.setImageResource(icon);
            Bitmap bMap = BitmapFactory.decodeFile(icon);
            iv.setImageBitmap(bMap);
            return iv;
        }

        private class MyViewHolder {
            TextView tvTitle, tvDesc;
            ImageView ivIcon;
        }
    }

    private void getDataInList() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DailySelfieThumb");
        File[] filelist = dir.listFiles();
        listSelfies.clear();
        try {
            for (File f : filelist) { // do your stuff here }
                RowData ld = new RowData();
                ld.setTitle(f.getName());
                ld.setDescription(f.getPath());
                ld.setImgResId(f.getPath());
                // Add this object into the ArrayList myList
                listSelfies.add(ld);

            }
        }catch (Throwable e) {
            e.printStackTrace();
        }



    }

    private void uploadDataInList() {
        getDataInList();
        customAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        Bitmap photoThumb;
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode == RESULT_OK)
        {
            photoThumb = Utilities.getThumbnailFromFile(fileUri.getPath().toString());
            Utilities.saveBitmapToFile(photoThumb, nameFile);
            uploadDataInList();
        }
    }
}
