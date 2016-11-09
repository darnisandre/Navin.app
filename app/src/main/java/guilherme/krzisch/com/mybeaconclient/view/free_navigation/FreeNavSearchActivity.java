package guilherme.krzisch.com.mybeaconclient.view.free_navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
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
import guilherme.krzisch.com.mybeaconclient.view.MainPageActivity;
import guilherme.krzisch.com.mybeaconclient.view.util.TTSManager;
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;

public class FreeNavSearchActivity extends AppCompatActivity {

    @InjectView(R.id.FreeNavSearchActivityView) View FreeNavSearchActivityView;
    ArrayList<BeaconObject> ar = new ArrayList<BeaconObject>();
    BeaconDTO lastBeacon = null;
    Timer timer = new Timer();
    public static Context baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_nav_search);
        ButterKnife.inject(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        baseContext = getBaseContext();

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button buttonSearchAgain = (Button) FreeNavSearchActivityView.findViewById(R.id.buttonSearchFreeNav);
        buttonSearchAgain.setEnabled(false);
        buttonSearchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do your logic for long click and remember to return it
                MyApp.getAppTTS().initQueue("Buscando..");
                StartFreeNavigation();
            }});

        //inicia thread
        MyApp.getAppTTS().initQueue("Por favor, ande pelo local.");
        MyApp.getAppTTS().addQueue("Assim que for identificado um beacon você será notificado.");
        StartFreeNavigation();

    }

    final Handler myHandler = new Handler();

    private void StartFreeNavigation() {

        Button buttonSearchAgain = (Button) FreeNavSearchActivityView.findViewById(R.id.buttonSearchFreeNav);
        buttonSearchAgain.setEnabled(false);

        TextView textViewAction = (TextView) this.findViewById(R.id.textViewAction);
        TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);
        ProgressBar loadingImage = (ProgressBar) this.findViewById(R.id.progressBarLoading);
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
                        if (bObj.getLastDistanceRegistered() < 2 && b.getType().getDescription().equals("OBJECT_BEACON_TYPE")) {
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
        },0,100);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {

            Vibrator v = (Vibrator) baseContext.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 100 milliseconds
            v.vibrate(100);

            MyApp.getAppTTS().addQueue("Encontrado");

            TextView textViewAction = (TextView) FreeNavSearchActivityView.findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) FreeNavSearchActivityView.findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) FreeNavSearchActivityView.findViewById(R.id.progressBarLoading);
            Button buttonSearchAgain = (Button) FreeNavSearchActivityView.findViewById(R.id.buttonSearchFreeNav);

            loadingImage.setVisibility(ImageView.INVISIBLE);
            //MyApp.getAppTTS().initQueue("Você está próximo a um beacon");
            MyApp.getAppTTS().addQueue(lastBeacon.getDescription());
            MyApp.getAppTTS().addQueue("Clique em buscar novamente para continuar a navegação, ou volte à página inicial.");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_home:
                goToMmain();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToMmain() {
        Intent intent = new Intent(getBaseContext(), MainPageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //MyBeaconFacade.stopMyBeaconsManagerOperation();
        timer.cancel();
        MyApp.getAppTTS().initQueue("");
    }
}
