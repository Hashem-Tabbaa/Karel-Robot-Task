import acm.util.ErrorException;
import stanford.karel.SuperKarel;

/**
 * The main point of the <tt>Karel</tt> class that make
 * <strong>Karel the robot</strong> deals with any map of any size
 * by divided them to <strong>Four equal champers
 * </strong> and if <i>impossible</i> divide it to <strong>Tow equal champers</strong>.
 *
 * <p>There are <strong>six cases </strong>:
 * <p> - First, <strong>impossible to divide</strong>.
 * <p> - Second, <strong>two divided champers</strong>.
 * <p> - Third, <strong>one line divided champers </strong>.
 * <p> - Fourth, <strong>even width even height</strong>.
 * <p> - Fifth, <strong>rectangle map</strong>.
 * <p> - Sixth, <strong>perfect odd square</strong>.
 * <p>
 * all of these cases have been fully specified at  {@link #checkMap()},
 * {@link #worldCasePoints()} methods.
 *
 * <p> the technique I used on <strong>Step (1)</strong> is:
 * <p>check the map size, scan, move, while Karel moving he will put beepers in place,
 * remove unnecessary beepers and return to origin point <i>(1,1)</i>.
 *
 * <p>but on <strong>Step (2)</strong> I use different technique, because Karel will deal with maps as completely clean,
 * so the technique have to achieve the lowest number of move <i>as possible</i>:
 * <p>check the map size, move, put beepers in place, return to origin point.
 *
 * <p><strong>Note that there are some odd cases in Step(2) that
 * the move number will be the same as Step (1)</strong>, because I think that the best practice for each case
 * is to get the time complexity <i>O(n*m)</i> after scanning.
 *
 * @author Dr.Motasem Aldiab
 * @author De.Fahed Jubair
 * @author Mohammad Daoud
 */

public class test extends SuperKarel {

    private int width = 1;
    private int height = 1;
    private int x = 1;
    private int y = 1;

    private int steps = 1;// represent the Karel Steps that he move
    private String worldCase;
    /**
     * All constant of Type <tt>String</tt> below represent the map type,
     * it is like a category of maps.
     */
    private final String EVEN_EVEN = "EVEN_EVEN"; // even width and height
    private final String EVEN_ODD = "EVEN_ODD"; // even width and odd height
    private final String ODD_EVEN = "ODD_EVEN"; // odd width and even height
    private final String ODD_ODD = "ODD_ODD";  // odd width and height but not equally  (odd rectangle)
    private final String PERFECT_ODD = "PERFECT_ODD"; // equal odd width and height (perfect odd square)
    private final String H_FILL = "H_FILL"; // vertical map
    private final String V_FILL = "V_FILL"; // horizontal map

    //Main method
    public void run() {
        setBeepersInBag(200);
        // caseRun(1);
        caseRun(2);

    }

    /**
     * this method will decide the case scenario  that karel will go through
     * there are two cases --
     * <T>1- to scan all map and pick unnecessary beepers.</T>
     * <T>2- to go with lowest number of moving steps as possible and assume that the map is completely clean.</T>
     *
     * @param caseNumber the scenario number.
     * @throws ErrorException if the specified initial nether <tt>1</tt> or <tt>2</tt>.
     */
    public void caseRun(int caseNumber) {
        findSize();
        worldCase = checkMap();
        if (caseNumber == 1)
            fill();
        else if (caseNumber == 2)
            fastFill();

        else
            throw new ErrorException("Illegal Case : " +
                    caseNumber);

        System.out.println("---------------------------------\n" +
                ">>> Karel travel in " + steps + " steps \n" +
                "---------------------------------");
        resetAll();
    }


    /**
     * checkMap() will make Karel know what type of map (world) he needs to deal with
     *
     * @return <strong>ODD_ODD</strong> if karel is dealing with <tt>width == 3 && height == 3</tt> or the
     * <tt>width</tt> and <tt>height</tt>
     * are odd and not equally. <i>i.e </i><tt>width == 49 && height == 33 </tt>
     * <p><strong>H_FILL</strong> if Karel is dealing with <tt>( height == 1 || height == 2) && width > 3)</tt>
     * <p><strong>V_FILL</strong> if Karel is dealing with <tt>( width == 1 || width == 2) && height > 3)</tt>
     * <p><strong>EVEN_EVEN</strong> if Karel is dealing with even width and height
     * <tt>( width % 2 == 0 && height % 2 == 0 )</tt>
     * <p><strong>EVEN_ODD</strong> if Karel is dealing with only even width<tt>( width % 2 == 0 )</tt>
     * <p><strong>ODD_EVEN</strong> if Karel is dealing with only even height<tt>( height % 2 == 0 )</tt>
     * <p><strong>ODD_ODD</strong> if Karel is dealing with not equally odd width and height
     * <p><strong>PERFECT_ODD</strong> if Karel is dealing with equally odd width and height
     * <tt>(width == height && width % 2 != 0)</tt>
     * @throws ErrorException if the map cannot be divided to any champers <tt>(height <= 2 && width <= 2)</tt>.
     */

    public String checkMap() {

        if (width == 3 && height == 3)
            return ODD_ODD;
        else if (height <= 2 && width <= 2)
            throw new ErrorException("this type of map cannot be divided !!: width = " + width + " height = " + height);
        else if (height == 1 || height == 2)
            return H_FILL;
        else if (width == 1 || width == 2)
            return V_FILL;
        else if (width % 2 == 0 && height % 2 == 0)
            return EVEN_EVEN;
        else if (width % 2 == 0)
            return EVEN_ODD;
        else if (height % 2 == 0)
            return ODD_EVEN;
        else if (width == height)
            return PERFECT_ODD;
        else
            return ODD_ODD;
    }

    /**
     * moveToWall() will make Karel moves until block by wall
     * {@link #beeperPutter()} before start moving to check if Karel should put beeper or continue
     * and after each move to also keep checking for putting beepers
     * see also {@link #move()} for move method that make karel moves 1 step in Karel library
     */
    private void moveToWall() {

        while (frontIsClear()) {
            beeperPutter();
            move();
            moveUpdater();
            beeperPutter();
        }
    }

    /**
     * findSize() will make Karel knows the size of the map, So he will know the map that deals with.
     * {@link #correctDirection()} this method will execute before any move to make Karel at the right
     * direction -<i>facing East</i>- then karel will keep moving, update the X,Y - coordinates and
     * moving steps by
     * calling {@link #moveUpdater()} method and increase the <tt>width</tt> and <tt>height</tt>
     */

    public void findSize() {
        correctDirection();
        while (frontIsClear() && facingEast()) {
            move();
            width++;
            moveUpdater();
        }
        turnLeft();
        while (frontIsClear() && facingNorth()) {
            move();
            height++;
            moveUpdater();
        }
    }
    /**
     * XYCoordinates() to update the X, Y - coordinate
     */
    public void XYCoordinates() {
        if (facingEast())
            x++;
        if (facingWest())
            x--;
        if (facingNorth())
            y++;
        if (facingSouth())
            y--;
    }

    /**
     * fill() will make Karel deal with any type of map for <strong>Step(1)</strong>
     * Karel will put all beepers needed and pick others while scanning with top-down
     * square wave movement
     * <p>Each {@link #move()} method will have {@link #moveUpdater()} method
     * to update Karel movement steps and X,Y- coordinate
     */

    public void fill() {
        beeperPutter();
        // on point (1,1) karel will stop
        while (getX() != 1 || getY() != 1) {
            /*
             * to redirect Karel for the right direction and the continuity of the process
             * we need to know which cases Karel needed to change his direction.
             */
            if (frontIsBlocked() && leftIsBlocked()) {
                beeperPutter();
                turnAround();
            } else if (frontIsBlocked() && facingNorth()) {
                turnLeft();
                beeperPutter();
                move();
                moveUpdater();

            } else if (frontIsBlocked() && facingWest()) {
                turnLeft();
                beeperPutter();
                move();
                moveUpdater();
                turnLeft();
            } else if (frontIsBlocked() && facingEast()) {
                // to avoid the block by the wall while moving
                if (getY() == 1 && (height % 2 == 0))
                    break;
                turnRight();
                beeperPutter();

                move();
                moveUpdater();
                turnRight();
            }
            moveToWall();
        }
        //to redirect Karel for right direction for even height to keep moving and to stop
        // with right direction for odd height
        turnAround();
        //if Karel reached the first line and not on the origin point we will correct his direction
        // and exit the method
        if (getY() == 1 && (height % 2 == 0)) {
            if (getX() == 1) {
                correctDirection();
                return;
            }
            moveToWall();
            correctDirection();
        }
        beeperPutter();
    }
    /**
     * stepsCounter() to update Karel movement steps
     */
    public void stepsCounter() {
        steps++;
    }
    /**
     * getSteps() to retrieve Karel movement steps.
     *
     * @return steps
     */
    public int getSteps() {
        return steps;
    }
    /**
     * resetAll() will re-assign steps, width and height to <strong>1</strong>
     * so you can run Karel as much as you want.
     */
    public void resetAll() {
        this.steps = 1;
        this.width = 1;
        this.height = 1;

    }



    /**
     * getX() to retrieve <tt>x</tt> coordinate
     *
     * @return x
     */
    @Override
    public int getX() {
        return x;
    }
    /**
     * getY() to retrieve <tt>y</tt> coordinate
     *
     * @return y
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * getWidth() to retrieve the <tt>width</tt> of the map
     *
     * @return width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * getHeight() to retrieve the <tt>height</tt> of the map
     *
     * @return width
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * LRCenterPoints() to find the right and left center points for <tt>H_FILL, V_FILL</tt> cases after divide it
     * to two equal champers,the formula to handle these two cases wil be like Binary search algorithm :
     * <p>After divided the map in half we put two pointers .i.e if map is <tt>10 * 1</tt> the center will
     * be when <tt>x == 5 && x == 6</tt> the <strong>right pointer</strong> will be at <tt>6</tt>
     * and the <strong>left pointer</strong> at <tt>5</tt>
     * <p>Then we subtract the <strong>right pointer</strong> from the width <tt>(width + 1) / 2</tt> and <tt>1</tt>
     * from <strong>left pointer</strong> now we look like we are dealing with new different maps
     * <p>The next step is to find the centers of that maps and to do that we need to do the same thing (find the
     * center of that map)
     * <p>The last step is to add that center to the <strong>right pointer</strong> and
     * subtract it to the <strong>left pointer</strong>.
     *
     * @param isRightPoint represent if right point is need to find pass <tt>true</tt> else pass <tt>false</tt>
     * @return rightPoint if <tt>isRightPoint == true</tt> and leftPoint if <tt>isRightPoint == false</tt>
     */

    public int LRCenterPoints(boolean isRightPoint) {
        int param;
        if (worldCase.equals(H_FILL))
            param = width;
        else param = height;

        int RightCenter = param / 2 + 1;
        int mainCenter = param / 2;
        int x = param - RightCenter;
        int gap = (x + 1) / 2;
        int rightPoint = RightCenter + gap;
        int leftPoint;
        if (param % 2 == 0)
            leftPoint = mainCenter - gap;
        else
            leftPoint = RightCenter - gap;

        if (isRightPoint)
            return rightPoint;
        else return leftPoint;

    }

    /**
     * worldCasePoints() will make Karel knows which point should he put the beepers on
     * <p>Each case has a unique points conditions .i.e for <strong>PERFECT_ODD</strong> case there are <strong>Four</strong>
     * situation to put beepers at:
     * <p><tt>if (x == y) || x+y == width+1 || x == center || y == center </tt>
     *
     * @return <tt>true</tt>  if the point is the right one else will return <tt>false</tt>
     */
    private boolean worldCasePoints() {
        // PERFECT_ODD case points conditions
        if (worldCase.equals(PERFECT_ODD))
            return getX() == getY() ||// condition for principal diagonals
                    getX() + getY() == width + 1 ||// condition for secondary diagonals
                    getX() == (width + 1) / 2 || // condition for center odd --cutting vertically
                    getY() == (height + 1) / 2; // condition for center odd  -- cutting horizontally

            // ODD_ODD case points
        else if (worldCase.equals(ODD_ODD))
            return getX() == (width + 1) / 2 ||
                    getY() == (height + 1) / 2;

            //EVEN_EVEN case points conditions
        else if (worldCase.equals(EVEN_EVEN))
            return getX() == width / 2 || // condition for first center --cutting vertically
                    getX() == width / 2 + 1 || // condition for second center --cutting vertically
                    getY() == height / 2 || // condition for first center --cutting horizontally
                    getY() == height / 2 + 1;// condition for second center --cutting horizontally

            //ODD_EVEN case points conditions
        else if (worldCase.equals(ODD_EVEN))
            return getX() == width / 2 + 1 ||
                    getY() == height / 2 ||
                    getY() == height / 2 + 1;

            //EVEN_ODD case points conditions
        else if (worldCase.equals(EVEN_ODD))
            return getX() == width / 2 ||
                    getX() == width / 2 + 1 ||
                    getY() == height / 2 + 1;
            //H_FILL or V_FILL  case points conditions -- in this and V_FILL case there are some odd cases
        else if (worldCase.equals(H_FILL) || worldCase.equals(V_FILL))
            return HVFillCases();

        else return false;
    }

    /**
     * HVFillCases() will send the points condition for Karel to make him know if
     * he should put a beeper or not
     */
    public boolean HVFillCases() {
        int rightPoint, leftPoint, caseRun, caseRunPoint;
        if (worldCase.equals(H_FILL)) {
            caseRunPoint = getX();
            caseRun = width;
        } else {
            caseRunPoint = getY();
            caseRun = height;
        }
        rightPoint = LRCenterPoints(true);
        leftPoint = LRCenterPoints(false);
        // to divide the map to 2 champers
        if (caseRun > 2 && caseRun <= 6)
            if (caseRun % 2 == 0)
                return caseRunPoint == caseRun / 2 ||
                        caseRunPoint == caseRun / 2 + 1;

            else return caseRunPoint == caseRun / 2 + 1;
        // if the width is from the multiplications of 4
        if (caseRun % 4 == 0)
            return caseRunPoint == caseRun / 2 ||
                    caseRunPoint == caseRun / 2 + 1 ||
                    caseRunPoint == rightPoint ||
                    caseRunPoint == leftPoint;
            // if the width is even number
        else if (caseRun % 2 == 0)
            return (getX() == width && getY() == height) ||
                    (getX() == width && getY() == 1) ||
                    (getX() == 1 && getY() == 1) ||
                    (getX() == 1 && getY() == height) ||
                    caseRunPoint == caseRun / 2 ||
                    caseRunPoint == caseRun / 2 + 1 ||
                    caseRunPoint == rightPoint ||
                    caseRunPoint == leftPoint;

            // if the width is less than multiplications of 4 by 1  it will be at specific point than other odd numbers
        else if ((caseRun+1)% 4 == 0)
            return caseRunPoint == (caseRun + 1) / 2 ||
                    caseRunPoint == rightPoint ||
                    caseRunPoint == leftPoint;

        else return (getX() == width && getY() == height) ||
                    (getX() == width && getY() == 1) ||
                    (getX() == 1 && getY() == 1) ||
                    (getX() == 1 && getY() == 2) ||
                    (getX() == 1 && getY() == caseRun) ||
                    caseRunPoint == (caseRun + 1) / 2 ||
                    caseRunPoint == rightPoint ||
                    caseRunPoint == leftPoint;
    }

    /**
     * correctDirection() wil redirect Karel to right direction ( <i>East</i> ) before start
     * in case Karel not facing <i>East</i>
     */
    public void correctDirection() {
        if (facingWest())
            turnAround();
        else if (facingNorth())
            turnRight();
        else if (facingSouth())
            turnLeft();
    }


    /*
     * part 2 of the assignment
     */

    /**
     * fastMoveToCenter() will make Karel move to the center in <strong>vertical</strong> or <strong>horizontal</strong>
     */
    public void fastMoveToCenter() {
        boolean isVerticalMove = !facingWest() && !facingEast();

        if (isVerticalMove) {
            for (int i =1 ; i < (height + 1) / 2; i++) {
                beeperPutter();// to know if Karel should put beeper on the current point
                move();
                moveUpdater(); // update X,Y - coordinate and steps

            }
        } else {
            for (int i =1 ; i < (width + 1) / 2; i++) {
                beeperPutter();
                move();
                moveUpdater();
            }
        }
        beeperPutter(); // to check also for putting beepers

    }

    /**
     * fastFill() will make Karel deals with any map on any size with the lowest number of moves
     * and pretend the map is <strong>completely clean</strong> before start moving.
     * <p>.i.e if Karel deal with clean map of size <tt>10 * 10</tt> with {@link #fastFill()} method will do
     * <Strong> 71 </Strong> steps, but with {@link #fill()} method Karel will do <strong>127</strong>steps
     *
     * <p>The technique (algorithm) that used on this method just like to connect all dots with one single line
     * (the game that we used to play when we were kids).
     * <p>There are some cases that we need to divide by visit all point
     * <strong>.i.e PERFECT_ODD,H_FILL and V_FILL</strong>
     * in the map,(this problem may I do it wrong)
     * <p><strong>NOTE: </strong>I have tried my best to make it optimize.
     */
    public void fastFill() {
        if (worldCase.equals(EVEN_EVEN)) {
            turnLeft();
            fastMoveToCenter();
            turnLeft();
            FFEvenMove();
            turnRight();
            move();
            moveUpdater();
            turnRight();
            FFEvenMove();
            turnLeft();
            returnHome();

        } else if (worldCase.equals(EVEN_ODD)) {
            turnAround();
            for (int i = 0; i < 2; i++) {
                fastMoveToCenter();
                turnRight();
            }
            fastMoveToCenter();
            turnLeft();
            move();
            moveUpdater();
            turnLeft();
            moveToWall();
            turnLeft();
            move();
            moveUpdater();
            turnLeft();
            fastMoveToCenter();
            turnLeft();
            returnHome();
            correctDirection();

        } else if (worldCase.equals(ODD_EVEN)) {
            for (int i = 0; i < 2; i++) {
                turnLeft();
                fastMoveToCenter();
            }
            turnLeft();
            moveToWall();
            turnRight();
            move();
            moveUpdater();
            turnRight();
            moveToWall();
            turnRight();
            move();
            moveUpdater();
            turnRight();
            fastMoveToCenter();
            turnRight();
            move();
            moveUpdater();
            fastMoveToCenter();
            turnRight();
            fastMoveToCenter();
            correctDirection();

        } else if (worldCase.equals(ODD_ODD)) {
            for (int i = 0; i < 2; i++) {
                turnLeft();
                fastMoveToCenter();
                for (int j = 0; j < 2; j++) {
                    turnLeft();
                    moveToWall();
                }
            }
            correctDirection();
        } else fill();
    }

    /**
     * FFEvenMove() fast fill even move will support Karel to fill {@link #EVEN_EVEN} map with the lowest number of move
     * by move with half square pattern
     */
    private void FFEvenMove() {
        fastMoveToCenter();
        turnLeft();
        fastMoveToCenter();
        turnRight();
        move();
        moveUpdater();
        turnRight();
        fastMoveToCenter();
        turnLeft();
        fastMoveToCenter();
    }



    /**
     * beeperPutter() will make karel decide to put beeper or not
     * depending on the beeper is already there plus it is world case point
     * if not Karel will pick up all beepers
     */
    public void beeperPutter() {
        if (!beepersPresent() && worldCasePoints())
            putBeeper();
        else if (!worldCasePoints())
            while (beepersPresent())
                pickBeeper();
    }

    /**
     * returnHome() will make Karel return to origin point <tt>(1,1)</tt> , but in some cases only
     */

    public void returnHome() {
        while (frontIsClear()) {
            beeperPutter();
            move();
            moveUpdater();
        }
        turnLeft();
        beeperPutter();
        while (frontIsClear()) {
            move();
            moveUpdater();
        }
        turnLeft();
    }

    /**
     * moveUpdater() will update both X,Y coordinate and Karel steps
     */
    public void moveUpdater() {
        stepsCounter();
        XYCoordinates();
    }

}