package guilherme.krzisch.com.mybeaconclient.view.old_files;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.GateModule.GateManager;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.AboutActivity;
import guilherme.krzisch.com.mybeaconclient.view.util.TTSManager;


public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.lblLatitudeValue) TextView txtLatitude;
    @InjectView(R.id.lblLongitudeValue) TextView txtLongitude;
    @InjectView(R.id.txtTitle) TextView txtTitle;
    @InjectView(R.id.mainActivityView) View mainActivityView;
    @InjectView(R.id.editTextGateDescription) EditText editTextGateDescription;
    TTSManager ttsManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //mostra na tela a posição do GPS
        //txtLatitude.setText(this.getLatitude(this.mainActivityView));
        //txtLongitude.setText(this.getLongitude(this.mainActivityView));

        //TextToSpeech tts = new TextToSpeech(this, this);
        //Locale localeBR = new Locale("pt","br");
        //tts.setLanguage(localeBR);
        //tts.speak("Testando leitor de tela.", TextToSpeech.QUEUE_ADD, null);
        //inicia o monitoramento quando abre o app
        //MyBeaconFacade.startMyBeaconsManagerOperation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                goToAboutActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToAboutActivity(){
        Intent intent = new Intent(getBaseContext(), AboutActivity.class);
        startActivity(intent);
    }

    public void goToCategories(View view){
        //Intent intent = new Intent(getBaseContext(), TabActivity.class);
        //startActivity(intent);
    }

    public void sync(View view) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                MyBeaconFacade.syncAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getBaseContext(), "Synced!", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    public void startOperation(View view) {
        if(!TextUtils.isEmpty(editTextGateDescription.getText().toString())) {
            GateManager.getInstance().writeToFile("//" + editTextGateDescription.getText().toString());
        }
        MyBeaconFacade.startMyBeaconsManagerOperation();
    }

    public void stopOperation(View view) {
        MyBeaconFacade.stopMyBeaconsManagerOperation();
    }
}
