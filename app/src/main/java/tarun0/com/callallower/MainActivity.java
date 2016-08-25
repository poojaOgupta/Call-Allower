package tarun0.com.callallower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import tarun0.com.callallower.utils.Util;

public class MainActivity extends AppCompatActivity {
    public static String selectedContactNames = "";
    Switch onOffSwitch;
    TextView selectedContacts;
    Button delete;
    Button editButton;
    private AdView mAdView;
    private boolean loggingOn = true;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MyApplication) getApplication()).startTracking();

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onOffSwitch = (Switch) findViewById(R.id.switch_on_off);
        assert (onOffSwitch != null);
        onOffSwitch.setChecked(Util.isServiceRunning(CallBlockingService.class, MainActivity.this));
        setOnOffSwitch();


        selectedContacts = (TextView) findViewById(R.id.selected_contacts);

        if (selectedContactNames != null && !selectedContactNames.equals("")) {
            selectedContacts.setText(selectedContactNames);
        }

        delete = (Button)findViewById(R.id.remove);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContentResolver().delete(ListsContract.BlackListEntry.CONTENT_URI, null, null);
            }
        });

        editButton = (Button) findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditBlacklistActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        onOffSwitch.setChecked(Util.isServiceRunning(CallBlockingService.class, MainActivity.this));
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void setOnOffSwitch() {
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && Util.isServiceRunning(CallBlockingService.class, MainActivity.this)) {
                    //Do nothing as Switch On but service is already running
                    if (loggingOn)
                        Log.d(TAG +" Switch", b+"");
                }
                else if (b && !Util.isServiceRunning(CallBlockingService.class, MainActivity.this)) {
                    //Switch On but Service not running
                    //Start Service

                    boolean stopped = true; //Considering the worst case when it doesn't start for some reason.
                    while (stopped) {
                        stopped = stopService(new Intent(MainActivity.this, CallBlockingService.class));
                        Log.d("Blocking",  stopped+"");
                        if (!stopped) {
                            if (loggingOn)
                            Log.d(TAG, "Started Successfully.");
                        }
                    }
                    startService(new Intent(MainActivity.this, CallBlockingService.class));
                    if (loggingOn)
                        Log.d(TAG + " Switch", b+"");
                }
                else if (!b && Util.isServiceRunning(CallBlockingService.class, MainActivity.this)) {
                    //Switch Off but Service running
                    //Stop Service
                    boolean running = true; //Considering the worst case when it doesn't stop for some reason.
                    while (running) {
                        running = stopService(new Intent(MainActivity.this, CallBlockingService.class));
                        if (loggingOn)
                            Log.d(TAG, running+"");

                        if (!running) {
                            if (loggingOn)
                                Log.d(TAG, "Stopped Successfully.");
                        }
                    }
                    if (loggingOn)
                        Log.d(TAG + " Switch", b+"");
                }
                else if (!b && !Util.isServiceRunning(CallBlockingService.class, MainActivity.this)) {
                    //Switch Off but Service not running
                    //Do nothing
                    if (loggingOn)
                        Log.d(TAG + " Switch", b+"");
                }
            }
        });
    }

}


