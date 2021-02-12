package com.example.todo.statistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.todo.R
import com.example.todo.data.Result
import com.example.todo.data.Task
import com.example.todo.data.TasksRemoteDataSource
import kotlinx.coroutines.*

class StatisticsActivity : AppCompatActivity() {

    lateinit var dataList : List<Task>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        //TODO get list from data source and show statics from getActiveAndCompletedStats function

        val job = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + job)
        uiScope.launch{
            val list =TasksRemoteDataSource.getTasks()
            if (list is Result.Success){
                dataList=list.data
                showData(dataList)
                findViewById<ProgressBar>(R.id.progress_circular).visibility= INVISIBLE
            }else{
                Toast.makeText(applicationContext,"some error accurate", Toast.LENGTH_SHORT).show()

            }
        }

        }

    private fun showData(dataList: List<Task>) {
        var statusResult= getActiveAndCompletedStats(dataList)
        var uncompeteted =statusResult.activeTasksPercent.toString()
        var Compeleted=statusResult.completedTasksPercent.toString()
        findViewById<TextView>(R.id.statistics_no_tasks).setText("Uncompleted tasks percent = $uncompeteted")
        findViewById<TextView>(R.id.stats_active_text).setText("Completed tasks percent = $Compeleted")
    }
}



