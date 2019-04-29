package com.ebookfrenzy.finalcolorpicker

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    var namedColors = arrayListOf<CharSequence>()
    var colors:IntArray = intArrayOf(0,0,0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        setSupportActionBar(toolbar)

        /*
        var actionBar = getSupportActionBar()
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setIcon(R.mipmap.ic_launcher)
        actionBar?.setDisplayShowTitleEnabled(false)
        */
        setupSeekBarListener(redSeekBar, 0)
        setupSeekBarListener(greenSeekBar, 1)
        setupSeekBarListener(blueSeekBar, 2)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setLogo(R.mipmap.ic_launcher)
        supportActionBar?.setDisplayUseLogoEnabled(true)
    }

    private fun setupSeekBarListener(seekBar: SeekBar, idx:Int){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                colors[idx] = progress
                seekBarProgress()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun seekBarProgress(){
        redProgress.text = colors[0].toString()
        greenProgress.text = colors[1].toString()
        blueProgress.text = colors[2].toString()
        colorView.setBackgroundColor(Color.rgb(colors[0], colors[1], colors[2]))
    }

    private fun saveAlertbox(){
        val f = openFileOutput("dataStorage.txt", Context.MODE_APPEND)
        val fOut =  OutputStreamWriter(f)
        var colorName: EditText = EditText(this)
        var str:String = colors[0].toString() + " " + colors[1].toString() + " " +
                colors[2].toString()
        val simpleAlert = AlertDialog.Builder(this@MainActivity).create()
        simpleAlert.setTitle("Save Color")
        simpleAlert.setMessage(str)
        simpleAlert.setView(colorName)

        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Save", {
            dialogInterface, i ->
            var result = colorName.text.toString()
            val line = "${colors[0]} ${colors[1]} ${colors[2]} $result\n"
            fOut.append(line)
            fOut.close()
            println(result)
            Toast.makeText(applicationContext, "Saved to internal Storage",
                    Toast.LENGTH_SHORT).show()
        })

        simpleAlert.show()
    }

    private fun showColorList() {
        readValues()
        selector("Saved Colors",namedColors, {dialogInterface, i ->
            pickColor(i)
            var name = namedColors[i].split( " ")
            toast("Your color is ${name[3]}!") })
    }


    private fun pickColor(i: Int) {
        val chosen = namedColors[i].split(" ")
        var r:Int = chosen[0].toInt()
        var g:Int = chosen[1].toInt()
        var b:Int = chosen[2].toInt()
        println(r)
        println(g)
        println(b)
        colors[0] = r
        colors[1] = g
        colors[2] = b
        redSeekBar.progress = r
        greenSeekBar.progress = g
        blueSeekBar.progress = b
        seekBarProgress()
    }

    fun readValues() {
        try {
            val file = openFileInput("dataStorage.txt")
            val br = file.bufferedReader()
            namedColors.clear()
            br.forEachLine {
                namedColors.add(it)
            }
            file.close()
        } catch (ex:Exception){
            print(ex.message)}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_help -> {

                Toast.makeText(applicationContext, "This Application lets you pick a color.",
                        Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.save_color -> {
                saveAlertbox()

                return true}
            R.id.recall_color ->{
                showColorList()
                return true}
            else -> super.onOptionsItemSelected(item)
        }
    }
}