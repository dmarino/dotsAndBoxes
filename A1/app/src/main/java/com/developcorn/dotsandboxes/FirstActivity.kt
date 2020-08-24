package com.developcorn.dotsandboxes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
    }

    fun CreateGame(v: View){

        showPlayerDialog("",true);
    }


    fun JoinGame(v: View){

        var builder = AlertDialog.Builder(this)

        builder.setTitle("Game Name")
        builder.setMessage("Enter the name of the Game")

        val myInput = EditText(this)
        myInput.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(myInput)

        builder.setPositiveButton("Save"){
                dialog, which ->

            val gameName: String = myInput.text.toString()

            if(gameName !=""){

                showPlayerDialog(gameName, false)
            }

        }

        builder.setNegativeButton("Cancel"){
                dialog, which ->

        }

        val dialog : AlertDialog = builder.create()
        dialog.show()

    }


    fun showPlayerDialog( gameName: String, isNew: Boolean){


        var builder = AlertDialog.Builder(this)

        builder.setTitle("Name")
        builder.setMessage("Enter your name")

        val myInput = EditText(this)
        myInput.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(myInput)


        builder.setPositiveButton("Ok"){
                dialog, which ->

            val playerName: String = myInput.text.toString()
            if(playerName !=""){

                if(isNew){

                    CloudManager.createGame(playerName, this)
                }
                else{

                    CloudManager.JoinGame(gameName, playerName, this, false)
                }

            }

        }

        builder.setNegativeButton("Cancel"){
                dialog, which ->

        }

        val dialog : AlertDialog = builder.create()
        dialog.show()


    }

}
