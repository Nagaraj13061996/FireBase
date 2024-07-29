package com.example.firebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.EmployeeItemBinding

class MainAdapter(
    private var employees: List<EmployeeInfo>,
    private val onItemClick: (EmployeeInfo) -> Unit,
    private val onDeleteClick: (EmployeeInfo) -> Unit,
) : RecyclerView.Adapter<EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = EmployeeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employees[position]
        holder.bind(employee, onItemClick,onDeleteClick)
    }

    override fun getItemCount(): Int = employees.size

    fun updateData(newEmployees: List<EmployeeInfo>) {
        val diffCallback = EmployeeDiffCallback(employees, newEmployees)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        employees = newEmployees
        diffResult.dispatchUpdatesTo(this)
    }
}

class EmployeeViewHolder(private val binding: EmployeeItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        employee: EmployeeInfo,
        onItemClick: (EmployeeInfo) -> Unit,
        onDeleteClick: (EmployeeInfo) -> Unit
    ) {
        binding.employeeName.text = employee.employeeName
        binding.employeePhone.text = employee.employeeContactNumber
        binding.employeeAddress.text = employee.employeeAddress

        // Handle item click
        binding.edit.setOnClickListener {
            onItemClick(employee)
        }
        binding.delete.setOnClickListener {
            onDeleteClick(employee)
        }
    }
}

class EmployeeDiffCallback(
    private val oldList: List<EmployeeInfo>,
    private val newList: List<EmployeeInfo>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].employeeId == newList[newItemPosition].employeeId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
