/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.todo.tasks

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.addedittask.AddEditTaskActivity
import com.example.todo.data.Result
import com.example.todo.data.Task
import com.example.todo.data.TasksRemoteDataSource
import com.example.todo.taskdetail.TaskDetailsActivity
import com.example.todo.utils.TASK_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Adapter for the task list. Has a reference to the [TasksViewModel] to send actions back to it.
 */
class TasksAdapter(var tasksList: List<Task>,var context :Context) :
    RecyclerView.Adapter<TasksAdapter.ViewHolder>() {
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val dataTask =tasksList.get(position)

        holder.textView.text=dataTask.titleForList
        holder.checkBox.setOnClickListener(View.OnClickListener {
            if (holder.checkBox.isChecked){
                markTaskIsCompleted(dataTask.id)
                dataTask.isCompleted=true
            }else{
                markTaskIsCompleted(dataTask.id)
                dataTask.isCompleted=false
            }
        })

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context.applicationContext, TaskDetailsActivity::class.java)
            intent.putExtra("TITLE",dataTask.title)
            intent.putExtra("DESCRIPTION",dataTask.description)

            // only id
            intent.putExtra("ID",dataTask.id)
            context.startActivity(intent)
        })
    }

    private fun markTaskIsCompleted(position: String) {
        uiScope.launch {
            TasksRemoteDataSource.completeTask(position)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return ViewHolder(itemView)
    }

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var textView: TextView = view.findViewById(R.id.task_item_title_text)
         var checkBox: CheckBox = view.findViewById(R.id.task_item_complete_checkbox)


    }

    override fun getItemCount(): Int {
        return tasksList.size
    }
}
