package up.edu.pandemicgamestate;

/** Disease()
 * This is a Disease object, which keeps track of how many disease cubes it has left, and the
 * state of the disease (ie. is it cured?).
 * @author Nick Scott, Sarah Strong, Emily Vo.
 * @version 20 October 2020.
 */

public class Disease {
    // disease color variables
    public static final int BLUE = 0;
    public static final int YELLOW = 1;
    public static final int BLACK = 2;
    public static final int RED = 3;

    // disease state variables
    public static final int UNCURED = 0;
    public static final int CURED = 1;
    public static final int ERADICATED = 2;

    public static final int MAX_CUBES = 24;
    public static final int NUM_DISEASES = 4;

    // instance variables
    private int color;
    private int cubesLeft;
    private int state;

    /** Disease()
     * This constructor creates a disease of a certain color.
     * @param col The color of the disease.
     */
    public Disease(int col) {
        this.color = col;
        this.cubesLeft = MAX_CUBES;
        this.state = UNCURED;
    } // Disease()

    /** getState()
     * @return The state of the disease.
     */
    public int getState() {
        return this.state;
    } // getState()

    /** getColor()
     * @return The color of the disease.
     */
    public int getColor() {
        return this.color;
    } // getColor()

    /** getCubesLeft()
     * @return The number of cubes left.
     */
    public int getCubesLeft() {
        return this.cubesLeft;
    } // getCubesLeft()

    /** addCube()
     * This method is called whenever its infection card is pulled.
     */
    public void addCube() {
        this.cubesLeft--;
    } // addCube()

    /** removeCube()
     * This method is called whenever a player performs the Cure Disease action.
     */
    public void removeCube() {
        this.cubesLeft++;
    } // removeCube()

    /** cure()
     * This method is called whenever a player successfully performs the Cure Disease action.
     */
    public void cure() {
        this.state = CURED;
    } // cure()

    /** eradicate()
     * This method is called when ALL of the disease's cubes are gone from the board, and is cured.
     */
    public void eradicate() {
        this.state = ERADICATED;
    } // eradicate()
}
