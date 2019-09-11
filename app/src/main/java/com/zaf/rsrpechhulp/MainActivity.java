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
        // When the activity starts, set the default theme so the Splash Screen to be replaced
        setTheme(R.style.AppTheme);
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
                // When the info button is clicked the 'terms&conditions' Dialog pops up
                if(menuItem.getItemId()==R.id.info_button) {
                    showAlertDialogButtonClicked();
                }
                return false;
            }
        });
    }

    public void showAlertDialogButtonClicked() {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Disable the click outside of the dialog
        builder.setCancelable(false);
        // Set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView dialogTextTextView = customLayout.findViewById(R.id.dialog_text);
        dialogTextTextView.setText(getResources().getString(R.string.dialog_text_text));
        if (dialogTextTextView.getText().toString().contains(getResources().
                getString(R.string.privacy_policy_dutch))) {
            Utils.setClickableHighLightedText(dialogTextTextView, getResources().
                            getString(R.string.privacy_policy_dutch)
                    , new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the hyperlink clicked, open the website in a browser
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    // Selected CATEGORY_BROWSABLE so as to open directly on a browser
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    // Pass the url to the intent
                    intent.setData(Uri.parse(getResources().
                            getString(R.string.hetprivacybeleid_hyperlink)));
                    startActivity(intent);
                }
            });
        }
        builder.setView(customLayout);
        // Add a button
        builder.setPositiveButton(getResources().getString(R.string.confirm_dutch),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.cancel();
            }
        });
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // This method is called via View when the main button is clicked
    public void startMapsActivity(View view){
        // Starts the Map Activity
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

}
