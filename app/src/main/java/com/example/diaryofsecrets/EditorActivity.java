package com.example.diaryofsecrets;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;

import com.example.diaryofsecrets.data.MessageContract.MessageEntry;
import com.example.diaryofsecrets.data.MessageDbHelper;
import com.example.diaryofsecrets.navigation.ChangeTheme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    /**
     * EditText field to enter the pet's name
     */
    private TextView mCalendar;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mTitle;

    private EditText mMessageText;
    private ImageView mCalendarImage;

    /**
     * EditText field to enter the pet's weight
     */
    private static final int EXISTING_PET_LOADER = 1;
    private boolean mPetHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentPetUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DiaryPreference diaryPreference = new DiaryPreference(MyApplication.getContext());
        ChangeTheme.onActivityCreateSetTheme(this, diaryPreference.getTheme());
        setContentView(R.layout.activity_editor);

        mCurrentPetUri = getIntent().getData();

        if (mCurrentPetUri == null) {
            setTitle(getString(R.string.write_diary));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_diary));
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mTitle = (EditText) findViewById(R.id.messageTitle);
        mMessageText = (EditText) findViewById(R.id.message);
        mCalendar = (TextView) findViewById(R.id.calendar);
        mCalendarImage = (ImageView) findViewById(R.id.calendarImage);

        //getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);

        mTitle.setOnTouchListener(mTouchListener);
        mMessageText.setOnTouchListener(mTouchListener);
        mCalendar.setOnTouchListener(mTouchListener);
        mCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        mCalendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.editor_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               hideKeyboard();
               return false;
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // clear the text in toolbar
//        mToolbar.setTitle(getString(R.string.space));
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputManager != null)
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showDatePickerDialog(){
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void saveMessage() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String calendarString = mCalendar.getText().toString().trim();
        String titleString = mTitle.getText().toString().trim();
        String messageString = mMessageText.getText().toString().trim();

        if (mCurrentPetUri == null &&
                TextUtils.isEmpty(calendarString) && TextUtils.isEmpty(titleString) &&
                TextUtils.isEmpty(messageString)) {
            finish();
//            return;
        }
        else if(TextUtils.isEmpty(calendarString))
            Toast.makeText(this, getString(R.string.please_select_date), Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(messageString))
            Toast.makeText(this, getString(R.string.please_write_something), Toast.LENGTH_SHORT).show();
        else {
            // Create a ContentValues object where column names are the keys,
            // and pet attributes from the editor are the values.
            ContentValues values = new ContentValues();
            values.put(MessageEntry.COLUMN_MESSAGE_DATE, calendarString);
            values.put(MessageEntry.COLUMN_MESSAGE_TITLE, titleString);
            values.put(MessageEntry.COLUMN_MESSAGE, messageString);


            if (mCurrentPetUri == null) {
                // Insert a new pet into the provider, returning the content URI for the new pet.
                Uri newUri = getContentResolver().insert(MessageEntry.CONTENT_URI, values);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.insertion_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.insertion_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.updation_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.update_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }


    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        if (mCurrentPetUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MessageEntry._ID,
                MessageEntry.COLUMN_MESSAGE_DATE,
                MessageEntry.COLUMN_MESSAGE_TITLE,
                MessageEntry.COLUMN_MESSAGE};

        return new CursorLoader(this,   // Parent activity context
                mCurrentPetUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int dateColumnIndex = cursor.getColumnIndex(MessageEntry.COLUMN_MESSAGE_DATE);
            int titleColumnIndex = cursor.getColumnIndex(MessageEntry.COLUMN_MESSAGE_TITLE);
            int messageColumnIndex = cursor.getColumnIndex(MessageEntry.COLUMN_MESSAGE);

            // Extract out the value from the Cursor for the given column index
            String date = cursor.getString(dateColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String message = cursor.getString(messageColumnIndex);

            mCalendar.setText(date);
            mTitle.setText(title);
            mMessageText.setText(message);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveMessage();
                return true;
            // Respond to a click on the "Delete" menu option
//            case R.id.action_save_as_file:
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !validateImagePermissions()) {
//                    askStoragePermissions();
//                }else
//                    createTextFile(mCalendar.getText().toString().trim(), mMessageText.getText().toString().trim());
//                return true;
//            case R.id.export_db:
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !validateImagePermissions()) {
//                    askStoragePermissions();
//                }else
//                    exportDB();
//                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean validateImagePermissions() {
        // return WHETHER ALL MIME_IMAGE PERMISSIONS ARE GRANTED OR NOT
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askStoragePermissions() {
        // ASK FOR ALL THE PERMISSIONS THAT ARE NOT YET GRANTED
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_CAMERA);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (validateImagePermissions()) {
//            createTextFile(mCalendar.getText().toString().trim(), mMessageText.getText().toString().trim());
            exportDB();
        } else {
            Toast.makeText(this, getString(R.string.permissions_required), Toast.LENGTH_SHORT).show();
        }
    }


    public void createTextFile(String sFileName, String sBody) {
        try {
            if(TextUtils.isEmpty(sFileName) && TextUtils.isEmpty(sBody)) {
                Toast.makeText(this, getString(R.string.failed_to_create_text_file), Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                File root = new File("/storage/emulated/0/" + getString(R.string.app_name) );
                if (!root.exists()) {
                    root.mkdirs();
                }
                File storageFile = new File(root, sFileName);
                FileWriter writer = new FileWriter(storageFile);
                writer.append(sBody);
                writer.flush();
                writer.close();
                String toastMessage = getString(R.string.saved_as, sFileName);
                Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.example.diaryofsecrets" +"/databases/"+ MessageDbHelper.DATABASE_NAME;
        String backupDBPath = MessageEntry.TABLE_NAME;
        File currentDB = new File(data, currentDBPath);
//        File backupDB = new File(sd, backupDBPath);
        File backupDB = new File("/storage/emulated/0/" + "DIARY DATABASE");
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}