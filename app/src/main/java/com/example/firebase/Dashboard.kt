package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var adapter: MainAdapter
    var databaseReference: DatabaseReference? = null
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("EmployeeInfo")

        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter
        adapter = MainAdapter(emptyList(), onItemClick = { employee ->
            // Handle item click
            updateEmployeeData(employee)
        },
        onDeleteClick = { employee ->
            // Handle delete click
            deleteEmployeeData(employee)
        })
        binding.recyclerview.adapter = adapter

        // Load or update data
//        loadEmployeeData()
        getdata()

        binding.add.setOnClickListener {
            val intent=Intent(this,AddData::class.java)
            startActivity(intent)
        }
    }

    private fun deleteEmployeeData(employee: EmployeeInfo) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val employeeRef = databaseReference?.child(userId)?.child("EmployeeInfo")?.child(employee.employeeId!!)

            employeeRef?.removeValue()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { error ->
                    Toast.makeText(this, "Failed to delete data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    private fun deleteEmployee(employee: EmployeeInfo) {
        employee.employeeId?.let {
            databaseReference?.child(it)?.removeValue()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Delete Successfully", Toast.LENGTH_SHORT).show()
                    // Successfully deleted
                }
                ?.addOnFailureListener {
                    // Handle failure
                    Toast.makeText(this, "Delete Failure", Toast.LENGTH_SHORT).show()

                }
        }
    }

    private fun updateEmployeeData(employee: EmployeeInfo) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

//        val employeeRef = employee.employeeId?.let { databaseReference?.child(it) }
        val nemployee= EmployeeInfo(employeeId = employee.employeeId)
        val employeeIdToUpdate = "employee1Id" // Replace with the actual employee ID
        val updatedData = mapOf(
            "employeeName" to "John Smith",
            "employeeContactNumber" to "1112223333",
            "employeeAddress" to "789 New St"
        )

        val employeeRef = userId?.let { databaseReference?.child(it)?.child("EmployeeInfo")?.child(employee.employeeId!!) }


        employeeRef?.updateChildren(updatedData)?.addOnSuccessListener {
                // Handle success
                Toast.makeText(this, "Employee data updated successfully", Toast.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener { error ->
                // Handle failure
                Toast.makeText(this, "Failed to update data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadEmployeeData() {
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val employeeList = mutableListOf<EmployeeInfo>()
                for (snapshot in dataSnapshot.children) {
                    val employee = snapshot.getValue(EmployeeInfo::class.java)
                    if (employee != null) {
                        employeeList.add(employee)
                    }
                }
                // Use the list

                adapter.updateData(employeeList)
                Log.d("TAG", "Employee List: $employeeList")

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
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
                        adapter.updateData(employeeList)

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