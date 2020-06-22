package com.bullshitman.superman64

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentProviderOperation
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var justDoItBtn: Button
    private lateinit var rangeFrom: EditText
    private lateinit var rangeFor: EditText
    private lateinit var infoText: TextView
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        justDoItBtn = findViewById(R.id.just_do_it)
        rangeFrom = findViewById(R.id.num_from)
        rangeFor = findViewById(R.id.num_for)
        infoText = findViewById(R.id.info_text)
        val list = listOf(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        )
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        managePermissions.checkPermissions()
        justDoItBtn.setOnClickListener() {
            if (rangeFrom.text.toString().equals("") && rangeFor.text.toString().equals("")) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Не заполнен интервал слева или справа")
                builder.setPositiveButton("Ок", null)
                val dialog = builder.create()
                dialog.show()
            } else {
                val startRange = rangeFrom.text.toString().toInt()
                val endRange = rangeFor.text.toString().toInt()
                Log.d(TAG, "START:::  ${System.currentTimeMillis()}")
                var startTime = System.currentTimeMillis()
                infoText.text = "?"
                for (i in startRange..endRange) {
                    val ops = ArrayList<ContentProviderOperation>()
                    val rawContactInsertIndex: Int = ops.size
                    var temp = "+9936$i"

                    ops.add(
                        ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                            .withValue(RawContacts.ACCOUNT_TYPE, null)
                            .withValue(RawContacts.ACCOUNT_NAME, null).build()
                    )
                    ops.add(
                        ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(
                                ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex
                            )
                            .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                            .withValue(Phone.NUMBER, temp)
                            .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()
                    ) // Type of mobile number
                    ops.add(
                        ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(
                                ContactsContract.Data.RAW_CONTACT_ID,
                                rawContactInsertIndex
                            )
                            .withValue(
                                ContactsContract.Data.MIMETYPE,
                                StructuredName.CONTENT_ITEM_TYPE
                            )
                            .withValue(StructuredName.DISPLAY_NAME, temp) // Name of the person
                            .build()
                    )
                    try {
                        val res = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                        infoText.text = temp
                        Log.d(TAG, "Contact added $temp")
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                    }
                }
                Log.d(TAG, "FINISH:::  ${(System.currentTimeMillis() - startTime) / 1000} secs")
                Toast.makeText(
                    this,
                    "finished in  ${(System.currentTimeMillis() - startTime) / 1000} secs",
                    Toast.LENGTH_LONG
                ).show()
                infoText.text = "ГОТОВО"


            }
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