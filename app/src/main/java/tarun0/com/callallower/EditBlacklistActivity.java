package tarun0.com.callallower;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import tarun0.com.callallower.helper.DividerItemDecoration;
import tarun0.com.callallower.helper.SwipeHelper;

//ActionBarActivity deprecated but used as it's required to initialize Loader.
//The last parameter in init() couldn't be 'null' in AppCompatActivity.
public class EditBlacklistActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = this.getClass().getSimpleName();
    public static final int EDIT_LIST_LOADER = 0;
    ListItemAdapter adapter;
    RecyclerView recyclerView;
    TextView emptyListNotice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blacklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        getSupportLoaderManager().initLoader(EDIT_LIST_LOADER,null, this);

        emptyListNotice = (TextView) findViewById(R.id.empty_list_notice);

        adapter = new ListItemAdapter(EditBlacklistActivity.this, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(EditBlacklistActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(), R.drawable.row_item_divider_gradient)));
        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getContentResolver().delete(ListsContract.BlackListEntry.CONTENT_URI, null, null);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(EditBlacklistActivity.this,
                ListsContract.BlackListEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
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
}
