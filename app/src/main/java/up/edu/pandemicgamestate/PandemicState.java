package up.edu.pandemicgamestate;

import androidx.annotation.NonNull;
import java.util.Random;

/** PandemicState
 * This is the game state for Pandemic which contains all the information that is required to
 * play the game.
 * @author Nick Scott, Sarah Strong, and Emily Vo.
 * @version 20 October 2020.
 */
public class PandemicState {
    // game rule variables
    public static final int HAND_LIMIT = 7;
    public static final int NUM_ACTIONS = 4;
    public static final int MAX_STATIONS = 6;
    public static final int MAX_OUTBREAKS = 8;
    public static final int STARTING_RATE = 2;
    public static final int REQUIRED_CARDS_CURE = 5;
    public static final int NUM_EPIDEMICS = 5;
    public static final int NUM_DRAW_CARDS = 2;

    // game condition variables
    public static final int PLAY = 0;
    public static final int WIN = 1;
    public static final int LOSE = 2;

    // game action variables
    public static final int DRIVE_FERRY = 0;
    public static final int DIRECT_FLIGHT = 1;
    public static final int CHARTER_FLIGHT = 2;
    public static final int SHUTTLE_FLIGHT = 3;
    public static final int TREAT = 4;
    public static final int BUILD = 5;
    public static final int SHARE = 6;
    public static final int CURE = 7;
    public static final int DISCARD = 8;
    public static final int PASS = 9;
    public static final int END_TURN = 10;
    public static final int NUM_TYPE_OF_ACTIONS = 11;

    // instance variables
    private Disease[] diseases;
    private int outbreaks;
    private int infRate;
    private int stationsLeft;

    private int numPlayers;
    private int currPlayer;
    private int actionsLeft;
    private boolean needToDiscard;
    private int drawCardsLeft;

    private City[][] playerHands;
    private City[] currCity;

    private Deck infectionDeck;
    private Deck playerDeck;
    private int epiLeft;
    private int gameCondition;

    /** PandemicState()
     * The constructor class for the main Pandemic state. Sets all instance variables to start.
     * @param num Number of players.
     */
    public PandemicState(int num) {
        // create the four diseases
        this.diseases = new Disease[Disease.NUM_DISEASES];
        for(int i = 0; i < Disease.NUM_DISEASES; i++) {
            this.diseases[i] = new Disease(i);
        }

        // starting game parameters
        this.outbreaks = MAX_OUTBREAKS;
        this.infRate = STARTING_RATE;
        this.stationsLeft = MAX_STATIONS;
        this.epiLeft = NUM_EPIDEMICS;

        // choose starting player at random
        Random rng = new Random();
        this.numPlayers = num;
        this.currPlayer = rng.nextInt(numPlayers);
        this.actionsLeft = NUM_ACTIONS;
        this.needToDiscard = false;
        this.drawCardsLeft = NUM_DRAW_CARDS;

        // make the decks, initialize to same deck so they contain the same set of cities
        this.infectionDeck = new Deck(getCities(), rng);
        this.playerDeck = this.infectionDeck;

        // initialize player hands, the max cards are hand limit + 1 in the case of getting an
        // eighth card, then the player will be prompted to discard before getting any more cards
        this.playerHands = new City[this.numPlayers][HAND_LIMIT + 1];

        this.currCity = new City[this.numPlayers];
        // the players start at the CDC in Atlanta
        for(int i = 0; i < this.numPlayers; i++) {
            this.currCity[i] = this.playerDeck.getCity("Atlanta");
        }

        // build CDC
        this.playerDeck.getCity("Atlanta").buildStation();
        this.stationsLeft--;

        // determine how many cards are dealt out at the start
        int deal;
        switch(this.numPlayers) {
            case 2: deal = 4; break;
            case 3: deal = 3; break;
            default:
            case 4: deal = 2; break;
        }

        // this NULL city represents an "empty" slot in a player's hand
        City empty = new City(City.NULL);

        // deal out the starting hands
        for(int i = 0; i < this.numPlayers; i++) {
            for(int j = 0; j < deal; j++) {
                this.playerHands[i][j] = this.playerDeck.draw();
            }
            for(int j = deal; j < HAND_LIMIT + 1; j++) {
                this.playerHands[i][j] = empty;
            }
        }

        this.playerDeck.insertEpidemics(this.numPlayers);

        this.gameCondition = PLAY;
    } // PandemicState()

    /** PandemicState()
     * This is a deep copy constructor which will be used to send to human players.
     * @param orig The original game state that will be copied.
     */
    public PandemicState(PandemicState orig) {
        this.numPlayers = orig.numPlayers;

        this.diseases = new Disease[Disease.NUM_DISEASES];
        for(int i = 0; i < Disease.NUM_DISEASES; i++) {
            this.diseases[i] = orig.diseases[i];
        }

        this.outbreaks = orig.outbreaks;
        this.infRate = orig.infRate;
        this.stationsLeft = orig.stationsLeft;
        this.epiLeft = orig.epiLeft;

        this.currPlayer = orig.currPlayer;
        this.actionsLeft = orig.actionsLeft;
        this.drawCardsLeft = orig.drawCardsLeft;
        this.needToDiscard = orig.needToDiscard;

        this.infectionDeck = new Deck(orig.infectionDeck);
        this.playerDeck = new Deck(orig.playerDeck);

        this.playerHands = new City[this.numPlayers][HAND_LIMIT + 1];
        for(int i = 0; i < this.numPlayers; i++) {
            for(int j = 0; j < HAND_LIMIT + 1; j++) {
                this.playerHands[i][j] = new City(orig.playerHands[i][j]);
            }
        }

        this.currCity = new City[this.numPlayers];
        for(int i = 0; i < this.numPlayers; i++) {
            this.currCity[i] = new City(orig.currCity[i]);
        }

        this.gameCondition = orig.gameCondition;
    } // PandemicState()

    public PandemicState(int num, boolean test) {
        // create the four diseases
        this.diseases = new Disease[Disease.NUM_DISEASES];
        for(int i = 0; i < Disease.NUM_DISEASES; i++) {
            this.diseases[i] = new Disease(i);
        }

        // starting game parameters
        this.outbreaks = MAX_OUTBREAKS;
        this.infRate = STARTING_RATE;
        this.stationsLeft = MAX_STATIONS;
        this.epiLeft = NUM_EPIDEMICS;

        // have player one start the game
        this.numPlayers = num;
        this.currPlayer = 0; //player 1 starts
        this.actionsLeft = NUM_ACTIONS;
        this.needToDiscard = false;
        this.drawCardsLeft = NUM_DRAW_CARDS;

        // make the decks, initialize to same deck so they contain the same set of cities
        // blue cities
        City chicago = new City("Chicago", Disease.BLUE,
                new float[][]{{214.8f, 291.7f}, {261.8f, 314.7f}});
        City sanFrancisco = new City("San Francisco", Disease.BLUE,
                new float[][]{{45.9f, 314.7f}, {132.8f, 371.6f}});
        City montreal = new City("Montreal", Disease.BLUE,
                new float[][]{{336.7f, 286.7f}, {388.7f, 328.7f}});
        City newYork = new City("New York", Disease.BLUE,
                new float[][]{{426.7f, 305.8f}, {482.7f, 343.6f}});
        City washington = new City("Washington", Disease.BLUE,
                new float[][]{{396.7f, 380.6f}, {452.67f, 421.6f}});
        City atlanta = new City("Atlanta", Disease.BLUE,
                new float[][]{{258.8f, 374.6f}, {299.8f, 420.6f}});
        City london = new City("London", Disease.BLUE,
                new float[][]{{632.6f, 220.7f}, {674.6f, 260.7f}});
        City essen = new City("Essen", Disease.BLUE,
                new float[][]{{750.5f, 219.7f}, {785.5f, 243.7f}});
        City stPetersburg = new City("St. Petersburg", Disease.BLUE,
                new float[][]{{871.5f, 184.8f}, {922.4f, 217.7f}});
        City madrid = new City("Madrid", Disease.BLUE,
                new float[][]{{622.6f, 344.6f}, {663.6f, 388.6f}});
        City paris = new City("Paris", Disease.BLUE,
                new float[][]{{742.5f, 291.7f}, {770.5f, 339.6f}});
        City milan = new City("Milan", Disease.BLUE,
                new float[][]{{823.5f, 260.7f}, {854.5f, 310.7f}});

        // yellow cities
        City losAngeles = new City("Los Angeles", Disease.YELLOW,
                new float[][]{{101.9f, 441.6f}, {151.8f, 490.5f}});
        City miami = new City("Miami", Disease.YELLOW,
                new float[][]{{350.7f, 470.5f}, {395.7f, 520.5f}});
        City mexicoCity = new City("Mexico City", Disease.YELLOW,
                new float[][]{{212.8f, 482.5f}, {252.8f, 526.5f}});
        City bogota = new City("Bogota", Disease.YELLOW,
                new float[][]{{332.7f, 595.4f}, {371.7f, 631.4f}});
        City lima = new City("Lima", Disease.YELLOW,
                new float[][]{{278.8f, 719.3f}, {342.7f, 754.3f}});
        City santiago = new City("Santiago", Disease.YELLOW,
                new float[][]{{307.8f, 848.2f}, {363.7f, 887.2f}});
        City buenosAires = new City("Buenos Aires", Disease.YELLOW,
                new float[][]{{429.7f, 833.2f}, {490.7f, 869.2f}});
        City saoPaulo = new City("Sao Paulo", Disease.YELLOW,
                new float[][]{{496.7f, 728.3f}, {553.6f, 778.3f}});
        City lagos = new City("Lagos", Disease.YELLOW,
                new float[][]{{722.5f, 549.5f}, {769.5f, 615.5f}});
        City khartoum = new City("Khartoum", Disease.YELLOW,
                new float[][]{{857.5f, 547.5f}, {922.4f, 595.4f}});
        City kinshasa = new City("Kinshasa", Disease.YELLOW,
                new float[][]{{790.5f, 642.4f}, {842.5f, 691.3f}});
        City johannesburg = new City("Johannesburg", Disease.YELLOW,
                new float[][]{{849.5f, 754.3f}, {911.4f, 806.3f}});

        // black cities
        City moscow = new City("Moscow", Disease.BLACK,
                new float[][]{{952.4f, 257.7f}, {1000.4f, 305.7f}});
        City istanbul = new City("Istanbul", Disease.BLACK,
                new float[][]{{856.5f, 318.7f}, {915.4f, 376.6f}});
        City algiers = new City("Algiers", Disease.BLACK,
                new float[][]{{755.5f, 386.6f}, {806.5f, 448.6f}});
        City tehran = new City("Tehran", Disease.BLACK,
                new float[][]{{1027.4f, 293.7f}, {1095.3f, 345.6f}});
        City baghdad = new City("Baghdad", Disease.BLACK,
                new float[][]{{932.4f, 379.6f}, {994.4f, 426.6f}});
        City cairo = new City("Cairo", Disease.BLACK,
                new float[][]{{845.5f, 419.6f}, {905.4f, 471.5f}});
        City riyadh = new City("Riyadh", Disease.BLACK,
                new float[][]{{936.4f, 482.5f}, {1017.4f, 549.5f}});
        City karachi = new City("Karachi", Disease.BLACK,
                new float[][]{{1051.4f, 416.6f}, {1112.3f, 469.5f}});
        City delhi = new City("Delhi", Disease.BLACK,
                new float[][]{{1138.3f, 376.6f}, {1202.3f, 428.6f}});
        City mumbai = new City("Mumbai", Disease.BLACK,
                new float[][]{{1053.3f, 507.5f}, {1117.3f, 559.5f}});
        City chennai = new City("Chennai", Disease.BLACK,
                new float[][]{{1140.3f, 561.5f}, {1203.2f, 621.4f}});
        City kolkata = new City("Kolkata", Disease.BLACK,
                new float[][]{{1220.2f, 408.6f}, {1280.2f, 464.5f}});

        // red cities
        City beijing = new City("Beijing", Disease.RED,
                new float[][]{{1269.2f, 274.7f}, {1342.2f, 329.7f}});
        City seoul = new City("Seoul", Disease.RED,
                new float[][]{{1375.2f, 262.7f}, {1444.2f, 329.7f}});
        City tokyo = new City("Tokyo", Disease.RED,
                new float[][]{{1466.1f, 322.7f}, {1520.1f, 374.6f}});
        City shanghai = new City("Shanghai", Disease.RED,
                new float[][]{{1283.2f, 373.6f}, {1357.2f, 421.6f}});
        City hongKong = new City("Hong Kong", Disease.RED,
                new float[][]{{1297.2f, 462.5f}, {1349.2f, 516.5f}});
        City taipei = new City("Taipei", Disease.RED,
                new float[][]{{1389.2f, 433.6f}, {1464.1f, 500.5f}});
        City osaka = new City("Osaka", Disease.RED,
                new float[][]{{1475.1f, 420.6f}, {1533.1f, 478.5f}});
        City bangkok = new City("Bangkok", Disease.RED,
                new float[][]{{1228.2f, 519.5f}, {1288.2f, 574.4f}});
        City hoChiMinhCity = new City("Ho Chi Minh City", Disease.RED,
                new float[][]{{1308.2f, 593.4f}, {1368.1f, 644.4f}});
        City manila = new City("Manila", Disease.RED,
                new float[][]{{1422.1f, 590.4f}, {1476.1f, 652.4f}});
        City jakarta = new City("Jakarta", Disease.RED,
                new float[][]{{1222.2f, 657.4f}, {1285.2f, 719.3f}});
        City sydney = new City("Sydney", Disease.RED,
                new float[][]{{1474.1f, 817.2f}, {1554.1f, 887.2f}});

        //blue connections
        chicago.setConnections(new City[]{sanFrancisco, losAngeles, mexicoCity, atlanta, montreal});
        washington.setConnections(new City[]{atlanta, montreal, newYork});
        atlanta.setConnections(new City[]{washington, chicago, miami});
        newYork.setConnections(new City[]{montreal, washington, madrid, london});
        montreal.setConnections(new City[]{chicago, newYork, washington});
        sanFrancisco.setConnections(new City[]{losAngeles, chicago, tokyo, manila});
        london.setConnections(new City[]{essen, newYork, madrid, paris});
        madrid.setConnections(new City[]{newYork, london, paris, saoPaulo, algiers});
        paris.setConnections(new City[]{london, essen, milan, algiers, madrid});
        essen.setConnections(new City[]{london, paris, milan, stPetersburg});
        milan.setConnections(new City[]{essen, paris, istanbul});
        stPetersburg.setConnections(new City[]{essen, istanbul, moscow});

        //black connections
        istanbul.setConnections(new City[]{stPetersburg, moscow, milan, algiers, cairo, baghdad});
        moscow.setConnections(new City[]{stPetersburg, istanbul, tehran});
        algiers.setConnections(new City[]{madrid, paris, istanbul, cairo});
        cairo.setConnections(new City[]{algiers, istanbul, baghdad, riyadh});
        baghdad.setConnections(new City[]{istanbul, tehran, karachi, riyadh, cairo});
        riyadh.setConnections(new City[]{cairo, baghdad, karachi});
        karachi.setConnections(new City[]{riyadh, baghdad, tehran, delhi, mumbai});
        tehran.setConnections(new City[]{moscow, baghdad, karachi, delhi});
        delhi.setConnections(new City[]{tehran, karachi, mumbai, chennai, kolkata});
        mumbai.setConnections(new City[]{karachi, delhi, chennai});
        kolkata.setConnections(new City[]{delhi, chennai, bangkok, hongKong});
        chennai.setConnections(new City[]{mumbai, delhi, kolkata, bangkok, jakarta});

        //red connections
        bangkok.setConnections(new City[]{kolkata, chennai, hongKong, jakarta, hoChiMinhCity});
        hongKong.setConnections(new City[]{kolkata, bangkok, hoChiMinhCity, manila, taipei, shanghai});
        jakarta.setConnections(new City[]{chennai, bangkok, hoChiMinhCity, sydney});
        hoChiMinhCity.setConnections(new City[]{jakarta, bangkok, hongKong, manila});
        manila.setConnections(new City[]{hoChiMinhCity, hongKong, taipei, sydney, sanFrancisco});
        taipei.setConnections(new City[]{hongKong, osaka, manila, shanghai});
        shanghai.setConnections(new City[]{beijing, seoul, tokyo, hongKong, taipei});
        beijing.setConnections(new City[]{shanghai, seoul});
        seoul.setConnections(new City[]{beijing, shanghai, tokyo});
        tokyo.setConnections(new City[]{seoul, shanghai, osaka, sanFrancisco});
        osaka.setConnections(new City[]{tokyo, taipei});
        sydney.setConnections(new City[]{jakarta, manila, losAngeles});

        //yellow connections
        losAngeles.setConnections(new City[]{sydney, sanFrancisco, chicago, mexicoCity});
        mexicoCity.setConnections(new City[]{losAngeles, chicago, miami, bogota, lima});
        miami.setConnections(new City[]{mexicoCity, atlanta, washington, bogota});
        bogota.setConnections(new City[]{mexicoCity, miami, lima, buenosAires, saoPaulo});
        lima.setConnections(new City[]{mexicoCity, bogota, santiago});
        santiago.setConnections(new City[]{lima});
        buenosAires.setConnections(new City[]{bogota, saoPaulo});
        saoPaulo.setConnections(new City[]{buenosAires, bogota, madrid, lagos});
        lagos.setConnections(new City[]{saoPaulo, khartoum, kinshasa});
        khartoum.setConnections(new City[]{cairo, lagos, kinshasa, johannesburg});
        kinshasa.setConnections(new City[]{lagos, khartoum, johannesburg});
        johannesburg.setConnections(new City[]{kinshasa, khartoum});

        this.infectionDeck = new Deck(new City[]{paris, atlanta, losAngeles, mexicoCity, miami,
        bogota});
        this.playerDeck = new Deck(new City[]{atlanta, chicago, washington, losAngeles, essen,
        montreal, newYork, london, sanFrancisco, sydney, tokyo, saoPaulo, lima, santiago, miami,
        paris});

        // initialize player hands, the max cards are hand limit + 1 in the case of getting an
        // eighth card, then the player will be prompted to discard before getting any more cards
        this.playerHands = new City[this.numPlayers][HAND_LIMIT + 1];
        this.currCity = new City[this.numPlayers];

        // the players start at the CDC in Atlanta
        for(int i = 0; i < this.numPlayers; i++) {
            this.currCity[i] = this.playerDeck.getCity("Atlanta");
        }

        // build CDC
        this.playerDeck.getCity("Atlanta").buildStation();
        this.stationsLeft--;

        // determine how many cards are dealt out at the start
        int deal;
        switch(this.numPlayers) {
            case 2: deal = 4; break;
            case 3: deal = 3; break;
            default:
            case 4: deal = 2; break;
        }

        // this NULL city represents an "empty" slot in a player's hand
        City empty = new City(City.NULL);

        // deal out the premeditated starting hands
        for(int i = 0; i < this.numPlayers; i++) {
            for(int j = 0; j < deal; j++) {
                this.playerHands[i][j] = this.playerDeck.draw();
            }
            for(int j = deal; j < HAND_LIMIT + 1; j++) {
                this.playerHands[i][j] = empty;
            }
        }

        this.playerDeck.insertEpidemics(this.numPlayers);
        this.gameCondition = PLAY;
    } // PandemicState()

    /** getCities()
     * This is a helper method which initializes all of the cities that are on the Pandemic game
     * board, complete with coordinates and their connections.
     * @return An array of all the cities.
     */
    public City[] getCities() {
        // blue cities
        City chicago = new City("Chicago", Disease.BLUE,
                new float[][]{{214.8f, 291.7f}, {261.8f, 314.7f}});
        City sanFrancisco = new City("San Francisco", Disease.BLUE,
                new float[][]{{45.9f, 314.7f}, {132.8f, 371.6f}});
        City montreal = new City("Montreal", Disease.BLUE,
                new float[][]{{336.7f, 286.7f}, {388.7f, 328.7f}});
        City newYork = new City("New York", Disease.BLUE,
                new float[][]{{426.7f, 305.8f}, {482.7f, 343.6f}});
        City washington = new City("Washington", Disease.BLUE,
                new float[][]{{396.7f, 380.6f}, {452.67f, 421.6f}});
        City atlanta = new City("Atlanta", Disease.BLUE,
                new float[][]{{258.8f, 374.6f}, {299.8f, 420.6f}});
        City london = new City("London", Disease.BLUE,
                new float[][]{{632.6f, 220.7f}, {674.6f, 260.7f}});
        City essen = new City("Essen", Disease.BLUE,
                new float[][]{{750.5f, 219.7f}, {785.5f, 243.7f}});
        City stPetersburg = new City("St. Petersburg", Disease.BLUE,
                new float[][]{{871.5f, 184.8f}, {922.4f, 217.7f}});
        City madrid = new City("Madrid", Disease.BLUE,
                new float[][]{{622.6f, 344.6f}, {663.6f, 388.6f}});
        City paris = new City("Paris", Disease.BLUE,
                new float[][]{{742.5f, 291.7f}, {770.5f, 339.6f}});
        City milan = new City("Milan", Disease.BLUE,
                new float[][]{{823.5f, 260.7f}, {854.5f, 310.7f}});

        // yellow cities
        City losAngeles = new City("Los Angeles", Disease.YELLOW,
                new float[][]{{101.9f, 441.6f}, {151.8f, 490.5f}});
        City miami = new City("Miami", Disease.YELLOW,
                new float[][]{{350.7f, 470.5f}, {395.7f, 520.5f}});
        City mexicoCity = new City("Mexico City", Disease.YELLOW,
                new float[][]{{212.8f, 482.5f}, {252.8f, 526.5f}});
        City bogota = new City("Bogota", Disease.YELLOW,
                new float[][]{{332.7f, 595.4f}, {371.7f, 631.4f}});
        City lima = new City("Lima", Disease.YELLOW,
                new float[][]{{278.8f, 719.3f}, {342.7f, 754.3f}});
        City santiago = new City("Santiago", Disease.YELLOW,
                new float[][]{{307.8f, 848.2f}, {363.7f, 887.2f}});
        City buenosAires = new City("Buenos Aires", Disease.YELLOW,
                new float[][]{{429.7f, 833.2f}, {490.7f, 869.2f}});
        City saoPaulo = new City("Sao Paulo", Disease.YELLOW,
                new float[][]{{496.7f, 728.3f}, {553.6f, 778.3f}});
        City lagos = new City("Lagos", Disease.YELLOW,
                new float[][]{{722.5f, 549.5f}, {769.5f, 615.5f}});
        City khartoum = new City("Khartoum", Disease.YELLOW,
                new float[][]{{857.5f, 547.5f}, {922.4f, 595.4f}});
        City kinshasa = new City("Kinshasa", Disease.YELLOW,
                new float[][]{{790.5f, 642.4f}, {842.5f, 691.3f}});
        City johannesburg = new City("Johannesburg", Disease.YELLOW,
                new float[][]{{849.5f, 754.3f}, {911.4f, 806.3f}});

        // black cities
        City moscow = new City("Moscow", Disease.BLACK,
                new float[][]{{952.4f, 257.7f}, {1000.4f, 305.7f}});
        City istanbul = new City("Istanbul", Disease.BLACK,
                new float[][]{{856.5f, 318.7f}, {915.4f, 376.6f}});
        City algiers = new City("Algiers", Disease.BLACK,
                new float[][]{{755.5f, 386.6f}, {806.5f, 448.6f}});
        City tehran = new City("Tehran", Disease.BLACK,
                new float[][]{{1027.4f, 293.7f}, {1095.3f, 345.6f}});
        City baghdad = new City("Baghdad", Disease.BLACK,
                new float[][]{{932.4f, 379.6f}, {994.4f, 426.6f}});
        City cairo = new City("Cairo", Disease.BLACK,
                new float[][]{{845.5f, 419.6f}, {905.4f, 471.5f}});
        City riyadh = new City("Riyadh", Disease.BLACK,
                new float[][]{{936.4f, 482.5f}, {1017.4f, 549.5f}});
        City karachi = new City("Karachi", Disease.BLACK,
                new float[][]{{1051.4f, 416.6f}, {1112.3f, 469.5f}});
        City delhi = new City("Delhi", Disease.BLACK,
                new float[][]{{1138.3f, 376.6f}, {1202.3f, 428.6f}});
        City mumbai = new City("Mumbai", Disease.BLACK,
                new float[][]{{1053.3f, 507.5f}, {1117.3f, 559.5f}});
        City chennai = new City("Chennai", Disease.BLACK,
                new float[][]{{1140.3f, 561.5f}, {1203.2f, 621.4f}});
        City kolkata = new City("Kolkata", Disease.BLACK,
                new float[][]{{1220.2f, 408.6f}, {1280.2f, 464.5f}});

        // red cities
        City beijing = new City("Beijing", Disease.RED,
                new float[][]{{1269.2f, 274.7f}, {1342.2f, 329.7f}});
        City seoul = new City("Seoul", Disease.RED,
                new float[][]{{1375.2f, 262.7f}, {1444.2f, 329.7f}});
        City tokyo = new City("Tokyo", Disease.RED,
                new float[][]{{1466.1f, 322.7f}, {1520.1f, 374.6f}});
        City shanghai = new City("Shanghai", Disease.RED,
                new float[][]{{1283.2f, 373.6f}, {1357.2f, 421.6f}});
        City hongKong = new City("Hong Kong", Disease.RED,
                new float[][]{{1297.2f, 462.5f}, {1349.2f, 516.5f}});
        City taipei = new City("Taipei", Disease.RED,
                new float[][]{{1389.2f, 433.6f}, {1464.1f, 500.5f}});
        City osaka = new City("Osaka", Disease.RED,
                new float[][]{{1475.1f, 420.6f}, {1533.1f, 478.5f}});
        City bangkok = new City("Bangkok", Disease.RED,
                new float[][]{{1228.2f, 519.5f}, {1288.2f, 574.4f}});
        City hoChiMinhCity = new City("Ho Chi Minh City", Disease.RED,
                new float[][]{{1308.2f, 593.4f}, {1368.1f, 644.4f}});
        City manila = new City("Manila", Disease.RED,
                new float[][]{{1422.1f, 590.4f}, {1476.1f, 652.4f}});
        City jakarta = new City("Jakarta", Disease.RED,
                new float[][]{{1222.2f, 657.4f}, {1285.2f, 719.3f}});
        City sydney = new City("Sydney", Disease.RED,
                new float[][]{{1474.1f, 817.2f}, {1554.1f, 887.2f}});

        //blue connections
        chicago.setConnections(new City[]{sanFrancisco, losAngeles, mexicoCity, atlanta, montreal});
        washington.setConnections(new City[]{atlanta, montreal, newYork});
        atlanta.setConnections(new City[]{washington, chicago, miami});
        newYork.setConnections(new City[]{montreal, washington, madrid, london});
        montreal.setConnections(new City[]{chicago, newYork, washington});
        sanFrancisco.setConnections(new City[]{losAngeles, chicago, tokyo, manila});
        london.setConnections(new City[]{essen, newYork, madrid, paris});
        madrid.setConnections(new City[]{newYork, london, paris, saoPaulo, algiers});
        paris.setConnections(new City[]{london, essen, milan, algiers, madrid});
        essen.setConnections(new City[]{london, paris, milan, stPetersburg});
        milan.setConnections(new City[]{essen, paris, istanbul});
        stPetersburg.setConnections(new City[]{essen, istanbul, moscow});

        //black connections
        istanbul.setConnections(new City[]{stPetersburg, moscow, milan, algiers, cairo, baghdad});
        moscow.setConnections(new City[]{stPetersburg, istanbul, tehran});
        algiers.setConnections(new City[]{madrid, paris, istanbul, cairo});
        cairo.setConnections(new City[]{algiers, istanbul, baghdad, riyadh});
        baghdad.setConnections(new City[]{istanbul, tehran, karachi, riyadh, cairo});
        riyadh.setConnections(new City[]{cairo, baghdad, karachi});
        karachi.setConnections(new City[]{riyadh, baghdad, tehran, delhi, mumbai});
        tehran.setConnections(new City[]{moscow, baghdad, karachi, delhi});
        delhi.setConnections(new City[]{tehran, karachi, mumbai, chennai, kolkata});
        mumbai.setConnections(new City[]{karachi, delhi, chennai});
        kolkata.setConnections(new City[]{delhi, chennai, bangkok, hongKong});
        chennai.setConnections(new City[]{mumbai, delhi, kolkata, bangkok, jakarta});

        //red connections
        bangkok.setConnections(new City[]{kolkata, chennai, hongKong, jakarta, hoChiMinhCity});
        hongKong.setConnections(new City[]{kolkata, bangkok, hoChiMinhCity, manila, taipei, shanghai});
        jakarta.setConnections(new City[]{chennai, bangkok, hoChiMinhCity, sydney});
        hoChiMinhCity.setConnections(new City[]{jakarta, bangkok, hongKong, manila});
        manila.setConnections(new City[]{hoChiMinhCity, hongKong, taipei, sydney, sanFrancisco});
        taipei.setConnections(new City[]{hongKong, osaka, manila, shanghai});
        shanghai.setConnections(new City[]{beijing, seoul, tokyo, hongKong, taipei});
        beijing.setConnections(new City[]{shanghai, seoul});
        seoul.setConnections(new City[]{beijing, shanghai, tokyo});
        tokyo.setConnections(new City[]{seoul, shanghai, osaka, sanFrancisco});
        osaka.setConnections(new City[]{tokyo, taipei});
        sydney.setConnections(new City[]{jakarta, manila, losAngeles});

        //yellow connections
        losAngeles.setConnections(new City[]{sydney, sanFrancisco, chicago, mexicoCity});
        mexicoCity.setConnections(new City[]{losAngeles, chicago, miami, bogota, lima});
        miami.setConnections(new City[]{mexicoCity, atlanta, washington, bogota});
        bogota.setConnections(new City[]{mexicoCity, miami, lima, buenosAires, saoPaulo});
        lima.setConnections(new City[]{mexicoCity, bogota, santiago});
        santiago.setConnections(new City[]{lima});
        buenosAires.setConnections(new City[]{bogota, saoPaulo});
        saoPaulo.setConnections(new City[]{buenosAires, bogota, madrid, lagos});
        lagos.setConnections(new City[]{saoPaulo, khartoum, kinshasa});
        khartoum.setConnections(new City[]{cairo, lagos, kinshasa, johannesburg});
        kinshasa.setConnections(new City[]{lagos, khartoum, johannesburg});
        johannesburg.setConnections(new City[]{kinshasa, khartoum});

        return new City[]{chicago, sanFrancisco, montreal, newYork, washington, atlanta, london,
                essen, stPetersburg, madrid, paris, milan, losAngeles, miami, mexicoCity, bogota,
                lima, santiago, buenosAires, saoPaulo, lagos, khartoum, kinshasa, johannesburg,
                moscow, istanbul, algiers, tehran, baghdad, cairo, riyadh, karachi, delhi, mumbai,
                chennai, kolkata, beijing, seoul, tokyo, shanghai, hongKong, taipei, osaka, bangkok,
                hoChiMinhCity, manila, jakarta, sydney};
    } // getCities()

    /** needToDiscard()
     * This method checks to see if the current player has too many cards. If so, the only action
     * the player can do is discard a card.
     * @return Whether the current player needs to discard.
     */
    public boolean needToDiscard() {
        // count the number of empty slots in the current player's hand
        int numNull = 0;
        for(int i = 0; i < HAND_LIMIT + 1; i++) {
            if(this.playerHands[this.currPlayer][i].getName().equals("NULL")) {
                numNull++;
            }
        }

        // if there are no empty slots, there are eight cards, so the player needs to discard
        if(numNull == 0) {
            this.needToDiscard = true;
        }
        else {
            this.needToDiscard = false;
        }
        return this.needToDiscard;
    } // needToDiscard()

    /** treat()
     * This method performs the "treat disease" action, which removes a disease cube from the
     * location of the current player.
     * @param player The player that performed this action.
     * @return Whether the action was valid.
     */
    public boolean treat(int player) {
        if (this.checkDoableActions(player)[TREAT]) {
            return false;
        }

        // which city player is in
        City curr = this.currCity[player];
        int col = curr.getColor();

        // if treating the disease was successful, remove a cube
        if(curr.treatDisease()) {
            this.diseases[col].removeCube();
            this.actionsLeft--;
            return true;
        }
        return false;
    } // treat()

    /** driveFerry()
     * This method performs the Drive/Ferry action, which moves a player to an adjacent connected
     * city.
     * @param player The player that performed this action.
     * @param newCity The city the player is trying to go to.
     * @return Whether the action was valid.
     */
    public boolean driveFerry(int player, City newCity) {
        if(this.checkDoableActions(player)[DRIVE_FERRY]) {
            return false;
        }

        for(int i = 0; i < this.currCity[player].getConnections().length; i++){
            // check if the current city is adjacent to the new city
            if(this.currCity[player].getConnections()[i] == newCity){
                this.currCity[player] = newCity;
                this.actionsLeft--;
                return true;
            }
        }
        return false;
    } // driveFerry()

    /** directFlight()
     * This method performs the Direct Flight action, which moves a player to the city of a card
     * they discarded.
     * @param player The player that performed this action.
     * @param newCity The city the player discarded in order to go to it.
     * @return Whether the action was valid.
     */
    public boolean directFlight(int player, City newCity) {
        if(this.checkDoableActions(player)[DIRECT_FLIGHT]) {
            return false;
        }

        // makes sure the player has the card, then discard it and move the player
        if(this.hasCard(player, newCity)) {
            this.currCity[player] = newCity;
            this.discard(player, newCity);
            this.actionsLeft--;
            return true;
        }
        return false;
    } // directFlight()

    /** charterFlight()
     * This method performs the Charter Flight action, which moves a player to any city on the board
     * if they discard the card of the city they are in.
     * @param player The player that performed this action.
     * @param newCity The city the player is trying to go to.
     * @return Whether the action was valid.
     */
    public boolean charterFlight(int player, City newCity) {
        if(this.checkDoableActions(player)[CHARTER_FLIGHT]) {
            return false;
        }

        this.discard(player, this.currCity[player]);
        this.currCity[player] = newCity;
        this.actionsLeft--;
        return true;

    } // charterFlight()

    /** shuttleFlight()
     * This method performs the Shuttle Flight action, which moves a player between two research
     * stations.
     * @param player The player that performed the action.
     * @param newCity The city the player is trying to go to.
     * @return Whether the action was valid.
     */
    public boolean shuttleFlight(int player, City newCity) {
        if (this.checkDoableActions(player)[SHUTTLE_FLIGHT]) {
            return false;
        }

        //check if both locations have a research station, if so, move them
        if (currCity[player].hasStation() && newCity.hasStation()) {
            this.currCity[player] = newCity;
            this.actionsLeft--;
            return true;
        }
        return false;
    } // shuttleFlight()

    /** buildStation()
     * This method performs the Build Research Station action, which builds a research station if
     * the player discards the city that they are currently in.
     * @param player The player that performed the action.
     * @return Whether the action was valid.
     */
    public boolean buildStation(int player) {
        if (this.checkDoableActions(player)[BUILD]) {
            return false;
        }

        // gets the city the player is in
        City curr = this.currCity[player];
        // checks to see if ... a) there are stations left, and b) the player has that card
        if (this.stationsLeft > 0 && this.hasCard(player, curr)) {
            // attempts to build a station, then discard card
            if(curr.buildStation()) {
                this.discard(player, this.currCity[player]);
                this.stationsLeft--;
                this.actionsLeft--;
                return true;
            }
        }
        return false;
    } // buildStation()

    /** forgoAction()
     * This method removes an action from the current player, if they choose to do so.
     * @param player The player that performed the action.
     * @return Whether the action was valid.
     */
    public boolean forgoAction(int player) {
        if(this.checkDoableActions(player)[PASS]) {
            return false;
        }

        this.actionsLeft--;
        return true;

    } // forgoAction()

    /** discard()
     * This method removes a certain card from a player's hand, usually to perform actions or to
     * satisfy the hand limit.
     * @param player The player who is discarding a card.
     * @param disCity The city card they are trying to discard.
     * @return Whether the action was valid.
     */
    public boolean discard(int player, City disCity){
        if(player != this.currPlayer) {
            return false;
        }

        // this NULL city represents an "empty" slot in a player's hand
        City empty = new City(City.NULL);

        if(hasCard(player, disCity)) {
            // finds the index of the card to discard and make it "empty"
            for(int i = 0; i < playerHands[player].length; i++) {
                if(playerHands[player][i].getName().equals(disCity.getName())) {
                    playerHands[player][i] = empty;
                    return true;
                }
            }
        }

        return false;
    } // discard()

    /** discardToCure()
     * This is a helper method which is called when a player cures a disease. It removes five cards
     * from their hand of the same color.
     * @param player The player that has cured the disease.
     * @param color The disease that is being cured.
     * @return Whether the discard was valid.
     */
    public boolean discardToCure(int player, int color) {
        // this NULL city represents an "empty" slot in a player's hand
        City empty = new City(City.NULL);

        int count = 0;
        if(player == this.currPlayer) {
            for(int i = 0; i < playerHands[player].length; i++) {
                if(playerHands[player][i].getColor() == color && count != REQUIRED_CARDS_CURE) {
                    playerHands[player][i] = empty;
                    count++;
                }
            }
            this.diseases[color].cure();
            this.actionsLeft--;
            this.gameWon();
            return true;
        }
        return false;
    } // discardToCure()

    /** share()
     * This method performs the Share Knowledge action, which either gives or takes a city card of
     * two players that are in the same city.
     * @param player The player that performed the action.
     * @return Whether the action was valid.
     */
    public boolean share(int player) {
        if(this.checkDoableActions(player)[SHARE]) {
            return false;
        }

        for(int i = 0; i < this.numPlayers; i++) {
            // make sure we are not looking at the same player
            if(i != player) {
                // check to see if another player is in the same city
                if(currCity[i] == currCity[player]) {
                    for(int j = 0; j < this.numPlayers; j++) {
                        // check to see if that player has that city card
                        if(playerHands[i][j] == this.currCity[this.currPlayer]) {
                            // then insert that card into current player's hand
                            return swapCards(this.currCity[this.currPlayer], i, player, j);
                        }
                        // check to see if current player has that city card
                        else if(playerHands[player][j] == this.currCity[this.currPlayer]) {
                            // then insert that card into other player's hand
                            return swapCards(this.currCity[this.currPlayer], player, i, j);
                        }
                    }
                }
            }
        }
        return false;
    } // share()

    /** cure()
     * This method performs the Cure Disease action, which discards five cards of the same color
     * in order to cure the disease (required to win).
     * @param player The player that performed the action.
     * @return Whether the action was valid.
     */
    public boolean cure(int player) {
        if(this.checkDoableActions(player)[CURE]) {
            return false;
        }

        // counters for cards
        int blue = 0;
        int yellow = 0;
        int black = 0;
        int red = 0;
        // go through the player hand and determine which disease can be cured, if any
        for(int i = 0; i < this.playerHands[player].length; i++) {
            if(!this.playerHands[player][i].getName().equals("NULL")) {
                switch(this.playerHands[player][i].getColor()) {
                    // 1) increments counter of cards, 2) if there are five cards, successful cure
                    case Disease.BLUE:
                        blue++;
                        if(blue == 5) {
                            return this.discardToCure(player, Disease.BLUE);
                        }
                        break;
                    case Disease.YELLOW:
                        yellow++;
                        if(yellow == 5) {
                            return this.discardToCure(player, Disease.YELLOW);
                        }
                        break;
                    case Disease.BLACK:
                        black++;
                        if(black == 5) {
                            return this.discardToCure(player, Disease.BLACK);
                        }
                        break;
                    case Disease.RED:
                        red++;
                        if(red == 5) {
                            return this.discardToCure(player, Disease.RED);
                        }
                        break;
                }
            }
        }
        return false;
    } // cure()

    /** swapCards()
     * This is a helper method which transfers cards, and appropriately replaces it with an "empty"
     * city card in the original player's hand.
     * @param location The city card that is wanting to be swapped.
     * @param origPlayer The player in which the city card is coming from.
     * @param newPlayer The player in which the city card is going to.
     * @param origIdx The index of the city card is in for the original player.
     * @return Whether the action was valid.
     */
    public boolean swapCards(City location, int origPlayer, int newPlayer, int origIdx) {
        // this NULL city represents an "empty" slot in a player's hand
        City empty = new City(City.NULL);

        for(int i = 0; i < this.playerHands[newPlayer].length; i++) {
            // if we find an empty slot in the player's hand, place the card in
            if(this.playerHands[newPlayer][i].getName().equals("NULL")) {
                this.playerHands[newPlayer][i] = location;
                // and remove the card from the original player's hand with an "empty" city card
                this.playerHands[origPlayer][origIdx] = empty;
                this.actionsLeft--;
                return true;
            }
        }
        return false;
    } // swapCards()

    /** hasCard()
     * This is a helper method which checks to see if a player has a certain city card.
     * @param player The player whose hand we are checking.
     * @param card The city card we are looking for.
     * @return Whether the player has that city card.
     */
    public boolean hasCard(int player, City card) {
        // check if the player has the new city card in their hand
        for(int i = 0; i < this.playerHands[player].length; i++) {
            if(this.playerHands[player][i] == card) {
                return true;
            }
        }
        return false;
    } // hasCard()

    /** endTurn()
     * This method performs the End Turn action, which will carry out the rest of the player's turn,
     * including drawing two city cards and drawing infection cards.
     * @param player The player that performed the action.
     * @return Whether the action was valid.
     */
    public boolean endTurn(int player) {
        if(this.checkDoableActions(player)[END_TURN]) {
            return false;
        }

        if(this.actionsLeft == 0) {
            while(this.drawCardsLeft != 0) {
                if(!drawCard()) {
                    return false;
                }
                this.drawCardsLeft--;
            }
            // if it gets to here, the player has successfully drawn two cards.
            this.drawCardsLeft = NUM_DRAW_CARDS;
            this.drawInfectionCards();

            // continue to next player
            this.currPlayer = ++this.currPlayer % this.numPlayers;
            this.actionsLeft = NUM_ACTIONS;
            return true;
        }
        else {
            return false;
        }
    } // endTurn()

    /** drawCard()
     *  This method draws a city card for the player and adds it into their hand.
     * @return Whether drawing a card was successful.
     */
    public boolean drawCard() {
        if(this.needToDiscard()) {
            return false;
        }

        for(int i = 0; i < HAND_LIMIT + 1; i++) {
            if(this.playerHands[this.currPlayer][i].getName().equals("NULL")) {
                City draw = this.playerDeck.draw();
                if(draw.getName().equals("Epidemic")) {
                    this.epidemic();
                }
                else {
                    this.playerHands[this.currPlayer][i] = draw;
                }
                return true;
            }
        }
        return false;
    } // drawCard()

    /** epidemic()
     * This method is called when an Epidemic card is pulled instead of a regular city card.
     * Epidemics increase the difficulty of the game significantly.
     */
    public void epidemic() {
        // step 1) increase: adjust the infection rate
        switch(this.epiLeft) {
            case 5: case 4: break;
            case 3: case 2: this.infRate = 3; break;
            case 1: this.infRate = 4; break;
        }
        this.epiLeft--;

        // step 2) infect: draw bottom card, infect that city at max
        City epidemic = this.infectionDeck.drawBottomCard();
        for(int i = 0; i < City.MAX_CUBES; i++) {
            if (epidemic.infectCity(this.diseases)) {
                this.outbreaks++;
                i = City.MAX_CUBES;
            }
        }

        // step 3) intensify: shuffle all previously drawn cards
        this.infectionDeck.shuffleEpidemic();

        this.gameLost();
    } // epidemic()

    /** drawInfectionCards()
     * This method draws infection cards equal to the current infection rate, then places a disease
     * cube there. An outbreak occurs if the city already has three cubes.
     */
    public void drawInfectionCards() {
        for(int i = 0; i < this.infRate; i++) {
            City infect = this.infectionDeck.draw();
            if(infect.infectCity(this.diseases)) {
                outbreaks++;
            }
        }

        this.gameLost();
    } // drawInfectionCards()

    /** gameLost()
     * This method checks to see if the game is lost yet.
     */
    public void gameLost() {
        // LOSE CONDITION #1: no more disease cubes to place
        for(int i = 0; i < Disease.NUM_DISEASES; i++) {
            if(this.diseases[i].getCubesLeft() < 0) {
                this.gameCondition = LOSE;
            }
        }
        // LOSE CONDITION #2: eight outbreaks
        if(this.outbreaks >= MAX_OUTBREAKS) {
            this.gameCondition = LOSE;
        }
        // LOSE CONDITION #3: can't draw player cards
        else if(this.playerDeck.getCurrPos() == this.playerDeck.getDeckSize()) {
            this.gameCondition = LOSE;
        }
    } // gameLost()

    /** gameWon()
     * This method checks to see if the game is won yet.
     */
    public void gameWon() {
        for(int i = 0; i < Disease.NUM_DISEASES; i++) {
            if(this.diseases[i].getState() == Disease.UNCURED) {
                return;
            }
        }
        this.gameCondition = WIN;
    } // gameWon()

    /** checkDoableActions()
     * This method checks to see what actions can be performed; not necessarily if they are valid.
     * @param player The player that is performing the action.
     * @return An array containing all actions and whether they are doable.
     */
    public boolean[] checkDoableActions(int player) {
        // declaring a boolean array automatically sets values to false
        boolean[] canDo = new boolean[NUM_TYPE_OF_ACTIONS];

        // make sure the player is the current player
        if(this.currPlayer == player) {
            // count the number of cards in the player's hand
            int numCards = 0;
            // also check to see if the player has a card that they are currently in
            boolean currCard = false;
            for(int i = 0; i < this.playerHands[this.currPlayer].length; i++) {
                if(!this.playerHands[this.currPlayer][i].getName().equals("NULL")) {
                    numCards++;
                    if(this.playerHands[this.currPlayer][i] == this.currCity[this.currPlayer]) {
                        currCard = true;
                    }
                }
            }

            if(!this.needToDiscard()) {
                if(this.actionsLeft != 0) {
                    // if there are actions left, a player can always use the drive/ferry action
                    canDo[DRIVE_FERRY] = true;
                    // and they can always forgo actions
                    canDo[PASS] = true;

                    // checks to make sure the player has at least one card
                    if(numCards != 0) {
                        canDo[DIRECT_FLIGHT] = true;
                    }

                    // if the player has the card of the city they're in
                    if(currCard) {
                        canDo[CHARTER_FLIGHT] = true;

                        // and if there isn't a research station in the city they're in
                        if(!this.currCity[this.currPlayer].hasStation()) {
                            canDo[BUILD] = true;
                        }

                        // and check to see if there is at least two people in one city
                        for(int i = 0; i < this.numPlayers; i++) {
                            // make sure we are not looking at the same player
                            if(i != this.currPlayer) {
                                // check to see if another player is in the same city
                                if(currCity[i] == currCity[player]) {
                                    for(int j = 0; j < this.numPlayers; j++) {
                                        // check to see if that player has that city card
                                        if(playerHands[i][j] == this.currCity[this.currPlayer]) {
                                            canDo[SHARE] = true;
                                        }
                                        // or if the current player has that city card
                                        else if(playerHands[player][j] ==
                                                this.currCity[this.currPlayer]) {
                                            // then insert that card into other player's hand
                                            canDo[SHARE] = true;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // checks to see if there's a research station
                    if(this.currCity[this.currPlayer].hasStation()) {
                        // and if there are at least two research stations
                        if (!(this.stationsLeft > 6)) {
                            canDo[SHUTTLE_FLIGHT] = true;
                        }

                        // and if the current player has five cards of the same color
                        // counters for cards
                        int blue = 0;
                        int yellow = 0;
                        int black = 0;
                        int red = 0;
                        // go through the player hand and determine which disease can be cured
                        for(int i = 0; i < this.playerHands[player].length; i++) {
                            if(!this.playerHands[player][i].getName().equals("NULL")) {
                                switch(this.playerHands[player][i].getColor()) {
                                    // count cards
                                    case Disease.BLUE:
                                        blue++;
                                        if(blue == 5) {
                                            canDo[CURE] = true;
                                        }
                                        break;
                                    case Disease.YELLOW:
                                        yellow++;
                                        if(yellow == 5) {
                                            canDo[CURE] = true;
                                        }
                                        break;
                                    case Disease.BLACK:
                                        black++;
                                        if(black == 5) {
                                            canDo[CURE] = true;
                                        }
                                        break;
                                    case Disease.RED:
                                        red++;
                                        if(red == 5) {
                                            canDo[CURE] = true;
                                        }
                                        break;
                                }
                            }
                        }
                    }

                    // checks to see if there is at least one cube at the city
                    if(this.currCity[this.currPlayer].getCubes() != 0) {
                        canDo[TREAT] = true;
                    }
                }
                // if there are no actions left, player must end their turn
                else {
                    canDo[END_TURN] = true;
                }
            }
            // if player needs to discard, that is the only action they can do
            else {
                canDo[DISCARD] = true;
            }
        }

        return canDo;
    } // checkDoableActions()

    @NonNull
    @Override
    /** toString()
    * This method converts all the information of the Pandemic Game State and puts into a String.
    * @return The String with all of the information.
    */
    public String toString() {
        String gameState = "PANDEMIC GAME STATE\n";

        gameState += "\t-DISEASES-\n";
        for(int i = 0; i < Disease.NUM_DISEASES; i++) {
            gameState += "Color: " + this.diseases[i].getColor() + "\n";
            gameState += "Cubes Left: " + this.diseases[i].getCubesLeft() + "\n";
            gameState += "State: ";
            switch(diseases[i].getState()) {
                case 0: gameState += "Uncured\n"; break;
                case 1: gameState += "Cured\n"; break;
                case 2: gameState += "Eradicated\n"; break;
            }
        }
        gameState += "________________\n";

        gameState += "\t-CURRENT VARIABLES-\n";
        gameState += "Outbreaks: " + this.outbreaks + "\n";
        gameState += "Infection Rate: " + this.infRate + "\n";
        gameState += "Research Stations Left: " + this.stationsLeft + "\n";
        gameState += "Number of Players: " + this.numPlayers + "\n";
        gameState += "Current Player: " + this.currPlayer + "\n";
        gameState += "Need to Discard: " + this.needToDiscard + "\n";
        gameState += "Actions Left: " + this.actionsLeft + "\n";
        gameState += "Epidemics Left: " + this.epiLeft + "\n";
        gameState += "Player Hands: \n";
        for(int i = 0; i < this.playerHands.length; i++){
            gameState += "Player " + i + ": ";
            for(int j = 0; j < this.playerHands[i].length; j++){
                gameState += " " + this.playerHands[i][j].getName();
            }
            gameState += "\n";
        }
        gameState += "Player Locations: \n";
        for(int i = 0; i < this.currCity.length; i++){
            gameState += "Player " + i + " Location: " + this.currCity[i].getName() + "\n";
        }
        gameState += "Infection Deck: \n";
        for(int i = 0; i < this.infectionDeck.getDeckSize(); i++){
            gameState += this.infectionDeck.getCityAtIndex(i).getName() + "\n";
        }
        gameState += "Infection Deck Index Location: " + this.infectionDeck.getCurrPos() + "\n";
        gameState += "Player Deck/City Information: \n";
        for(int i = 0; i < this.playerDeck.getDeckSize(); i++){
            gameState += "City Name: " + this.playerDeck.getCityAtIndex(i).getName() + ":\n";
            gameState += "\t Has Research Station: " +
                    this.playerDeck.getCityAtIndex(i).hasStation() + "\n";
            gameState += "\t Color: " + this.playerDeck.getCityAtIndex(i).getColor() + "\n";
            gameState += "\t Location/Hit Box: \n";
            for(int j = 0; j < 2; j++){
                for(int k = 0; k < 2; k++){
                    gameState += "\t" + this.playerDeck.getCityAtIndex(i).getLocation()[j][k] + "\n";
                }
            }
            gameState += "\t Number of Cubes: " + this.playerDeck.getCityAtIndex(i).getCubes() + "\n";
            gameState += "\t Connections: \n";
            for(int j = 0; j < this.playerDeck.getCityAtIndex(i).getConnections().length; j++){
                gameState += "\t" +
                        this.playerDeck.getCityAtIndex(i).getConnections()[j].getName() + "\n";
            }
        }
        gameState += "Player Deck Index Location: " + playerDeck.getCurrPos() + "\n";
        gameState += "________________\n";
        gameState += "--------------------------------------------\n";
        return gameState;
    } // toString()

    public Deck getDeck() {
        return this.playerDeck;
    }
}