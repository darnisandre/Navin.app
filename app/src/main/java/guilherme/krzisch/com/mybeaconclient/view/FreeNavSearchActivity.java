package guilherme.krzisch.com.mybeaconclient.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.BeaconObject;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconManager;
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;

public class FreeNavSearchActivity extends AppCompatActivity {

    ArrayList<BeaconObject> ar = new ArrayList<BeaconObject>();
    Thread t = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_nav_search);
        ButterKnife.inject(this);

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView textView = (TextView) this.findViewById(R.id.textViewDistance);
        textView.setText("Distância: ");

        //busca todos beacons da configuração ativa
        BeaconMappingDTO mapping = MyApp.getBeaconMapping();
        List<BeaconDTO> bLst = mapping.getBeacons();

        ar = new ArrayList<BeaconObject>();

        //adiciona todos eles no framework
        for (BeaconDTO b: bLst) {
            BeaconObject a = new BeaconObject(String.valueOf(b.getId()), b.getUuid(),
              Integer.valueOf(b.getMajor().toString()), Integer.valueOf(b.getMinor().toString()), 0, "", 0,0);
            ar.add(a);
        }
        MyBeaconFacade.addBeaconsLocally(ar);

        //inicia o monitoramento
        MyBeaconFacade.startMyBeaconsManagerOperation();

        //inicia thread
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(20);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDistance();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_free_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        switch (item.getItemId()){
//            case R.id.action_about:
//                goToAboutActivity();
//                return true;
//            case R.id.action_refresh:
//                refresh();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.interrupt();
        MyBeaconFacade.stopMyBeaconsManagerOperation();
    }

    public void updateDistance(){
        TextView textViewName = (TextView) this.findViewById(R.id.textViewBeaconName);
        TextView textViewDesc = (TextView) this.findViewById(R.id.textViewBeaconDesc);
        TextView textViewDist = (TextView) this.findViewById(R.id.textViewDistance);

        List<BeaconDTO> lst = MyApp.getBeaconMapping().getBeacons();

        for(BeaconDTO b : lst){
            BeaconObject bObj = MyBeaconManager.getInstance().getBeaconObject(String.valueOf(b.getId()));
            if(bObj.getLastDistanceRegistered() < 1.2) {
                textViewName.setText("UUID: " + b.getUuid());
                textViewDesc.setText("Descrição: " + b.getDescription());
                textViewDist.setText("Distância: " + bObj.getLastDistanceRegistered());
            }
        }
    }
}
