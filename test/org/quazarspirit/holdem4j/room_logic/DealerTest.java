package org.quazarspirit.holdem4j.room_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.game_logic.Card;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.RankEvaluatorTest;
import org.quazarspirit.holdem4j.game_logic.Round;
import org.quazarspirit.holdem4j.game_logic.card_pile.Deck;
import org.quazarspirit.holdem4j.game_logic.card_pile.ICardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.NullCardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.PocketCards;
import org.quazarspirit.holdem4j.player_logic.BotPlayer;
import org.quazarspirit.holdem4j.player_logic.IPlayer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DealerTest {
    static final Game testGame = new Game(Game.VARIANT.TEXAS_HOLDEM, Game.BET_STRUCTURE.NO_LIMIT, Game.PLAYER_TYPE.AI);

    DealerTest() { System.setProperty("TEST", "true"); }

    @Test
    void deal() {
        for(int j = 0; j <= Game.MAX_SEATS.FULL_RING.toInt(); j+=1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, j, testGame);
            Deck deck = new Deck(testGame);
            table.getDealer().deal();

            for (int i = 0; i < iPlayers.size(); i+=1) {
                IPlayer iPlayer = iPlayers.get(i);
                ICardPile pCards = table.getPocketCards(iPlayer);

                if (!pCards.equals(NullCardPile.GetSingleton())) {
                    PocketCards pocketCards = (PocketCards) pCards;
                    PocketCards expectedPocketCards = new PocketCards();
                    expectedPocketCards.pushCard(deck.getCardAt(i));
                    expectedPocketCards.pushCard(deck.getCardAt(i + iPlayers.size()));
                    assertTrue(pocketCards.equals(expectedPocketCards));
                }
            }
        }
    }

    @Test
    void draw() {
       for(int j = 0; j <= Game.MAX_SEATS.FULL_RING.toInt(); j+=1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, j, testGame);
            Deck deck = new Deck(testGame);
            Dealer dealer = table.getDealer();
            for(Round.ROUND_PHASE roundPhase: Round.ROUND_PHASE.values()) {
                dealer.draw(roundPhase.getDrawCount());
            }

            assertEquals(table.getBoard().asString(), "3c/4c/5c/7c/9c");
        }
    }

    @Test
    void playRoundPhase() {
        for(int j = 0; j <= Game.MAX_SEATS.FULL_RING.toInt(); j+=1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, j, testGame);
            Deck deck = new Deck(testGame);
            Dealer dealer = table.getDealer();
            for(Round.ROUND_PHASE roundPhase: Round.ROUND_PHASE.values()) {
                dealer.playRoundPhase();
                table.nextRoundPhase();
            }

            //assertEquals(table.getBoard().asString(), "3c/4c/5c/7c/9c");
        }
    }


    @Test
    void fillAllowedAction() {
    }

    @Test
    void handlePlayerAction() {
    }

    @Test
    void update() {
    }
}