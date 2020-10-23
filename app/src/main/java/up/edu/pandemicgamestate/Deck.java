package up.edu.pandemicgamestate;

import java.util.Random;

/** Deck
 * This is a Deck object which contains an array of City objects, which will be used as City Cards
 * for the players in this case.
 * @author Nick Scott, Sarah Strong, and Emily Vo.
 * @version 20 October 2020.
 */
public class Deck {
    public static final int NUM_CITIES = 48;

    // instance variables
    private City[] deck;
    private int currPos;

    /** Deck()
     * This constructor initializes the deck, and then shuffles it.
     * @param cards The city cards to put into the deck.
     * @param rng A random number generator.
     */
    public Deck(City[] cards, Random rng) {
        // arbitrary amount of times cards will be swapped (1000 is a high amount, should
        // shuffle a sufficient number of times
        int shuffle = 1000;

        // randomize the cards by swapping two cards
        for (int i = 0; i < shuffle; i++) {
            int card1 = rng.nextInt(cards.length);
            int card2 = rng.nextInt(cards.length);

            City temp = cards[card1];
            cards[card1] = cards[card2];
            cards[card2] = temp;
        }

        // set it to instance deck
        this.deck = cards;

        // currPos starts at -1 so when it first deals, it goes to index 0
        this.currPos = -1;
    } // Deck()

    /** Deck()
     * This is a deep copy constructor which creates a deck of copies of the City Cards.
     * @param orig The Deck object we are making a deep copy of.
     */
    public Deck(Deck orig) {
        this.deck = new City[orig.deck.length];
        for(int i = 0; i < orig.deck.length; i++) {
            this.deck[i] = new City(orig.deck[i]);
        }
    } // Deck()

    public Deck(City[] cities) {
        this.deck = cities;
    }

    /** draw()
     * Draws the next card in the deck.
     * @return The card drawn.
     */
    public City draw() {
        this.currPos++;
        if(currPos == this.deck.length - 1) {
            return null;
        }
        return deck[currPos];
    } // draw()

    /** insertEpidemics()
     * This is a helper method which inserts the epidemic cards after the initial cards have been
     * drawn.
     * @param numPlayers The number of players.
     */
    public void insertEpidemics(int numPlayers) {
        City epidemic = new City(City.EPIDEMIC);

        if(numPlayers == 2 || numPlayers == 4) {

        }
    } // insertEpidemics()

    /** getCity()
     * This is a helper method which finds a city in the deck.
     * @param city The name of the city to find.
     * @return The city.
     */
    public City getCity(String city) {
        // iterate through the deck until we find the city name
        for(int i = 0; i < this.deck.length; i++) {
            if(this.deck[i].getName().equals(city)) {
                return this.deck[i];
            }
        }
        return null;
    } // getCity()

    /** drawBottomCard()
     * This method is called when an epidemic is pulled, and we need to pull the card on the bottom
     * of the deck.
     * @return The bottom card.
     */
    public City drawBottomCard() {
        return this.deck[NUM_CITIES - 1];
    } // drawBottomCard()

    /** shuffleEpidemic()
     * This method brings the bottom card up to the discard pile, shifting every card down, and then
     * shuffles only the discard pile.
     */
    public void shuffleEpidemic() {
        // shift all cards up
        City temp = this.deck[NUM_CITIES - 1];
        for(int i = NUM_CITIES - 1; i > this.currPos + 1; i--) {
            this.deck[i] = this.deck[i - 1];
        }
        // in order to put the new card onto the discard pile
        this.deck[this.currPos + 1] = temp;

        // now we shuffle only the discard pile, shuffle is arbitrary
        int shuffle = 500;
        Random rng = new Random();
        for(int i = 0; i < shuffle; i++) {
            int card1 = rng.nextInt(this.currPos + 2);
            int card2 = rng.nextInt(this.currPos + 2);

            City swap = this.deck[card1];
            this.deck[card1] = this.deck[card2];
            this.deck[card2] = swap;
        }

        this.currPos = -1;
    } // shuffleEpidemic()

    /** getCurrPos()
     * @return The current position of the deck.
     */
    public int getCurrPos() {
        return this.currPos;
    } // getCurrPos()

    /** getDeckSize()
     * @return The size of the deck.
     */
    public int getDeckSize() {
        int count = 0;
        for(int i = 0; i < this.deck.length; i++) {
            if(!this.deck[i].getName().equals("NULL")) {
                count++;
            }
        }

        return count;
    } // getDeckSize()

    /** getCityAtIndex()
     * @param idx The index of the deck.
     * @return The city at that given index.
     */
    public City getCityAtIndex(int idx) {
        return this.deck[idx];
    } // getCityAtIndex()
}
