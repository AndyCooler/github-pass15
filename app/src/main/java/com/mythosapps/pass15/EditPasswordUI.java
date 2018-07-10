package com.mythosapps.pass15;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mythosapps.pass15.types.PasswordEntry;
import com.mythosapps.pass15.util.DateUtil;

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
    private boolean show;

    public EditPasswordUI(Activity parent, String title, PasswordEntry entry, boolean show) {
        this.parent = parent;
        this.title = title;
        this.existingEntry = entry;
        if (existingEntry == null) {
            existingEntry = new PasswordEntry("","","","", null, null);
        }
        this.show = show;
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
        TextView usernameLabel = new TextView(parent);
        usernameLabel.setText(R.string.edit_password_username);
        linearLayout.addView(usernameLabel);

        LinearLayout userNamelinearLayout = new LinearLayout(parent);
        userNamelinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        userNamelinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        userNamelinearLayout.addView(usernameTextField);
        Button bt = new Button(parent);
        bt.setGravity(1);
        bt.setText("Copy");
        bt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) parent.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("user name", usernameTextField.getText());
                clipboard.setPrimaryClip(clip);
            }
        });
        userNamelinearLayout.addView(bt);
        linearLayout.addView(userNamelinearLayout);

        passwordTextField = new EditText(parent);
        int mode = show ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL : InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD;
        passwordTextField.setInputType(mode);
        passwordTextField.setText(existingEntry.getPassword());
        passwordTextField.setSelection(0, existingEntry.getPassword().length());
        passwordTextField.setEnabled(true);
        passwordTextField.setGravity(4);
        TextView passwordLabel = new TextView(parent);
        passwordLabel.setText(R.string.edit_password_password);
        linearLayout.addView(passwordLabel);

        LinearLayout passwordlinearLayout = new LinearLayout(parent);
        passwordlinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        passwordlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        passwordlinearLayout.addView(passwordTextField);
        Button btPwd = new Button(parent);
        btPwd.setGravity(1);
        btPwd.setText("Copy");
        btPwd.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        btPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) parent.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("password", passwordTextField.getText());
                clipboard.setPrimaryClip(clip);
            }
        });
        passwordlinearLayout.addView(btPwd);
        linearLayout.addView(passwordlinearLayout);

        builder.setView(linearLayout);


        builder.setPositiveButton(okButtonText, okButtonListener);
        builder.setNeutralButton(cancelButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.show();
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
