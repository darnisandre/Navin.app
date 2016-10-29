package guilherme.krzisch.com.mybeaconclient.view.route_navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.BeaconObject;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconManager;
import guilherme.krzisch.com.mybeaconclient.view.MainPageActivity;
import guilherme.krzisch.com.mybeaconclient.view.util.Compass;
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;
import navin.dto.BeaconTypeDTO;
import navin.dto.RouteDTO;
import navin.tree.BeaconNode;
import navin.tree.BeaconRelation;
import navin.tree.BeaconTree;

public class RouteActivity extends AppCompatActivity {

    BeaconDTO lastBeacon = null;
    ArrayList<BeaconObject> ar = new ArrayList<BeaconObject>();
    List<BeaconNode> rotaCalculada = new ArrayList<BeaconNode>();
    BeaconTree tree = null;
    List<BeaconDTO> bLst = new ArrayList<BeaconDTO>();
    List<Long> idLst = new ArrayList<Long>();
    Timer timer= new Timer();
    public static Context baseContext;
    private static final String TAG = "CompassActivity";
    private Compass compass;
    final Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        ImageView pointer = (ImageView) findViewById(R.id.imageViewPonteiro);
        pointer.setVisibility(View.INVISIBLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        baseContext = getBaseContext();

        compass = new Compass(this);
        compass.arrowView = (ImageView) findViewById(R.id.imageViewPonteiro);

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button rootView = (Button) findViewById(R.id.buttonContinueNav);
        rootView.setEnabled(false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do your logic for long click and remember to return it
                Button rootView = (Button) findViewById(R.id.buttonContinueNav);
                rootView.setEnabled(false);

                ImageView pointer = (ImageView) findViewById(R.id.imageViewPonteiro);
                pointer.setVisibility(View.VISIBLE);

                if (rotaCalculada.size() > 0) getDirection();
            }
        });

        Bundle b = getIntent().getExtras();
        int value = -1; // or other values
        if(b != null) {
            value = b.getInt("id");
        }

        if(value != -1){
            List<RouteDTO> routeLst = MyApp.getRoutes();
            RouteDTO route = null;

            for(RouteDTO r : routeLst){
                if(r.getId() == value){
                    route = r;
                    break;
                }
            }
            if(route == null){
                List<RouteDTO> routeLstPersonalized = MyApp.getRoutesPersonalized();
                for(RouteDTO r : routeLstPersonalized){
                    if(r.getId() == value){
                        route = r;
                        break;
                    }
                }
            }

            if(route != null) {
                bLst = route.getBeacons();
                idLst = new ArrayList<Long>();

                for (BeaconDTO bea : bLst) {
                    idLst.add(bea.getId());
                }

                BeaconMappingDTO mapping = MyApp.getBeaconMapping();
                tree = new BeaconTree(mapping);

                TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
                textViewTitle.setText(route.getName());

                getProximityBeacon();
            }

        }
    }

    private void getProximityBeacon() {

        Button rootView = (Button) findViewById(R.id.buttonContinueNav);
        rootView.setEnabled(false);

        TextView textViewAction = (TextView) this.findViewById(R.id.textViewAction);
        TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);
        ProgressBar loadingImage = (ProgressBar) this.findViewById(R.id.progressBarLoading);
        MyApp.getAppTTS().initQueue("Estamos identificando sua posição");
        textViewAction.setText("Identificando posição..");
        loadingImage.setVisibility(ImageView.VISIBLE);
        textViewDesc.setText("");


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                List<BeaconDTO> lst = MyApp.getBeaconMapping().getBeacons();

                for(BeaconDTO b : lst){
                    if(lastBeacon == null || lastBeacon.getId() != b.getId()) {
                        BeaconObject bObj = MyBeaconManager.getInstance().getBeaconObject(String.valueOf(b.getId()));
                        Log.i("UUID","Distance: " + bObj.getLastDistanceRegistered() + " ID: " + bObj.getRemoteId());
                        if (bObj.getLastDistanceRegistered() < 2) {
                            Log.i("ROTA","inciando rota");
                            lastBeacon = b;
                            rotaCalculada = tree.getRoute(idLst, b.getId());
                            rotaCalculada.remove(0);
                            idLst.remove(b.getId());
                            Log.i("UUID","ID encontrado: " + bObj.getRemoteId() + " Distance: " + bObj.getLastDistanceRegistered());
                            myHandler.post(routeOk);
                            //StopFreeNavigation();
                            this.cancel();
                            return;
                        }
                    }
                }



            }
        },0,1000);
    }

    private void getDirection(){

        if(lastBeacon != null){
            BeaconNode next = rotaCalculada.get(0);
            final BeaconRelation relation = tree.getRelation(lastBeacon.getId(), next.getBeacon().getId());

            TextView textViewAction = (TextView) this.findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);

            float point1 = compass.getAzimuth();
            double point2 = relation.getDegree();

            textViewAction.setText("Vire lentamente para a direita até o celular vibrar");
            MyApp.getAppTTS().addQueue("Vire lentamente para a direita até o celular vibrar");


            compass.setPoint((int) relation.getDegree());

            //TODO HERE textview informando pra virar para direção x
            textViewDesc.setText("Vire até " + relation.getDegree() + " graus.");

            //TODO HERE só passar dessa parte quando estiver na direção correta
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    int roundedCompass = (int) compass.getAzimuth();
                    int roundeRelation =  relation.getDegree() == 360 ? 0 : (int) relation.getDegree();

                   /* int minRelation, maxRelation;
                    if(roundeRelation - 10 < 0){
                        minRelation = 0;
                        maxRelation = roundeRelation + 15;
                    }else if(roundeRelation + 10 >= 360){
                        minRelation = roundeRelation - 15;
                        maxRelation = 359;
                    }else{
                        minRelation = roundeRelation - 10;
                        maxRelation = roundeRelation + 10;
                    }*/

                    if(Math.abs(roundedCompass) >= 345 || Math.abs(roundedCompass) <= 15){
                        myHandler.post(directionOK);
                        this.cancel();
                        return;
                    }
                }
            },0,200);


        }

    }

    final Runnable directionOK = new Runnable() {
        public void run() {

            Vibrator v = (Vibrator) baseContext.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);

            BeaconNode next = rotaCalculada.get(0);
            final BeaconRelation relation = tree.getRelation(lastBeacon.getId(), next.getBeacon().getId());

            TextView textViewAction = (TextView) findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) findViewById(R.id.textViewDesc);
            textViewDesc.setText("Ande em frente por " + relation.getDistance() + " metros");
            MyApp.getAppTTS().initQueue("Ande em frente por " + relation.getDistance() + " metros");
            getNextBeacon();
        }

    };

    private void getNextBeacon() {

        View rootView = this.findViewById(R.id.RouteActivityView);
        rootView.setOnClickListener(null);

        TextView textViewAction = (TextView) this.findViewById(R.id.textViewAction);
        TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);
        ProgressBar loadingImage = (ProgressBar) this.findViewById(R.id.progressBarLoading);
        textViewAction.setText("Buscando..");
        loadingImage.setVisibility(ImageView.VISIBLE);


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                List<BeaconDTO> lst = MyApp.getBeaconMapping().getBeacons();

                for(BeaconDTO b : lst){
                    if(lastBeacon == null || lastBeacon.getId() != b.getId()) {
                        BeaconObject bObj = MyBeaconManager.getInstance().getBeaconObject(String.valueOf(b.getId()));
                        Log.i("UUID","Distance: " + bObj.getLastDistanceRegistered() + " ID: " + bObj.getRemoteId());
                        if (bObj.getLastDistanceRegistered() < 2) {
                            BeaconNode b2 = rotaCalculada.get(0);
                            Log.i("UUID","ID esperado: " + b2.getBeacon().getId().toString());
                            Log.i("UUID","ID encontrado: " + bObj.getRemoteId() + " Distance: " + bObj.getLastDistanceRegistered());
                            if(b2.getBeacon().getId() == b.getId()){
                                //estou no caminho certo
                                lastBeacon = b;
                                Log.i("ROTA","continuando rota");
                                if(rotaCalculada.size() > 0) rotaCalculada.remove(0);
                                //remove o id para não passar de novo nesse ponto (como destino, auxiliar ainda pode passar)
                                if(idLst.size() > 0) idLst.remove(b.getId());
                                myHandler.post(routeOk);
                                //getNextBeacon();
                            }
                            else{
                                //TODO HERE verificar se o ponto é um OBJECT e está na rota (mesmo não sendo o próximo)
                                //então mostrar informações e remover ponto da lista.
                                //estou no caminho errado mas ~~certo~~
                                if(b.getType().equals("OBJECT_BEACON_TYPE")) {
                                    lastBeacon = b;
                                    Log.i("ROTA", "continuando rota");
                                    rotaCalculada = tree.getRoute(idLst, b.getId());
                                    if (rotaCalculada.size() > 0) rotaCalculada.remove(0);
                                    if (idLst.size() > 0) idLst.remove(b.getId());
                                    myHandler.post(routeOk);
                                }else {
                                    //senão
                                    //recalcular a rota
                                    lastBeacon = b;
                                    Log.i("ROTA", "recalculando rota");
                                    rotaCalculada = tree.getRoute(idLst, b.getId());
                                    if (rotaCalculada.size() > 0) rotaCalculada.remove(0);
                                    if (idLst.size() > 0) idLst.remove(b.getId());
                                    myHandler.post(recalculateRoute);
                                }
                            }
                            this.cancel();
                            return;
                        }
                    }
                }



            }
        },0,1000);
    }

    final Runnable routeOk = new Runnable() {
        public void run() {

            compass.setPoint((int) compass.getAzimuth() + 90);
            ImageView pointer = (ImageView) findViewById(R.id.imageViewPonteiro);
            pointer.setVisibility(View.INVISIBLE);

            Vibrator v = (Vibrator) baseContext.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(100);

            TextView textViewAction = (TextView) findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) findViewById(R.id.progressBarLoading);

            loadingImage.setVisibility(ImageView.INVISIBLE);

            //verificar se acabou a rota
            if (rotaCalculada.size() == 0) {
                textViewAction.setText("Você chegou ao seu destino final");
                textViewDesc.setText(lastBeacon.getDescription());

                MyApp.getAppTTS().initQueue("" + textViewAction.getText());
                MyApp.getAppTTS().addQueue("" + textViewDesc.getText());
            }
            else
            {
                //verificar se é um ponto final pelo tipo do beacon
                if(lastBeacon.getType().equals("OBJECT_BEACON_TYPE")){
                    textViewAction.setText("Você chegou a um de seus destinos");
                    textViewDesc.setText(lastBeacon.getDescription() + "\nAinda restam destinos na sua rota, quando desejar clique em continuar navegação.");
                }
                else{
                    textViewAction.setText("Você está próximo a um Beacon");
                    textViewDesc.setText("Clique em continuar navegação para prosseguir.");
                }

                MyApp.getAppTTS().initQueue("" + textViewAction.getText());
                MyApp.getAppTTS().addQueue("" + textViewDesc.getText());

                Button rootView = (Button) findViewById(R.id.buttonContinueNav);
                rootView.setEnabled(true);
            }
        }
    };

    final Runnable recalculateRoute = new Runnable() {
        public void run() {

            compass.setPoint((int) compass.getAzimuth() + 90);
            ImageView pointer = (ImageView) findViewById(R.id.imageViewPonteiro);
            pointer.setVisibility(View.INVISIBLE);

            Vibrator v = (Vibrator) baseContext.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(100);

            TextView textViewAction = (TextView) findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) findViewById(R.id.progressBarLoading);

            loadingImage.setVisibility(ImageView.INVISIBLE);

            if (rotaCalculada.size() == 0) {
                textViewAction.setText("Você chegou ao seu destino final");
                textViewDesc.setText(lastBeacon.getDescription());

                MyApp.getAppTTS().initQueue("" + textViewAction.getText());
                MyApp.getAppTTS().addQueue("" + textViewDesc.getText());
            }
            else {

                //verificar se é um ponto final pelo tipo do beacon
                if(lastBeacon.getType().equals("OBJECT_BEACON_TYPE")){
                    textViewAction.setText("Você chegou a um de seus destinos");
                    textViewDesc.setText(lastBeacon.getDescription()  + "\nAinda restam destinos na sua rota, quando desejar clique em continuar navegação.");
                }
                else{
                    textViewAction.setText("Você chegou a um ponto que não é o próximo destino da sua rota. Sua rota foi recalculada.");
                    textViewDesc.setText("Clique em continuar navegação para prosseguir.");
                }

                MyApp.getAppTTS().initQueue("" + textViewAction.getText());
                MyApp.getAppTTS().addQueue("" + textViewDesc.getText());

                Button rootView = (Button) findViewById(R.id.buttonContinueNav);
                rootView.setEnabled(true);
            }
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
        timer.cancel();
        //MyBeaconFacade.stopMyBeaconsManagerOperation();
        MyApp.getAppTTS().initQueue("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start compass");
        compass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        compass.stop();
    }
}
