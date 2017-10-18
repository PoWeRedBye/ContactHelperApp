package com.contacthelpersqliteversion.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.contacthelpersqliteversion.Utils.BaseToolbarActivity;
import com.contacthelpersqliteversion.Adapters.ContactDataBaseAdapter;
import com.contacthelpersqliteversion.Utils.ImageUtil;
import com.contacthelpersqliteversion.Model.Contact;
import com.example.maxim_ozarovskiy.contacthelperapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;

import static com.contacthelpersqliteversion.Utils.ImageUtil.FILE_NAME;
import static com.contacthelpersqliteversion.Utils.ImageUtil.REQUEST_IMAGE_CAMERA;
import static com.contacthelpersqliteversion.Utils.ImageUtil.REQUEST_IMAGE_GALLERY;
import static com.contacthelpersqliteversion.Utils.ImageUtil.directory;


public class UpdateContactActivity extends BaseToolbarActivity implements View.OnClickListener {

    private static final int CONTEXT_MENU_GALLERY = 1;
    private static final int CONTEXT_MENU_CAMERA = 2;
    private Button saveBtn;
    private Button backBtn;
    private Button addPhotoBtn;
    private EditText contactName;
    private EditText phoneNumber;
    private EditText mailAdress;
    private ImageView imageView;
    private static String selectedImagePath;
    private static String realPhotoPath;

    private File mNewAvatarFile;
    private String currentPhotoPath;

    private String name;
    private String phone;
    private String mail;
    private String image;

    private Contact mContact;
    private ContactDataBaseAdapter dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Contact");
        setContentView(R.layout.contact_add_edit);

        dbManager = new ContactDataBaseAdapter(this);
        dbManager.open();


        contactName = (EditText) findViewById(R.id.name_text_edit);
        phoneNumber = (EditText) findViewById(R.id.phone_text_edit);
        mailAdress = (EditText) findViewById(R.id.email_text_edit);

        saveBtn = (Button) findViewById(R.id.contact_save_button);
        backBtn = (Button) findViewById(R.id.contact_add_edit_back_button);
        addPhotoBtn = (Button) findViewById(R.id.contact_photo_add);

        imageView = (ImageView) findViewById(R.id.contact_icon_logo);

        mContact = getIntent().getParcelableExtra("Contact");

        String id = mContact.getId();
        String name = mContact.getName();
        String phone = mContact.getPhone();
        String mail = mContact.getMail();
        String contactImage = mContact.getPhoto();
        if(contactImage != null) {
            Picasso.with(getApplicationContext()).load(new File(contactImage)).placeholder(R.drawable.freepik).into(imageView);
        }
        contactName.setText(name);
        phoneNumber.setText(phone);
        mailAdress.setText(mail);

        saveBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        registerForContextMenu(addPhotoBtn);
        addPhotoBtn.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View v) {
                openContextMenu(v);
                v.showContextMenu();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Gallery/Camera");
        menu.add(Menu.NONE, CONTEXT_MENU_GALLERY, Menu.NONE, "Gallery");
        menu.add(Menu.NONE, CONTEXT_MENU_CAMERA, Menu.NONE, "Camera");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_MENU_GALLERY:
                onGalleryClick();
                break;

            case CONTEXT_MENU_CAMERA:
                onCameraClick();
                break;

        }
        return super.onContextItemSelected(item);


    }

    private void onGalleryClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions(PERMISSION_GALLERY_REQUEST_CODE);
        } else {
            Intent intent_gallery = new Intent(Intent.ACTION_PICK);
            intent_gallery.setType("image/*");
            startActivityForResult(intent_gallery, REQUEST_IMAGE_GALLERY);
        }
    }

    private void onCameraClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions(PERMISSION_CAMERA_REQUEST_CODE);
        } else {
            currentPhotoPath = ImageUtil.onCameraStart(this);
        }
    }

    @Override
    protected void onRequestAccessPermissionResult(int requestCode) {
        super.onRequestAccessPermissionResult(requestCode);
        switch (requestCode) {
            case PERMISSION_CAMERA_REQUEST_CODE:
                currentPhotoPath = ImageUtil.onCameraStart(this);
                break;

            case PERMISSION_GALLERY_REQUEST_CODE:
                Intent intent_gallery = new Intent(Intent.ACTION_PICK);
                intent_gallery.setType("image/*");
                startActivityForResult(intent_gallery, REQUEST_IMAGE_GALLERY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case REQUEST_IMAGE_GALLERY:
                    Uri selectedImageUri = intent.getData();
                    selectedImagePath = getImagePath(selectedImageUri);
                    Bitmap bitmap_gallery = BitmapFactory.decodeFile(selectedImagePath);
                    imageView.setImageBitmap(bitmap_gallery);
                    break;


                case REQUEST_IMAGE_CAMERA:
                    String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
                    if (currentPhotoPath != null) {
                        File file = new File(Uri.parse(currentPhotoPath).getPath());
                        if (file.exists()) {
                            try {
                                Bitmap bitmap = ImageUtil.scaleImage(file, ImageUtil.MAX_IMAGE_SIZE, ImageUtil.MAX_IMAGE_SIZE);
                                ImageUtil.saveImageToDisk(bitmap, FILE_NAME);
                                Picasso.with(this)
                                        .load(file)
                                        .fit()
                                        .centerCrop()
                                        .into(imageView);
                                currentPhotoPath = null;
                                mNewAvatarFile = new File(directory + "/" + FILE_NAME + imageName + ".jpg");
                                realPhotoPath = mNewAvatarFile.getPath();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                                mNewAvatarFile = null;
                            }
                        }
                    }
                    break;
            }
        }
    }


    public String getImagePath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_save_button:

                int id = Integer.parseInt(mContact.getId());
                name = contactName.getText().toString();
                phone = phoneNumber.getText().toString();
                mail = mailAdress.getText().toString();
                if (getSelectedImage() == null){
                    image = mContact.getPhoto();
                }else {
                    image = getSelectedImage();
                }

                if (isValidData()) {
                    dbManager.updateContact(id, name, phone, mail, image);
                    this.returnHome();
                    Toast.makeText(getApplicationContext(), "Contact Edit!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Contact is not Edit!!!", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.contact_add_edit_back_button:
                finish();
                break;
        }
    }

    private String getSelectedImage() {
        String imagePath;
        if (selectedImagePath != null) {
            imagePath = selectedImagePath;
        } else {
            imagePath = realPhotoPath;
        }
        return imagePath;
    }

    private boolean isValidData() {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter the Name!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getApplicationContext(), "Please enter the Phone!!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mail)) {
            Toast.makeText(getApplicationContext(), "Please enter the Mail!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), ContactListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }
}
