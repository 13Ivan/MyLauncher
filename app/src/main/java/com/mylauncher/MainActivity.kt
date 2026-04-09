package com.mylauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDate: TextView
    private val handler = Handler(Looper.getMainLooper())

    private val updateTime = object : Runnable {
        override fun run() {
            val now = Date()
            tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            val days = arrayOf("Воскресенье","Понедельник","Вторник","Среда","Четверг","Пятница","Суббота")
            val months = arrayOf("января","февраля","марта","апреля","мая","июня","июля","августа","сентября","октября","ноября","декабря")
            val cal = Calendar.getInstance()
            tvDate.text = "${days[cal.get(Calendar.DAY_OF_WEEK)-1]}, ${cal.get(Calendar.DAY_OF_MONTH)} ${months[cal.get(Calendar.MONTH)]}".uppercase()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTime = findViewById(R.id.tvTime)
        tvDate = findViewById(R.id.tvDate)

        handler.post(updateTime)
        loadApps()
    }

    private fun loadApps() {
        val grid = findViewById<GridView>(R.id.appGrid)
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .filter { it.activityInfo.packageName != packageName }

        val labels = apps.map { it.loadLabel(pm).toString() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, labels)
        grid.adapter = adapter

        grid.setOnItemClickListener { _, _, pos, _ ->
            val app = apps[pos]
            val launchIntent = pm.getLaunchIntentForPackage(app.activityInfo.packageName)
            launchIntent?.let { startActivity(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTime)
    }
}
