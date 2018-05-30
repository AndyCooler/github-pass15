package com.mythosapps.pass15;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mythosapps.pass15.storage.ConfigStorageFacade;
import com.mythosapps.pass15.storage.StorageFactory;
import com.mythosapps.pass15.types.ColorsUI;
import com.mythosapps.pass15.util.AppVersion;

public class MainActivity extends AppCompatActivity {

    private Integer beginnTime = null; //value
    private Integer endeTime = null; //value
    private Integer beginn15 = null; //value
    private Integer ende15 = null; //value
    private Integer previousSelectionBeginnTime = null; //viewId
    private Integer previousSelectionEndeTime = null; //viewId
    private Integer previousSelectionBeginn15 = null; //viewId
    private Integer previousSelectionEnde15 = null; //viewId

    // Storage
    private ConfigStorageFacade storage;
    private String loadedUnlockCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Unlock..");
        storage = StorageFactory.getConfigStorage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadedUnlockCode = storage.loadUnlockCode(this);
        TextView messageView = (TextView) findViewById(R.id.messageView);
        if (loadedUnlockCode == null) {
            messageView.setText(R.string.message_view_set_code);
        } else {
            messageView.setText(R.string.message_view_enter_code);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_unlock_pattern) {
            // TODO funktioniert noch nicht! Wenn man null hier speichert, danach wird nicht mehr der echte Code gespeichert
            //storage.saveUnlockCode(null);
            //reStartMainActivity();
            return true;
        }
        if (id == R.id.action_about) {
            String about = "Pass15 by Andreas, \n";
            about += "Version: " + AppVersion.getVersionName(this) + "\n";
            about += "Build-ID:" + AppVersion.getVersionCode(this) + "\n";
            about += "Code: Andreas. Many many tests: Julian";

            Toast.makeText(getApplicationContext(), about, Toast.LENGTH_SHORT).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void reStartMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, withId);
        startActivity(intent);
    }

    public void verarbeiteKlick(View v) {
        TextView view = (TextView) v;
        int viewId = view.getId();
        boolean isBeginnTime = viewId == R.id.beginnA || viewId == R.id.beginnB || viewId == R.id.beginnC || viewId == R.id.beginnD;
        boolean isEndeTime = viewId == R.id.endeA || viewId == R.id.endeB || viewId == R.id.endeC || viewId == R.id.endeD;
        boolean isBeginn15 = viewId == R.id.beginn00 || viewId == R.id.beginn15 || viewId == R.id.beginn30 || viewId == R.id.beginn45;
        boolean isEnde15 = viewId == R.id.ende00 || viewId == R.id.ende15 || viewId == R.id.ende30 || viewId == R.id.ende45;
        boolean isSelected = false;
        boolean isDeselected = false;
        if (isBeginnTime) {
            if (previousSelectionBeginnTime != null && viewId == previousSelectionBeginnTime) {
                setTransparent(viewId);
                beginnTime = null;
                previousSelectionBeginnTime = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionBeginnTime);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                beginnTime = Integer.valueOf((String) view.getText());
                previousSelectionBeginnTime = viewId;
                isSelected = true;
            }
        }
        if (isEndeTime) {
            if (previousSelectionEndeTime != null && viewId == previousSelectionEndeTime) {
                setTransparent(viewId);
                endeTime = null;
                previousSelectionEndeTime = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionEndeTime);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                endeTime = Integer.valueOf((String) view.getText());
                previousSelectionEndeTime = viewId;
                isSelected = true;
            }
        }
        if (isBeginn15) {
            if (previousSelectionBeginn15 != null && viewId == previousSelectionBeginn15) {
                setTransparent(viewId);
                beginn15 = null;
                previousSelectionBeginn15 = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionBeginn15);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                beginn15 = Integer.valueOf((String) view.getText());
                previousSelectionBeginn15 = viewId;
                isSelected = true;
            }
        }
        if (isEnde15) {
            if (previousSelectionEnde15 != null && viewId == previousSelectionEnde15) {
                setTransparent(viewId);
                ende15 = null;
                previousSelectionEnde15 = null;
                isDeselected = true;
            } else {
                setTransparent(previousSelectionEnde15);
                view.setBackgroundColor(ColorsUI.SELECTION_BG);
                ende15 = Integer.valueOf((String) view.getText());
                previousSelectionEnde15 = viewId;
                isSelected = true;
            }
        }

        if (isSelected || isDeselected) {
            boolean isComplete = beginnTime != null && endeTime != null && beginn15 != null && ende15 != null;
            if (isComplete) {
                String selectedUnlockCode = "" + beginnTime + beginn15 + endeTime + ende15;

                if (loadedUnlockCode == null) {
                    if (storage.saveUnlockCode(selectedUnlockCode)) {
                        Log.i(getClass().getName(), "save unlock code '" + selectedUnlockCode + "' success.");
                        Toast.makeText(MainActivity.this.getApplicationContext(), R.string.new_unlock_code_save_success, Toast.LENGTH_SHORT).show();
                        reStartMainActivity();
                    } else {
                        Log.e(getClass().getName(), "save unlock code '" + selectedUnlockCode + "' failed.");
                        loadedUnlockCode = null;
                    }
                } else {
                    Log.i(getClass().getName(), "unlock trying with code '" + selectedUnlockCode + "'");
                    if (selectedUnlockCode.equals(loadedUnlockCode)) {
                        startPasswordsEditorActivity();
                    }
                }
            }
        }
    }

    public void startPasswordsEditorActivity() {
        Intent intent = new Intent(this, PasswordsActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, TimeUtils.createID());
        startActivity(intent);
    }

    private void setTransparent(Integer viewId) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setBackgroundColor(ColorsUI.SELECTION_NONE_BG);
        }
    }

    private void setSelected(Integer viewId) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setBackgroundColor(ColorsUI.SELECTION_BG);
        }
    }

    private void setActivation(Integer viewId, boolean activated) {
        if (viewId != null) {
            TextView view = (TextView) findViewById(viewId);
            view.setTextColor(activated ? ColorsUI.ACTIVATED : ColorsUI.DEACTIVATED);
        }
    }

}
