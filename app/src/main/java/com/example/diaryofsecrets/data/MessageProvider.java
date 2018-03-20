package com.example.diaryofsecrets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.diaryofsecrets.data.MessageContract.MessageEntry;

/**
 * {@link ContentProvider} for Pets app.
 */
public class MessageProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = MessageProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the diary table */
    private static final int MESSAGES = 100;

    /** URI matcher code for the content URI for a single message in the diary table */
    private static final int MESSAGE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(MessageContract.CONTENT_AUTHORITY, MessageContract.PATH_MESSAGES, MESSAGES);
        sUriMatcher.addURI(MessageContract.CONTENT_AUTHORITY, MessageContract.PATH_MESSAGES + "/#", MESSAGE_ID);
    }

    /** Database helper object */
    private MessageDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new MessageDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch(match){
            case MESSAGES:
                cursor = database.query(MessageEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case(MESSAGE_ID):
                selection = MessageEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MessageEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri" + uri);
        }

        //If the data at this URI changes, then we know we need to update he cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
         switch (match){
             case MESSAGES:
                 return insertMessage(uri, contentValues);
             default:
                 throw new IllegalArgumentException("Unknown Uri" + uri);
         }
    }

    private Uri insertMessage(Uri uri, ContentValues values) {
        // Check that the name is not null
        String date = values.getAsString(MessageEntry.COLUMN_MESSAGE_DATE);
        String message = values.getAsString(MessageEntry.COLUMN_MESSAGE);
        if (date == null ) {
            throw new IllegalArgumentException("Message requires a date");
        }
        else if (message == null) {
            throw new IllegalArgumentException("Message is required");
        }

        // No need to check the title, any value is valid (including null).

        //Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(MessageEntry.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all the listeners that data has changed for the pet content uri
        getContext().getContentResolver().notifyChange(uri , null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MESSAGES:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case MESSAGE_ID:
                // For the MESSAGE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MessageEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_MESSAGE_DATE} key is present,
        // check that the name value is not null.
        if (values.containsKey(MessageEntry.COLUMN_MESSAGE_DATE)) {
            String name = values.getAsString(MessageEntry.COLUMN_MESSAGE_DATE);
            if (name == null) {
                throw new IllegalArgumentException("message requires a date");
            }
        }

        if (values.containsKey(MessageEntry.COLUMN_MESSAGE)) {
            String name = values.getAsString(MessageEntry.COLUMN_MESSAGE);
            if (name == null) {
                throw new IllegalArgumentException("message is required");
            }
        }

        // No need to check the message and title, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated =  database.update(MessageEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MESSAGES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MessageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGE_ID:
                // Delete a single row given by the ID in the URI
                selection = MessageEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(MessageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MESSAGES:
                return MessageEntry.CONTENT_LIST_TYPE;
            case MESSAGE_ID:
                return MessageEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}