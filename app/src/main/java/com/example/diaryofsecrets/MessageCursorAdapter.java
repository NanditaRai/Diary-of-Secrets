package com.example.diaryofsecrets;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.diaryofsecrets.data.MessageContract;

import java.util.HashMap;
import java.util.Set;

/**
 * {@link MessageCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class MessageCursorAdapter extends CursorAdapter {
    private HashMap<Integer, Boolean> mSelectedPositions = new HashMap<Integer, Boolean>();
    private HashMap<Long, Boolean> mSelectedIds = new HashMap<Long, Boolean>();

    /**
     * Constructs a new {@link MessageCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public MessageCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView = (TextView) view.findViewById(R.id.list_item_message_title);
        TextView summaryTextView = (TextView) view.findViewById(R.id.list_item_message_summary);
        TextView dateTextView = (TextView) view.findViewById(R.id.list_item_message_date);
        TextView yearTextView = (TextView) view.findViewById(R.id.list_item_message_year);

        int dateColumnIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_MESSAGE_DATE);
        int titleColumnIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_MESSAGE_TITLE);
        int messageColumnIndex = cursor.getColumnIndex(MessageContract.MessageEntry.COLUMN_MESSAGE);

        String title = cursor.getString(titleColumnIndex);
        String message = cursor.getString(messageColumnIndex);
        String fullDate = cursor.getString(dateColumnIndex);
        String part[] = fullDate.split(",");
        String date = part[0];
        String year = part[1];

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.no_title);
        }

        titleTextView.setText(title);
        summaryTextView.setText(message);
        dateTextView.setText(date);
        yearTextView.setText(year);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);//let the adapter handle setting up the row views
        v.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.backgroundGray)); //default color

        if (mSelectedPositions.get(position) != null) {
            v.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.mediumGray));// this is a selected position so make it red
        }
        return v;
    }

    public void setNewSelection(int position, long id, boolean value) {
        mSelectedPositions.put(position, value);
        mSelectedIds.put(id, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelectedPositions.get(position);
        return result == null ? false : result;
    }

    public Set<Long> getCurrentCheckedPosition() {
//        return mSelectedPositions.keySet();
        return mSelectedIds.keySet();
    }

    public void removeSelection(int position, long id) {
        mSelectedPositions.remove(position);
        mSelectedIds.remove(id);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelectedPositions = new HashMap<Integer, Boolean>();
        mSelectedIds = new HashMap<Long, Boolean>();
        notifyDataSetChanged();
    }

}