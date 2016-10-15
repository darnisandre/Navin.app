package guilherme.krzisch.com.mybeaconclient.view.splash_screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import guilherme.krzisch.com.mybeaconclient.view.sync_options.MainLocationActivity;
import guilherme.krzisch.com.mybeaconclient.view.sync_options.MainSyncActivity;

/**
 * Created by deeee on 17/08/2016.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainLocationActivity.class);
        startActivity(intent);
        finish();
    }
}