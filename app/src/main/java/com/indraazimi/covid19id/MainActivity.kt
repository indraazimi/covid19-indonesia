/*
 * Copyright (c) 2021 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk kelas Pemrograman untuk Perangkat Bergerak 2.
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.covid19id

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.indraazimi.covid19id.databinding.ActivityMainBinding
import com.indraazimi.covid19id.widget.WidgetUtils
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var myAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding.chart) {
            setNoDataText(getString(R.string.belum_ada_data))
            description.text = ""
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false

            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.setDrawInside(false)
        }

        myAdapter = MainAdapter()
        with(binding.recyclerView) {
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            setHasFixedSize(true)
            adapter = myAdapter
        }

        // Berfungsi agar label yang tampil di sumbu X menjadi tanggal
        val formatter = SimpleDateFormat("dd MMM", Locale("ID", "id"))
        binding.chart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val pos = value.toInt() - 1
                val isValidPosition = pos >= 0 && pos < myAdapter.itemCount
                return if (isValidPosition) formatter.format(myAdapter.getDate(pos)) else ""
            }
        }

        // Berfungsi agar ketika grafik di-klik oleh pengguna,
        // RecyclerView akan scroll menampilkan data yang sesuai
        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(entry: Entry?, highlight: Highlight) {
                val pos = myAdapter.itemCount - highlight.x.toInt()
                binding.recyclerView.scrollToPosition(pos)
            }

            override fun onNothingSelected() {}
        })

        viewModel.getData().observe(this, {
            myAdapter.setData(it)
            WidgetUtils.saveData(prefs, it.last())
            WidgetUtils.updateUI(this)
        })
        viewModel.getEntries().observe(this, { updateChart(it) })
        viewModel.getStatus().observe(this, { updateProgress(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_maps) {
            startActivity(Intent(this, MapsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateChart(entries: List<Entry>) {
        val dataset = LineDataSet(entries, getString(R.string.jumlah_kasus_positif))
        dataset.color = ContextCompat.getColor(this, R.color.purple_500)
        dataset.fillColor = dataset.color
        dataset.setDrawFilled(true)
        dataset.setDrawCircles(false)

        binding.chart.data = LineData(dataset)
        binding.chart.invalidate()
    }

    private fun updateProgress(status: ApiStatus) {
        when (status) {
            ApiStatus.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            ApiStatus.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
            }
            ApiStatus.FAILED -> {
                binding.progressBar.visibility = View.GONE
                binding.errorTextView.visibility = View.VISIBLE
            }
        }
    }
}