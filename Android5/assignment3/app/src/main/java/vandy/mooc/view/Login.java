package vandy.mooc.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import vandy.mooc.R;
import vandy.mooc.model.mediator.VideoDataMediator;
import vandy.mooc.model.mediator.webdata.Video;

public class Login extends Activity {
    private EditText userET;
    private String user;
    private EditText passET;
    private String pass;
    private Button buttonLogin;
    private VideoDataMediator vdm;
    public static Context contextOfApplication;




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
                login();
                contextOfApplication = getApplicationContext();

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
        //put token in null to save another users token
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());//token saved
        prefs.edit().putString("token", null).commit();

        //call securedrestbuilder first time
        vdm = new VideoDataMediator(user, pass);
        getVideoList();
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
            Context context = getApplicationContext();
            if (listVideo == null){
                CharSequence text = "Error in account";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else {
                Intent intent = new Intent(context, VideoListActivity.class);
                startActivity(intent);


            }
        }
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }
}
