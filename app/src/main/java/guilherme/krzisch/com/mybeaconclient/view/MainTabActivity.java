package guilherme.krzisch.com.mybeaconclient.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.mybeaconframework.BasicModule.MyBeaconFacade;
import guilherme.krzisch.com.mybeaconclient.view.add_routes.AddRouteActivity;
import guilherme.krzisch.com.mybeaconclient.view.free_navigation.FreeNavSearchActivity;
import guilherme.krzisch.com.mybeaconclient.view.route_navigation.RouteActivity;
import guilherme.krzisch.com.mybeaconclient.view.route_navigation.RouteMainActivity;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.AboutActivity;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.TutorialActivity;
import guilherme.krzisch.com.mybeaconclient.view.util.DepthPageTransformer;
import guilherme.krzisch.com.mybeaconclient.view.util.SlidingTabLayout;
import navin.dto.BeaconDTO;
import navin.dto.CategoryDTO;
import navin.dto.RouteDTO;

public class MainTabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SlidingTabLayout mSlidingTabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static Context baseContext;
    @InjectView(R.id.mainTabActivityView) View mainTabActivityView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        baseContext = getBaseContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mostra o icone na barra
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);

        //actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //habilita as abas
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        //muda efeito da transição de abas
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getAppTTS().addQueue(MyApp.getLocation().getDescription().toString() + " - " + MyApp.getLocation().getActiveConfiguration().getDescription().toString());
                Snackbar.make(view, MyApp.getLocation().getDescription().toString() + " - " + MyApp.getLocation().getActiveConfiguration().getDescription().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
                clearRoutes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    private void clearRoutes(){
        //TODO recarregar rotas do servidor
        Toast.makeText(getBaseContext(), "Rotas personalizadas removidas!", Toast.LENGTH_LONG).show();
        MyApp.getInternalCache().refreshRoutes(Integer.parseInt(MyApp.getLocation().getId().toString()));
        MyApp.setRoutes(MyApp.getInternalCache().getRoutes(Integer.parseInt(MyApp.getLocation().getId().toString())));
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
            //return  MyApp.getRoutes().size() + 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
               case 0:
                    return "Início";
               case 1:
                   return "Navegar sem rota";
               default:
                   return "Rotas";
                   //return MyApp.getRoutes().get(position-2).getName();
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        private void welcomeMessage(){

            /*if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                String myText1 = "Bem-vindo";
                MyApp.getAppTTS().initQueue(myText1);
                //adding it to queue
                String myText2 = "Para ouvir as opções disponíveis deslize a tela da direita para a esquerda.";
                MyApp.getAppTTS().addQueue(myText2);
            }else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                String myText1 = "Navegar sem rota";
                MyApp.getAppTTS().initQueue(myText1);
                //adding it to queue
                String myText2 = "Para selecionar essa opção, pressione no centro da tela.";
                MyApp.getAppTTS().addQueue(myText2);
            }else{
                String myText1 = "Esta é a rota " + getArguments().getInt(ARG_SECTION_NUMBER);
                MyApp.getAppTTS().initQueue(myText1);
                //adding it to queue
                String myText2 = "Para selecionar essa opção, pressione no centro da tela.";
                MyApp.getAppTTS().addQueue(myText2);
            }*/

        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            welcomeMessage();
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    View rootView1 = inflater.inflate(R.layout.fragment_main_tab, container, false);
                    rootView1.setOnLongClickListener(null);
                    TextView textTitle2 = (TextView) rootView1.findViewById(R.id.textViewTitle);
                    textTitle2.setText("Bem-vindo ao sistema de navegação indoor Nav.In." +
                            "Você está no " + MyApp.getLocation().getDescription().toString());
                    textTitle2.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                    return rootView1;
                case 2:
                    View rootView2 = inflater.inflate(R.layout.fragment_no_category_tab, container, false);
                    rootView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // do your logic for long click and remember to return it
                            Intent intent = new Intent(baseContext, FreeNavSearchActivity.class);
                            startActivity(intent);
                            }});
                    return rootView2;
                case 3:
                    View rootView4 = inflater.inflate(R.layout.fragment_route_tab, container, false);

                    ListView listView;
                    final List<RouteDTO> routes;

                    // Get ListView object from xml
                    listView = (ListView) rootView4.findViewById(R.id.listViewRoutes);

                    routes = MyApp.getRoutes();

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

                    return rootView4;
                default:
                    return null;
                    /*
                    View rootView3 = inflater.inflate(R.layout.fragment_category_tab, container, false);
                    TextView textViewDesc = (TextView) rootView3.findViewById(R.id.textViewDesc);
                    TextView textTitle = (TextView) rootView3.findViewById(R.id.textViewTitle);
                    //TextView textCatLst = (TextView) rootView3.findViewById(R.id.textViewCat);
                    textTitle.setText(MyApp.getRoutes().get(getArguments().getInt(ARG_SECTION_NUMBER)-3).getName());
                    textViewDesc.setText(MyApp.getRoutes().get(getArguments().getInt(ARG_SECTION_NUMBER)-3).getDescription());

                    //Essa parte era pra mostrar as categorias, mas não vai ter mais
                    //List<BeaconDTO> beaconLst = (MyApp.getRoutes().get(getArguments().getInt(ARG_SECTION_NUMBER)-3).getBeacons());
                    //String bodyText = "";

                    *//*Set<String> set = new HashSet<String>();

                    for(BeaconDTO b : beaconLst){
                        List<CategoryDTO> cLst = b.getCategories();
                        if(cLst != null) {
                            for (CategoryDTO c : cLst) {
                                set.add(c.getName());
                            }
                        }
                    }

                    Iterator it = set.iterator();
                    while(it.hasNext()) {
                        bodyText += ((String)(it.next()));
                        bodyText += "\n";
                    }*//*

                    *//*textCatLst.setText(bodyText);*//*

                    rootView3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // do your logic for long click and remember to return it
                            Intent intent = new Intent(baseContext, RouteActivity.class);
                            Bundle b = new Bundle();
                            b.putInt("id", Integer.parseInt(MyApp.getRoutes().get(getArguments().getInt(ARG_SECTION_NUMBER)-3).getId().toString())); //Your id
                            intent.putExtras(b); //Put your id to your next Intent
                            startActivity(intent);
                        }});

                    return rootView3;*/
            }
        }
    }
}
