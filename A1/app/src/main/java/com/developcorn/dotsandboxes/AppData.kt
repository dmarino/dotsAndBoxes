package com.developcorn.dotsandboxes


//singleton to store the data of the app
class AppData{
    companion object DataHolder
    {

        lateinit var game: Game;
        var playereNumber:Int = -1;

        //var dots: MutableList<MutableList<Dot>> = mutableListOf()
        //var boxes: MutableList<MutableList<Box>> = mutableListOf()

        var firstDot: Dot? = null

        fun SetData(pWidth:Int, pHeight:Int){


            game.width= pWidth.toFloat();
            game.height= pHeight.toFloat();

            //right now i'm just eyebowling the distance but
            //probably should use the width and height to distribute the distances better
            var x=150f
            var y=200f

            //creates the dots and adds them to the array
            for(i in 0..game.rows){

                //create the dotrow
                var dotsR = DotRow()

                for(j in 0..game.cols){

                    var dot = Dot(x,y)
                    dotsR.dots.add(dot)

                    x+=200
                }

                game.dotRows.add(dotsR);

                y+=200
                x=150f
            }

            //creates the boxes and adds them to the array
            var boxRows = game.rows-1;
            for(i in 0 .. boxRows){

                //create the
                var boxR = BoxRow()

                var boxCols = game.cols-1
                for(j in 0.. boxCols){

                    var box = Box( game.dotRows[i].dots[j], game.dotRows[i].dots[j+1], game.dotRows[i+1].dots[j], game.dotRows[i+1].dots[j+1],-1)
                    boxR.boxes.add(box)
                }

                game.boxRows.add(boxR);

            }

            CloudManager.SaveGame()
        }


        //returns true if the point x,y is in one of the dots
        fun IsTouching(x:Float, y:Float): Boolean{

            for(i in 0..game.rows){

                for(j in 0..game.cols){

                    var dot = game.dotRows[i].dots[j]
                    //if its in x range
                    if(x >= dot.x- game.radius && x<=dot.x+ game.radius){

                        //if its in y range
                        if(y>= dot.y- game.radius && y<=dot.y+ game.radius){

                            if(firstDot == null){

                                firstDot = dot
                                return true;
                            }
                            else{

                                //if its a vertical line
                                if(firstDot!!.x == dot.x){

                                    //find the row
                                    var row = 0
                                    if(firstDot!!.y < dot.y){row=i-1}
                                    else{row = i}

                                    if(!game.boxRows[row].boxes[j].Left){
                                        game.boxRows[row].boxes[j].Left = true;
                                        game.boxRows[row].boxes[j].CheckIfWon(game.playerTurn)

                                    }

                                    if(!game.boxRows[row].boxes[j-1].Right){
                                        game.boxRows[row].boxes[j-1].Right = true;
                                        game.boxRows[row].boxes[j-1].CheckIfWon(game.playerTurn)

                                    }

                                    return true;

                                }
                                else if(firstDot!!.y == dot.y){


                                    //find the col
                                    var col = 0
                                    if(firstDot!!.x < dot.x){col=j-1}
                                    else{col = j}

                                    if(!game.boxRows[i].boxes[col].Top){
                                        game.boxRows[i].boxes[col].Top = true;
                                        game.boxRows[i].boxes[col].CheckIfWon(game.playerTurn)
                                    }

                                    if(!game.boxRows[i-1].boxes[col].Bottom){
                                        game.boxRows[i-1].boxes[col].Bottom = true;
                                        game.boxRows[i-1].boxes[col].CheckIfWon(game.playerTurn)

                                    }

                                    return true;
                                }

                                firstDot=null;
                            }
                            return false;
                        }
                    }
                }
            }

            return false;
        }

        fun CheckIfGameEnd():Boolean{

            for(boxR:BoxRow in game.boxRows){
                for(box in boxR.boxes) {
                    if(box.player ==-1){
                        return false
                    }

                }
            }
            return true;
        }
    }
}