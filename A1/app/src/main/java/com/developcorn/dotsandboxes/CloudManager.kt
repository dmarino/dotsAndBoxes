package com.developcorn.dotsandboxes

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


//singleton that manages the write and read info from the cloud
class CloudManager {

    companion object{

        val database = FirebaseFirestore.getInstance()

        //saves an item to the database
        fun createGame(playerName: String, context:Context) {

            var game: Game = Game("", mutableListOf());

            // Add a new document with a generated ID
            database.collection("games")
                .add(game)
                .addOnSuccessListener { documentReference ->
                    game.name = documentReference.id
                    AppData.game = game
                    AppData.playereNumber = 0
                    JoinGame(game.name, playerName, context, true)

                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "There was a problem", Toast.LENGTH_LONG).show()
                    Log.i("ERROR", "Error adding document", e)
                }
        }

        fun JoinGame(gameName: String, playerName: String, context: Context, isHost:Boolean){

            var player = Player(playerName)

            val docRef = database.collection("games").document(gameName)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {

                        val g = document.toObject(Game::class.java)
                        if (g != null) {

                            if(g.hasBegun || g.players.size==4){
                                Toast.makeText(context, "Sorry, the game already has 4 players or it has begun", Toast.LENGTH_LONG).show()

                            }
                            else{

                                AppData.game = g
                                AppData.game.name = gameName

                                var player = Player(playerName)
                                player.turn = g.players.size
                                AppData.playereNumber = player.turn
                                AppData.game.players.add(player)

                                SaveGame()

                                val i = Intent(context, PlayersActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                i.putExtra("isHost", isHost)
                                context.startActivity(i)

                            }

                        }
                    } else {
                        Toast.makeText(context, "There was no game with that name", Toast.LENGTH_LONG).show()
                        Log.d("", "No such document")
                    }
                }
                .addOnFailureListener { exception ->

                    Toast.makeText(context, "There was a problem", Toast.LENGTH_LONG).show()
                    Log.i("ERROR", "Error", exception)
                }


        }


        fun SaveGame(){

            Log.i("SAVE", AppData.game.name)

            if(AppData.game!=null){
                database.collection("games").document(AppData.game.name)
                    .set(AppData.game)
                    .addOnSuccessListener { Log.d("", "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w("", "Error writing document", e) }
            }

        }

    }
}