package com.developcorn.dotsandboxes

import android.graphics.Path
import android.util.Log

//path classes
data class  Point(var x: Float, var y: Float){

    constructor():this(0f,0f){

    }
}

data class  Line (var points: MutableList<Point>){

    constructor():this(mutableListOf()){

    }
}

data class Player(var name:String, var turn: Int, var points:Int, var paths: MutableList<Line>){

    constructor(name: String): this(name,0,0, mutableListOf()){
    }

    constructor():this("",0,0, mutableListOf()){

    }

}

//game classes
data class Game(var name: String, var players: MutableList<Player>){

    var hasBegun = false

    var curLine = Line()

    var dotRows: MutableList<DotRow> = mutableListOf()
    var boxRows: MutableList<BoxRow> = mutableListOf()

    var playerTurn =0

    val radius = 45f;

    var cols = 4;
    var rows = 4;

    var width=0f;
    var height =0f;

    constructor():this("", mutableListOf()){

    }

}


data class  DotRow(var dots:MutableList<Dot>){

    constructor():this(mutableListOf()){

    }

}

data class Dot(var x:Float, var y:Float){

    constructor():this(0f,0f){

    }
}

data class  BoxRow(var boxes:MutableList<Box>){

    constructor():this(mutableListOf()){

    }

}

data class Box(var topL: Dot, var topR: Dot, var botL: Dot, var botR:Dot , var player:Int){

    var Top: Boolean=false
    var Bottom: Boolean =false
    var Right: Boolean = false
    var Left: Boolean = false


    fun CheckIfWon(p: Int){

        if(Top && Bottom && Right && Left){

            player=p
        }
    }

    constructor():this(Dot(), Dot(), Dot(), Dot(), -1){

    }

}
