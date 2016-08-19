package tarun0.com.callallower.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import tarun0.com.callallower.ListItemAdapter;

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {

    ListItemAdapter adapter;

    public SwipeHelper(ListItemAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.remove(viewHolder.getAdapterPosition());
    }
}
