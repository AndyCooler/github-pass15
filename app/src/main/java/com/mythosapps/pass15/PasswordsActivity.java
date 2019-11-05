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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This activity lets the user see on how many days they were working in a month, and what kind of
 * day each day was.
 */
public class PasswordsActivity extends AppCompatActivity {

    // Navigation
    public final static String EXTRA_MESSAGE = "com.mythosapps.pass15.MESSAGE";

    private static ViewGroup.LayoutParams TEXTVIEW_LAYOUT_PARAMS_FLOW = new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, .3f);
    private static ViewGroup.LayoutParams TEXTVIEW_LAYOUT_PARAMS_MAX = new TableRow.LayoutParams(0, WRAP_CONTENT, .7f);

    // Storage
    private ConfigStorageFacade plaintextStorage;
    private ConfigStorageFacade encryptedStorage;

    // View state and view state management
    List<PasswordEntry> list = null;
    private Random random = new Random();
    private static boolean isPaused;
    private static Timestamp lastUserActivity = new Timestamp(System.currentTimeMillis());
    private String searchKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getName(), "onCreate() started.");
        // prevent android system screenshot before onPause():
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_passwords);

        encryptedStorage = StorageFactory.getEncryptedStorage();
        plaintextStorage = StorageFactory.getConfigStorage();

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

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSearch();
            }
        });

        setTitle("Passwords list");

        Log.i(getClass().getName(), "onCreate() finished.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getName(), "onResume() started");

        // lock automatically after 30 seconds
        Timestamp back10sec = new Timestamp(System.currentTimeMillis() - 30 * 1000);
        if (isPaused && back10sec.after(lastUserActivity)) {
            startMainActivity();
        } else {
            initialize();
        }
        isPaused = false;
        lastUserActivity = new Timestamp(System.currentTimeMillis());

        Log.i(getClass().getName(), "onResume() finished.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        lastUserActivity = new Timestamp(System.currentTimeMillis());
    }

    private void initialize() {
        Log.i(getClass().getName(), "initialize() started");
        setTitle("Passwords");

        TableLayout table = (TableLayout) findViewById(R.id.tableView);
        table.removeAllViews();
        table.setShrinkAllColumns(true);

        TableRow row = null;
        TableRow previousRow = null;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1.0f);

        String previousCategory = null;

        list = new ArrayList<>();
        List<PasswordEntry> loadedUnsortedList = encryptedStorage.loadConfigXml(this);
        // migrate from version with unencrypted plaintextStorage
        if (loadedUnsortedList.isEmpty()) {
            loadedUnsortedList = plaintextStorage.loadConfigXml(this);
            if (!loadedUnsortedList.isEmpty()) {
                boolean migrationSuccess = encryptedStorage.saveExternalConfigXml(this, loadedUnsortedList);
                Log.i(getClass().getName(), "migration for entries success:" + migrationSuccess);
                if (migrationSuccess) {
                    // TODO delete old unencrypted files instead of emptying them
                }
            }
        }

        // Migrate old data:
        for (final PasswordEntry data : loadedUnsortedList) {
            PasswordEntry.addEntryToCategory(list, data);
        }

        if (searchKeyword == null || searchKeyword.trim().equals("")) {
            searchKeyword = "@@no_search_keyword@@";
        }
        for (final PasswordEntry data : list) {

            if (data != null) {
                previousRow = row;
                row = new TableRow(this);
                row.setLayoutParams(lp);

                int color = matches(data.getName()) || matches(data.getUsername()) ? ColorsUI.RED_FLAGGED : ColorsUI.DARK_BLUE_DEFAULT;
                row.addView(createTextViewInFlow(data.getCategory(), color));
                row.addView(createTextViewMaxWidth(data.getName(), color));

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

    private boolean matches(String name) {
        return  name == null ? false : name.contains(searchKeyword);
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
        textView.setBackgroundColor(random.nextInt(4)); // TODO ??
        textView.setPadding(10, 5, 5, 5);
        textView.setSingleLine(true);
        return textView;
    }

    private TextView createTextViewInFlow(String text, int color) {
        TextView textView = createTextView(text, color);
        // siehe https://developer.android.com/guide/topics/ui/layout/linear#prioritize-weight
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_FLOW);
        textView.setGravity(Gravity.LEFT);
        //textView.setSingleLine(false);
        return textView;
    }

    private TextView createTextViewRight(String text, int color) {
        TextView textView = createTextView(text, color);
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_FLOW);
        textView.setGravity(Gravity.RIGHT);
        return textView;
    }

    private TextView createTextViewMaxWidth(String text, int color) {
        TextView textView = createTextView(text, color);
        textView.setLayoutParams(TEXTVIEW_LAYOUT_PARAMS_MAX);
        textView.setGravity(Gravity.LEFT);
        return textView;
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

        if (id == R.id.action_export) {
            String filename = "Pass15.export.xml";
            if (plaintextStorage.exportConfigXml(this, list, filename)) {
                Toast.makeText(PasswordsActivity.this.getApplicationContext(), R.string.export_success, Toast.LENGTH_LONG).show();
            }

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
        final EditPasswordUI taskUI = new EditPasswordUI(this, getString(R.string.new_password_title), null);

        taskUI.setOkButton(getString(R.string.new_password_new_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                PasswordEntry entry = taskUI.getEntry();
                entry.setCreated(entry.getLastModified());
                if (!taskUI.getEntry().isEmpty()) {
                    if (list.contains(entry)) {
                        Toast.makeText(PasswordsActivity.this.getApplicationContext(), R.string.new_password_error_exists, Toast.LENGTH_SHORT).show();
                    } else {
                        PasswordEntry.addEntryToCategory(list, entry);
                        encryptedStorage.saveExternalConfigXml(PasswordsActivity.this, list);
                    }
                }
                initialize();
            }
        });
        taskUI.setCancelButton(getString(R.string.new_password_cancel_button));
        taskUI.show();
    }

    public void menuEditTask(PasswordEntry data) {
        final String oldName = data.getName();
        final String oldCategory = data.getCategory();
        final int listIndex = list.indexOf(data);
        final EditPasswordUI taskUI = new EditPasswordUI(this, getString(R.string.edit_password_title), data);

        taskUI.setOkButton(getString(R.string.edit_password_edit_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (taskUI.getEntry().isEmpty()) {
                    PasswordEntry.deleteByIndex(list, listIndex);
                } else {
                    PasswordEntry.replaceByNameCat(list, taskUI.getEntry(), oldName, oldCategory); // applies changes from UI
                }
                encryptedStorage.saveExternalConfigXml(PasswordsActivity.this, list);
                initialize();
            }
        });
        taskUI.setCancelButton(getString(R.string.new_password_cancel_button));
        taskUI.show();
    }

    public void menuSearch() {
        final SearchUI taskUI = new SearchUI(this);

        taskUI.setOkButton(getString(R.string.search_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchKeyword = taskUI.getSearchKeyword();
                initialize();
            }
        });
        taskUI.setCancelButton(getString(R.string.new_password_cancel_button));
        taskUI.show();
    }
}
