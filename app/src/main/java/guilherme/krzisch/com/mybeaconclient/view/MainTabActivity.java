package guilherme.krzisch.com.mybeaconclient.view;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import butterknife.InjectView;
import guilherme.krzisch.com.mybeaconclient.MyApp;
import guilherme.krzisch.com.mybeaconclient.R;
import guilherme.krzisch.com.mybeaconclient.view.add_routes.AddRouteActivity;
import guilherme.krzisch.com.mybeaconclient.view.free_navigation.FreeNavSearchActivity;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.AboutActivity;
import guilherme.krzisch.com.mybeaconclient.view.util.DepthPageTransformer;
import guilherme.krzisch.com.mybeaconclient.view.util.SlidingTabLayout;

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
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        Intent intent = getIntent();
        startActivity(intent);
        finish();
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
            return  MyApp.getRoutes().size() + 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
               case 0:
                    return "Home";
               case 1:
                   return "Navegar sem rota";
               default:
                   return MyApp.getRoutes().get(position-2).getName();
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
                    TextView textTitle2 = (TextView) rootView1.findViewById(R.id.txtTitle);
                    textTitle2.setText("Bem-vindo ao sistema de navegação indoor Nav.In." +
                            "Você está no " + MyApp.getLocation().getDescription().toString());
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
                default:
                    View rootView3 = inflater.inflate(R.layout.fragment_category_tab, container, false);
                    TextView textView = (TextView) rootView3.findViewById(R.id.section_label);
                    TextView textTitle = (TextView) rootView3.findViewById(R.id.txtTitle);
                    textTitle.setText(MyApp.getRoutes().get(getArguments().getInt(ARG_SECTION_NUMBER)-3).getName());
                    textView.setText(MyApp.getRoutes().get(getArguments().getInt(ARG_SECTION_NUMBER)-3).getDescription());
                    return rootView3;
            }
        }
    }
}
