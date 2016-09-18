package guilherme.krzisch.com.mybeaconclient.view.add_routes;

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
import navin.dto.BeaconDTO;

public class AddRouteActivity extends AppCompatActivity {

    ListView listView;
    List<BeaconDTO> beacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listViewCategories);

        beacons = MyApp.getBeaconMapping().getBeacons();

        List<String> values = new ArrayList<String>();
        for(BeaconDTO b : beacons){
            values.add(b.getDescription());
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

        for (int i = 0; i < len; i++)
            if (checked.get(i)) {
                BeaconDTO item = beacons.get(i);
                /* do whatever you want with the checked item */
                Log.i("Item", "" + item.getId());
            }

    }

}
