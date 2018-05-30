package com.mythosapps.pass15;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.mythosapps.pass15.storage.ConfigStorageFacade;
import com.mythosapps.pass15.storage.StorageFactory;
import com.mythosapps.pass15.types.ColorsUI;
import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This activity lets the user see on how many days they were working in a month, and what kind of
 * day each day was.
 */
public class PasswordsActivity extends AppCompatActivity {

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.pass15.MESSAGE";

    // Storage
    private ConfigStorageFacade storage;

    // View state and view state management
    List<PasswordEntry> list = null;
    private Random random = new Random();
    private boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "onCreate() started.");
        // prevent android system screenshot before onPause():
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_passwords);

        storage = StorageFactory.getConfigStorage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMonth);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.i(getClass().getName(), "intent action : " + action);
        Log.i(getClass().getName(), "intent type   : " + type);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuNewTask();
            }
        });

        setTitle("Passwords list");

        Log.i(getClass().getName(), "onCreate() finished.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getName(), "onResume() started");

        if (isPaused) {
            startMainActivity();
            isPaused = false;
        } else {
            initialize();
        }

        Log.i(getClass().getName(), "onResume() finished.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    private void initialize() {
        Log.i(getClass().getName(), "initialize() started");
        setTitle("Passwords");

        TableLayout table = (TableLayout) findViewById(R.id.tableView);
        table.removeAllViews();
        table.setColumnStretchable(4, true);
        TableRow row = null;
        TableRow previousRow = null;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        String previousCategory = null;

        //list = storage.loadConfigXml(this);
        list = new ArrayList<>();
        List<PasswordEntry> loadedUnsortedList = storage.loadConfigXml(this);

        // Migrate old data:
        for (final PasswordEntry data : loadedUnsortedList) {
            PasswordEntry.addEntryToCategory(list, data);
        }

        for (final PasswordEntry data : list) {

            if (data != null) {
                previousRow = row;
                row = new TableRow(this);
                row.setLayoutParams(lp);

                row.addView(createTextView(data.getCategory()));
                row.addView(createTextView(data.getName()));
                //int itemColor = calcItemColor(task0.getKindOfDay(), task0.isComplete());
                row.addView(createTextView(trimmed(data.getUsername())));
                row.addView(createTextView("***"));

                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuEditTask(data);
                    }
                });
                previousCategory = addWeekSeparatorLine(data, table, previousCategory, row, previousRow);
                table.addView(row);
            }
        }

//        View line = new View(this);
//        line.setBackgroundColor(ColorsUI.DARK_GREY_SAVE_ERROR);
//        line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 4));
//        table.addView(line);

        Log.i(getClass().getName(), "initialize() finished.");
    }

    private String trimmed(String displayString) {
        return displayString.length() > 12 ? displayString.substring(0, 12) : displayString;
    }

    private String addWeekSeparatorLine(PasswordEntry data, TableLayout table, String previousCategory, ViewGroup row, ViewGroup previousRow) {
        String category = data.getCategory();
        String newPreviousCategory = previousCategory;
        if (!category.equals(previousCategory)) {
            if (previousCategory != null) {
                View line = new View(this);
                line.setBackgroundColor(ColorsUI.DARK_BLUE_DEFAULT);
                line.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                table.addView(line);
            }
            newPreviousCategory = category;
        }
        return newPreviousCategory;
    }


    private TextView createTextView(String text, int color) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setBackgroundColor(random.nextInt(4));
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(5, 5, 5, 5); // 15, 5, 15, 5
        textView.setPadding(10, 5, 5, 5);
        return textView;
    }

    private TextView createTextView(String text) {
        return createTextView(text, ColorsUI.DARK_BLUE_DEFAULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_passwords, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_lock_screen) {
            startMainActivity();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, withId);
        startActivity(intent);
    }

    public void menuNewTask() {
        final EditPasswordUI taskUI = new EditPasswordUI(this, getString(R.string.new_password_title), null, false);

        taskUI.setOkButton(getString(R.string.new_password_new_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PasswordEntry entry = taskUI.getEntry();
                entry.setCreated(entry.getLastModified());
                if (list.contains(entry)) {
                    Toast.makeText(PasswordsActivity.this.getApplicationContext(), R.string.new_password_error_exists, Toast.LENGTH_SHORT).show();
                    return;
                }
                PasswordEntry.addEntryToCategory(list, entry);
                storage.saveExternalConfigXml(PasswordsActivity.this, list);
                initialize();
            }
        });
        taskUI.setCancelButton(getString(R.string.new_password_cancel_button));
        taskUI.show();
    }

    public void menuEditTask(PasswordEntry data) {
        final String oldName = data.getName();
        final String oldCategory = data.getCategory();
        final EditPasswordUI taskUI = new EditPasswordUI(this, getString(R.string.edit_password_title), data, true);

        taskUI.setOkButton(getString(R.string.edit_password_edit_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PasswordEntry.replaceByNameCat(list, taskUI.getEntry(), oldName, oldCategory); // applies changes from UI
                storage.saveExternalConfigXml(PasswordsActivity.this, list);
                initialize();
            }
        });
        taskUI.setCancelButton(getString(R.string.new_password_cancel_button));
        taskUI.show();
    }

}
