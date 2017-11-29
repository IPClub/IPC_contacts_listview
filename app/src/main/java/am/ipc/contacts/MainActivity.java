package am.ipc.contacts;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.support.design.widget.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 111;
    ListView lv;
    ArrayAdapter<MyContact> adapter;
    List<MyContact> contacts = new ArrayList<>();
    ProgressDialog pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);
    }


    public void contacts(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            startProcess();
        }


    }


    public void startProcess(){
        new ContactTask().execute();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.WHITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{
                                                Manifest.permission.READ_CONTACTS},
                                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            }
                        }).show();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }else{
            startProcess();
        }
    }

    class ContactTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            pb = new ProgressDialog (MainActivity.this);
            pb.setTitle("Please wait");
            pb.setMessage("Loading all contacts");
            pb.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            contacts = new ArrayList<>();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    MyContact myContact = new MyContact();
                    myContact.setFullName(name);
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor cursor2 = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        cursor2.moveToFirst();
                        while (!cursor2.isAfterLast()) {
                            String phoneNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            myContact.setPhone(phoneNumber);
                            contacts.add(myContact);
                            cursor2.moveToNext();
                        }
                        cursor2.close();
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
            Collections.sort(contacts);
            Log.i("__IPC","size: "+contacts.size());
            for(MyContact x :contacts){
                Log.e("__IPC",x.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb.hide();
            adapter = new ArrayAdapter<MyContact>(MainActivity.this,android.R.layout.simple_list_item_1,contacts);
            lv.setAdapter(adapter);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startProcess();

                } else {

                    Snackbar.make(this.findViewById(android.R.id.content),
                            "Please Grant Permissions",
                            Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.WHITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{
                                                    Manifest.permission.READ_CONTACTS},
                                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                }
                            }).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
