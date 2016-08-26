package tarun0.com.callallower;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.ArrayList;
import java.util.List;

import tarun0.com.callallower.adapter.ListItemAdapter;
import tarun0.com.callallower.data.ListsContract;
import tarun0.com.callallower.helper.DividerItemDecoration;
import tarun0.com.callallower.helper.SwipeHelper;
import tarun0.com.callallower.service.CallBlockingService;
import tarun0.com.callallower.utils.Util;
// ActionBarActivity deprecated but used as it's required to initialize Loader.
// The last parameter in init() couldn't be 'null' in AppCompatActivity.
// Only solution I found was to extend the deprecated activity class
public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private Switch onOffSwitch;
    private AdView mAdView;
    Tracker mTracker;
    private final String TAG = this.getClass().getSimpleName();
    public static final int EDIT_LIST_LOADER = 0;
    private ListItemAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyListNotice;

    private FloatingActionMenu menuLabelsRight;
    private FloatingActionButton deleteAllButton;
    private FloatingActionButton addContactsButton;
    private final int REQUEST_CONTACT = 0;
    public static ArrayList<String> blocked;

    private boolean loggingOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPhonePermission()) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.permission_not_granted_phone), Toast.LENGTH_LONG).show();
            finish();
        }

        mTracker = ((MyApplication) getApplication()).getmTracker();
        mTracker.setScreenName(TAG);

        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

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

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        getSupportLoaderManager().initLoader(EDIT_LIST_LOADER,null, this);

        emptyListNotice = (TextView) findViewById(R.id.empty_list_notice);
        deleteAllButton = (FloatingActionButton) findViewById(R.id.delete_all_button);
        addContactsButton = (FloatingActionButton) findViewById(R.id.add_contact_button);

        adapter = new ListItemAdapter(MainActivity.this, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(), R.drawable.row_item_divider_gradient)));
        recyclerView.setAdapter(adapter);


        blocked = new ArrayList<>();
        menuLabelsRight = (FloatingActionMenu) findViewById(R.id.menu_labels_right);
        menuLabelsRight.setClosedOnTouchOutside(true);

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_title))
                        .setMessage(getResources().getString(R.string.dialog_message))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                getContentResolver().delete(ListsContract.BlackListEntry.CONTENT_URI, null, null);
                                deleteAllButton.hideButtonInMenu(true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).show();

            }
        });

        setAddContactsButtonListener();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(MainActivity.this,
                ListsContract.BlackListEntry.CONTENT_URI,
                null,
                null,
                null,
                ListsContract.BlackListEntry.COLUMN_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        ItemTouchHelper.Callback callback = new SwipeHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
        if (data.getCount()>0) {
            emptyListNotice.setVisibility(View.GONE);
        } else emptyListNotice.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {
            // we got a result from the contact picker

            List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
            blocked.clear();
            ArrayList<ContentValues> cv = new ArrayList<>();
            ContentValues cvArray[] = new ContentValues[contacts.size()];


            for (Contact contact : contacts) {
                try {

                    String s = Util.setPhoneNumber(contact.getPhone(0));

                    ContentValues value = new ContentValues();

                    if (!contact.getLastName().equals("---")) {
                        value.put(ListsContract.BlackListEntry.COLUMN_NAME, contact.getFirstName() + " " + contact.getLastName());
                    } else
                        value.put(ListsContract.BlackListEntry.COLUMN_NAME, contact.getFirstName());

                    value.put(ListsContract.BlackListEntry.COLUMN_NUMBER, s);
                    cv.add(value);

                    blocked.add(s);
                    if (loggingOn)
                        Log.d(TAG + "Saved Number", s);
                }
                catch (NullPointerException e){
                    Log.e(TAG, e.toString());
                }
            }
            getContentResolver().bulkInsert(ListsContract.BlackListEntry.CONTENT_URI, cv.toArray(cvArray));

        }
    }

    private void setAddContactsButtonListener() {
        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLabelsRight.close(true);
                if (checkContactsReadPermission()) {
                    startContactsPickerActivity();
                }
                else Toast.makeText(MainActivity.this, getResources().getString(R.string.permission_not_granted_contacts), Toast.LENGTH_LONG).show();
            }
        });
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

    private boolean checkContactsReadPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int pid = android.os.Process.myPid();
                PackageManager pckMgr = getPackageManager();
                int uid = pckMgr.getApplicationInfo(getComponentName().getPackageName(), PackageManager.GET_META_DATA).uid;
                enforcePermission(android.Manifest.permission.READ_CONTACTS, pid, uid, getResources().getString(R.string.permission_not_granted_contacts));
                return true;
            }
            catch (PackageManager.NameNotFoundException | SecurityException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        } else return true;
    }

    private boolean checkPhonePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int pid = android.os.Process.myPid();
                PackageManager pckMgr = getPackageManager();
                int uid = pckMgr.getApplicationInfo(getComponentName().getPackageName(), PackageManager.GET_META_DATA).uid;
                enforcePermission(Manifest.permission.READ_PHONE_STATE, pid, uid, getResources().getString(R.string.permission_not_granted_phone));
                return true;
            }
            catch (PackageManager.NameNotFoundException | SecurityException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        } else return true;
    }

    private void startContactsPickerActivity() {
        Intent intent = new Intent(MainActivity.this, ContactPickerActivity.class)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
        startActivityForResult(intent, REQUEST_CONTACT);
    }

}


