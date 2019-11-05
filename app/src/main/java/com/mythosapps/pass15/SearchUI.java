package com.mythosapps.pass15;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mythosapps.pass15.types.ColorsUI;
import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.DateUtil;
import com.mythosapps.pass15.util.PasswordGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.mythosapps.pass15.R.style.Theme_AppCompat_Light_Dialog;

public class SearchUI {

    private final Activity parent;

    private String okButtonText;
    private DialogInterface.OnClickListener okButtonListener;
    private String cancelButtonText;
    private EditText searchKeyWordField;


    public SearchUI(Activity parent) {
        this.parent = parent;
    }

    public void setOkButton(String okButtonText, DialogInterface.OnClickListener okButtonListener) {
        this.okButtonText = okButtonText;
        this.okButtonListener = okButtonListener;
    }

    public void setCancelButton(String cancelButtonText) {
        this.cancelButtonText = cancelButtonText;
    }

    public void show() {
        initializeUI();
    }

    private void initializeUI() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent, R.style.AlertDialogEditPasswordUI);
        builder.setTitle("Search");

        LinearLayout linearLayout = new LinearLayout(parent);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        searchKeyWordField = new EditText(parent);
        searchKeyWordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        searchKeyWordField.setEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchKeyWordField.setBackgroundTintList( ColorStateList.valueOf(ColorsUI.DARK_BLUE_DEFAULT ) );
        }

        linearLayout.addView(searchKeyWordField);

        builder.setView(linearLayout);
        builder.setPositiveButton(okButtonText, okButtonListener);
        builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final List<String> list = new ArrayList<String>();

        final AlertDialog dialog = builder.show();
    }

    public String getSearchKeyword() {
        return searchKeyWordField.getText().toString();
    }

}
