package guilherme.krzisch.com.mybeaconclient.view.add_routes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.view.MainTabActivity;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.AboutActivity;
import navin.dto.BeaconDTO;
import navin.dto.CategoryDTO;
import navin.dto.RouteDTO;

public class AddRouteActivity extends AppCompatActivity {

    ListView listView;
    List<CategoryDTO> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listViewCategories);

        categories = MyApp.getCategories();

        List<String> values = new ArrayList<String>();
        for(CategoryDTO b : categories){
            values.add(b.getName() + " - " + b.getDescription());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_ok:
                saveNewRoute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewRoute() {
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listViewCategories);

        int len = listView.getCount();
        SparseBooleanArray checked = listView.getCheckedItemPositions();

        //Cria o objeto para a nova rota
        RouteDTO newRoute = new RouteDTO();
        newRoute.setId((long)99);
        newRoute.setName("Rota personalizada");
        newRoute.setDescription("Rota criada pelo usuário");

        //verifica as categorias selecionadas na view
        List<CategoryDTO> categoryLstSelected = new ArrayList<CategoryDTO>();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                CategoryDTO item = categories.get(i);
                categoryLstSelected.add(item);
                //TODO criar rota com as categorias selecionadas
                /* do whatever you want with the checked item */
                Log.i("Item", "" + item.getId());

            }
        }

        //pega os beacons referentes as categorias selecionas na view
        List<BeaconDTO> beaconsLstSelected = new ArrayList<BeaconDTO>();
        for(CategoryDTO c : categoryLstSelected){
            for(BeaconDTO b : c.getBeacons()) {
                beaconsLstSelected.add(b);
            }
        }

        //adiciona os beacons que fazem parte da rota
        newRoute.setBeacons(beaconsLstSelected);

        //Salva na lista de rotas
        List<RouteDTO> routeLst = MyApp.getRoutes();
        ArrayList<RouteDTO> newValues = new ArrayList<RouteDTO>();
        for(RouteDTO r : routeLst){
            newValues.add(r);
        }
        newValues.add(newRoute);
        MyApp.setRoutes(newValues);

        Intent intent = new Intent(getBaseContext(), MainTabActivity.class);
        startActivity(intent);
        finish();
    }

}
