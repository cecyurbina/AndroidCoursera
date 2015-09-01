package vandy.mooc.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import vandy.mooc.R;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.model.mediator.VideoDataMediator;
import vandy.mooc.model.mediator.webdata.Video;
import vandy.mooc.oauth.SecuredRestBuilder;
import vandy.mooc.oauth.UnsafeHttpsClient;
import vandy.mooc.presenter.VideoOps;

public class Login extends Activity {
    private EditText userET;
    private String user;
    private EditText passET;
    private String pass;
    private Button buttonLogin;
    private VideoDataMediator vdm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userET = (EditText) this.findViewById(R.id.etUserName);
        passET = (EditText) this.findViewById(R.id.etPass);
        buttonLogin = (Button) this.findViewById(R.id.btnSingIn);
        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                user = userET.getText().toString();
                pass = passET.getText().toString();
                Log.d("$$$user", user);
                Log.d("$$pass", pass);
                login();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void login(){
        vdm = new VideoDataMediator(user, pass);
        /*if (vdm.getVideoList() == null){
            Toast.makeText(Login.this, "Wrong username/password", Toast.LENGTH_LONG).show();
        }*/
        getVideoList();
        /*SecuredRestBuilder videoServiceApi = new SecuredRestBuilder()
                .setLoginEndpoint(SERVER_URL + VideoSvcApi.TOKEN_PATH)
                .setUsername(USERNAME)
                .setPassword(PASSWORD)
                .setClientId(CLIENT_ID)
                .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
                .setEndpoint(SERVER_URL).setLogLevel(RestAdapter.LogLevel.FULL).build()
                .create(VideoSvcApi.class);*/
    }

    public void getVideoList(){
        new GetVideos().execute();
    }

    private class GetVideos extends AsyncTask<Void, Void, Integer> {

        private boolean loginValid = false;
        private List<Video> listVideo = null;

        @Override
        protected Integer doInBackground(Void... params) {
            listVideo = vdm.getVideoList();
            return 1;
        }


        protected void onPostExecute(Integer result) {
/*
            showDialog("Downloaded " + result + " bytes");

*/
            if (listVideo == null){
                Log.d("%%%%&&&&&", "es nulo");
            }
            else {
                Log.d("%%%&&&&&", "no es nulo");
                
            }
        }
    }
}
