package com.example.proj1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.proj1.utils.AddNewTasks;
import com.example.proj1.utils.Model;
import com.example.proj1.controllers.TasksController;
import com.example.proj1.utils.DatabaseHelper;
import com.example.proj1.OnDialogCloseListener;

import com.example.proj1.utils.RecyclerViewTouchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomepageActivity extends AppCompatActivity implements OnDialogCloseListener{

    TextView profile_name;
    TextView profile_id;
    SharedPreferences sharedPreferences;
    Intent intent;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private DatabaseHelper myDB;
    private List<Model> mList;
    private TasksController adapter;
    String col = "_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        profile_name = findViewById(R.id.txt_profile_name);
        profile_name.setText(getIntent().getExtras().get("name").toString());

//        profile_id = findViewById(R.id.txt_profile_id);
//        profile_id.setText(getIntent().getExtras().get(col).toString());

        mRecyclerView = findViewById(R.id.recyclerview);
        fab = findViewById(R.id.fab);
        myDB = new DatabaseHelper(HomepageActivity.this);
        mList = new ArrayList<>();
        adapter = new TasksController(myDB, HomepageActivity.this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        mList = myDB.getAllTasks(this);
        Collections.reverse(mList);
        adapter.setTasks(mList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTasks.newInstance().show(getSupportFragmentManager() , AddNewTasks.TAG);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface){
        mList = myDB.getAllTasks(this);
        Collections.reverse(mList);
        adapter.setTasks(mList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            // do something here
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        sharedPreferences = getSharedPreferences(
                LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LoginActivity.session_status, false);
        editor.putString(LoginActivity.TAG_ID, null);
        editor.putString(LoginActivity.TAG_EMAIL, null);
        editor.putString(LoginActivity.TAG_NAME, null);
        editor.putString(LoginActivity.TAG_PHONE, null);
        editor.commit();
        intent = new Intent(HomepageActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}
