package com.contacthelpersqliteversion.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.contacthelpersqliteversion.Model.Contact;
import com.contacthelpersqliteversion.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private List<Contact> contact;
    private ItemClickListener<Contact> itemListener;

    public MyAdapter(Context context, List<Contact> contact, ItemClickListener<Contact> itemListener) {

        this.context = context;
        this.contact = contact;
        this.itemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, null);

        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {



        holder.name.setText(contact.get(position).getName());
        holder.phone.setText(contact.get(position).getPhone());
        holder.mail.setText(contact.get(position).getMail());

        String contactImage = contact.get(position).getPhoto();
        if (contactImage != null) {
            Picasso.with(context).load(new File(contactImage)).placeholder(R.drawable.freepik).into(holder.image);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.ItemClick(contact.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {


        CardView cardView;
        ImageView image;
        TextView name;
        TextView phone;
        TextView mail;
        TextView id;


        public ViewHolder(View v) {
            super(v);

            cardView = (CardView) v.findViewById(R.id.card);
            image = (ImageView) v.findViewById(R.id.contact_icon);
            name = (TextView) v.findViewById(R.id.contact_name);
            phone = (TextView) v.findViewById(R.id.contact_phone);
            mail = (TextView) v.findViewById(R.id.contact_mail);

        }


    }

    public interface ItemClickListener<T> {
        void ItemClick(T v, int position);
    }



}
