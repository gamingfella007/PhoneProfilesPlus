package sk.henrichg.phoneprofilesplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;


public class ImportantInfoActivity extends AppCompatActivity {

    static final String EXTRA_SHOW_QUICK_GUIDE = "show_quick_guide";

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // must by called before super.onCreate() for PreferenceActivity
        GlobalGUIRoutines.setTheme(this, false, true, false); // must by called before super.onCreate()
        GlobalGUIRoutines.setLanguage(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_important_info);

        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // set a custom tint color for status bar
            switch (ApplicationPreferences.applicationTheme(getApplicationContext(), true)) {
                case "color":
                    tintManager.setStatusBarTintColor(ContextCompat.getColor(getBaseContext(), R.color.primary));
                    break;
                case "white":
                    tintManager.setStatusBarTintColor(ContextCompat.getColor(getBaseContext(), R.color.primaryDark19_white));
                    break;
                default:
                    tintManager.setStatusBarTintColor(ContextCompat.getColor(getBaseContext(), R.color.primary_dark));
                    break;
            }
        }
        */

        /*if (Build.VERSION.SDK_INT >= 21) {
            View toolbarShadow = findViewById(R.id.activity_important_info_toolbar_shadow);
            if (toolbarShadow != null)
                toolbarShadow.setVisibility(View.GONE);
        }*/

        Toolbar toolbar = findViewById(R.id.activity_important_info_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.important_info_activity_title);
            getSupportActionBar().setElevation(GlobalGUIRoutines.dpToPx(0));
        }

        TabLayout tabLayout = findViewById(R.id.activity_important_info_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.important_info_important_info_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.important_info_quick_guide_tab));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager2 viewPager = findViewById(R.id.activity_important_info_pager);
        HelpActivityFragmentStateAdapterX adapter = new HelpActivityFragmentStateAdapterX(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        // add Fragments in your ViewPagerFragmentAdapter class
        adapter.addFragment(new ImportantInfoHelpFragment());
        adapter.addFragment(new QuickGuideHelpFragment());

        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.OnConfigureTabCallback() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 1)
                            tab.setText(R.string.important_info_quick_guide_tab);
                        else
                            tab.setText(R.string.important_info_important_info_tab);
                    }
                });
        tabLayoutMediator.attach();

        Intent intent = getIntent();
        if (intent.getBooleanExtra(EXTRA_SHOW_QUICK_GUIDE, false)) {
            tabLayout.setScrollPosition(1,0f,true);
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
