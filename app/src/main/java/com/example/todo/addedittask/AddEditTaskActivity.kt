package com.example.todo.addedittask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import com.example.todo.R
import com.example.todo.data.Result
import com.example.todo.data.Task
import com.example.todo.data.TasksRemoteDataSource
import com.example.todo.tasks.TasksActivity
import com.example.todo.utils.TASK_ID
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class AddEditTaskActivity : AppCompatActivity() {
    private lateinit var titleView: TextView
    private lateinit var descrption: TextView
    private lateinit var task : Task
    var id : String?=null
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)
        titleView = findViewById(R.id.add_task_title_edit_text)
        descrption = findViewById(R.id.add_task_description_edit_text)
         id  = intent.getStringExtra("ID")
        findViewById<ProgressBar>(R.id.progress_circular).visibility=View.INVISIBLE

        id?.let {
            fillData(it)
            Toast.makeText(applicationContext,id, Toast.LENGTH_SHORT).show()
        }
        //TODO get task details
        //TODO setup float button action
        //TODO setup validation
        findViewById<FloatingActionButton>(R.id.save_task_fab).setOnClickListener(View.OnClickListener {
            if (titleView.text!=""&&descrption.text!=""){
                saveData(titleView.text.toString(),descrption.text.toString())
            }else{
                Toast.makeText(applicationContext,"some missed data", Toast.LENGTH_SHORT).show()

            }
        })

    }

    private fun saveData(text: String, text1: String) {

        uiScope.launch{
            findViewById<ProgressBar>(R.id.progress_circular).visibility=View.VISIBLE
            id?.let {
                TasksRemoteDataSource.deleteTask(it)
            }
            TasksRemoteDataSource.saveTask(Task(text,text1))

            finish()

        }
    }

    private fun fillData(id : String) {
///kotlin
        uiScope.launch{
            findViewById<ProgressBar>(R.id.progress_circular).visibility=View.VISIBLE

            val t =TasksRemoteDataSource.getTask(id)
            if (t is Result.Success){
                task=t.data
                titleView.setText(task.title)
                descrption.setText(task.description)
                findViewById<ProgressBar>(R.id.progress_circular).visibility=View.INVISIBLE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}