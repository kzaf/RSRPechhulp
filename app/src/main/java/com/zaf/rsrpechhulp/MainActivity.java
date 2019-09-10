package com.zaf.rsrpechhulp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Once the activity starts, set the default theme so the Splash Screen to be replaced
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarOptions();
    }

    private void toolbarOptions() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_screen_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.info_button) {
                    showAlertDialogButtonClicked();
                }
                return false;
            }
        });
    }

    public void showAlertDialogButtonClicked() {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView tv = customLayout.findViewById(R.id.dialog_text);
        tv.setText(getResources().getString(R.string.dialog_text_text));
        if (tv.getText().toString().contains("hetprivacybeleid")) {
            Utils.setClickableHighLightedText(tv, "hetprivacybeleid", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(getResources().getString(R.string.hetprivacybeleid_hyperlink)));
                    startActivity(intent);
                }
            });
        }
        builder.setView(customLayout);
        // add a button
        builder.setPositiveButton("BEVESTIG", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startMapsActivity(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
