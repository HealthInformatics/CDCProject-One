package edu.gatech.cdcproject.demo.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import edu.gatech.cdcproject.demo.R;
import edu.gatech.cdcproject.demo.settings.SettingsActivity;

/**
 * Created by guoweidong on 3/28/16.
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupUI();
    }

    private void setupUI() {
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.onBackPressed();
            }
        });

        // About Content
        ((TextView)findViewById(R.id.contentTxt)).setText(Html.fromHtml(getString(R.string.about_content)));
    }
}
