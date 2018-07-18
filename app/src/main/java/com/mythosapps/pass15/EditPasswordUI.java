package com.mythosapps.pass15;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static com.mythosapps.pass15.R.style.Theme_AppCompat_Light_Dialog;

public class EditPasswordUI {

    private final Activity parent;
    private final String title;
    private String okButtonText;
    private DialogInterface.OnClickListener okButtonListener;
    private String cancelButtonText;
    private EditText categoryTextField;
    private EditText nameTextField;
    private EditText usernameTextField;
    private EditText passwordTextField;
    private PasswordEntry existingEntry;

    public EditPasswordUI(Activity parent, String title, PasswordEntry entry) {
        this.parent = parent;
        this.title = title;
        this.existingEntry = entry;
        if (existingEntry == null) {
            existingEntry = new PasswordEntry("","","","", null, null);
        }
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle(title);

        LinearLayout linearLayout = new LinearLayout(parent);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        categoryTextField = new EditText(parent);
        categoryTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        categoryTextField.setText(existingEntry.getCategory());
        categoryTextField.setSelection(0, existingEntry.getCategory().length());
        categoryTextField.setEnabled(true);
        TextView categoryLabel = new TextView(parent);
        categoryLabel.setText(R.string.edit_password_category);
        linearLayout.addView(categoryLabel);
        linearLayout.addView(categoryTextField);

        nameTextField = new EditText(parent);
        nameTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        nameTextField.setText(existingEntry.getName());
        nameTextField.setSelection(0, existingEntry.getName().length());
        nameTextField.setEnabled(true);
        TextView nameLabel = new TextView(parent);
        nameLabel.setText(R.string.edit_password_site);
        linearLayout.addView(nameLabel);
        linearLayout.addView(nameTextField);

        usernameTextField = new EditText(parent);
        usernameTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        usernameTextField.setText(existingEntry.getUsername());
        usernameTextField.setSelection(0, existingEntry.getUsername().length());
        usernameTextField.setEnabled(true);
        usernameTextField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_content_copy_black_24dp, 0);
        TextView usernameLabel = new TextView(parent);
        usernameLabel.setText(R.string.edit_password_username);
        linearLayout.addView(usernameLabel);

        usernameTextField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (usernameTextField.getRight() - usernameTextField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        ClipboardManager clipboard = (ClipboardManager) parent.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("username", usernameTextField.getText());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(parent.getApplicationContext(), "Username copied.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
        linearLayout.addView(usernameTextField);

        passwordTextField = new EditText(parent);
        passwordTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordTextField.setText(existingEntry.getPassword());
        passwordTextField.setSelection(0, existingEntry.getPassword().length());
        passwordTextField.setEnabled(true);
        passwordTextField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_content_copy_black_24dp, 0);
        TextView passwordLabel = new TextView(parent);
        passwordLabel.setText(R.string.edit_password_password);
        linearLayout.addView(passwordLabel);

        passwordTextField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (passwordTextField.getRight() - passwordTextField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        ClipboardManager clipboard = (ClipboardManager) parent.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("password", passwordTextField.getText());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(parent.getApplicationContext(), "Password copied.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
        linearLayout.addView(passwordTextField);

        builder.setView(linearLayout);


        builder.setPositiveButton(okButtonText, okButtonListener);
        //builder.setNeutralButtonIcon(parent.getDrawable(R.drawable.ic_visibility_black_24dp)); requires API22
        builder.setNeutralButton("Show/Hide", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing! Trick, muss gesetzt werden und nach dialog.show dann überschieben werden
            }
        });
        builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final List<String> list = new ArrayList<String>();

        final AlertDialog dialog = builder.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.isEmpty()) {
                    passwordTextField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    list.add("1");
                } else {
                    passwordTextField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    list.clear();
                }
            }
        });
    }

    public PasswordEntry getEntry() {
        existingEntry.setCategory(categoryTextField.getText().toString());
        existingEntry.setName(nameTextField.getText().toString());
        existingEntry.setUsername(usernameTextField.getText().toString());
        existingEntry.setPassword(passwordTextField.getText().toString());
        existingEntry.setLastModified(DateUtil.today());
        return existingEntry;
    }

}
