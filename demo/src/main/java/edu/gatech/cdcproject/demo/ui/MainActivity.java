package edu.gatech.cdcproject.demo.ui;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.about.AboutActivity;
import edu.gatech.cdcproject.demo.community.CommunityFragment;
import edu.gatech.cdcproject.demo.foodidentify.FoodIdentifyFragment;
import edu.gatech.cdcproject.demo.healthrecord.HealthRecordFragment;
import edu.gatech.cdcproject.demo.settings.SettingsActivity;
import static edu.gatech.cdcproject.demo.util.LogUtils.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = makeLogTag(MainActivity.class);

    private Toolbar toolbar;

    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupNavDrawer();

        switchFragment(new CommunityFragment(), getString(R.string.navdrawer_community));

    }

    private void setupNavDrawer() {
        // Toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // NavDrawer
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(R.layout.navdrawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.navdrawer_community).withIcon(R.drawable.ic_community_black_24dp).withIdentifier(getInteger(R.integer.navdrawer_community)),
                        new PrimaryDrawerItem().withName(R.string.navdrawer_foodidentify).withIcon(R.drawable.ic_food_indentify_black_24dp).withIdentifier(getInteger(R.integer.navdrawer_foodidentify)),
                        new PrimaryDrawerItem().withName(R.string.navdrawer_healthrecord).withIcon(R.drawable.ic_health_record_black_24dp).withIdentifier(getInteger(R.integer.navdrawer_healthrecord)),
                        new SectionDrawerItem().withName("Others"),
                        new PrimaryDrawerItem().withName(R.string.navdrawer_settings).withIcon(R.drawable.ic_settings_black_24dp).withIdentifier(getInteger(R.integer.navdrawer_settings)),
                        new PrimaryDrawerItem().withName(R.string.navdrawer_about).withIcon(R.drawable.ic_about_black_24dp).withIdentifier(getInteger(R.integer.navdrawer_about))
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {

                            onNext((int) drawerItem.getIdentifier());
                        }

                        return false;
                    }
                })
                .build();
    }

    private int getInteger(int id) {
        return getResources().getInteger(id);
    }

    private void onNext(int itemId) {
        if(itemId == getInteger(R.integer.navdrawer_community)) {
            toolbar.setTitle(R.string.navdrawer_community);
            switchFragment(new CommunityFragment(), getString(R.string.navdrawer_community));
        } else if(itemId == getInteger(R.integer.navdrawer_foodidentify)) {
            toolbar.setTitle(R.string.navdrawer_foodidentify);
            switchFragment(new FoodIdentifyFragment(), getString(R.string.navdrawer_foodidentify));
        } else if(itemId == getInteger(R.integer.navdrawer_healthrecord)) {
            toolbar.setTitle(R.string.navdrawer_healthrecord);
            switchFragment(new HealthRecordFragment(), getString(R.string.navdrawer_healthrecord));
        } else if(itemId == getInteger(R.integer.navdrawer_settings)) {
            startActivityWithParentStack(new Intent(this, SettingsActivity.class));
        } else if(itemId == getInteger(R.integer.navdrawer_about)) {
            startActivityWithParentStack(new Intent(this, AboutActivity.class));
        } else {
            LOGE(TAG, "Unkown navigation drawer item id. ");
        }

    }

    private void switchFragment(Fragment frag, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, frag, tag)
                .commit();
    }

    private void startActivityWithParentStack(Intent intent) {
        startActivity(intent);
    }
}