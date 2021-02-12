package com.example.todo.taskdetail

import android.content.Intent
import android.icu.text.CaseMap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.todo.R
import com.example.todo.addedittask.AddEditTaskActivity
import com.example.todo.data.TasksRemoteDataSource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TaskDetailsActivity : AppCompatActivity() {
    lateinit var id: String
    lateinit var tTextView: TextView
    lateinit var dTextView: TextView
    lateinit var title:String
    lateinit var desc:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)
        //TODO get task details
        title = intent.getStringExtra("TITLE").toString()
         desc = intent.getStringExtra("DESCRIPTION").toString()
        id = intent.getStringExtra("ID").toString()
        tTextView=findViewById(R.id.task_detail_title_text)
        dTextView=findViewById(R.id.task_detail_description_text)

        //TODO setup float button action
        findViewById<FloatingActionButton>(R.id.edit_task_fab).setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("ID",id)
            startActivity(intent)
        })
    }

    override fun onStart() {
        super.onStart()
        tTextView.setText(title)
        dTextView.setText(desc)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete ->{
                deleteTaskA(id)
                finish()
                true
            }//TODO delete Item
            else -> false
        }

    }

    private fun deleteTaskA(id : String) {
        val job = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + job)
        uiScope.launch{
            TasksRemoteDataSource.deleteTask(id)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
        return true
    }


}