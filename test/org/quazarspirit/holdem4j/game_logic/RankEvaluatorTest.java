package org.quazarspirit.holdem4j.game_logic;

import org.junit.jupiter.api.Test;
import org.quazarspirit.holdem4j.game_logic.card_pile.CardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.Deck;
import org.quazarspirit.holdem4j.game_logic.card_pile.Hand;
import org.quazarspirit.utils.ImmutableKV;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RankEvaluatorTest {
    Deck deckFromString(String str) {
        Deck deck = new Deck();
        deck.clear();
        String[] splitStr = str.split(CardPile.CARD_CHAR_SEPARATOR);

        for (String s: splitStr) {
            deck.pushCard(new Card(s));
        }

        return deck;
    }

    Deck generateDeck() {
        Deck deck = new Deck();
        deck.clear();
        for(int i = 0; i < deck.getMaxSize(); i +=1) {
          int colorInt = i % 4;
          int rankInt = i % 13;
          deck.pushCard(new Card("" + Card.RANKS.charAt(rankInt) + Card.COLORS.charAt(colorInt)));
        }

        return deck;
    }

    ArrayList<Hand> genStraightHands(Deck deck) {
       return genStraightHands(deck, 9);
    }

    ArrayList<Hand> genStraightHands(Deck deck, int kMax) {
        ArrayList<Hand> handList = new ArrayList<>();
        for (int k = 0; k < kMax; k += 1) {
            for (int i = 0; i < deck.size(); i += deck.size() / Card.COLORS.length()) {
                Hand hand = new Hand();
                for (int j = 0; j < hand.getMaxSize(); j++) {
                    hand.pushCard(deck.getCardAt(i + j));
                }
                handList.add(hand);
            }
        }
        return handList;
    }

    String removeRankRepetition(String str, int repCount) {
        String sanitizedHandString = "NONE";
        String originalHandString = str;
        for (char character: originalHandString.toCharArray()) {
            int count = (int) originalHandString.chars().filter(ch -> ch == character).count();
            if (count == repCount) {
                sanitizedHandString = originalHandString.replace("" + character, "");
                break;
            }
        }

        return sanitizedHandString;
    }
    @Test
    void evaluateRoyalFlush() {
        Deck deck = new Deck();
        deck.init("TJQKA");
        System.out.println(deck.asString());
        ArrayList<Hand> handList = genStraightHands(deck, 8);

        for(Hand hand: handList){
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            assertEquals(eval.getKey(), Hand.HAND_RANK.ROYAL_FLUSH);
            assertEquals(eval.getValue().highnessAsString(), hand.getCardAt(hand.getMaxSize() - 1).getRank());

        }
    }

    @Test
    void evaluateStraightFlush() {
        Deck deck = new Deck();

        ArrayList<Hand> handList = genStraightHands(deck);

        for(Hand hand: handList){
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            assertEquals(eval.getKey(), Hand.HAND_RANK.STRAIGHT_FLUSH);
            hand.sort(CardPile.SORT_CRITERIA.RANK);
            assertEquals(eval.getValue().highnessAsString(), hand.getCardAt(hand.getMaxSize() - 1).getRank());
        }
    }

    @Test
    void evaluateFlush() {
        Deck initialDeck = new Deck();
        Deck deck = initialDeck.shuffle();
        deck.sort(CardPile.SORT_CRITERIA.COLOR);
        ArrayList<Hand> handList = genStraightHands(deck);

        for(Hand hand: handList){
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            if (eval.getKey() != Hand.HAND_RANK.STRAIGHT_FLUSH) {
                assertEquals(eval.getKey(), Hand.HAND_RANK.FLUSH);
                hand.sort(CardPile.SORT_CRITERIA.RANK);
                assertEquals(eval.getValue().highnessAsString(), hand.getCardAt(hand.getMaxSize() - 1).getRank());
            }
        }
    }

    /**
     * Particular straight tested here
     */
    void evaluateWheel() {
        Deck deck = deckFromString("2c/3d/4h/5s/Ac/2d/3h/4s/5c/Ad/2h/3s/4c/5d/Ah/2s/3c/4d/5h/As");
        Hand hand = new Hand();

            for(int i = 0; i < deck.size(); i+=5) {
                hand.clear();
                for (int j = 0; j < hand.getMaxSize(); j++) {
                    hand.pushCard(deck.getCardAt(i + j));
                }
                Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
                assertEquals(eval.getKey(), Hand.HAND_RANK.STRAIGHT);
                hand.sort(CardPile.SORT_CRITERIA.RANK);
                assertEquals(eval.getValue().highnessAsString(), hand.getCardAt(3).getRank());
            }
        }

    @Test
    void evaluateStraight() {
        Deck deck = generateDeck();
        ArrayList<Hand> handList = genStraightHands(deck);

        // All straights except wheel
        for(Hand hand: handList){
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            assertEquals(eval.getKey(), Hand.HAND_RANK.STRAIGHT);
            hand.sort(CardPile.SORT_CRITERIA.RANK);
            assertEquals(eval.getValue().highnessAsString(), hand.getCardAt(hand.getMaxSize() - 1).getRank());
        }

        //Wheel straight (A2345)
        evaluateWheel();
    }

    @Test
    void evaluateFourOfAKind() {
        Deck deck = new Deck();
        deck.sort(CardPile.SORT_CRITERIA.RANK);
        ArrayList<Hand> handList = new ArrayList<>();

        for (int k = 0; k < deck.size() -5; k += 4) {

            Hand hand = new Hand();
            for (int j = 0; j < hand.getMaxSize(); j++) {
                hand.pushCard(deck.getCardAt(k + j));
            }
            handList.add(hand);
        }

        // PARTICULAR case for FOK Ace
        Hand lastHand = new Hand();
        for(int i = 0; i < 5; i++) {
            lastHand.pushCard(deck.getCardAt(deck.getMaxSize() - i - 1));
        }
        handList.add(lastHand);

        int FOKCount = 0;
        for(Hand hand: handList){
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            assertEquals(eval.getKey(), Hand.HAND_RANK.FOUR_OF_A_KIND);
            assertEquals(eval.getValue().highnessAsString(), hand.getCardAt(hand.getMaxSize() - 1).getRank());
            FOKCount += 1;
        }

        assertEquals(13, FOKCount);
    }

    @Test
    void evaluateFullHouse() {
        ArrayList<Hand> handList = new ArrayList<>();
        Deck deck = new Deck();
        deck.sort(CardPile.SORT_CRITERIA.RANK);

        for(int i = 1; i < deck.getMaxSize() - 3; i+=4) {
            Hand hand = new Hand();
            for (int j = 0; j < hand.getMaxSize(); j+=1) {
                hand.pushCard(deck.getCardAt(i + j));
            }
            handList.add(hand);
        }

        Hand aceHand = new Hand();
        for (int k = 1; k < aceHand.getMaxSize() + 1; k+=1) {
            aceHand.pushCard(deck.getCardAt(deck.getMaxSize() - k - 1));
        }
        handList.add(aceHand);

        Deck deckTest = deckFromString("2c/2d/3c/3d/2h");
        Hand testHand = new Hand();
        for (int k = 0; k < deckTest.size(); k+=1) {
            testHand.pushCard(deckTest.getCardAt(k));
        }
        handList.add(testHand);

        for(Hand hand: handList){
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            HandRankInfo rankInfo = eval.getValue();
            assertEquals(Hand.HAND_RANK.FULL_HOUSE, eval.getKey());

            Hand handCheck = new Hand(hand);
            handCheck.sort(CardPile.SORT_CRITERIA.RANK);

            int Pair_Index = rankInfo.getPairIndex();
            ArrayList<Map.Entry<Integer, String>> rankReps = rankInfo.getRankRepetitions();

            String rankInfoPair = rankReps.get(Pair_Index).getValue();
            char firstCard = handCheck.getCardAt(0).getRank().charAt(0);
            int count = (int) handCheck.asString(CardPile.SORT_CRITERIA.RANK, "").chars().filter(ch -> ch == firstCard).count();

            if (count == 2) {
                // PAIR
                assertEquals(rankInfo.highnessAsString(), handCheck.getCardAt(handCheck.size() -1).getRank());
                assertEquals(rankInfoPair,Character.toString(firstCard));
            } else {
                // TOK
                assertEquals(rankInfo.highnessAsString(), Character.toString(firstCard));
                assertEquals(rankInfoPair, handCheck.getCardAt(handCheck.size() -1).getRank());
            }
        }
    }

    @Test
    void evaluateThreeOfAKind() {
        Deck deck = deckFromString("2c/2d/2h/3c/3d/3h/4c/4d/4h/5c/5d/5h/6c/6d/6h/7c/7d/7h/8c/8d/8h/9c/9d/9h/Tc/Td/Th/Jc/Jd/Jh/Qc/Qd/Qh/Kc/Kd/Kh/Ac/Ad/Ah/2s/3s");
        ArrayList<Hand> handList = new ArrayList<>();
        for(int i = 0; i < deck.size() -3; i += 3) {
            Hand hand = new Hand();
            for (int j = 0; j <= 3; j+=1) {
                hand.pushCard(deck.getCardAt(i + j));
            }

            String newRANKS = Card.RANKS;
            newRANKS = newRANKS.replace("" + newRANKS.charAt(i / 3), "");

            Random rd = new Random();
            for (int j = 0; j <= 2; j +=1) {
                int randInt = rd.nextInt(newRANKS.length() - 1);
                hand.pushCard(new Card(newRANKS.charAt(randInt) + "c"));
                newRANKS = newRANKS.replace("" + newRANKS.charAt(randInt), "");
            }
            handList.add(hand);
        }

        for(Hand hand: handList){
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            assertEquals(eval.getKey(), Hand.HAND_RANK.THREE_OF_A_KIND);

            Hand newHand = new Hand(hand);
            newHand.sort(CardPile.SORT_CRITERIA.RANK);

            String sanitizedHandString = removeRankRepetition(newHand.asString(CardPile.SORT_CRITERIA.RANK, ""), 3);

            assertEquals(eval.getValue().highnessAsString(), Character.toString(sanitizedHandString.charAt(sanitizedHandString.length() - 1)));
        }

    }

    void addPair(Hand hand, char rank) {
        hand.pushCard(new Card(rank + "c"));
        hand.pushCard(new Card(rank + "d"));
    }

    @Test
    void evaluateDoublePair() {
        Deck deck = new Deck();
        ArrayList<ImmutableKV<Hand, String>> handList = new ArrayList<>();
        for (int i = 0; i < Card.RANKS.length(); i+=1) {
            for (int j = i+1; j < Card.RANKS.length(); j+=1) {
                Hand hand = new Hand();
                addPair(hand, Card.RANKS.charAt(i));
                addPair(hand, Card.RANKS.charAt(j));

                String newRANKS = Card.RANKS;
                newRANKS = newRANKS.replace("" + Card.RANKS.charAt(i), "");
                newRANKS = newRANKS.replace("" + Card.RANKS.charAt(j), "");
                // System.out.println(Card.RANKS.charAt(i) + " " + Card.RANKS.charAt(j) + " " + newRANKS);

                Random rd = new Random();
                int randInt = rd.nextInt(newRANKS.length() - 1);
                Card randomCard = new Card(newRANKS.charAt(randInt) + "h");
                hand.pushCard(randomCard);

                String sanitizedHandString = removeRankRepetition(hand.asString(CardPile.SORT_CRITERIA.RANK, ""), 2);
                sanitizedHandString = removeRankRepetition(sanitizedHandString, 2);
                handList.add(new ImmutableKV<Hand, String>(hand, sanitizedHandString));
            }
        }

        for(int i = 0; i < handList.size(); i+=1) {
            Hand hand = handList.get(i).getKey();
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            assertEquals(Hand.HAND_RANK.DOUBLE_PAIR, eval.getKey());
            assertEquals(eval.getValue().highnessAsString(), handList.get(i).getValue());
        }
    }


    @Test
    void evaluatePair() {
        Deck deck = new Deck();
        ArrayList<ImmutableKV<Hand, String>> handList = new ArrayList<>();
        for (int i = 0; i < Card.RANKS.length(); i+=1) {
            Hand hand = new Hand();
            addPair(hand, Card.RANKS.charAt(i));

            String newRANKS = Card.RANKS;
            newRANKS = newRANKS.replace("" + Card.RANKS.charAt(i), "");

            Random rd = new Random();
            do {
                int randInt = rd.nextInt(newRANKS.length() - 1);
                newRANKS = newRANKS.replace("" + newRANKS.charAt(randInt), "");
                Card randomCard = new Card(newRANKS.charAt(randInt) + "h");
                hand.pushCard(randomCard);
            } while(hand.size() < hand.getMaxSize());

            Hand newHand = new Hand(hand);
            newHand.sort(CardPile.SORT_CRITERIA.RANK);
            String sanitizedHandString = removeRankRepetition(newHand.asString(CardPile.SORT_CRITERIA.RANK, ""), 2);
            handList.add(new ImmutableKV<Hand, String>(hand, "" + sanitizedHandString.substring(sanitizedHandString.length()-1)));
        }

        for(int i = 0; i < handList.size(); i+=1) {
            Hand hand = handList.get(i).getKey();
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            System.out.println(hand.asString());

            assertEquals(Hand.HAND_RANK.PAIR, eval.getKey());
            assertEquals(eval.getValue().highnessAsString(), handList.get(i).getValue());
        }
    }

    @Test
    void evaluateCardHigh() {
        Deck deck = new Deck();
        ArrayList<Hand> handList = new ArrayList<>();

        for (int k = 0; k < 600; k += 1) {
            Hand hand = new Hand();
            for (int j = 0; j < hand.getMaxSize(); j++) {

                String newRANKS = Card.RANKS;

                do {
                    Random rd = new Random();
                    int randInt = rd.nextInt(newRANKS.length());
                    char c = newRANKS.charAt(randInt);
                    newRANKS = newRANKS.replace(Character.toString(c), "");
                    String color = Character.toString(Card.COLORS.charAt(hand.size() % 4));
                    Card randomCard = new Card(c + color);
                    hand.pushCard(randomCard);
                } while(hand.size() < hand.getMaxSize());
            }
            handList.add(hand);
        }

        for (Hand hand: handList) {
            Map.Entry<Hand.HAND_RANK, HandRankInfo> eval = RankEvaluator.evaluate(hand);
            System.out.println(hand.asString());
            if (eval.getKey() != Hand.HAND_RANK.STRAIGHT) {
                assertEquals(eval.getKey(), Hand.HAND_RANK.CARD_HIGH);

                Hand newHand = new Hand(hand);
                newHand.sort(CardPile.SORT_CRITERIA.RANK);
                String highness = newHand.asString(CardPile.SORT_CRITERIA.RANK);
                highness = highness.substring(highness.length() - 1);

                assertEquals(eval.getValue().highnessAsString(), highness);
            }

        }
    }
}