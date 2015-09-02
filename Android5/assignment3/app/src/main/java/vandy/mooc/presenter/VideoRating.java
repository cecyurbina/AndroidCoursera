package vandy.mooc.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;
import vandy.mooc.model.mediator.VideoDataMediator;
import vandy.mooc.model.mediator.webdata.Video;
import vandy.mooc.model.mediator.webdata.VideoServiceProxy;
import vandy.mooc.model.mediator.webdata.VideoStatus;
import vandy.mooc.utils.Constants;
import vandy.mooc.utils.VideoStorageUtils;
import vandy.mooc.view.VideoListActivity;

/**
 * Created by Cecilia Urbina on 15/07/15.
 */
public class VideoRating extends AsyncTask<Video, Void, Void> {
    private VideoServiceProxy mVideoServiceProxy;
    private Context mContext;
    private VideoDataMediator vdm;
    /**
     * Used to enable garbage collection.
     */

    //initiate vars
    public VideoRating(Context aContext) {
        super();
        //my params here
        mContext = aContext;
        vdm = new VideoDataMediator("user0", "pass");

    }

    @Override
    protected Void doInBackground(Video... params) {
        //Response response = mVideoServiceProxy.getData(params[0].getId());
        long i = 1;
        //VideoStatus vs = mVideoServiceProxy.setVideoRating(params[0].getId(), params[0].getRating());
        vdm.getmVideoServiceProxy().setVideoRating(params[0].getId(), params[0].getRating());
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        //do stuff
    }


}