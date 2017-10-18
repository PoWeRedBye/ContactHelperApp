package com.contacthelpersqliteversion.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.contacthelpersqliteversion.Model.Contact;
import com.contacthelpersqliteversion.Adapters.ContactDataBaseAdapter;
import com.example.maxim_ozarovskiy.contacthelperapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;


public class ContactDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView name_view;
    private TextView phone_number_view;
    private TextView mail_adress_view;
    private ImageView contact_photo_detail;
    private Button call_button;
    private Button send_message_button;
    private Button delete_contact_button;
    private Button send_mail_button;
    private Button back_button;
    private Button update_button;

    private Contact contact;
    private ContactDataBaseAdapter mdb;

    private String id;
    private String name;
    private String phone;
    private String mail;
    private String contactImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_profile_activity);

        name_view = (TextView) findViewById(R.id.firstname_text_detail);
        phone_number_view = (TextView) findViewById(R.id.phone_text_detail);
        mail_adress_view = (TextView) findViewById(R.id.email_text_detail);
        contact_photo_detail = (ImageView) findViewById(R.id.contact_photo_detail);

        call_button = (Button) findViewById(R.id.detail_call_button);
        send_message_button = (Button) findViewById(R.id.send_message_button);
        delete_contact_button = (Button) findViewById(R.id.delete);
        back_button = (Button) findViewById(R.id.back_view_contact_button);
        send_mail_button = (Button) findViewById(R.id.send_mail_button);
        update_button = (Button) findViewById(R.id.update);

        mdb = new ContactDataBaseAdapter(this);
        mdb.open();


        contact = getIntent().getParcelableExtra("Contact");


        id = contact.getId();
        name = contact.getName();
        phone = contact.getPhone();
        mail = contact.getMail();
        contactImage = contact.getPhoto();

        name_view.setText(name);
        phone_number_view.setText(phone);
        mail_adress_view.setText(mail);

        if(contactImage != null) {
            Picasso.with(getApplicationContext()).load(new File(contactImage)).placeholder(R.drawable.freepik).into(contact_photo_detail);
        }
        call_button.setOnClickListener(this);
        send_message_button.setOnClickListener(this);
        send_mail_button.setOnClickListener(this);
        back_button.setOnClickListener(this);
        delete_contact_button.setOnClickListener(this);
        update_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.detail_call_button:
                intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + contact.getPhone()));
                startActivity(intent);
                break;
            case R.id.send_message_button:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms: " + contact.getPhone()));
                startActivity(intent);
                break;
            case R.id.send_mail_button:
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto: " + contact.getMail()));
                startActivity(intent);
                break;
            case R.id.back_view_contact_button:
                /*intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivity(intent);*/
                finish();
                break;
            case R.id.delete:
                int id = Integer.parseInt(contact.getId());
                mdb.delRec(id);
                finish();
                break;
            case R.id.update:
                intent = new Intent(getApplicationContext(), UpdateContactActivity.class);
                intent.putExtra("Contact",contact);
                startActivity(intent);
        }
    }
}
