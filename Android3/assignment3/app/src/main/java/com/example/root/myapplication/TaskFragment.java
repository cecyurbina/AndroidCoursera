package com.example.root.myapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Cecilia Urbina on 22/04/15.
 */
public class TaskFragment extends Fragment {

    interface TaskCallbacks {
        void onPreExecute();
        void onProgressUpdate(int percent);
        void onCancelled();
        void onPostExecute();
    }

    private TaskCallbacks mCallbacks;
    private  DownloadImageAsync mTask;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        //mTask = new DownloadImageAsync();
        //mTask.execute();
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void executeAsync(){
        mTask = new DownloadImageAsync();
        mTask.execute();
    }

    public class DownloadImageAsync extends AsyncTask<String, Void, String> {
        //ProgressDialog progressDialog;
        Uri imageSavedURI;
        Uri imageGreyURI;

        @Override
        protected String doInBackground(String... params) {
            imageSavedURI = Utils.downloadImage(getActivity(), ((MainActivity)getActivity()).getURL());
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
            if (imageSavedURI == null) {
                Toast toast = Toast.makeText(getActivity(), "error al descargar imagen", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                new ConvertGreyImageAsync().execute(imageSavedURI);
            }

        }

        @Override
        protected void onPreExecute() {
            //progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setMessage("wait to save image");
            //progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    private class ConvertGreyImageAsync extends AsyncTask<Uri, Void, Uri> {
        Uri imageGreyURI;
        //ProgressDialog progressDialog = null;

        @Override
        protected void onPostExecute(Uri result) {
            //if (progressDialog.isShowing() && progressDialog != null) {
            //    progressDialog.dismiss();
            //}
            ((MainActivity)getActivity()).openImage(imageGreyURI);
        }

        @Override
        protected Uri doInBackground(Uri... params) {
            imageGreyURI = Utils.grayScaleFilter(getActivity(), params[0]);
            return imageGreyURI;
        }

        @Override
        protected void onPreExecute() {
            //if (progressDialog == null) {
            //    progressDialog = new ProgressDialog(getActivity());
            //    progressDialog.setMessage("converting to grey scale");
            //}
            //progressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }




}
