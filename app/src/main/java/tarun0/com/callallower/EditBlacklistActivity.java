package tarun0.com.callallower;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import tarun0.com.callallower.helper.SwipeHelper;

//ActionBarActivity deprecated but used as it's required to initialize Loader.
//The last parameter in init() couldn't be 'null' in AppCompatActivity.
public class EditBlacklistActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = this.getClass().getSimpleName();
    public static final int EDIT_LIST_LOADER = 0;
    ListItemAdapter adapter;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blacklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        getSupportLoaderManager().initLoader(EDIT_LIST_LOADER,null, this);

        adapter = new ListItemAdapter(EditBlacklistActivity.this, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(EditBlacklistActivity.this));
        recyclerView.setAdapter(adapter);

        /*ListItemAdapter adapter = new ListItemAdapter(this, c);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*/
        /*RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);*/

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
