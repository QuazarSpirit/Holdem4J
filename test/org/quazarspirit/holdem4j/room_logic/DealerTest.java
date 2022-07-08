package org.quazarspirit.holdem4j.room_logic;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.game_logic.BettingRound;
import org.quazarspirit.holdem4j.game_logic.Game;
import org.quazarspirit.holdem4j.game_logic.card_pile.*;
import org.quazarspirit.holdem4j.game_logic.chip_pile.NullBet;
import org.quazarspirit.holdem4j.player_logic.player.IPlayer;
import org.quazarspirit.holdem4j.player_logic.enums.PLAYER_ACTION;
import org.quazarspirit.utils.publisher_subscriber_pattern.Event;
import org.quazarspirit.utils.publisher_subscriber_pattern.ISubscriber;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DealerTest {
    static final Game testGame = new Game(Game.VARIANT.TEXAS_HOLDEM, Game.BET_STRUCTURE.NO_LIMIT, Game.PLAYER_TYPE.AI);

    DealerTest() { System.setProperty("TEST", "true"); }

    private void testPocketCards(ArrayList<IPlayer> iPlayers, Deck deck, Table table) {
        for (int i = 0; i < iPlayers.size(); i+=1) {
            IPlayer iPlayer = iPlayers.get(i);
            ICardPile pCards = table.getPocketCards(iPlayer);

            System.out.println(pCards.asString());

            if (!pCards.equals(NullCardPile.GetSingleton())) {
                PocketCards pocketCards = (PocketCards) pCards;
                PocketCards expectedPocketCards = new PocketCards();
                expectedPocketCards.pushCard(deck.getCardAt(i));
                expectedPocketCards.pushCard(deck.getCardAt(i + iPlayers.size()));
                assertTrue(pocketCards.equals(expectedPocketCards));
            }
        }
    }

    @Test
    void deal() {
        for(int j = 0; j <= Game.MAX_SEATS.FULL_RING.toInt(); j+=1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, j, testGame);
            Deck deck = new Deck(testGame);
            table.getDealer().deal();

            testPocketCards(iPlayers, deck, table);
        }
    }

    @Test
    void draw() {
       for(int j = 0; j <= Game.MAX_SEATS.FULL_RING.toInt(); j+=1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, j, testGame);
            Dealer dealer = table.getDealer();
            for(BettingRound.PHASE roundPhase: BettingRound.PHASE.values()) {
                dealer.draw(roundPhase.getDrawCount());
            }

            assertEquals("3c/4c/5c/7c/9c", table.getBoard().asString());
        }
    }

    @Test
    void playOneTableRound() {
        final ArrayList<BettingRound.PHASE> staticHistory = new ArrayList<>(){
            {
                add(BettingRound.PHASE.PRE_FLOP);
                add(BettingRound.PHASE.FLOP);
                add(BettingRound.PHASE.TURN);
                add(BettingRound.PHASE.RIVER);
                add(BettingRound.PHASE.SHOWDOWN);
                add(BettingRound.PHASE.STASIS);
            }
        };
        class SubscriberTest implements ISubscriber{
            private final ArrayList<BettingRound.PHASE> _history = new ArrayList<>();
            @Override
            public void update(Event event) {
                if(event.data.get("type") == BettingRound.EVENT.NEXT) {
                    BettingRound.PHASE roundPhase = (BettingRound.PHASE) event.data.get("round_phase");
                    if (_history.size() > 0) {
                        if (roundPhase != _history.get(_history.size() -1)) {

                            _history.add(roundPhase);
                        }
                    } else {
                        _history.add(roundPhase);
                    }
                }
            }

            public ArrayList<BettingRound.PHASE> getHistory() {
                return _history;
            }
        }

        for(int j = 0; j <= Game.MAX_SEATS.FULL_RING.toInt(); j+=1) {
            ArrayList<IPlayer> iPlayers = new ArrayList<>();
            Table table = TableTest.initTableWithPlayers(iPlayers, j, testGame);

            SubscriberTest subscriberTest = new SubscriberTest();
            table.addSubscriber(subscriberTest);

            Deck deck = new Deck(testGame);
            Dealer dealer = table.getDealer();

            int playerCount = iPlayers.size();

            for(BettingRound.PHASE roundPhase: BettingRound.PHASE.values()) {
                dealer.playRoundPhase();
                if (roundPhase == BettingRound.PHASE.PRE_FLOP) {
                    testPocketCards(iPlayers, deck, table);
                    deck.burn(playerCount * 2);
                }

                if (roundPhase == BettingRound.PHASE.RIVER) {
                    Board testBoard = new Board();
                    dealer.drawWithContext(3, deck, testBoard);
                    dealer.drawWithContext(1, deck, testBoard);
                    dealer.drawWithContext(1, deck, testBoard);

                    assertEquals(table.getBoard().asString(), testBoard.asString());
                }

                table.nextBettingRoundPhase();
            }

            assertEquals(staticHistory, subscriberTest.getHistory());
        }
    }

    @Test
    void fillAllowedAction() {
        // There is technically only 2 possible values for allowedActions:
        // [FOLD, CALL, RAISE] and [FOLD, CHECK, BET]
        // Fold stays in any case and depending on aggressive play
        // or not you can't check and bet or call and raise
        ArrayList<PLAYER_ACTION> aggressive_history_1 = new ArrayList<>() {
            { add(PLAYER_ACTION.BET); }
        };

        ArrayList<PLAYER_ACTION> aggressive_history_2 = new ArrayList<>() {
            { add(PLAYER_ACTION.RAISE); }
        };

        ArrayList<PLAYER_ACTION> aggressive_allowed = new ArrayList<>() {
            { add(PLAYER_ACTION.FOLD); add(PLAYER_ACTION.CALL); add(PLAYER_ACTION.RAISE); }
        };

        ArrayList<PLAYER_ACTION> passive_history = new ArrayList<>() {
            { add(PLAYER_ACTION.CHECK); add(PLAYER_ACTION.CALL); add(PLAYER_ACTION.FOLD); }
        };
        ArrayList<PLAYER_ACTION> passive_allowed = new ArrayList<>() {
            {
                add(PLAYER_ACTION.FOLD);
                add(PLAYER_ACTION.CHECK);
                add(PLAYER_ACTION.BET);
            }
        };

        HashMap<ArrayList<PLAYER_ACTION>, ArrayList<PLAYER_ACTION>> testValues = new HashMap<>() {
            {
                put(aggressive_history_1, aggressive_allowed);
                put(aggressive_history_2, aggressive_allowed);
                put(passive_history, passive_allowed);
            }
        };

        ArrayList<IPlayer> iPlayers = new ArrayList<>();
        // fillAllowed isn't dependent of player count
        Table table = TableTest.initTableWithPlayers(iPlayers, 10, testGame);
        Dealer dealer = table.getDealer();

        for (Map.Entry<ArrayList<PLAYER_ACTION>, ArrayList<PLAYER_ACTION>> set: testValues.entrySet()) {
            assertEquals(dealer.fillAllowedAction(set.getKey()), set.getValue());
        }
    }

    @Test
    void handlePlayerAction() {
        // When a round phase is started, an event
        // is sent to each player in position order.
        // When the player responds to this event
        // handlePlayerAction is responsible to
        // replication this action on table

        ArrayList<IPlayer> iPlayers = new ArrayList<>();
        Table table = TableTest.initTableWithPlayers(iPlayers, 10, testGame);
        System.out.println(table);
        Dealer dealer = table.getDealer();

        JSONObject jsonObject_1 = new JSONObject();
        jsonObject_1.put("player_action", PLAYER_ACTION.FOLD);
        jsonObject_1.put("bet", NullBet.getSingleton());
        IPlayer botPlayer_1 = iPlayers.get(0);

        System.out.println(table.getPlayingPositions());
        dealer.handlePlayerAction(jsonObject_1, botPlayer_1);
        System.out.println(table.getPlayingPositions());

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("player_action", PLAYER_ACTION.BET);
        jsonObject2.put("bet", NullBet.getSingleton());
        IPlayer botPlayer_2 = iPlayers.get(2);
        System.out.println(table.getPlayingPositions());
        dealer.handlePlayerAction(jsonObject2, botPlayer_2);
        System.out.println(table.getPlayingPositions());

    }

    @Test
    void update() {
    }
}