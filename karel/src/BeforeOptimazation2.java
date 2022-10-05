/*
 * File: BlankKarel.java
 * ---------------------
 * This class is a blank one that you can change at will.
 */
import org.jetbrains.annotations.Contract;
import stanford.karel.*;

public class BeforeOptimazation2 extends SuperKarel {
    private int width = 1;
    private int height = 1;
    private int x = 1;
    private int y = 1;
    private int total_moves = 0;
    private String world_type;

    public void run(){
        setBeepersInBag(1000);

        exploreMapDimension();

        world_type = getWorldType();
        System.out.println("World type: " + world_type);

        if(world_type == "ODD_ODD")
            odd_odd();
        else if(world_type == "EVEN_ODD")
            even_odd();
        else if(world_type == "ODD_EVEN")
            odd_even();
        else if(world_type == "EVEN_EVEN")
            even_even();
        restart();
    }
    private void odd_odd(){
        //Karel now is standing in the top-left corner of the map and facing the west.
        int middle_x = (width+1)/2;
        int middle_y = (height+1)/2;
        goTo( middle_x, this.y,false);
        goTo(this.x, 1, true);
        goTo(width, middle_y, false);
        goTo(1, this.y, true);
    }
    private void even_odd(){
        //Even width and odd height
        //Karel now is standing in the top-left corner of the map and facing the west.
        int middle_x2 = (width/2);
        int middle_x1 = middle_x2+1;
        int middle_y = (height+1)/2;
        goTo(middle_x1, this.y, false);
        fillTwoVerticalLines(middle_x1, middle_x2);
        goTo(width, middle_y, false);
        goTo(1, this.y, true);
    }
    private void odd_even(){
        //Odd width and even height
        //Karel now is standing in the top-left corner of the map and facing the west.
        int middle_x = (width+1)/2;
        int middle_y1 = (height/2);
        int middle_y2 = middle_y1+1;
        goTo(middle_x, this.y, false);
        goTo(this.x, 1, true);
        goTo(width, middle_y1, false);
        fillTwoHorizontalLines(middle_y1, middle_y2);
    }
    private void even_even(){
        //even width and even height
        //Karel now is standing in the top-left corner of the map and facing the west.
        int middle_x2 = (width/2);
        int middle_x1 = middle_x2+1;
        int middle_y1 = (height/2);
        int middle_y2 = middle_y1+1;
        goTo(middle_x1, this.y, false);
        fillTwoVerticalLines(middle_x1, middle_x2);
        goTo(width, middle_y1, false);
        fillTwoHorizontalLines(middle_y1, middle_y2);
    }
    private void goTo(int finalX, int finalY, boolean putBeepers){
        if(finalX > this.x)
            faceDirection("EAST");
        else if(finalX < this.x)
            faceDirection("WEST");
        while(this.x != finalX) {
            if(putBeepers && noBeepersPresent())
                putBeeper();
            moveKarel();
        }
        if(putBeepers && noBeepersPresent())
            putBeeper();
        if(finalY > this.y)
            faceDirection("NORTH");
        else if(finalY < this.y)
            faceDirection("SOUTH");
        while(this.y != finalY) {
            if(putBeepers && noBeepersPresent())
                putBeeper();
            moveKarel();
        }
        if(putBeepers && noBeepersPresent())
            putBeeper();
        assert(this.x == finalX && this.y == finalY);
    }
    private void fillTwoVerticalLines(int x1, int x2){
        assert (x1 == this.x && x1 > x2);
        boolean toLeft = true;
        for(int i = 0 ; i<height-1 ; i++){
            if(beepersPresent()){
                goTo(this.x-1, this.y, false);
                continue;
            }
            if(toLeft)
                goTo(x2, y, true);
            else
                goTo(x1, y, true);
            goTo(this.x, this.y-1, false);
            toLeft = !toLeft;
        }
        if(toLeft)
            goTo(x2, y, true);
        else
            goTo(x1, y, true);
    }
    private void fillTwoHorizontalLines(int y1, int y2){
        assert(y1 == this.y && y1 < y2);
        boolean toUp = true;
        for(int i = 0 ; i<width-1 ; i++){
            if(beepersPresent()){
                goTo(this.x-1, this.y, false);
                continue;
            }
            if(toUp)
                goTo(x, y2, true);
            else
                goTo(x, y1, true);
            goTo(this.x-1, this.y, false);
            toUp = !toUp;
        }
        if(toUp)
            goTo(x, y2, true);
        else
            goTo(x, y1, true);
    }
    private void faceDirection(String direction){
        if(direction == "NORTH")
            while(!facingNorth())
                turnLeft();
        else if(direction == "SOUTH")
            while(!facingSouth())
                turnLeft();
        else if(direction == "EAST")
            while(!facingEast())
                turnLeft();
        else if(direction == "WEST")
            while(!facingWest())
                turnLeft();
    }

    private void moveKarel(){
        if(frontIsBlocked())
            return;
        move();
        total_moves++;
        if(facingSouth())
            y--;
        else if(facingNorth())
            y++;
        else if(facingEast())
            x++;
        else if(facingWest())
            x--;
    }
    private void exploreMapDimension(){
        while(frontIsClear()){
            moveKarel();
            width++;
        }
        turnLeft();
        while(frontIsClear()){
            moveKarel();
            height++;
        }
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);
    }

    private void restart(){
        goToStart();
        System.out.println("Karel has finished his mission!");
        System.out.println("Karel has moved " + total_moves + " times.");
        resetValues();
    }
    void resetValues(){
        this.x = 1;
        this.y = 1;
        this.total_moves = 0;
        this.height = 1;
        this.width = 1;
    }
    private void goToStart(){
        while(this.x != 1){
            while(notFacingWest())
                turnLeft();
            moveKarel();
        }
        while(this.y != 1){
            while(notFacingSouth())
                turnLeft();
            moveKarel();
        }
        while(notFacingEast())
            turnLeft();
    }
    private String getWorldType(){
        if(width < 2 && height<2)
            return "IMPOSSIBLE";
        if(width % 2 == 0 && height % 2 == 0)
            return  "EVEN_EVEN";
        else if(width % 2 != 0 && height % 2 == 0)
            return "ODD_EVEN";
        else if(width % 2 == 0 && height % 2 != 0)
            return "EVEN_ODD";
        else
            return  "ODD_ODD";
    }
}