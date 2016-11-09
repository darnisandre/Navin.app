package guilherme.krzisch.com.mybeaconclient.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.view.add_routes.AddRouteActivity;
import guilherme.krzisch.com.mybeaconclient.view.free_navigation.FreeNavSearchActivity;
import guilherme.krzisch.com.mybeaconclient.view.route_navigation.RouteLstActivity;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.AboutActivity;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.TutorialActivity;
import navin.dto.RouteDTO;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(false);

        Button freeNav = (Button) findViewById(R.id.buttonFreeNav);
        Button routeNav = (Button) findViewById(R.id.buttonRouteNav);

        //onclick do botão
        freeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), FreeNavSearchActivity.class);
                startActivity(intent);
            }
        });

        //onclick do botão
        routeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RouteLstActivity.class);
                startActivity(intent);
            }
        });

        TextView appDesc = (TextView) findViewById(R.id.textViewAppDesc);
        appDesc.setText("Você está no " + MyApp.getLocation().getDescription() + ". Aqui é possível " +
                "iniciar uma navegação sem rota ou com rota. Na navegação sem rota você receberá informações assim que " +
                "se aproximar de um Beacon. Na navegação com rota você poderá selecionar uma rota e será guiado por este local.");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_about:
                goToAboutActivity();
                return true;
            case R.id.action_addroute:
                goToAddRouteActivity();
                return true;
            case R.id.action_tuto:
                goToTutorialActivity();
                return true;
            case R.id.action_clear:
                showConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }


    private void showConfirmationDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Tem certeza que deseja remover as rotas personalizadas?")
                .setCancelable(false)
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                clearRoutes();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Não",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void clearRoutes(){
        Toast.makeText(getBaseContext(), "Rotas personalizadas removidas!", Toast.LENGTH_LONG).show();
        MyApp.getInternalCache().setRoutesPersonalized(Integer.parseInt(MyApp.getLocation().getId().toString()), new ArrayList<RouteDTO>());
        MyApp.setRoutesPersonalized(new ArrayList<RouteDTO>());
        refresh();
    }

    private void goToTutorialActivity(){
        Intent intent = new Intent(getBaseContext(), TutorialActivity.class);
        startActivity(intent);
    }

    private void goToAboutActivity(){
        Intent intent = new Intent(getBaseContext(), AboutActivity.class);
        startActivity(intent);
    }

    private void goToAddRouteActivity(){
        Intent intent = new Intent(getBaseContext(), AddRouteActivity.class);
        startActivity(intent);
    }
}
