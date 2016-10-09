package guilherme.krzisch.com.mybeaconclient.view.route_navigation;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;
import navin.dto.BeaconTypeDTO;
import navin.dto.RouteDTO;
import navin.tree.BeaconNode;
import navin.tree.BeaconTree;

public class RouteActivity extends AppCompatActivity {

    BeaconDTO lastBeacon = null;
    ArrayList<BeaconObject> ar = new ArrayList<BeaconObject>();
    List<BeaconNode> rotaCalculada = new ArrayList<BeaconNode>();
    BeaconTree tree = null;
    List<BeaconDTO> bLst = new ArrayList<BeaconDTO>();
    List<Long> idLst = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

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

                //TODO HERE pegar o id do beacon mais proximo pra começar a rota
                //TODO HERE montar a rota a partir desse id
                //TODO HERE iniciar navegação
                /*ar = new ArrayList<BeaconObject>();

                //adiciona todos eles no framework
                List<BeaconDTO> bLst2 = mapping.getBeacons();
                for (BeaconDTO b1: bLst2) {
                    BeaconObject a = new BeaconObject(String.valueOf(b1.getId()), b1.getUuid(),
                            Integer.valueOf(b1.getMajor().toString()), Integer.valueOf(b1.getMinor().toString()), 0, "", 0,0);
                    ar.add(a);
                }
                MyBeaconFacade.addBeaconsLocally(ar);

                //inicia o monitoramento
                MyBeaconFacade.startMyBeaconsManagerOperation();*/

                getProximityBeacon();


                //Isso é só pra começar do beacon inicial enquanto não pegamos o que ta mais perto dele
                /*Long minorId = Long.MAX_VALUE;
                List<BeaconDTO> auxLst = mapping.getBeacons();
                for (BeaconDTO bDto : auxLst) {
                    if (bDto.getId() < minorId) minorId = bDto.getId();
                }*/

                /*List<BeaconNode> withoutHeur = tree.getRoute(idLst, minorId);
                List<BeaconNode> withHeur = tree.getRouteTspHeuristic(idLst, minorId);

                String withoutHeuristic = "Caminho sem heurísitica: \n";
                for (BeaconNode bn : withoutHeur) {
                    withoutHeuristic += bn.getBeacon().getId() + " > ";
                }

                String withHeuristic = "Caminho com heurísitica: \n";
                for (BeaconNode bn : withHeur) {
                    withHeuristic += bn.getBeacon().getId() + " > ";
                }

                TextView textViewFrstPath = (TextView) this.findViewById(R.id.textViewFrstPath);
                TextView textViewScndPath = (TextView) this.findViewById(R.id.textViewScndPath);

                textViewFrstPath.setText(withoutHeuristic);
                textViewScndPath.setText(withHeuristic);*/
            }

        }
    }

    final Handler myHandler = new Handler();

    private void getProximityBeacon() {

        View rootView = this.findViewById(R.id.RouteActivityView);
        rootView.setOnClickListener(null);

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
                            Log.i("ROTA","FIRST");
                            lastBeacon = b;
                            rotaCalculada = tree.getRoute(idLst, b.getId());
                            rotaCalculada.remove(0);
                            rotaCalculada.remove(0);
                            Log.i("FOUND", bObj.getRemoteId());
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

    private void getNextBeacon() {

        View rootView = this.findViewById(R.id.RouteActivityView);
        rootView.setOnClickListener(null);

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
                            BeaconNode b2 = rotaCalculada.get(0);
                            Log.i("UUDI",b2.getBeacon().getId().toString());
                            Log.i("UUDI",b.getId().toString());
                            if(b2.getBeacon().getId() == b.getId()){
                                //estou no caminho certo
                                if(rotaCalculada.size() > 0) rotaCalculada.remove(0);
                                lastBeacon = b;
                                Log.i("ROTA","CERTO");
                                myHandler.post(routeOk);
                                //getNextBeacon();
                            }
                            else{
                                //recalcular a rota
                                lastBeacon = b;
                                Log.i("ROTA","RECALCULADO");
                                rotaCalculada = tree.getRoute(idLst, b.getId());
                                if(rotaCalculada.size() > 0) rotaCalculada.remove(0);
                                myHandler.post(recalculateRoute);
                            }

                            //Log.i("FOUND", bObj.getRemoteId());
                            //StopFreeNavigation();
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

            //TODO HERE verificar se acabou a rota
            if (rotaCalculada.size() == 0) {
                textViewAction.setText("Você ao seu destino final, ponto: " + lastBeacon.getId());
                textViewDesc.setText(lastBeacon.getDescription());
            }
            else
            {
                textViewAction.setText("Você chegou ao ponto: " + lastBeacon.getId());

                String withHeuristic = "Rota: \n";
                for (BeaconNode bn : rotaCalculada) {
                    withHeuristic += bn.getBeacon().getId() + " > ";
                }

                //TODO HERE verificar se é um ponto final pelo tipo do beacon
                //se sim
                //textViewDesc.setText(rotaCalculada.get(0).getBeacon().getDescription());
                //senão
                //textViewDesc.setText(withHeuristic + " Próximo ponto: " + rotaCalculada.get(0).getBeacon().getId());

                textViewDesc.setText(withHeuristic + " Próximo ponto: " + rotaCalculada.get(0).getBeacon().getId());

                MyApp.getAppTTS().addQueue("" + textViewAction.getText());
                MyApp.getAppTTS().addQueue("" + textViewDesc.getText());

                View rootView = findViewById(R.id.RouteActivityView);
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do your logic for long click and remember to return it
                        MyApp.getAppTTS().initQueue("Buscando..");

                        if(rotaCalculada.size() > 0) getNextBeacon();
                    }
                });
            }
        }
    };

    final Runnable recalculateRoute = new Runnable() {
        public void run() {

            TextView textViewAction = (TextView) findViewById(R.id.textViewAction);
            TextView textViewDesc = (TextView) findViewById(R.id.textViewDesc);
            ProgressBar loadingImage = (ProgressBar) findViewById(R.id.progressBarLoading);

            loadingImage.setVisibility(ImageView.INVISIBLE);
            textViewAction.setText("Você chegou ao ponto " + lastBeacon.getId() + " que não está na sua rota. Estamos recalculando sua rota.");

            String withHeuristic = "Nova Rota: \n";
            for (BeaconNode bn : rotaCalculada) {
                withHeuristic += bn.getBeacon().getId() + " > ";
            }

            textViewDesc.setText(withHeuristic);
            MyApp.getAppTTS().addQueue("" + textViewAction.getText());
            MyApp.getAppTTS().addQueue("" + textViewDesc.getText());

            View rootView = findViewById(R.id.RouteActivityView);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // do your logic for long click and remember to return it
                    MyApp.getAppTTS().initQueue("Buscando..");

                    if(rotaCalculada.size() > 0) getNextBeacon();
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
        //MyBeaconFacade.stopMyBeaconsManagerOperation();
        MyApp.getAppTTS().initQueue("");
    }
}
