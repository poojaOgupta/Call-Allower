package tarun0.com.callallower;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public ListItemAdapter(Context mContext, Cursor cursor) {
        this.mCursor = cursor;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowItemView = inflater.inflate(R.layout.row_item, parent, false);

        return new ViewHolder(rowItemView);
    }


    @Override
    public void onBindViewHolder(ListItemAdapter.ViewHolder holder, int position) {
        TextView textViewName = holder.tvName;
        TextView textViewNumber = holder.tvNumber;

        mCursor.moveToFirst();
        mCursor.moveToPosition(position);

        textViewName.setText(
                mCursor.getString(mCursor.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NAME))
        );

        textViewNumber.setText(
                mCursor.getString(mCursor.getColumnIndex(ListsContract.BlackListEntry.COLUMN_NUMBER))
        );
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
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
        //TODO Add code to remove the item (producing error atm)
        Toast.makeText(mContext, "Item at position " + position + " should be removed", Toast.LENGTH_SHORT).show();
    }
}
