package vandy.mooc.presenter;

import android.content.Context;
import android.os.AsyncTask;

import vandy.mooc.model.mediator.VideoDataMediator;
import vandy.mooc.model.mediator.webdata.AverageVideoRating;
import vandy.mooc.model.mediator.webdata.Video;
import vandy.mooc.model.mediator.webdata.VideoServiceProxy;
import vandy.mooc.view.ui.VideoAdapter;

/**
 * Created by Cecilia Urbina on 15/07/15.
 */
public class VideoRating extends AsyncTask<Video, Void, Void> {
    private VideoServiceProxy mVideoServiceProxy;
    private Context mContext;
    private VideoDataMediator vdm;
    private VideoAdapter mVideoAdapter;
    private int id;
    private AverageVideoRating response;
    /**
     * Used to enable garbage collection.
     */

    //initiate vars
    public VideoRating(Context aContext, VideoAdapter videoAdapter) {
        super();
        //my params here
        mContext = aContext;
        vdm = new VideoDataMediator("user0", "pass");
        mVideoAdapter = videoAdapter;

    }

    @Override
    protected Void doInBackground(Video... params) {
        //Response response = mVideoServiceProxy.getData(params[0].getId());

        long i = 1;
        id = (int) params[0].getId();
        //VideoStatus vs = mVideoServiceProxy.setVideoRating(params[0].getId(), params[0].getRating());
        response = vdm.getmVideoServiceProxy().setVideoRating(params[0].getId(), params[0].getRating());
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        //do stuff
        mVideoAdapter.showToast();
        mVideoAdapter.update(id, (int) response.getRating());

    }


}