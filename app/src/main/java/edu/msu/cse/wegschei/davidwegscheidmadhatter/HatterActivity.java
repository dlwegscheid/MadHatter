package edu.msu.cse.wegschei.davidwegscheidmadhatter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;


public class HatterActivity extends ActionBarActivity {

    /**
     * The hatter view object
     */
    private HatterView hatterView = null;

    /**
     * The color select button
     */
    private Button colorButton = null;

    /**
     * The feather checkbox
     */
    private CheckBox featherCheck = null;

    /**
     * The hat choice spinner
     */
    private Spinner spinner;

    /**
     * Request code when selecting a picture
     */
    private static final int SELECT_PICTURE = 1;

    /**
     * Request code when selecting a color
     */
    private static final int SELECT_COLOR = 2;

    private static final String PARAMETERS = "parameters";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hatter);

        /*
         * Get some of the views we'll keep around
         */
        hatterView = (HatterView)findViewById(R.id.hatterView);
        colorButton = (Button)findViewById(R.id.buttonColor);
        featherCheck = (CheckBox)findViewById(R.id.checkFeather);
        spinner = (Spinner) findViewById(R.id.spinnerHat);

        /*
         * Set up the spinner
         */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hats_spinner, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
                hatterView.setHat(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        /*
         * Restore any state
         */
        if(savedInstanceState != null) {
            hatterView.getFromBundle(PARAMETERS, savedInstanceState);

            spinner.setSelection(hatterView.getHat());
        }
    }

    /**
     * Handle a Picture button press
     * @param view The view this is called from
     */
    public void onPicture(View view) {
        // Get a picture from the gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    /**
     * Handle a Color button press
     * @param view The view this is called from
     */
    public void onColor(View view) {
        // Get a color
        //Intent intent = new Intent(this, ColorSelectActivity.class);

        //this.startActivityForResult(intent, SELECT_COLOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            // Response from the picture selection activity
            Uri imageUri = data.getData();

            // We have to query the database to determine the document ID for the image
            Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":")+1);
            cursor.close();

            // Next, we query the content provider to find the path for this
            // document id.
            cursor = getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();

            if(path != null) {
                Log.i("Path", path);
                hatterView.setImagePath(path);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        hatterView.putToBundle(PARAMETERS, outState);
    }
}
