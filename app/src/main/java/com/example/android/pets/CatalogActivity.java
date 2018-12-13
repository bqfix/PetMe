package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);

        displayDatabaseInfo();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Update Databases info string when activity is restarted
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(PetEntry.TABLE_NAME, null, null, null, null, null, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("The current number of pets is " + cursor.getCount() + "\n\n");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(PetEntry._ID));
                String name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME));
                String breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED));
                int intGender = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER));
                int weight = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT));

                //If breed is empty, print unknown
                if (breed.equals("")) {
                    breed = "Unknown";
                }
                
                //Change gender from int to string
                String gender;
                switch (intGender) {
                    case 1: gender = "Male"; break;
                    case 2: gender = "Female"; break;
                    default: gender = "Unknown";
                }

                String line = id + " - " + name + " - " + breed + " - " + gender + " - " + weight + "\n";
                displayView.append(line);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
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
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void insertData() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        long newRowId = sqLiteDatabase.insert(PetEntry.TABLE_NAME, null, values);
    }
}