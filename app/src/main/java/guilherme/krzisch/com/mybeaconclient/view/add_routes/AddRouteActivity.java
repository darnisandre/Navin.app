package guilherme.krzisch.com.mybeaconclient.view.add_routes;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.view.MainPageActivity;
import guilherme.krzisch.com.mybeaconclient.view.MainTabActivity;
import guilherme.krzisch.com.mybeaconclient.view.free_navigation.FreeNavSearchActivity;
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

        Button saveRoute = (Button) findViewById(R.id.buttonSave);

        //onclick do botão
        saveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewRoute();
            }
        });

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listViewCategories);

        categories = MyApp.getCategories();

        List<String> values = new ArrayList<String>();
        for(CategoryDTO b : categories){
            if(!(b.getId() == 1))
                values.add(b.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, values){
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

    private void saveNewRoute() {
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listViewCategories);
        EditText editName = (EditText) findViewById(R.id.editTextName);
        EditText editDesc = (EditText) findViewById(R.id.editTextDesc);

        int len = listView.getCount();
        SparseBooleanArray checked = listView.getCheckedItemPositions();

        //Cria o objeto para a nova rota
        RouteDTO newRoute = new RouteDTO();

        //verifica as categorias selecionadas na view
        List<CategoryDTO> categoryLstSelected = new ArrayList<CategoryDTO>();
        for (int i = 0; i < len; i++) {
            if (checked.get(i)) {
                CategoryDTO item = categories.get(i);
                categoryLstSelected.add(item);
                /* do whatever you want with the checked item */
                Log.i("Item", "" + item.getId());

            }
        }

        if(categoryLstSelected.size() > 0) {
            //pega os beacons referentes as categorias selecionas na view
            List<BeaconDTO> beaconsLstSelected = new ArrayList<BeaconDTO>();
            for (CategoryDTO c : categoryLstSelected) {
                for (BeaconDTO b : c.getBeacons()) {
                    beaconsLstSelected.add(b);
                }
            }

            //adiciona os beacons que fazem parte da rota
            newRoute.setBeacons(beaconsLstSelected);


            List<RouteDTO> routeLst = MyApp.getRoutes();
            long routeId = 0;

            //Salva na lista de rotas
            ArrayList<RouteDTO> newValues = new ArrayList<RouteDTO>();
            for (RouteDTO r : routeLst) {
                //newValues.add(r);
                //pega o maior id de rota
                if(r.getId() > routeId) routeId = r.getId();
            }

            List<RouteDTO> routeLstPersonalized = MyApp.getRoutesPersonalized();
            for (RouteDTO r : routeLstPersonalized) {
                newValues.add(r);
                if(r.getId() > routeId) routeId = r.getId();
            }

            routeId++;

            newRoute.setId(routeId);

            newRoute.setName(editName.getText().toString().equals("") ? "Rota personalizada " + routeId : editName.getText().toString());
            newRoute.setDescription(editDesc.getText().toString().equals("") ? "Rota criada pelo usuário" : editDesc.getText().toString());

            newValues.add(newRoute);
            MyApp.setRoutesPersonalized(newValues);

            //TODO HERE ver como salvar as rotaas personalizadas no cache
            MyApp.getInternalCache().setRoutesPersonalized(Integer.parseInt(MyApp.getLocation().getId().toString()), newValues);
            Toast.makeText(getBaseContext(), "Rota adicionada com sucesso!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getBaseContext(), MainPageActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getBaseContext(), "É necessário selecionar pelo menos uma categoria!", Toast.LENGTH_LONG).show();
        }


    }

}
