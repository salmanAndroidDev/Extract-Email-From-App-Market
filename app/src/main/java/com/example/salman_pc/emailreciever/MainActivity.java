package com.example.salman_pc.emailreciever;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ListView listView;
    ArrayAdapter<String> adapter;
    private List<String> emailList;
    public static int key = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.clear) {

            new AlertDialog.Builder(this)
            .setTitle("delete all emails")
            .setMessage("Do you really wanna delete all emails from the list")
            .setIcon(android.R.drawable.ic_delete)
            .setNegativeButton("no",null)
            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clear();
                }
            })
            .show();


            return true;
        }else if(id == R.id.share){
            share();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("emails", MODE_PRIVATE);
        listView = (ListView) findViewById(R.id.lv_show_mails);
        emailList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, emailList);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            storeEmailAddresses(intent);
        } else {
            for (int i = 0; i < key; i++) {
                String email = sharedPreferences.getString(String.valueOf(i), "null");
                if (!emailList.contains(email)) {
                    emailList.add(email);
                }
            }
            listView.setAdapter(adapter);
        }
    }

    public void storeEmailAddresses(Intent intent) {
        String emailAddress = intent.getExtras().getStringArray(Intent.EXTRA_EMAIL)[0];
        Bundle bundle = this.getIntent().getExtras();
        String[] emails = bundle.getStringArray(Intent.EXTRA_EMAIL);

        if (emailAddress != null) {
            sharedPreferences.edit().putString(String.valueOf(key), emailAddress).apply();
            key++;
            finish();
        }
    }

    public void clear() {
        key = 0;
        sharedPreferences.edit().clear().apply();
        emailList.clear();
        adapter.notifyDataSetChanged();
    }

    public void share(){
        String emailBank = "" ;
        for(String key : emailList){
            emailBank += key+"\n";
        }

        ShareCompat.IntentBuilder.from(this)
        .setChooserTitle("send emails by")
        .setType("text/plain")
        .setText(emailBank)
        .startChooser();
    }
}
