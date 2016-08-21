package tarun0.com.callallower;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import tarun0.com.callallower.adapter.CursorRecyclerViewAdapter;

public class ListItemAdapter extends CursorRecyclerViewAdapter<ListItemAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public ListItemAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        TextView textViewName = viewHolder.tvName;
        TextView textViewNumber = viewHolder.tvNumber;

        mCursor = cursor;
        mCursor.moveToFirst();
        mCursor.moveToPosition(viewHolder.getAdapterPosition());

        textViewName.setText(
                mCursor.getString(mCursor.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NAME))
        );

        textViewNumber.setText(
                mCursor.getString(mCursor.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NUMBER))
        );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowItemView = inflater.inflate(R.layout.row_item, parent, false);

        return new ViewHolder(rowItemView);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvName;
        public TextView tvNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_name);
            tvNumber = (TextView) itemView.findViewById(R.id.item_number);
        }
    }

    public void remove(int position) {
        //TODO Add code to remove the item (producing an error atm)
        //java.lang.IllegalStateException: attempt to re-open an already-closed object: SQLiteQuery: SELECT * FROM blacklist

        Cursor c = getCursor();
        if (c.getCount() == 1) {
            Toast.makeText(mContext, "All numbers deleted!", Toast.LENGTH_SHORT).show();
        }
        c.moveToPosition(position);
        String number = c.getString(c.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NUMBER));
        String delete[] = new String[1];
        delete[0] = number;
        mContext.getContentResolver().delete(ListsContract.BlackListEntry.CONTENT_URI,
                ListsContract.BlackListEntry.COLUMN_NUMBER + " = ?",
                delete);
        notifyItemRemoved(position);
    }
}
