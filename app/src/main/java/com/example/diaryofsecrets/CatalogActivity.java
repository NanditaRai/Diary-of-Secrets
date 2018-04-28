package com.example.diaryofsecrets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.diaryofsecrets.data.MessageContract.MessageEntry;
import com.example.diaryofsecrets.navigation.ChangeTheme;
import com.example.diaryofsecrets.navigation.ChangeThemeFragment;
import com.example.diaryofsecrets.navigation.RateApplicationFragment;
import com.example.diaryofsecrets.navigation.SetAppLockFragment;
import com.example.diaryofsecrets.navigation.SetReminderFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Set;


/**
 * Displays list of pages that were entered and stored in the diary app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    private static final int PET_LOADER = 0;
    private static final int SELECT_PICTURE = 7;

    MessageCursorAdapter mCursorAdapter;
    private DiaryPreference mDiaryPreference;
    private View mEmptyListView;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private ImageView mProfileImage;
    private Uri mSelectedImageUri;
    private RelativeLayout mProfileImageParentView;
    private ListView mMessageListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDiaryPreference = new DiaryPreference(MyApplication.getContext());
        ChangeTheme.onActivityCreateSetTheme(this, mDiaryPreference.getTheme());
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mMessageListView = (ListView) findViewById(R.id.list);
        mCursorAdapter = new MessageCursorAdapter(this, null);
        mMessageListView.setAdapter(mCursorAdapter);

        mEmptyListView = (View) findViewById(R.id.empty_view);
        mMessageListView.setEmptyView(mEmptyListView);

        getLoaderManager().initLoader(PET_LOADER, null, this);

        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(MessageEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setItemIconTintList(null);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        updateNavigationDrawer();
        //set profile image
        View mHeaderView = mNavigationView.getHeaderView(0);
        mProfileImage = (ImageView) mHeaderView.findViewById(R.id.profile_image);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(this);
        //handle the drawer toggle event
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we don't want anything to happen so we leave this blank
                if (mDiaryPreference.getProfileImageUri() != null) {
//                    setProfileImageInView(Uri.parse(mDiaryPreference.getProfileImageUri()));
//                    mProfileImage.setImageURI(Uri.parse(mDiaryPreference.getProfileImageUri()));
                    Picasso.with(CatalogActivity.this).load(Uri.parse(mDiaryPreference.getProfileImageUri())).into(mProfileImage);
                }
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(mDrawerToggle);




        mMessageListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        mMessageListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                mCursorAdapter.clearSelection();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub

                nr = 0;
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // TODO Auto-generated method stub
                switch (item.getItemId()) {

                    case R.id.item_delete:
                        nr = 0;
                        showDeleteMessage();

                        //no need to call as this steps are already being taken in onDestroyActionMode()
//                        mCursorAdapter.clearSelection();
//                        mode.finish();
                }
                return false;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // TODO Auto-generated method stub
                if (checked) {
                    nr++;
                    mCursorAdapter.setNewSelection(position, id, checked);
                } else {
                    nr--;
                    mCursorAdapter.removeSelection(position, id);
                }
                mode.setTitle(nr + " selected");

            }
        });


        mMessageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                // TODO Auto-generated method stub

                mMessageListView.setItemChecked(position, !mCursorAdapter.isPositionChecked(position));
                return false;
            }
        });
    }

    /**
     * This is called when the image from the camera is captured
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            mSelectedImageUri = data.getData();
            if (null != mSelectedImageUri) {
                Picasso.with(CatalogActivity.this).load(mSelectedImageUri).into(mProfileImage);
                mDiaryPreference.setProfileImageUri(mSelectedImageUri);
            } else {
                Toast.makeText(this, R.string.image_not_selected_properly, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void updateNavigationDrawer() {
        //change the text in drawer if app lock is already set
        if (!TextUtils.isEmpty(mDiaryPreference.getPassword())) {
            Menu menu = mNavigationView.getMenu();
            MenuItem menuItem = menu.findItem(R.id.set_app_lock);
            menuItem.setTitle(getString(R.string.change_personal_lock));
        }
    }

    private void deleteAllEntries() {
        int rowsDeleted = getContentResolver().delete(MessageEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from diary database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                MessageEntry._ID,
                MessageEntry.COLUMN_MESSAGE_DATE,
                MessageEntry.COLUMN_MESSAGE_TITLE,
                MessageEntry.COLUMN_MESSAGE
        };

        return new CursorLoader(this, MessageEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.go_to_home:
                if (getSupportFragmentManager().findFragmentById(R.id.catalog_home_frame) != null) {
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.catalog_home_frame)).commit();
                    mFab.setVisibility((View.VISIBLE));
                }
                break;
            case R.id.set_app_lock:
                mFab.setVisibility(View.INVISIBLE);
                openAppLockFragment();
                return true;
            case R.id.set_reminder:
                mFab.setVisibility(View.INVISIBLE);
                openSetReminderFragment();
                return true;
            case R.id.rate_app:
                mFab.setVisibility(View.INVISIBLE);
                openRateApplicationFragment();
                return true;
            case R.id.change_theme:
                mFab.setVisibility(View.INVISIBLE);
                openChangeThemeFragment();
                return true;
            case R.id.log_out:
                //show dialog to confirm logging out
                showLogOutMessage();
            default:
                break;
        }
        return false;
    }

    /**
     * open the fragment to set the private lock on the app
     */
    private void openAppLockFragment() {
        SetAppLockFragment setAppLockFragment = new SetAppLockFragment();
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag(SetAppLockFragment.TAG) == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.catalog_home_frame, setAppLockFragment, SetAppLockFragment.TAG);
            transaction.commit();
        }
    }

    /**
     * open the fragment to allow the user to set the reminder to write diary
     */
    private void openSetReminderFragment() {
        SetReminderFragment setReminderFragment = new SetReminderFragment();
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag(SetReminderFragment.TAG) == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.catalog_home_frame, setReminderFragment, SetReminderFragment.TAG);
            transaction.commit();
        }
    }

    /**
     * open the fragment to allow the user ot rate the application on play store
     */
    private void openRateApplicationFragment() {
        RateApplicationFragment rateApplicationFragment = new RateApplicationFragment();
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag(RateApplicationFragment.TAG) == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.catalog_home_frame, rateApplicationFragment, RateApplicationFragment.TAG);
            transaction.commit();
        }
    }

    /**
     * open the fragment to allow the user yo change the theme according to his/her choice
     */
    private void openChangeThemeFragment() {
        ChangeThemeFragment changeThemeFragment = new ChangeThemeFragment();
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentByTag(ChangeThemeFragment.TAG) == null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.catalog_home_frame, changeThemeFragment, ChangeThemeFragment.TAG);
            transaction.commit();
        }
    }

    private void showDeleteMessage() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.ask_before_deleting))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //get selected ids and delete from database
                        ArrayList<Uri> Uris = new ArrayList<>();
                        Set<Long> Ids = mCursorAdapter.getCurrentCheckedPosition();
                        for(long id : Ids){
                            Uri currentPetUri = ContentUris.withAppendedId(MessageEntry.CONTENT_URI, id);
                            Uris.add(currentPetUri);
                        }
                        deletePets(Uris);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
    }

    private void deletePets(ArrayList<Uri> uris) {
        int rowsDeleted = 0;
        for(Uri uri: uris) {
            rowsDeleted = getContentResolver().delete(uri, null, null);
        }
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


    /**
     * show dialog before logging out
     */
    private void showLogOutMessage() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        //calling sync state is necessary or else your hamburger icon wont show up
        mDrawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onBackPressed() {
        mNavigationView.getMenu().getItem(0).setChecked(true);
        //remove the fragments from the frame and go back to the main home page
        if (getSupportFragmentManager().findFragmentById(R.id.catalog_home_frame) != null) {
            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.catalog_home_frame)).commit();
            mFab.setVisibility(View.VISIBLE);
        } else {
            showLogOutMessage();
        }
    }

    @Override
    public void onResume() {
        mFab.setVisibility(View.VISIBLE);
        super.onResume();
    }

}
