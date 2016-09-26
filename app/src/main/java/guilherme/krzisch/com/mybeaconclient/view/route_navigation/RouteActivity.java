package guilherme.krzisch.com.mybeaconclient.view.route_navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;
import navin.dto.RouteDTO;
import navin.tree.BeaconNode;
import navin.tree.BeaconTree;

public class RouteActivity extends AppCompatActivity {

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
                List<BeaconDTO> bLst = route.getBeacons();
                List<Long> idLst = new ArrayList<Long>();

                for (BeaconDTO bea : bLst) {
                    idLst.add(bea.getId());
                }

                BeaconMappingDTO mapping = MyApp.getBeaconMapping();
                BeaconTree tree = new BeaconTree(mapping);

                //Isso é só pra começar do beacon inicial enquanto não pegamos o que ta mais perto dele
                Long minorId = Long.MAX_VALUE;
                List<BeaconDTO> auxLst = mapping.getBeacons();
                for (BeaconDTO bDto : auxLst) {
                    if (bDto.getId() < minorId) minorId = bDto.getId();
                }

                List<BeaconNode> withoutHeur = tree.getRoute(idLst, minorId);
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
                textViewScndPath.setText(withHeuristic);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_free_nav, menu);
        return true;
    }
}
