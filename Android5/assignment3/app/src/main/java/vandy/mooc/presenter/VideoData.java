package vandy.mooc.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;
import vandy.mooc.model.mediator.webdata.Video;
import vandy.mooc.model.mediator.webdata.VideoServiceProxy;
import vandy.mooc.model.mediator.webdata.VideoStatus;
import vandy.mooc.utils.Constants;
import vandy.mooc.utils.VideoStorageUtils;
import vandy.mooc.view.VideoListActivity;

/**
 * Created by Cecilia Urbina on 15/07/15.
 */
public class VideoData extends AsyncTask<Video, Void, Void> {
    private VideoServiceProxy mVideoServiceProxy;
    private Context mContext;
    /**
     * Used to enable garbage collection.
     */

    //initiate vars
    public VideoData(Context aContext) {
        super();
        //my params here
        mContext = aContext;
        mVideoServiceProxy = new RestAdapter
                .Builder()
                .setEndpoint(Constants.SERVER_URL)
                .build()
                .create(VideoServiceProxy.class);
    }

    @Override
    protected Void doInBackground(Video... params) {
        //Response response = mVideoServiceProxy.getData(params[0].getId());
        Response response = mVideoServiceProxy.getData(params[0].getId());
        long i = 1;
        VideoStatus vs = mVideoServiceProxy.setVideoRating(i, 4);
        Log.d("&&&&", vs.getState().name());
        String fileName = params[0].getTitle();
        String extension = params[0].getContentType();
        VideoStorageUtils.storeVideoInExternalDirectory(mContext.getApplicationContext(),
                response, fileName);
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {

        //do stuff
    }


}