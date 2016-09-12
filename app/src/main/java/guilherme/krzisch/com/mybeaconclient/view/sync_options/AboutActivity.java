package guilherme.krzisch.com.mybeaconclient.view.sync_options;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import guilherme.krzisch.com.mybeaconclient.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
