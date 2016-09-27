package guilherme.krzisch.com.mybeaconclient.view.sync_options;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.view.testes.CompassActivity;
import guilherme.krzisch.com.mybeaconclient.view.MainTabActivity;
import navin.dto.BeaconMappingDTO;
import navin.dto.CategoryDTO;
import navin.dto.LocationDTO;
import navin.dto.RouteDTO;
import navin.tree.BeaconTree;


public class MainSyncActivity extends AppCompatActivity {

    @InjectView(R.id.mainSyncActivityView) View mainSyncActivityView;
    @InjectView(R.id.switchBT) Switch switchBT;
    @InjectView(R.id.switchGPS) Switch switchGPS;
    @InjectView(R.id.chkCompass) CheckBox chkCompass;
    @InjectView(R.id.buttonInit) Button buttonInit;
    @InjectView(R.id.progressBar) ProgressBar progressBar;
    @InjectView(R.id.textViewLocal) TextView textViewLocal;
    LocationManager lm = null;
    Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sync);
        ButterKnife.inject(this);

        //onclick do botão
        buttonInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainTabActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //onchange do switch do bluetooth
        switchBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //pede para ligar BT
                    enableBT(mainSyncActivityView);

                }
            }
        });
        if(switchBT.isChecked()){
            switchBT.setEnabled(false);
        }else{
            switchBT.setEnabled(true);
        }

        //onchange do switch do gps
        switchGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //pede para ligar BT
                    enableGPS(mainSyncActivityView);
                    switchGPS.setEnabled(false);
                }
            }
        });
        if(switchGPS.isChecked()){
            switchGPS.setEnabled(false);
        }else{
            switchGPS.setEnabled(true);
        }

        //verifica se tem bússola
        if(this.compassVerify(this.mainSyncActivityView)){
            chkCompass.setChecked(true);
        }else{
            chkCompass.setChecked(false);
        }

        //onlongclick da tela
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

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            switchBT.setChecked(false);
            switchBT.setEnabled(true);
        }else{
            switchBT.setChecked(true);
            switchBT.setEnabled(false);
        }

        //inicia o location manager
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        boolean GPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!GPSEnabled){
            switchGPS.setChecked(false);
            switchGPS.setEnabled(true);
        }else{
            if(location == null){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            switchGPS.setChecked(true);
            switchGPS.setEnabled(false);
        }

        this.verifyOK();
    }

    public void verifyOK(){

        //TODO achar um modo de poder utilizar chamada REST na main sem usar isso
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //verifica se tudo está ligado
        if(switchBT.isChecked() && switchGPS.isChecked()){
            Log.i("GPS", String.valueOf(getLatitude(this.mainSyncActivityView)));
            if(getLatitude(this.mainSyncActivityView) == 0) {
                progressBar.setVisibility(View.VISIBLE);
                textViewLocal.setVisibility(View.VISIBLE);
                textViewLocal.setText("Buscando localização..");
                String myText1 = String.valueOf(textViewLocal.getText());
                MyApp.getAppTTS().initQueue(myText1);
            }
            else if(getLatitude(this.mainSyncActivityView) != 0){

                //-30.05849
                //-51.17602

                //TODO HERE
                //depois que tem a localização do GPS, buscar no banco de dados e sincronizar os objetos
                //mudar o texto do textViewLocal para o nome do local que buscou no banco
                RouteDTO r = MyApp.getInternalCache().getRoutes(1).get(0);
                LocationDTO l = MyApp.getInternalCache().getLocations(-30.05849,-51.17602).get(0);
                //LocationDTO l = MyApp.getInternalCache().getLocations(0,0).get(0);
                BeaconMappingDTO mapping = MyApp.getInternalCache().getBeaconMapping(2);
                //CategoryDTO category = MyApp.getInternalCache().getCategories(1).get(0);

                BeaconTree tree = new BeaconTree(mapping);
                tree.getRoute(Arrays.asList(10l,11l,12l),8l);
                tree.getRouteTspHeuristic(Arrays.asList(10l,11l,12l),8l);
                tree.getRouteA(Arrays.asList(10l,11l,12l),8l);

                //acho que vamos ter que fazer isso pra toda estrutura
                MyApp.setLocation(MyApp.getInternalCache().getLocations(-30.05849,-51.17602).get(0));
                //MyApp.setLocation(MyApp.getInternalCache().getLocations(0,0).get(0));
                MyApp.setRoutes(MyApp.getInternalCache().getRoutes(Integer.parseInt(MyApp.getLocation().getId().toString())));
                MyApp.setBeaconMapping(MyApp.getInternalCache().getBeaconMapping(Integer.parseInt(MyApp.getLocation().getId().toString())));
                MyApp.setCategories(MyApp.getInternalCache().getCategories(Integer.parseInt(MyApp.getLocation().getId().toString())));

                progressBar.setVisibility(View.INVISIBLE);
                textViewLocal.setText(MyApp.getBeaconMapping().getLocation().getDescription());// + "\n" + category.getName());
                textViewLocal.setVisibility(View.VISIBLE);

                //voz informando que encontrou a localização
                String myText = String.valueOf(textViewLocal.getText());
                MyApp.getAppTTS().initQueue(myText);

                //habilita o botão para ir pro aplicativo quando tudo estiver ligado e sincronizado
                buttonInit.setEnabled(true);
            }
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            textViewLocal.setVisibility(View.INVISIBLE);
            buttonInit.setEnabled(false);
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
            switchBT.setChecked(true);
            switchBT.setEnabled(false);
            //verifica se tudo está ligado
            //this.verifyOK();
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
            switchGPS.setChecked(true);
            switchGPS.setEnabled(false);
            //verifica se tudo está ligado
            //this.verifyOK();
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

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location locat) {
            // Called when a new location is found by the network location provider.
            Log.i("GPS", "found");
            location = locat;
            lm.removeUpdates(locationListener);

            verifyOK();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };


    public double getLatitude(View view){
        //pega a posição atual do GPS
        if(location != null) {
            double latitude = location.getLatitude();
            lm.removeUpdates(locationListener);
            return latitude;
        }
        else{
            return 0;
        }
    }

    public double getLongitude(View view){
        //pega a posição atual do GPS
        if(location != null){
            double longitude = location.getLongitude();
            lm.removeUpdates(locationListener);
            return longitude;
        }
        else{
            return 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RESUME", "RESUME");
        refresh();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) { //bluetooth
            if(resultCode == Activity.RESULT_OK){
                switchBT.setChecked(true);
                switchBT.setEnabled(false);
                //verifica se tudo está ligado
                //this.verifyOK();
            }else{
                switchBT.setChecked(false);
                switchBT.setEnabled(true);
            }
        }
        if (requestCode == 2) { //GPS
            if(resultCode == Activity.RESULT_OK){
                switchGPS.setChecked(true);
                switchGPS.setEnabled(false);
                //verifica se tudo está ligado
                //this.verifyOK();
            }else{
                switchGPS.setChecked(false);
                switchGPS.setEnabled(true);
            }
        }
    }//onActivityResult

}
