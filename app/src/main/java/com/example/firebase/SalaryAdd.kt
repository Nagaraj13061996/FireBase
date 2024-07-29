package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SalaryAdd : AppCompatActivity() {
    private lateinit var employeeNameEdt: EditText
    private lateinit var employeesalary: EditText
    private lateinit var employeexperience: EditText
    private lateinit var sendDatabtn: Button
    private lateinit var getData: Button

    // creating a variable for our
    // Firebase Database.
    private lateinit var firebaseDatabase: FirebaseDatabase

    // creating a variable for our Database
    // Reference for Firebase.
    var databaseReference: DatabaseReference? = null

    // creating a variable for
    // our object class
    var employeeInfo: SalaryInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_salary_add)



        employeeNameEdt = findViewById(R.id.idEdtEmployeeName)
        employeexperience = findViewById<EditText>(R.id.idEdtEmployeeExperience)
        employeesalary= findViewById<EditText>(R.id.idEdtEmployeeSalary)

        // below line is used to get the
        // instance of our FIrebase database.
        firebaseDatabase = FirebaseDatabase.getInstance()

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("SalaryInfo")

        // initializing our object
        // class variable.
        employeeInfo = SalaryInfo()
        sendDatabtn = findViewById<Button>(R.id.idBtnSendData)
        getData = findViewById<Button>(R.id.getDataBtn)

        getData.setOnClickListener {
            getdata()
        }

        // adding on click listener for our button.
        sendDatabtn.setOnClickListener(View.OnClickListener {
            // getting text from our edittext fields.
            val name = employeeNameEdt.getText().toString()
            val salary = employeesalary.getText().toString()
            val experience = employeexperience.getText().toString()

            // below line is for checking whether the
            // edittext fields are empty or not.
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(salary) && TextUtils.isEmpty(experience)) {
                // if the text fields are empty
                // then show the below message.
                Toast.makeText(this@SalaryAdd, "Please add some data.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // else call the method to add
                // data to our database.
                addDatatoFirebase(name, salary, experience)
            }
        })
    }

    private fun addDatatoFirebase(name: String, salary: String, experience: String) {
        // below 3 lines of code is used to set
        // data in our object class.
        employeeInfo!!.employeeName = name
        employeeInfo!!.salary = salary
        employeeInfo!!.experience = experience



        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val employeeRef = firebaseDatabase.getReference("Users/$userId/salaryInfo")

        // Generate a new unique ID for the employee
        val employeeId = employeeRef.push().key

        if (employeeId != null) {
            // Set the employee data at the generated ID
            employeeRef.child(employeeId).setValue(employeeInfo)
                .addOnSuccessListener {
                    Toast.makeText(this, "Employee data added successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Failed to add employee data: $error", Toast.LENGTH_SHORT).show()
                }
        }

//        val id: String? = databaseReference?.push()?.getKey()
//        if (id != null) {
//            employeeInfo!!.employeeInfoId = id
//        }

//        if (userId != null) {
//            val employeeId = databaseReference?.child(userId)?.child("SalaryInfo")?.push()?.key
//            val employeeInfo = SalaryInfo(employeeInfoId = employeeId ?: "", employeeName =  name,salary= salary,experience= experience)
//
//
////            databaseReference?.child(id)?.setValue((employeeInfo))
//            Log.i("TAG", "addDatatoFirebaseKeysss:$id ")
//            if (id != null) {
//                databaseReference?.child(userId)?.child("SalaryInfo")?.child(employeeId ?: "")
//                    ?.setValue(employeeInfo)
//
//                    ?.addOnSuccessListener {
//                        // After adding this data we are showing toast message
//                        Toast.makeText(this@SalaryAdd, "Data added successfully", Toast.LENGTH_SHORT)
//                            .show()
//                        val intent = Intent(this,Dashboard::class.java)
//                        startActivity(intent)
//                    }
//                    ?.addOnFailureListener { error ->
//                        // If the data is not added or there is an error
//                        Toast.makeText(
//                            this@SalaryAdd,
//                            "Failed to add data: $error",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//            }
//
//        }
    }


    private fun getdata() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            databaseReference?.child(userId)?.child("SalaryInfo")
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