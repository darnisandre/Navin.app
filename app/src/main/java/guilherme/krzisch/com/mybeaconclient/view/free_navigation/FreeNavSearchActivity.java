package guilherme.krzisch.com.mybeaconclient.view.free_navigation;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.BeaconObject;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconManager;
import guilherme.krzisch.com.mybeaconclient.view.util.TTSManager;
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;

public class FreeNavSearchActivity extends AppCompatActivity {

    @InjectView(R.id.FreeNavSearchActivityView) View FreeNavSearchActivityView;
    ArrayList<BeaconObject> ar = new ArrayList<BeaconObject>();
    Thread t = new Thread();
    BeaconDTO lastBeacon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_nav_search);
        ButterKnife.inject(this);

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        MyApp.getAppTTS().initQueue("Ande pelo local..");
        MyApp.getAppTTS().addQueue("Assim que for identificado um experimento você será notificado.");

        FreeNavSearchActivityView.setOnClickListener(null);

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
        StartFreeNavigation();

    }

    final Handler myHandler = new Handler();

    private void StartFreeNavigation() {

        FreeNavSearchActivityView.setOnClickListener(null);

        TextView textViewAction = (TextView) this.findViewById(R.id.textViewAction);
        TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);
        ProgressBar loadingImage = (ProgressBar) this.findViewById(R.id.progressBarLoading);
        textViewAction.setText("Buscando..");
        loadingImage.setVisibility(ImageView.VISIBLE);
        textViewDesc.setText("");



        Timer timer= new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                List<BeaconDTO> lst = MyApp.getBeaconMapping().getBeacons();

                for(BeaconDTO b : lst){
                    if(lastBeacon == null || lastBeacon.getId() != b.getId()) {
                        BeaconObject bObj = MyBeaconManager.getInstance().getBeaconObject(String.valueOf(b.getId()));
                        if (bObj.getLastDistanceRegistered() < 1.5) {
                            lastBeacon = b;
                            Log.i("FOUND", bObj.getRemoteId());
                            myHandler.post(myRunnable);
                            //StopFreeNavigation();
                            this.cancel();
                            return;
                        }
                    }
                }



            }
        },0,1000);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {

            TextView textViewAction = (TextView) FreeNavSearchActivityView.findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) FreeNavSearchActivityView.findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) FreeNavSearchActivityView.findViewById(R.id.progressBarLoading);

            loadingImage.setVisibility(ImageView.INVISIBLE);
            textViewAction.setText("Você está próximo a um beacon, a qualquer momento pressione no centro da tela para continuar a navegação.");
            textViewDesc.setText(lastBeacon.getDescription());
            MyApp.getAppTTS().addQueue("" + textViewAction.getText());
            MyApp.getAppTTS().addQueue("" + textViewDesc.getText());

            FreeNavSearchActivityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // do your logic for long click and remember to return it
                MyApp.getAppTTS().initQueue("Buscando experimentos..");

                StartFreeNavigation();
            }});
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_free_nav, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.interrupt();
        MyBeaconFacade.stopMyBeaconsManagerOperation();
        MyApp.getAppTTS().initQueue("");
    }
}
