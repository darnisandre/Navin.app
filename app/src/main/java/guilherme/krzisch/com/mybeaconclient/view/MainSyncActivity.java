package guilherme.krzisch.com.mybeaconclient.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.R;



public class MainSyncActivity extends AppCompatActivity {

    @InjectView(R.id.mainSyncActivityView) View mainSyncActivityView;
    @InjectView(R.id.chkBT) CheckBox chkBT;
    @InjectView(R.id.chkGPS) CheckBox chkGPS;
    @InjectView(R.id.chkCompass) CheckBox chkCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sync);
        ButterKnife.inject(this);

        //limpa os checkboxes
        chkBT.setChecked(false);
        chkGPS.setChecked(false);
        chkCompass.setChecked(false);
        chkCompass.setText("Bússola (Algumas funcionalidades ficaram limitadas sem este recurso)");

        //pede para ligar BT
        this.enableBT(this.mainSyncActivityView);

        //pede para ligar GPS
        this.enableGPS(this.mainSyncActivityView);

        //verifica se tem bússola
        if(this.compassVerify(this.mainSyncActivityView)){
            chkCompass.setChecked(true);
            chkCompass.setText("Bússola");
        }

        //verifica se tudo está ligado
        this.verifyOK();

        this.mainSyncActivityView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getBaseContext(), CompassActivity.class);
                startActivity(intent);
                // do your logic for long click and remember to return it
                return true; }});

    }

    private boolean compassVerify(View mainSyncActivityView) {
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        //List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(gsensor == null || msensor == null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        //limpa os checkboxes
        chkBT.setChecked(false);
        chkGPS.setChecked(false);

        //pede para ligar BT
        this.enableBT(this.mainSyncActivityView);

        //pede para ligar GPS
        this.enableGPS(this.mainSyncActivityView);

        //verifica se tudo está ligado
        this.verifyOK();
    }

    public void verifyOK(){
        //verifica se tudo está ligado
        if(chkBT.isChecked() && chkGPS.isChecked()){
            Intent intent = new Intent(getBaseContext(), MainTabActivity.class);
            startActivity(intent);
            finish();
        }
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
        else{
            chkBT.setChecked(true);

            //verifica se tudo está ligado
            this.verifyOK();
        }
    }

    public void enableGPS(View view){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!GPSEnabled){
            int REQUEST_ENABLE_GPS = 2;
            this.showGPSDisabledAlertToUser(REQUEST_ENABLE_GPS);
            //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS);
        }else{
            chkGPS.setChecked(true);

            //verifica se tudo está ligado
            this.verifyOK();
        }
    }

    private void showGPSDisabledAlertToUser(int id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS está desativado. Você deseja habilitar?")
                .setCancelable(false)
                .setPositiveButton("Ir para configurações",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent, id);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) { //bluetooth
            if(resultCode == Activity.RESULT_OK){
                chkBT.setChecked(true);
                //verifica se tudo está ligado
                this.verifyOK();
            }
        }
        if (requestCode == 2) { //GPS
            if(resultCode == Activity.RESULT_OK){
                chkGPS.setChecked(true);
                //verifica se tudo está ligado
                this.verifyOK();
            }
        }
    }//onActivityResult

}
