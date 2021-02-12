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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.addedittask.AddEditTaskActivity
import com.example.todo.data.Result
import com.example.todo.data.Task
import com.example.todo.data.TasksRemoteDataSource
import com.example.todo.statistics.StatisticsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*


/**
 * Main activity for the todoapp. Holds the Navigation Host Fragment and the Drawer, Toolbar, etc.
 */
class TasksActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TasksAdapter
    var dataList : List<Task> = emptyList()
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigationDrawer()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        recyclerView = findViewById(R.id.tasks_list)
        val lay : RecyclerView.LayoutManager= LinearLayoutManager(this)
        recyclerView.layoutManager=lay

        adapter= TasksAdapter(dataList,this)
        recyclerView.adapter=adapter

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            when (menuItem.itemId) {
                R.id.statistics_fragment_dest -> {
                    val intent = Intent(this, StatisticsActivity::class.java)
                    startActivity(intent)
                    //TODO start StatisticsActivity
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        //TODO setup Float action button click
        findViewById<FloatingActionButton>(R.id.add_task_fab).setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        })
        //TODO loadTasks from TaskRemoteDataSource
        //loadTasks()
    }

    override fun onStart() {
        super.onStart()
        loadTasks()
    }
    private fun loadTasks() {
        uiScope.launch {
            val list =TasksRemoteDataSource.getTasks()
            if (list is Result.Success){
                dataList=list.data
                findViewById<ProgressBar>(R.id.progress_circular).visibility= View.INVISIBLE
                showData(dataList)
                /// check if list is empty
            }else{
                Toast.makeText(applicationContext,"some error accurate", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    //TODO setup recyclerView adapter
    private fun showData(dataList: List<Task>) {
        adapter.tasksList=dataList
        adapter.notifyDataSetChanged()
        // preformance
    }


    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout))
            .apply {
                setStatusBarBackground(R.color.colorPrimaryDark)
            }
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_clear -> {
                //not parsing glopal
                deleteCompeleted(dataList)
                dataList =getunCompletedTasks(dataList)
                showData(dataList)
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                loadTasks()
                true
            }
            else -> false
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
        return true
    }


    private fun showFilteringPopUpMenu() {
        val view = findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.active -> {
                        val list =getunCompletedTasks(dataList)
                        showData(list)
                    }//TODO filter active
                    R.id.completed ->
                    {
                        val list=getCompleteTasks(dataList)
                        showData(list)
                    }//TODO filter completed
                    else ->
                    {
                       showData(dataList)
                    } //TODO see all
                }
                true
            }
            show()
        }
    }

    private fun deleteCompeleted(list: List<Task>) {
        uiScope.launch {
            for (data in list){
                if (data.isCompleted)
                    TasksRemoteDataSource.deleteTask(data.id)
            }
        }
    }

    private fun getCompleteTasks(dataList: List<Task>) : List<Task> {
        var compelete: MutableList<Task> = mutableListOf()
        for (data in dataList){
            if (data.isCompleted)
                compelete.add(data)
        }
        return compelete
    }
    private fun getunCompletedTasks(dataList: List<Task>) : List<Task> {
         var uncompeleteed: MutableList<Task> = mutableListOf()
        for (data in dataList){
            if (!data.isCompleted)
                uncompeleteed.add(data)
        }
        return uncompeleteed
    }

}
