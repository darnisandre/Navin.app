package guilherme.krzisch.com.mybeaconclient.view.sync_options;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.BeaconObject;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.view.MainPageActivity;
import guilherme.krzisch.com.mybeaconclient.view.route_navigation.RouteMainActivity;
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;
import navin.dto.LocationDTO;
import navin.dto.RouteDTO;

public class MainLocationActivity extends AppCompatActivity {

    LocationManager lm = null;
    Location location = null;
    public static Context baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_location);

        baseContext = getBaseContext();

        //TODO achar um modo de poder utilizar chamada REST na main sem usar isso
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        TextView error = (TextView) findViewById(R.id.textViewError);
        error.setVisibility(View.INVISIBLE);
        ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
        local.setVisibility(View.INVISIBLE);
        ListView listView = (ListView) findViewById(R.id.listViewLocations);
        listView.setVisibility(View.INVISIBLE);

        enableBlueTooth();

    }

    public void enableBlueTooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // The REQUEST_ENABLE_BT constant passed to startActivityForResult() is a locally defined integer (which must be greater than 0), that the system passes back to you in your onActivityResult()
            // implementation as the requestCode parameter.
            int REQUEST_ENABLE_BT = 1;
            startActivityForResult(intentBtEnabled, REQUEST_ENABLE_BT);
        }
        else{
            loadLocations();
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

        TextView error = (TextView) findViewById(R.id.textViewError);
        error.setVisibility(View.INVISIBLE);
        ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
        local.setVisibility(View.VISIBLE);
        TextView location = (TextView) findViewById(R.id.textViewLocationStatus);
        location.setText("");
        ListView listView = (ListView) findViewById(R.id.listViewLocations);
        listView.setVisibility(View.INVISIBLE);

        enableBlueTooth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) { //bluetooth
            if (resultCode == Activity.RESULT_OK) {
                TextView error = (TextView) findViewById(R.id.textViewError);
                error.setVisibility(View.INVISIBLE);
                ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
                local.setVisibility(View.INVISIBLE);
                //chama método para buscar os locais
                loadLocations();
            } else {
                TextView error = (TextView) findViewById(R.id.textViewError);
                error.setVisibility(View.VISIBLE);
                ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
                local.setVisibility(View.INVISIBLE);
                //mostra mensagem de erro
            }
        }
    }

    private void locationFound() {

        ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
        local.setVisibility(View.VISIBLE);
        TextView location = (TextView) findViewById(R.id.textViewLocationStatus);
        location.setText("Atualizando dados");

        refreshData(-1);
    }

    private void refreshData(int locationID){

        //se encontrou pelo GPS
        if(locationID == -1){
            if (getLatitude() != 0 && getLongitude() != 0) {

                //-30.05849
                //-51.17602

                //MyApp.setLocation(MyApp.getInternalCache().getLocations(-30.05849,-51.17602).get(0));
                MyApp.setLocation(MyApp.getInternalCache().getLocations(0, 0).get(0));
                MyApp.setRoutes(MyApp.getInternalCache().getRoutes(Integer.parseInt(MyApp.getLocation().getId().toString())));
                MyApp.setRoutesPersonalized(MyApp.getInternalCache().getRoutesPersonalized(Integer.parseInt(MyApp.getLocation().getId().toString())));
                MyApp.setBeaconMapping(MyApp.getInternalCache().getBeaconMapping(Integer.parseInt(MyApp.getLocation().getId().toString())));
                MyApp.setCategories(MyApp.getInternalCache().getCategories(Integer.parseInt(MyApp.getLocation().getId().toString())));

                //adiciona os beacons no framework e inicia monitoramento
                BeaconMappingDTO mapping = MyApp.getBeaconMapping();
                List<BeaconDTO> bLst = mapping.getBeacons();

                ArrayList<BeaconObject> ar = new ArrayList<BeaconObject>();

                //adiciona todos eles no framework
                for (BeaconDTO b : bLst) {

                    int type;
                    Log.i("BTYPE:", b.getType().getDescription());
                    if(b.getType().getDescription().equals("OBJECT_BEACON_TYPE")){
                        type = BeaconObject.OBJECT_BEACON_TYPE;
                    }else if(b.getType().getDescription().equals("SECTOR_BEACON_TYPE")) {
                        type = BeaconObject.SECTOR_BEACON_TYPE;
                    }else{
                        type = 0;
                    }
                    BeaconObject a = new BeaconObject(String.valueOf(b.getId()), b.getUuid(),
                            Integer.valueOf(b.getMajor().toString()), Integer.valueOf(b.getMinor().toString()), type, "", 0, 0);
                    ar.add(a);
                }
                MyBeaconFacade.addBeaconsLocally(ar);

                //inicia o monitoramento
                MyBeaconFacade.startMyBeaconsManagerOperation();

                Toast.makeText(getBaseContext(), "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getBaseContext(), MainPageActivity.class);
                startActivity(intent);
                finish();

            }

        }
        //se selecionou um local da lista
        else{

            MyApp.setLocation(MyApp.getInternalCache().getLocation((long)locationID));
            MyApp.setRoutes(MyApp.getInternalCache().getRoutes(Integer.parseInt(MyApp.getLocation().getId().toString())));
            MyApp.setRoutesPersonalized(MyApp.getInternalCache().getRoutesPersonalized(Integer.parseInt(MyApp.getLocation().getId().toString())));
            MyApp.setBeaconMapping(MyApp.getInternalCache().getBeaconMapping(Integer.parseInt(MyApp.getLocation().getId().toString())));
            MyApp.setCategories(MyApp.getInternalCache().getCategories(Integer.parseInt(MyApp.getLocation().getId().toString())));

            //adiciona os beacons no framework e inicia monitoramento
            BeaconMappingDTO mapping = MyApp.getBeaconMapping();
            List<BeaconDTO> bLst = mapping.getBeacons();

            ArrayList<BeaconObject> ar = new ArrayList<BeaconObject>();

            //adiciona todos eles no framework
            for (BeaconDTO b : bLst) {
                int type;
                if(b.getType().getDescription().equals("OBJECT_BEACON_TYPE")){
                    type = BeaconObject.OBJECT_BEACON_TYPE;
                }else if(b.getType().getDescription().equals("SECTOR_BEACON_TYPE")) {
                    type = BeaconObject.SECTOR_BEACON_TYPE;
                }else{
                    type = 0;
                }
                BeaconObject a = new BeaconObject(String.valueOf(b.getId()), b.getUuid(),
                        Integer.valueOf(b.getMajor().toString()), Integer.valueOf(b.getMinor().toString()), type, "", 0, 0);
                ar.add(a);
            }
            MyBeaconFacade.addBeaconsLocally(ar);

            //inicia o monitoramento
            MyBeaconFacade.startMyBeaconsManagerOperation();

            Toast.makeText(getBaseContext(), "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getBaseContext(), MainPageActivity.class);
            startActivity(intent);
            finish();

        }

    }

    private void locationSelect() {

        ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
        local.setVisibility(View.INVISIBLE);

        final List<LocationDTO> locals = MyApp.getInternalCache().getLocations();

        //TODO HERE mostrar lista de locais
        List<String> values = new ArrayList<String>();
        for(LocationDTO b : locals){
            values.add(b.getDescription());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);
                if(position %2 == 1)
                {
                    // Set a background color for ListView regular row/item
                    view.setBackgroundColor(Color.parseColor("#eef9f9"));
                }
                else
                {
                    // Set the background color for alternate row/item
                    view.setBackgroundColor(Color.parseColor("#bde9e7"));
                }
                return view;
            }
        };

        // Get ListView object from xml
        ListView listView = (ListView) findViewById(R.id.listViewLocations);
        listView.setVisibility(View.VISIBLE);

        // Assign adapter to ListView
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i("LIST", "CLICK");
                //Capturando o objeto associado ao item da lista
                String objetoExemplo = (String) adapterView.getAdapter().getItem(position);
                int intExemplo = -1;
                for(LocationDTO r : locals){
                    if(r.getDescription().equals(objetoExemplo)){
                        intExemplo = Integer.parseInt(r.getId().toString());
                        ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
                        local.setVisibility(View.VISIBLE);
                        TextView location = (TextView) findViewById(R.id.textViewLocationStatus);
                        location.setText("Atualizando dados");
                        refreshData(intExemplo);
                        return;
                    }
                }
            }
        });
    }

    private void loadLocations() {
        //inicia o location manager
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        boolean GPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!GPSEnabled){
            TextView location = (TextView) findViewById(R.id.textViewLocationStatus);
            location.setText("Não foi possível obter sua localização através do GPS. Por favor selecione um dos locais abaixo:");
            //mostra todos locais para selecionar
            locationSelect();
        }else{
            if(location == null){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                TextView location = (TextView) findViewById(R.id.textViewLocationStatus);
                ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
                local.setVisibility(View.VISIBLE);
                ListView listView = (ListView) findViewById(R.id.listViewLocations);
                listView.setVisibility(View.INVISIBLE);
                location.setText("Buscando localização");
            }
            else{
                ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
                local.setVisibility(View.INVISIBLE);
                ListView listView = (ListView) findViewById(R.id.listViewLocations);
                listView.setVisibility(View.INVISIBLE);
                TextView location = (TextView) findViewById(R.id.textViewLocationStatus);
                location.setText("Localização obitida");

                locationFound();
            }
        }
    }

    public double getLatitude(){
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

    public double getLongitude(){
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

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location locat) {
            // Called when a new location is found by the network location provider.
            Log.i("GPS", "found");
            location = locat;
            lm.removeUpdates(locationListener);

            //mostra o local obtido
            ProgressBar local = (ProgressBar) findViewById(R.id.progressBarLocalSearch);
            local.setVisibility(View.INVISIBLE);
            TextView location = (TextView) findViewById(R.id.textViewLocationStatus);
            location.setText("Localização obitida");

            locationFound();

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };
}
