package up.edu.pandemicgamestate;

/** City
 * This is a City object, which represents a location on the game board, and is also used as
 * player cards.
 * @author Nick Scott, Sarah Strong, Emily Vo
 * @version 20 October 2020
 */

public class City {
    // special cards:
    // EPIDEMIC - when drawn, an epidemic happens
    // NULL - this represents an "empty" card in a player's hand
    public static final int EPIDEMIC = 1;
    public static final int NULL = 2;

    public static final int MAX_CUBES = 3;

    // instance variables
    private String cityName;
    private int color;
    private float[][] location;
    private int cubes;
    private City[] connections;
    private boolean hasStation;

    /** City()
     * This constructor will create a special City Card: either an EPIDEMIC or a NULL card.
     * @param type The special card.
     */
    public City(int type) {
        if(type == EPIDEMIC) {
            this.cityName = "Epidemic";
        }
        else if(type == NULL) {
            this.cityName = "NULL";
        }
    } // City()

    /** City()
     * This constructor will create a normal City, and initialize its variables.
     * @param name The name of the city.
     * @param col The color of the city (which disease is placed there).
     * @param loc The coordinates of the city (used for selecting cities).
     */
    public City(String name, int col, float[][] loc) {
        this.cityName = name;
        this.color = col;
        this.location = loc;
        this.cubes = 0;
        this.hasStation = false;
    } // City()

    /** City()
     * This is a deep copy constructor which creates a copy of a given city.
     * @param orig The city to be copied.
     */
    public City(City orig) {
        this.cityName = orig.cityName;
        this.color = orig.color;
        this.location = orig.location;
        this.cubes = orig.cubes;
        this.setConnections(orig.connections);
        this.hasStation = orig.hasStation;
    } // City()

    /** setConnections()
     * @param connect An array of adjacent cities.
     */
    public void setConnections(City[] connect) {
        this.connections = connect;
    } // setConnections()

    /** getColor()
     * @return The color of the city.
     */
    public int getColor() {
        return this.color;
    } // getColor()

    /** hasStation()
     * @return Whether there is a research station here.
     */
    public boolean hasStation() {
        return this.hasStation;
    } // hasStation()

    /** buildStation()
     * This is a helper method which builds a research station at the city.
     * @return Whether this action is valid.
     */
    public boolean buildStation() {
        if(this.hasStation == false) {
            this.hasStation = true;
            return true;
        }
        return false;
    } // buildStation()

    /** getConnections()
     * @return An array of the adjacent cities.
     */
    public City[] getConnections() {
        return this.connections;
    } // getConnections()

    /** treatDisease()
     * This is a helper method which removes a disease cube from the city.
     * @return Whether this action is valid.
     */
    public boolean treatDisease() {
        if(this.cubes > 0) {
            this.cubes--;
            return true;
        }

        return false;
    } // treatDisease()

    /** infectCity()
     * This method adds a disease cube to the city if its infection card is pulled.
     * @param diseases An array of diseases.
     * @return True if an outbreak occurs. False if only a cube was added.
     */
    public boolean infectCity(Disease[] diseases) {
        if(this.cubes == 3) {
            this.outbreak(diseases);
            return true;
        }
        else {
            this.cubes++;
            diseases[this.color].addCube();
            return false;
        }
    }

    /** getName()
     * @return The name of the city.
     */
    public String getName() {
        return this.cityName;
    } // getName()

    /** outbreak()
     * This method is called when a disease cube cannot be placed due to there being three cubes
     * already at a city. An outbreak occurs, which puts a disease cube in all of the connected
     * cities instead.
     * @param diseases An array of diseases.
     */
    public void outbreak(Disease[] diseases) {
        for(int i = 0; i < this.connections.length; i++) {
            this.connections[i].infectCity(diseases);
        }
    } // outbreak()

    /** getLocation()
     * @return A "hitbox" of the city, represented by coordinates.
     */
    public float[][] getLocation() {
        return this.location;
    } // getLocation()

    /** getCubes()
     * @return The number of cubes on the city.
     */
    public int getCubes() {
        return this.cubes;
    } // getCubes()
}
