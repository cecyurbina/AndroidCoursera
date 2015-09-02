package vandy.mooc.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.HttpResponseException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import vandy.mooc.model.mediator.webdata.Video;
import vandy.mooc.model.mediator.webdata.VideoServiceProxy;
import vandy.mooc.model.mediator.webdata.VideoStatus;
import vandy.mooc.oauth.SecuredRestBuilder;
import vandy.mooc.oauth.UnsafeHttpsClient;
import vandy.mooc.utils.Constants;
import vandy.mooc.utils.VideoStorageUtils;
import vandy.mooc.view.VideoListActivity;

/**
 * Created by Cecilia Urbina on 15/07/15.
 */
public class VideoData extends AsyncTask<Video, Void, Void> {
    private VideoServiceProxy mVideoServiceProxy;
    private Context mContext;
    private File mFile =  null;
    /**
     * Used to enable garbage collection.
     */

    //initiate vars
    public VideoData(Context aContext) {
        super();
        //my params here
        mContext = aContext;
        //TODO: change this
        /*mVideoServiceProxy = new RestAdapter
                .Builder()
                .setEndpoint(Constants.SERVER_URL)
                .build()
                .create(VideoServiceProxy.class);*/
        mVideoServiceProxy = new SecuredRestBuilder()
                .setLoginEndpoint(Constants.SERVER_URL
                        + VideoServiceProxy.TOKEN_PATH)
                .setUsername("user0")
                .setPassword("pass")
                .setClientId("mobile")
                .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
                .setEndpoint(Constants.SERVER_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL).build()
                .create(VideoServiceProxy.class);
    }

    @Override
    protected Void doInBackground(Video... params) {
        try {
            Response response = mVideoServiceProxy.getData(params[0].getId());
            String fileName = params[0].getTitle();
            mFile = VideoStorageUtils.storeVideoInExternalDirectory(mContext.getApplicationContext(), response, fileName);
        } catch (RetrofitError e) {
            System.out.println(e.getResponse().getStatus());
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        if (mFile != null){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(mFile));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(mFile), "video/mp4");
            mContext.startActivity(intent);
        }

    }


}