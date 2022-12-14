package com.example.notekeeper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.notekeeper.ui.itemList.ItemListFragment;
import com.example.notekeeper.ui.slideshow.SlideshowFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.notekeeper.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView mNavigationView;
    public NoteKeeperOpenHelper mDbOpenHelper;
    private static final int NOTE_UPLOADER_JOB_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        enableStrictMode();

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        ((TextView)findViewById(R.id.txt_ver_number)).setText("Version: " + BuildConfig.VERSION_NAME);

        mDrawer = binding.drawerLayout;
        mNavigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_courses, R.id.nav_slideshow)
                .setOpenableLayout(mDrawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);
        mNavigationView.setNavigationItemSelectedListener(this);
        selectNavigationMenuItem(mNavigationView.getMenu().findItem(R.id.nav_notes));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("NoteKeeperNotify", "NoteKeeperNotify", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void enableStrictMode() {
        if(BuildConfig.DEBUG){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();

            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
       
    }

    @Override
    protected void onDestroy() {
        //mDbOpenHelper.close();
        super.onDestroy();
    }

    private void updateNavHeader() {
        View headerView = mNavigationView.getHeaderView(0);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        ((TextView)headerView.findViewById(R.id.txt_user_name)).setText(pref.getString("pref_display_name", ""));
        ((TextView)headerView.findViewById(R.id.txt_user_email)).setText(pref.getString("pref_email_address", ""));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
            {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            case R.id.action_backup_notes: {
                backupNotes();
                return true;
            }
            case R.id.action_upload_notes: {
                scheduleNoteUpload();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void backupNotes() {
        Intent intent = new Intent(this, NoteBackupService.class);
        intent.putExtra(NoteBackupService.EXTRA_COURSE_ID, NoteBackup.ALL_COURSES);
        startService(intent);
    }
    private void scheduleNoteUpload() {
        PersistableBundle extras = new PersistableBundle();
        extras.putString(NoteUploaderJobService.EXTRA_DATA_URI, NoteKeeperProviderContract.Notes.CONTENT_URI.toString());

        ComponentName componentName = new ComponentName(this, NoteUploaderJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(NOTE_UPLOADER_JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(extras)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private ActivityMainBinding binding;
    private DrawerLayout mDrawer;


    private void selectNavigationMenuItem(MenuItem item) {
        item.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;

        Bundle bundle = new Bundle();

        switch(item.getItemId()) {
            case R.id.nav_notes:
                bundle.putInt("mode", ItemListFragment.NOTES_MODE);
                fragmentClass = ItemListFragment.class;
                break;
            case R.id.nav_courses:
                bundle.putInt("mode", ItemListFragment.COURSES_MODE);
                fragmentClass = ItemListFragment.class;
                break;
            case R.id.nav_share:
                handleShare();
                break;
            default:
                fragmentClass = ItemListFragment.class;
                break;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set MyFragment Arguments
        if(fragment != null) {
            fragment.setArguments(bundle);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_main, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            selectNavigationMenuItem(item);
            // Set action bar title
            setTitle(item.getTitle());
        }
        // Close the navigation drawer
        mDrawer.closeDrawers();
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.app_bar_main);
        Snackbar.make(view, "Share to - " +
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString("pref_favorite_social", ""),
                Snackbar.LENGTH_LONG)
                .show();
    }
}
