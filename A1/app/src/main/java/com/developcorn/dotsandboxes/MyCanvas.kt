package com.developcorn.dotsandboxes

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_players.*

private const val STROKE_WIDTH = 12f // has to be float

class MyCanvasView(context: Context) : View(context) {

    private var canDraw=true;

    //the distance of the stroke
    private val maxDistance=400f
    private var currentDistance=0f

    private val colorPoints = ResourcesCompat.getColor(resources, R.color.colorPaint3, null)

    // Path representing what's currently being drawn
    private var curPath = Path()

    private var paths = mutableListOf<Path>()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop


    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        //this is called so i know the widht and hight of the screen in the data object
        AppData.SetData(width, height);

        for(p in AppData.game.players){
            paths.add(Path())
        }

        //add listener for the database
        val ref = CloudManager.database.collection("games").document(AppData.game.name)
        ref.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("warning", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {

                val g = snapshot.toObject(Game::class.java)
                if (g != null) {
                    AppData.game = g

                    for(p:Player in g.players){

                        paths[p.turn].reset()

                        for (l in p.paths){

                            var index =0
                            paths[p.turn].moveTo(l.points[0].x, l.points[0].y)

                            for(point in l.points){

                                if(index < (l.points.size-1)){

                                    paths[p.turn].quadTo(point.x, point.y, l.points[index+1].x, l.points[index+1].y)
                                    index++
                                }
                            }

                        }
                    }
                }
                invalidate()

            } else {
                Log.d("", "Current data: null")
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw player's path and score
        paint.style = Paint.Style.STROKE

        var x =25f;

        for (p in AppData.game.players){

            paint.color = getColorPlayer(p.turn)
            canvas.drawPath(paths[p.turn], paint)

            paint.textSize = 100f;
            canvas.drawText( p.points.toString(), x , AppData.game.height -100, paint);

            x+=maxDistance-100;

        }

        // Draw any current squiggle
        if(canDraw){
            paint.color = getColorPlayer(AppData.playereNumber)
            canvas.drawPath(curPath, paint)
        }

        //draws circles
        paint.color = colorPoints
        paint.style = Paint.Style.FILL_AND_STROKE
        for(dotR:DotRow in AppData.game.dotRows){
            for(dot in dotR.dots) {
                canvas.drawCircle(dot.x, dot.y, AppData.game.radius - 20, paint);
            }
        }


        //draw boxes
        for(boxR:BoxRow in AppData.game.boxRows){
            for(box in boxR.boxes) {

                if(box.player !=-1){

                    paint.color =  getColorPlayer(AppData.game.players[box.player].turn)
                    paint.textSize = 100f;
                    canvas.drawText(AppData.game.players[box.player].name[0]+"", box.topL.x + 100, box.topL.y +100, paint);

                    paint.alpha = 80;
                    canvas.drawRect(box.topL.x, box.topL.y, box.botR.x, box.botR.y, paint)
                }

            }
        }
    }

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = colorPoints
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart() {

        //see if i'm touching a dot and its my turn
        canDraw = (AppData.IsTouching(motionTouchEventX, motionTouchEventY) && AppData.playereNumber == AppData.game.playerTurn)
        curPath.reset()


        //if i am move my position in the canvas to that point and set the distance of the current path to 0
        if(canDraw){

            curPath.moveTo(motionTouchEventX, motionTouchEventY)
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            currentDistance=0f
        }

    }

    private fun touchMove() {

        //if i indeed can draw then i draw the current pat my finger is following
        if(canDraw){

            val dx = Math.abs(motionTouchEventX - currentX)
            val dy = Math.abs(motionTouchEventY - currentY)

            //also find whats the distance in this movement and added to the current distance
            val delta = kotlin.math.sqrt((dx * dx) + (dy * dy))
            currentDistance +=delta

            //if its between the tolerance of movement and the current path is smaller than my max distance then i can
            //add the movement to the current path
            if ((dx >= touchTolerance || dy >= touchTolerance) && currentDistance<maxDistance) {
                // QuadTo() adds a quadratic bezier from the last point,
                // approaching control point (x1,y1), and ending at (x2,y2).
                curPath.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
                currentX = motionTouchEventX
                currentY = motionTouchEventY

                AppData.game.curLine.points.add(Point(currentX, currentY))
            }
        }

        //draw it again
        invalidate()
    }

    private fun touchUp() {

        //if i'm touching a point when i finish
        var isValid = AppData.IsTouching(currentX, currentY)

        if(isValid && canDraw){

            AppData.game.players[AppData.playereNumber].paths.add(AppData.game.curLine)

            if(AppData.game.playerTurn<(AppData.game.players.size-1)){
                AppData.game.playerTurn++
            }
            else{
                AppData.game.playerTurn=0
            }

            CloudManager.SaveGame()
        }

        // Reset all the stuff
        canDraw=false;
        curPath.reset()
        AppData.game.curLine = Line()
        invalidate()
        currentDistance=0f
    }

    fun getColorPlayer(index:Int):Int{

        if(index==0){
            return ResourcesCompat.getColor(resources, R.color.colorPaint1, null)
        }
        else if(index==1){
            return ResourcesCompat.getColor(resources, R.color.colorPaint2, null)
        }
        else if(index==2){
            return ResourcesCompat.getColor(resources, R.color.colorPaint3, null)
        }
        else{
            return ResourcesCompat.getColor(resources, R.color.colorPaint4, null)
        }
    }
}