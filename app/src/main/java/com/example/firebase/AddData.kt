package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddData : AppCompatActivity() {
    private lateinit var employeeNameEdt: EditText
    private lateinit var employeePhoneEdt: EditText
    private lateinit var employeeAddressEdt: EditText
    private lateinit var sendDatabtn: Button
    private lateinit var logout: Button
    lateinit var auth: FirebaseAuth

    // creating a variable for our
    // Firebase Database.
    private lateinit var firebaseDatabase: FirebaseDatabase

    // creating a variable for our Database
    // Reference for Firebase.
    var databaseReference: DatabaseReference? = null

    // creating a variable for
    // our object class
    var employeeInfo: EmployeeInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)
        // initializing our edittext and button
        employeeNameEdt = findViewById(R.id.idEdtEmployeeName)
        employeePhoneEdt = findViewById<EditText>(R.id.idEdtEmployeePhoneNumber)
        employeeAddressEdt = findViewById<EditText>(R.id.idEdtEmployeeAddress)

        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("EmployeeInfo")

        // initializing our object
        // class variable.
        employeeInfo = EmployeeInfo()
        sendDatabtn = findViewById<Button>(R.id.idBtnSendData)
        logout = findViewById<Button>(R.id.logout)
        auth = FirebaseAuth.getInstance()



        // Check if the user is logged in
        val currentUser = auth.currentUser
        Log.i("AddData", "currentUserLogged:$currentUser ")
        logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        getData.setOnClickListener {
//            val intent = Intent(this,SalaryAdd::class.java)
//            startActivity(intent)
//        }

        // adding on click listener for our button.
        sendDatabtn.setOnClickListener(View.OnClickListener {
            // getting text from our edittext fields.
            val name = employeeNameEdt.getText().toString()
            val phone = employeePhoneEdt.getText().toString()
            val address = employeeAddressEdt.getText().toString()

            // below line is for checking whether the
            // edittext fields are empty or not.
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(address)) {
                // if the text fields are empty
                // then show the below message.
                Toast.makeText(this@AddData, "Please add some data.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                employeeInfo!!.employeeName = name
                employeeInfo!!.employeeContactNumber = phone
                employeeInfo!!.employeeAddress = address

                val id: String? = databaseReference?.push()?.getKey()
                employeeInfo!!.employeeId = id
                addEmployeeInfoForUser(employeeInfo!!)
            }
        })
    }

    private fun addDatatoFirebase(name: String, phone: String, address: String) {
        // below 3 lines of code is used to set
        // data in our object class.
        employeeInfo!!.employeeName = name
        employeeInfo!!.employeeContactNumber = phone
        employeeInfo!!.employeeAddress = address

        // we are use add value event listener method
        // which is called with database reference.
        val id: String? = databaseReference?.push()?.getKey()
        employeeInfo!!.employeeId = id

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val employeeId = databaseReference?.child(userId)?.child("EmployeeInfo")?.push()?.key
            val employeeInfo = EmployeeInfo(employeeId ?: "", name, phone, address)


//            databaseReference?.child(id)?.setValue((employeeInfo))
            Log.i("TAG", "addDatatoFirebaseKeysss:$id ")
            if (id != null) {
                databaseReference?.child(userId)?.child("EmployeeInfo")?.child(employeeId ?: "")
                    ?.setValue(employeeInfo)

                    ?.addOnSuccessListener {
                        // After adding this data we are showing toast message
                        Toast.makeText(this@AddData, "Data added successfully", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this,Dashboard::class.java)
                        startActivity(intent)
                    }
                    ?.addOnFailureListener { error ->
                        // If the data is not added or there is an error
                        Toast.makeText(
                            this@AddData,
                            "Failed to add data: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

        }
    }




    private fun addEmployeeInfoForUser( employee: EmployeeInfo) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val employeeRef = firebaseDatabase.getReference("Users/$userId/EmployeeInfo")

        // Generate a new unique ID for the employee
        val employeeId = employeeRef.push().key

        // Check if the employeeId is not null
        if (employeeId != null) {
            // Set the employee data at the generated ID
            employeeRef.child(employeeId).setValue(employee)
                .addOnSuccessListener {
                    Toast.makeText(this, "Employee data added successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Failed to add employee data: $error", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun getdata() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            databaseReference?.child(userId)?.child("EmployeeInfo")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val employeeList = mutableListOf<EmployeeInfo>()
                        for (snapshot in dataSnapshot.children) {
                            val employee = snapshot.getValue(EmployeeInfo::class.java)
                            if (employee != null) {
                                employeeList.add(employee)
                            }
                        }
                        Log.d("TAG", "Employee List: $employeeList")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("TAG", "Failed to read value.", error.toException())
                    }
                })
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}


