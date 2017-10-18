package com.contacthelpersqliteversion.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.contacthelpersqliteversion.Model.Contact;
import com.contacthelpersqliteversion.Adapters.ContactDataBaseAdapter;
import com.contacthelpersqliteversion.Utils.CustomComparator;
import com.contacthelpersqliteversion.Adapters.MyAdapter;
import com.example.maxim_ozarovskiy.contacthelperapp.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ContactListActivity extends AppCompatActivity implements MyAdapter.ItemClickListener<Contact> {


    private ContactDataBaseAdapter dbManager;
    private List<Contact> contact;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button addContactBtn;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_list_activity);

        dbManager = new ContactDataBaseAdapter(this);
        dbManager.open();
        contact = new ArrayList<Contact>();
        contact = dbManager.getDataFromDB();


        Collections.sort(contact, new CustomComparator());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        relativeLayout = (RelativeLayout) findViewById(R.id.recl_view);

        myAdapter = new MyAdapter(this, contact, this);
        myAdapter.notifyDataSetChanged();

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(myAdapter);

        addContactBtn = (Button) findViewById(R.id.contact_add_button);

        addContactBtn.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent new_contact = new Intent(getApplicationContext(), AddContactActivity.class);
                startActivity(new_contact);
            }


        });

    }

    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    @Override
    public void ItemClick(Contact v, int position) {
        Intent view = new Intent(getApplicationContext(), ContactDetailActivity.class);
        view.putExtra("Contact", v);
        startActivity(view);
    }

}
