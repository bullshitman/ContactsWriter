package com.bullshitman.superman64

import android.Manifest
import android.content.ContentProviderOperation
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var justDoItBtn: Button
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        justDoItBtn = findViewById(R.id.just_do_it)
        val list = listOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        )
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        managePermissions.checkPermissions()

        Log.d(TAG, "START:::  ${System.currentTimeMillis()}")
        justDoItBtn.setOnClickListener() {
            var startTime = System.currentTimeMillis()
            for (x in 1..5) {
                for (i in 0..999999) {
                    val ops = ArrayList<ContentProviderOperation>()
                    val rawContactInsertIndex: Int = ops.size
                    var temp = "+9936$x"
                    temp = when (i) {
                        in 1..9 -> temp + "00000$i"
                        in 10..99 -> temp + "00000$i"
                        in 100..999 -> temp + "0000$i"
                        in 1000..9999 -> temp + "000$i"
                        in 10000..99999 -> temp + "00$i"
                        in 100000..999999 -> temp + "0$i"
                        in 1000000..9999999 -> temp + "$i"
                        else -> temp + "0000000"
                    }

                    ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                            .withValue(RawContacts.ACCOUNT_TYPE, null)
                            .withValue(RawContacts.ACCOUNT_NAME, null).build())
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(
                                    ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                            .withValue(Phone.NUMBER, temp)
                            .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()) // Type of mobile number
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(StructuredName.DISPLAY_NAME, temp) // Name of the person
                            .build())
                    try {
                        val res = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)

                        Log.d(TAG, "Contact added $temp")
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                    }
                }
            }
            Log.d(TAG, "FINISH:::  ${(System.currentTimeMillis() - startTime)/1000} secs")
            Toast.makeText(this, "finished in  ${(System.currentTimeMillis() - startTime)/1000} secs", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode ->{
                val isPermissionsGranted = managePermissions
                        .processPermissionsResult(requestCode,permissions,grantResults)

                if(isPermissionsGranted){
                    // Do the task now

                }else{

                }
                return
            }
        }
    }

}