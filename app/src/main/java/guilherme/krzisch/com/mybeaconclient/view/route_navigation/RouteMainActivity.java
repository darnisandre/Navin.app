package guilherme.krzisch.com.mybeaconclient.view.route_navigation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.view.MainPageActivity;
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

                Button buttonInit = (Button) this.findViewById(R.id.buttonNavInit);
                baseCon = buttonInit.getContext();
                buttonInit.setOnClickListener(new View.OnClickListener() {
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
}
