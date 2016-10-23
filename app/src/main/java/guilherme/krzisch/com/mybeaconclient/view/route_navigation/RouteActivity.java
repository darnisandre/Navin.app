package guilherme.krzisch.com.mybeaconclient.view.route_navigation;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private static final String TAG = "CompassActivity";

    private Compass compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        compass = new Compass(this);
        compass.arrowView = (ImageView) findViewById(R.id.imageViewPonteiro);

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button rootView = (Button) findViewById(R.id.buttonContinueNav);
        rootView.setVisibility(View.INVISIBLE);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do your logic for long click and remember to return it
                Button rootView = (Button) findViewById(R.id.buttonContinueNav);
                rootView.setVisibility(View.INVISIBLE);
                MyApp.getAppTTS().initQueue("Buscando..");

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

    final Handler myHandler = new Handler();

    private void getProximityBeacon() {

        Button rootView = (Button) findViewById(R.id.buttonContinueNav);
        rootView.setVisibility(View.INVISIBLE);

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

            //TODO HERE textview informando pra virar para direção x
            TextView textViewAction = (TextView) this.findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);
            textViewAction.setText("Vire lentamente para a direita até o celular vibrar");
            textViewDesc.setText("" + relation.getDegree());

            //TODO HERE só passar dessa parte quando estiver na direção correta
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    int roundedCompass = (int) compass.getAzimuth();
                    int roundeRelation =  relation.getDegree() == 360 ? 0 : (int) relation.getDegree();

                    if(roundedCompass == roundeRelation){
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

            //TODO HERE vibrar o celular

            BeaconNode next = rotaCalculada.get(0);
            final BeaconRelation relation = tree.getRelation(lastBeacon.getId(), next.getBeacon().getId());

            TextView textViewAction = (TextView) findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) findViewById(R.id.textViewDesc);
            textViewAction.setText("Ande em frente por " + relation.getDistance() + " metros");
            textViewDesc.setText("");
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

            TextView textViewAction = (TextView) findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) findViewById(R.id.progressBarLoading);

            loadingImage.setVisibility(ImageView.INVISIBLE);

            //verificar se acabou a rota
            if (rotaCalculada.size() == 0) {
                textViewAction.setText("Você chegou ao seu destino final\nPonto: " + lastBeacon.getId());
                textViewDesc.setText(lastBeacon.getDescription());
            }
            else
            {
                textViewAction.setText("Você chegou ao ponto: " + lastBeacon.getId());

                String withHeuristic = "Rota: \n";
                for (BeaconNode bn : rotaCalculada) {
                    withHeuristic += bn.getBeacon().getId() + ", ";
                }

                //verificar se é um ponto final pelo tipo do beacon
                if(lastBeacon.getType().equals("OBJECT_BEACON_TYPE")){
                    textViewAction.setText("Você chegou a um de seus destinos\nPonto: " + lastBeacon.getId());
                    textViewDesc.setText(lastBeacon.getDescription() + " Próximo ponto: " + rotaCalculada.get(0).getBeacon().getId());
                }
                else{
                    textViewDesc.setText(withHeuristic + " Próximo ponto: " + rotaCalculada.get(0).getBeacon().getId());
                }

                MyApp.getAppTTS().addQueue("" + textViewAction.getText());
                MyApp.getAppTTS().addQueue("" + textViewDesc.getText());

                Button rootView = (Button) findViewById(R.id.buttonContinueNav);
                rootView.setVisibility(View.VISIBLE);
            }
        }
    };

    final Runnable recalculateRoute = new Runnable() {
        public void run() {

            TextView textViewAction = (TextView) findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) findViewById(R.id.progressBarLoading);

            loadingImage.setVisibility(ImageView.INVISIBLE);

            if (rotaCalculada.size() == 0) {
                textViewAction.setText("Você já passou por todos pontos de sua rota! Volte à página inicial para ver outras opções.");
                //textViewDesc.setText(lastBeacon.getDescription());
            }
            else {

                textViewAction.setText("Você chegou ao ponto " + lastBeacon.getId() + " que não é o próximo destino da sua rota. Estamos recalculando sua rota.");

                String withHeuristic = "Nova Rota: \n";
                for (BeaconNode bn : rotaCalculada) {
                    withHeuristic += bn.getBeacon().getId() + ", ";
                }

                textViewDesc.setText(withHeuristic);
                MyApp.getAppTTS().addQueue("" + textViewAction.getText());
                MyApp.getAppTTS().addQueue("" + textViewDesc.getText());

                Button rootView = (Button) findViewById(R.id.buttonContinueNav);
                rootView.setVisibility(View.VISIBLE);
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
