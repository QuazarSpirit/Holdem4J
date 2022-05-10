package org.quazarspirit.holdem4j.game_logic;

import org.jetbrains.annotations.NotNull;
import org.quazarspirit.holdem4j.game_logic.card_pile.CardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.Hand;
import org.quazarspirit.utils.ImmutableKV;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class which evaluate Hand highness with provided rank
 */
public class RankEvaluator {
    static final Pattern FLUSH_PATTERN = Pattern.compile("^[Cc]{5}|[Dd]{5}|[Hh]{5}|[Ss]{5}$");

    /**
     * Method
     * @return Entry that returns both rank and rank as String
     * return example: {Hand.HAND_RANK.FULL_HOUSE, "2:3"}
     */
    static public Map.Entry<Hand.HAND_RANK, String> evaluate(Hand refHand) {

        Map.Entry<Boolean, Card> isRoyalFlushCheck = isRoyalFlush(refHand);
        if (isRoyalFlushCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, String>(Hand.HAND_RANK.ROYAL_FLUSH, isRoyalFlushCheck.getValue().getRank());
        }

        Map.Entry<Boolean, Card> isStraightFlushCheck = isStraightFlush(refHand);
        if (isStraightFlushCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, String>(Hand.HAND_RANK.STRAIGHT_FLUSH, isStraightFlushCheck.getValue().getRank());
        }

        Map.Entry<Boolean, Card> isFlushCheck = isFlush(refHand);
        if (isFlushCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, String>(Hand.HAND_RANK.FLUSH, isFlushCheck.getValue().getRank());
        }

        Map.Entry<Boolean, Card> isStraightCheck = isStraight(refHand);
        if (isStraightCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, String>(Hand.HAND_RANK.STRAIGHT, isStraightCheck.getValue().getRank());
        }

        ArrayList<Map.Entry<String, Integer>> rankRepetition = getRankRepetition(refHand);
        int rnkRepSize = rankRepetition.size();
        if (rnkRepSize > 0 && rnkRepSize <= 2) {
            Hand.HAND_RANK handRankResult;
            String cardRankResult;

            if (rnkRepSize == 1) {
                Map.Entry<String, Integer> rankRepKV = rankRepetition.get(0);
                cardRankResult = rankRepKV.getKey();
                if (rankRepKV.getValue() == 4) {
                    handRankResult = Hand.HAND_RANK.FOUR_OF_A_KIND;
                } else if (rankRepKV.getValue() == 3) {
                    handRankResult = Hand.HAND_RANK.THREE_OF_A_KIND;
                } else {
                    handRankResult = Hand.HAND_RANK.PAIR;
                }
            } else {
                // String is Card value and Integer Card rank
                Map.Entry<String, Integer> rankRes_1 = rankRepetition.get(0);
                Map.Entry<String, Integer> rankRes_2 = rankRepetition.get(1);

                int rank_0 = 0, rank_1 = 0, finalMin = 0, finalMax = 0;

                if (rankRes_1.getValue().equals(rankRes_2.getValue())) {
                    handRankResult = Hand.HAND_RANK.DOUBLE_PAIR;

                    rank_0 = Card.RANKS.indexOf(rankRes_1.getKey());
                    rank_1 = Card.RANKS.indexOf(rankRes_2.getKey());

                } else {
                    handRankResult = Hand.HAND_RANK.FULL_HOUSE;
                    rank_0 = rankRes_1.getValue();
                    rank_1 = rankRes_2.getValue();

                }

                finalMin = Math.min(rank_0, rank_1);
                finalMax = Math.max(rank_0, rank_1);

                cardRankResult = finalMin + ":" + finalMax;
            }


            return new ImmutableKV<Hand.HAND_RANK, String>(handRankResult, cardRankResult);
        }

        Hand hand = new Hand(refHand);
        hand.sort(CardPile.SORT_CRITERIA.RANK);

        return new ImmutableKV<Hand.HAND_RANK, String>(Hand.HAND_RANK.CARD_HIGH, hand.getCardAt(hand.size() -1).getRank());
    }

    /**
     * Method which sorts hand by rank and then checks if Card.RANKS contains handAsString sequence
     * @param refHand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, Card> isStraight(@NotNull Hand refHand) {
        Hand hand = new Hand(refHand);
        // TODO: REFACTORISATION with isFlush
        hand.sort(CardPile.SORT_CRITERIA.RANK);

        String handAsString = hand.asString(CardPile.SORT_CRITERIA.RANK).replace(CardPile.CARD_CHAR_SEPARATOR, "");
        if (Card.RANKS.contains(handAsString)) {
            Card lastCard = hand.getCardAt(hand.size()  -1);
            return new ImmutableKV<Boolean, Card>(Boolean.TRUE, lastCard);
        }

        return new ImmutableKV<Boolean, Card>(Boolean.FALSE, NullCard.GetSingleton());
    }

    /**
     * Method which sorts hand by color and then checks if Card.COLORS contains handAsString sequence
     * @param refHand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, Card> isFlush(@NotNull Hand refHand) {
        Hand hand = new Hand(refHand);
        hand.sort(CardPile.SORT_CRITERIA.COLOR);

        String handAsString = hand.asString(CardPile.SORT_CRITERIA.COLOR).replace(CardPile.CARD_CHAR_SEPARATOR, "");

        if (FLUSH_PATTERN.matcher(handAsString).matches()) {
            hand.sort(CardPile.SORT_CRITERIA.RANK);
            Card lastCard = hand.getCardAt(hand.size()  -1);
            return new ImmutableKV<Boolean, Card>(Boolean.TRUE, lastCard);
        }

        return new ImmutableKV<Boolean, Card>(Boolean.FALSE, NullCard.GetSingleton());
    }

    /**
     * Checks if straight flush is present in hand and returns HIGHER CARD
     * @param hand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, Card> isStraightFlush(Hand hand) {
        Map.Entry<Boolean, Card> isStraightCheck = isStraight(hand);
        boolean isStraight = (boolean) isStraightCheck.getKey();
        boolean isFlush =  (boolean) isFlush(hand).getKey();

        if (isStraight && isFlush) {
            Card lastCard = isStraightCheck.getValue();
            return new ImmutableKV<Boolean, Card>(Boolean.TRUE, lastCard);
        }

        return new ImmutableKV<Boolean, Card>(Boolean.FALSE, NullCard.GetSingleton());
    }

    /**
     * Checks if ROYAL flush is present in hand and returns Ace high card
     * @param hand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, Card> isRoyalFlush(Hand hand) {
        Map.Entry<Boolean, Card> isStraightFlushCheck = isStraightFlush(hand);

        if (isStraightFlushCheck.getKey() && isStraightFlushCheck.getValue().getRank().equals("A")) {
            return new ImmutableKV<Boolean, Card>(Boolean.TRUE, isStraightFlushCheck.getValue());
        }

        return new ImmutableKV<Boolean, Card>(Boolean.FALSE, NullCard.GetSingleton());
    }

    /**
     * Return filled hashmap with {rank, value} key pair
     * @return Hashmap of Card string and his occurrences example: {"A", 3} {"5", 2}
     */
    static private  ArrayList<Map.Entry<String, Integer>> getRankRepetition(Hand refHand) {
        Hand hand = new Hand(refHand);
        hand.sort(CardPile.SORT_CRITERIA.RANK);

        ArrayList<Map.Entry<String, Integer>> rankRepetitions = new  ArrayList<Map.Entry<String, Integer>>();
        String handAsString = hand.asString(CardPile.SORT_CRITERIA.RANK).replace(CardPile.CARD_CHAR_SEPARATOR, "");

        for(char rank: Card.RANKS.toCharArray()) {
            int count = (int) handAsString.chars().filter(ch -> ch == rank).count();

            // Means that there is multiple occurence of current rank
            if (count >= 2) {
                rankRepetitions.add(new ImmutableKV<String, Integer>(Character.toString(rank), count));
            }

            handAsString = handAsString.replace(Character.toString(rank), "");
        }

        return rankRepetitions;
    }
}
