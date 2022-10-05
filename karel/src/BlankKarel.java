/*
 * File: BlankKarel.java
 * ---------------------
 * This class is a blank one that you can change at will.
 */
import acm.util.ErrorException;
import org.jetbrains.annotations.NotNull;
import stanford.karel.*;

public class BlankKarel extends SuperKarel {
    private int width = 1;
    private int height = 1;
    private int x = 1;
    private int y = 1;
    private int total_moves = 0;

    public void run(){
        setBeepersInBag(1000);
        exploreMapWidth();
        System.out.println("Width: " + width);

        boolean isVertical = isVerticalMap();
        if(isVertical){
            exploreMapHeight(false);
            if(height <= 2)
                impossibleToDivide();
            else
                divideVerticalMap();
        }
        else if(isHorizontalMap())
            divideHorizontalMap();
        else
            divideRegularMap();

        restart();
    }
    private void divideHorizontalMap(){
        exploreMapHeight(false);
        if(width < 7) {
            blockEvenColumns();
            return;
        }
        int numberOfBarriers = 3;
        int numberOfChambers = 4;
        int totalChambersWidths = width - numberOfBarriers;
        int columnsToBeBlocked = totalChambersWidths % numberOfChambers;
        for(int i = width ; i>=1 ; i--){
            if(columnsToBeBlocked == 0)
                break;
            putBeepersOnColumn(i, this.y);
            columnsToBeBlocked--;
        }
        int oneChamberWidth = totalChambersWidths / numberOfChambers;
        int currentChamberWidth = 0;
        for(int i = (beepersPresent()) ? this.x-1 : this.x ; i>=1 ; i--){
            currentChamberWidth++;
            if (currentChamberWidth > oneChamberWidth) {
                putBeepersOnColumn(i, this.y);
                currentChamberWidth = 0;
            }
        }

    }
    private void divideVerticalMap(){
        if (height < 7) {
            blockEvenRows();
            return;
        }
        int numberOfBarriers = 3;
        int numberOfChambers = 4;
        int totalChambersHeights = height - numberOfBarriers;
        int rowsToBeBlocked = totalChambersHeights % numberOfChambers;
        for(int i = height ; i>=1 ; i--){
            if(rowsToBeBlocked == 0)
                break;
            putBeepersOnRow(i, this.x);
            rowsToBeBlocked--;
        }
        int oneChamberHeight = totalChambersHeights / numberOfChambers;
        int currentChamberHeight = 0;
        for(int i = (beepersPresent()) ? this.y-1 : this.y ; i>=1 ; i--){
            currentChamberHeight++;
            if (currentChamberHeight > oneChamberHeight) {
                putBeepersOnRow(i, this.x);
                currentChamberHeight = 0;
            }
        }
    }
    private void blockEvenRows(){
        goToPoint(this.x, height, false);
        for(int i = height ; i>=1 ; i--)
            if(i%2 == 0)
                putBeepersOnRow(i, this.x);
    }
    private void blockEvenColumns(){
        goToPoint(width, this.y, false);
        for(int i = width ; i>=1 ; i--)
            if(i%2 == 0)
                putBeepersOnColumn(i, this.y);
    }
    private void divideRegularMap(){
        //odd width
        if(width%2 == 1)
            splitOddWidth();
        //even width
        else
            splitEvenWidth();

        if(height%2 == 1)
            splitOddHeight();
        else
            splitEvenHeight();
    }
    private void impossibleToDivide(){
        restart();
        throw new ErrorException("This map size cannot be divided into any number of chambers");
    }
    private void splitOddHeight(){
        int mid_y = height/2 + 1;
        int startingFrom = width;
        putBeepersOnRow(mid_y, startingFrom);
    }
    private void splitEvenHeight(){
        int mid_y1 = height/2 + 1;
        int mid_y2 = height/2;
        putBeepersOnRow(mid_y1, 1);
        putBeepersOnRow(mid_y2, width);
    }
    private void splitEvenWidth(){
        int mid_x1 = (width/2) + 1;
        int mid_x2 = width/2;

        goToPoint(mid_x2, 1, false);
        exploreMapHeight(true);
        putBeepersOnColumn(mid_x1, height);
    }
    private void splitOddWidth()    {
        int mid_x = width/2 + 1;
        goToPoint(mid_x, 1, false);
        exploreMapHeight(true);
    }
    private boolean isHorizontalMap(){
        goToPoint(this.x, 1, false);
        faceDirection("NORTH");
        moveKarel();
        return frontIsBlocked();
    }
    private boolean isVerticalMap(){
        return width <= 2;
    }
    private void exploreMapWidth(){
        int cnt = 1;
        while(frontIsClear()){
            moveKarel();
            cnt++;
        }
        width = cnt;
    }
    private void exploreMapHeight(boolean putBeepers){
        faceDirection("NORTH");
        if(frontIsBlocked())
            faceDirection("SOUTH");
        while(frontIsClear()){
            if(putBeepers)
                putBeeper();
            moveKarel();
            height++;
        }
        if(putBeepers)
            putBeeper();
        System.out.println("Height is now calculated as: " + height);
    }
    private void putBeepersOnRow(int row, int startingFromX){
        int endingWithX = (startingFromX == width) ? 1 : width;
        goToPoint(startingFromX, row, false);
        goToPoint(endingWithX, this.y, true);
    }
    private void putBeepersOnColumn(int column, int startingFromY){
        int endingWithY = (startingFromY == height) ? 1 : height;
        System.out.println("Starting from: " + startingFromY + " ending with: " + endingWithY);
        goToPoint(column, startingFromY, false);
        goToPoint(this.x, endingWithY, true);
    }
    private void goToPoint(int finalX, int finalY, boolean putBeepers){
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
    private void faceDirection(@NotNull String direction){
        if(direction.equals("NORTH"))
            while(!facingNorth())
                turnLeft();
        else if(direction.equals("SOUTH"))
            while(!facingSouth())
                turnLeft();
        else if(direction.equals("EAST"))
            while(!facingEast())
                turnLeft();
        else if(direction.equals("WEST"))
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
    private void restart(){
        goToStart();
        faceDirection("EAST");
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
        goToPoint(1, 1, false);
    }
}