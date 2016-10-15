package guilherme.krzisch.com.mybeaconclient.view.free_navigation;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
    BeaconDTO lastBeacon = null;
    Timer timer = new Timer();

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
        MyApp.getAppTTS().addQueue("Assim que for identificado um beacon você será notificado.");

        Button buttonSearchAgain = (Button) FreeNavSearchActivityView.findViewById(R.id.buttonSearchFreeNav);
        buttonSearchAgain.setEnabled(false);
        buttonSearchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do your logic for long click and remember to return it
                StartFreeNavigation();
            }});

        //inicia thread
        StartFreeNavigation();

    }

    final Handler myHandler = new Handler();

    private void StartFreeNavigation() {

        Button buttonSearchAgain = (Button) FreeNavSearchActivityView.findViewById(R.id.buttonSearchFreeNav);
        buttonSearchAgain.setEnabled(false);

        TextView textViewAction = (TextView) this.findViewById(R.id.textViewAction);
        TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);
        ProgressBar loadingImage = (ProgressBar) this.findViewById(R.id.progressBarLoading);
        MyApp.getAppTTS().initQueue("Buscando..");
        textViewAction.setText("Buscando..");
        loadingImage.setVisibility(ImageView.VISIBLE);
        textViewDesc.setText("");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                List<BeaconDTO> lst = MyApp.getBeaconMapping().getBeacons();

                for(BeaconDTO b : lst){
                    if(lastBeacon == null || lastBeacon.getId() != b.getId()) {
                        BeaconObject bObj = MyBeaconManager.getInstance().getBeaconObject(String.valueOf(b.getId()));
                        //TODO HERE verificar se é um ponto final pelo tipo do beacon para não pegar os beacons auxilixares
                        //essa porra tem que ser tipada
                        if (bObj.getLastDistanceRegistered() < 2){// && b.getType().getDescription().equals("OBJECT_TYPE")) {
                            lastBeacon = b;
                            Log.i("FOUND", bObj.getRemoteId() + " Distance: " + bObj.getLastDistanceRegistered());
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

            MyApp.getAppTTS().addQueue("Encontrado");

            TextView textViewAction = (TextView) FreeNavSearchActivityView.findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) FreeNavSearchActivityView.findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) FreeNavSearchActivityView.findViewById(R.id.progressBarLoading);
            Button buttonSearchAgain = (Button) FreeNavSearchActivityView.findViewById(R.id.buttonSearchFreeNav);

            loadingImage.setVisibility(ImageView.INVISIBLE);
            //MyApp.getAppTTS().initQueue("Você está próximo a um beacon");
            MyApp.getAppTTS().addQueue(lastBeacon.getDescription());
            textViewAction.setText("");
            textViewDesc.setText(lastBeacon.getDescription());

            buttonSearchAgain.setEnabled(true);
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
        //MyBeaconFacade.stopMyBeaconsManagerOperation();
        timer.cancel();
        MyApp.getAppTTS().initQueue("");
    }
}
