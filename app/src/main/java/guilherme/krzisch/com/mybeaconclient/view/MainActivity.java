package guilherme.krzisch.com.mybeaconclient.view;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.GateModule.GateManager;


public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.lblLatitudeValue) TextView txtLatitude;
    @InjectView(R.id.lblLongitudeValue) TextView txtLongitude;
    @InjectView(R.id.txtTitle) TextView txtTitle;
    @InjectView(R.id.mainActivityView) View mainActivityView;
    @InjectView(R.id.editTextGateDescription) EditText editTextGateDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //liga o bluetooth automaticamente
        this.enableBT(this.mainActivityView);

        //mostra na tela a posição do GPS
        txtLatitude.setText(this.getLatitude(this.mainActivityView));
        txtLongitude.setText(this.getLongitude(this.mainActivityView));

        //TextToSpeech tts = new TextToSpeech(this, this);
        //Locale localeBR = new Locale("pt","br");
        //tts.setLanguage(localeBR);
        //tts.speak("Testando leitor de tela.", TextToSpeech.QUEUE_ADD, null);
        //inicia o monitoramento quando abre o app
        //MyBeaconFacade.startMyBeaconsManagerOperation();
    }

    public String getLatitude(View view){
        //pega a posição atual do GPS
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude = location.getLatitude();
        return Double.toString(latitude);
    }

    public String getLongitude(View view){
        //pega a posição atual do GPS
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        return Double.toString(longitude);
    }

    public void enableBT(View view){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // The REQUEST_ENABLE_BT constant passed to startActivityForResult() is a locally defined integer (which must be greater than 0), that the system passes back to you in your onActivityResult()
            // implementation as the requestCode parameter.
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(intentBtEnabled, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                goToSettingsActivity();
                return true;
            case R.id.action_about:
                goToAboutActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToSettingsActivity(){
        Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(intent);
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
