package com.developcorn.dotsandboxes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_main)

        //basically uses the canvas as the activity
        val myCanvasView = MyCanvasView(this)
        myCanvasView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)
        setContentView(myCanvasView)



        //things to do that i currently dont have time to do...
        //check that i dont get back to the first point that i touched
        //check that i don't redraw a line
        //basically all the logic tbh... but i don0t know what the best way to store the data
        //clean the code so is scalable and not only two players hardcoded
        //think about the zoom

    }
}
