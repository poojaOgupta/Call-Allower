package tarun0.com.callallower;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import tarun0.com.callallower.utils.Util;

//ActionBarActivity deprecated but used as it's required to initialize Loader.
//The last parameter in init() couldn't be 'null' in AppCompatActivity.
public class EditBlacklistActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = this.getClass().getSimpleName();
    public static final int EDIT_LIST_LOADER = 0;
    ListItemAdapter adapter;
    RecyclerView recyclerView;
    TextView emptyListNotice;

    private FloatingActionMenu menuLabelsRight;
    private FloatingActionButton deleteAllButton;
    private FloatingActionButton addContactsButton;
    private final int REQUEST_CONTACT = 0;
    public static ArrayList<String> blocked;

    private boolean loggingOn = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blacklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        getSupportLoaderManager().initLoader(EDIT_LIST_LOADER,null, this);

        emptyListNotice = (TextView) findViewById(R.id.empty_list_notice);
        deleteAllButton = (FloatingActionButton) findViewById(R.id.delete_all_button);
        addContactsButton = (FloatingActionButton) findViewById(R.id.add_contact_button);

        adapter = new ListItemAdapter(EditBlacklistActivity.this, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(EditBlacklistActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(), R.drawable.row_item_divider_gradient)));
        recyclerView.setAdapter(adapter);


        blocked = new ArrayList<>();
        menuLabelsRight = (FloatingActionMenu) findViewById(R.id.menu_labels_right);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(EditBlacklistActivity.this)
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
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callContactPickerActivity();
            }
        });

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
            if (cv.size()>0)
                deleteAllButton.showButtonInMenu(true);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(EditBlacklistActivity.this,
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

    private void callContactPickerActivity() {
        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditBlacklistActivity.this, ContactPickerActivity.class)
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });
    }
}
