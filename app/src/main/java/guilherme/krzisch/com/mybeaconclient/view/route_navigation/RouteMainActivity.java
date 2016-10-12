package guilherme.krzisch.com.mybeaconclient.view.route_navigation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import navin.dto.BeaconDTO;
import navin.dto.BeaconMappingDTO;
import navin.dto.RouteDTO;
import navin.tree.BeaconNode;
import navin.tree.BeaconTree;

public class RouteMainActivity extends AppCompatActivity {

    RouteDTO route = null;
    Context baseCon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_main);

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


            for(RouteDTO r : routeLst){
                if(r.getId() == value){
                    route = r;
                    break;
                }
            }


            if(route != null) {

                TextView textViewDesc = (TextView) this.findViewById(R.id.textViewDesc);
                TextView textTitle = (TextView) this.findViewById(R.id.textViewTitle);
                textTitle.setText(route.getName());
                textViewDesc.setText(route.getDescription());

                View rootView = this.findViewById(R.id.RouteMainActivityView);
                baseCon = rootView.getContext();
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // do your logic for long click and remember to return it
                        Intent intent = new Intent(baseCon, RouteActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("id", Integer.parseInt(route.getId().toString())); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivity(intent);
                    }});


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