package com.zaf.rsrpechhulp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Once the activity starts, set the default theme so the Splash Screen to be replaced
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_button:
                showAlertDialogButtonClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

}
