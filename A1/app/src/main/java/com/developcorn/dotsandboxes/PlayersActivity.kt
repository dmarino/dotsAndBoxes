package com.developcorn.dotsandboxes

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_players.*


class PlayersActivity : AppCompatActivity() {


    lateinit var registration: ListenerRegistration;

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)

        roomName.text = AppData.game.name

        var isHost = intent.getBooleanExtra("isHost",false)

        if(!isHost){

            btnStart.visibility = View.INVISIBLE
            sizeSpinner.isEnabled = false
            sizeSpinner.isClickable = false

            waitText.text = "waiting for host to start the game"
        }


        PopulateSpinners();

        val ref = CloudManager.database.collection("games").document(AppData.game.name)
        registration= ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("warning", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                val g = snapshot.toObject(Game::class.java)
                if (g != null) {
                    AppData.game = g

                    playersList.removeAllViews()

                    for(p in g.players){

                        var tv = TextView(this)
                        tv.text = p.name

                        playersList.addView(tv)
                    }

                    if(g.hasBegun){

                        registration.remove()

                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                    }
                }
                Log.d("HERE", "DocumentSnapshot data: ${snapshot.data}")

            } else {
                Log.d("", "Current data: null")
            }
        }


    }

    fun PopulateSpinners(){

        val sizeStrings = resources.getStringArray(R.array.BoardSize)
        val spinnerS = findViewById<Spinner>(R.id.sizeSpinner)
        if (spinnerS != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizeStrings)
            spinnerS.adapter = adapter

            spinnerS.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                    val size = sizeStrings[position].split("x")

                    AppData.game.rows = size[0].toInt()
                    AppData.game.cols = size[1].toInt()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }


    fun StartGame(v: View){

        Log.i("rows", AppData.game.rows.toString())
        AppData.game.hasBegun=true
        CloudManager.SaveGame()
    }

}
