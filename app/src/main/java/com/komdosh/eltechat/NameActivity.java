package com.komdosh.eltechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NameActivity extends AppCompatActivity {

    @BindView(R.id.name)
    public EditText txtName;

    @BindView(R.id.password)
    public EditText txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnJoin)
    public void join(){
        if (txtName.getText().toString().trim().length() > 0) {

            String name = txtName.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if(!name.contains(" ")) {
                Intent intent = new Intent(NameActivity.this,
                        MainActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("password", password);

                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.joinSpaceNameToast), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.joinEmptyNameToast), Toast.LENGTH_LONG).show();
        }
    }
}
