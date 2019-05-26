package com.android.anticrook;

import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("AntiCrook");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0,0);
        styledAttributes.recycle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
//                Toast.makeText(this, "bam quay lai", Toast.LENGTH_SHORT ).show();
                onBackPressed();

                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
}
