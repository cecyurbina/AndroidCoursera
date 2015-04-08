package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.net.URI;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
    private final static int DOWNLOAD_IMAGE = 0;
    private static Uri myUri;
    private Uri absolutePath;


    static class UIHandler extends Handler {
        WeakReference<DownloadImageActivity> mParent;

        public UIHandler(WeakReference<DownloadImageActivity> parent){
            mParent = parent;
        }

        @Override
        public void handleMessage(Message msg){
           DownloadImageActivity parent = mParent.get();
            if (parent != null){
                switch (msg.what){
                    case DOWNLOAD_IMAGE: {
                        parent.finish();
                        break;
                    }
                }
            }
        }

    }

    Handler handler = new UIHandler(new WeakReference<DownloadImageActivity>(this));

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
        super.onCreate(savedInstanceState);
        // Get the URL associated with the Intent data.
        // @@TODO -- you fill in here.
        myUri = Uri.parse(getIntent().getExtras().getString("URL"));

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.
         new Thread(new DownloadImage(handler)).start();
        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.  See
        // http://stackoverflow.com/questions/20412871/is-it-safe-to-finish-an-android-activity-from-a-background-thread
        // for more discussion about this topic.
    }

    private class DownloadImage implements Runnable {
        private final Handler handler;

        private DownloadImage(Handler handler) {
            this.handler = handler;
        }


        @Override
        public void run() {
            DownloadUtils downloadUtils = new DownloadUtils();
            absolutePath = downloadUtils.downloadImage(getApplicationContext(), myUri);
            Message msg = handler.obtainMessage(DOWNLOAD_IMAGE);
            handler.sendMessage(msg);

        }
    }

    @Override
    public void finish() {
        // Create one data intent
        Intent data = new Intent();
        data.putExtra("ABSOLUTE_PATH", absolutePath.toString());
        setResult(RESULT_OK, data);
        super.finish();
    }

}
