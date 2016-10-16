package guilherme.krzisch.com.mybeaconclient.view.route_navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import navin.dto.RouteDTO;

public class RouteLstActivity extends AppCompatActivity {

    public static Context baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_lst);

        baseContext = getBaseContext();

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        View rootView4 = this.findViewById(R.id.RouteLstActivityView);

        ListView listView;
        final List<RouteDTO> routes;
        ArrayList<RouteDTO> aux = new ArrayList<RouteDTO>();
        List<RouteDTO> routesServer;
        List<RouteDTO> routesPersonalized;

        // Get ListView object from xml
        listView = (ListView) rootView4.findViewById(R.id.listViewRoutes);

        routesServer = MyApp.getRoutes();
        routesPersonalized = MyApp.getRoutesPersonalized();

        aux.addAll(routesServer);
        aux.addAll(routesPersonalized);

        routes = aux;

        List<String> values = new ArrayList<String>();
        for(RouteDTO b : routes){
            values.add(b.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView4.getContext(),
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


        // Assign adapter to ListView
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Capturando o objeto associado ao item da lista
                String objetoExemplo = (String) adapterView.getAdapter().getItem(position);
                int intExemplo = -1;
                for(RouteDTO r : routes){
                    if(r.getName().equals(objetoExemplo)){
                        intExemplo = Integer.parseInt(r.getId().toString());
                    }
                }

                boolean booleanExemplo = true;

                Intent intent = new Intent(baseContext, RouteMainActivity.class);

                //O primeiro parametro é o nome deste extra a ser capturado na sua outra Activity
                intent.putExtra("id", intExemplo);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_free_nav, menu);
        return true;
    }
}
