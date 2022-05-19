package org.quazarspirit.holdem4j.game_logic;

import org.jetbrains.annotations.NotNull;
import org.quazarspirit.holdem4j.game_logic.card_pile.CardPile;
import org.quazarspirit.holdem4j.game_logic.card_pile.Hand;
import org.quazarspirit.utils.ImmutableKV;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
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
    static public Map.Entry<Hand.HAND_RANK, HandRankInfo> evaluate(Hand refHand) {

        Map.Entry<Boolean, HandRankInfo> isRoyalFlushCheck = isRoyalFlush(refHand);
        if (isRoyalFlushCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, HandRankInfo>(Hand.HAND_RANK.ROYAL_FLUSH, isRoyalFlushCheck.getValue());
        }

        Map.Entry<Boolean, HandRankInfo> isStraightFlushCheck = isStraightFlush(refHand);
        if (isStraightFlushCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, HandRankInfo>(Hand.HAND_RANK.STRAIGHT_FLUSH, isStraightFlushCheck.getValue());
        }

        Map.Entry<Boolean, HandRankInfo> isFlushCheck = isFlush(refHand);
        if (isFlushCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, HandRankInfo>(Hand.HAND_RANK.FLUSH, isFlushCheck.getValue());
        }

        Map.Entry<Boolean, HandRankInfo> isStraightCheck = isStraight(refHand);
        if (isStraightCheck.getKey()) {
            return new ImmutableKV<Hand.HAND_RANK, HandRankInfo>(Hand.HAND_RANK.STRAIGHT, isStraightCheck.getValue());
        }

        // TODO: REPAIR
        HandRankInfo rankInfo = getRankRepetitions(refHand);
        ArrayList<Map.Entry<Integer, String>> rankRepetition = rankInfo.getRankRepetitions();
        int rnkRepSize = rankRepetition.size();
        if (rnkRepSize > 0 && rnkRepSize <= 2) {
            Hand.HAND_RANK handRankResult;
            String highnessResult;

            if (rnkRepSize == 1) {
                Map.Entry<Integer, String> rankRepKV = rankRepetition.get(0);
                if (rankRepKV.getKey() == 4) {
                    handRankResult = Hand.HAND_RANK.FOUR_OF_A_KIND;
                } else if (rankRepKV.getKey() == 3) {
                    handRankResult = Hand.HAND_RANK.THREE_OF_A_KIND;
                } else {
                    handRankResult = Hand.HAND_RANK.PAIR;
                }

                // highnessResult = rankRepKV.getValue();
                highnessResult = String.valueOf(Card.RANKS.charAt(rankInfo.getHighness()));
            } else {
                // String is Card value and Integer Card rank
                Map.Entry<Integer, String> repRank_1 = rankRepetition.get(0);
                Map.Entry<Integer, String> repRank_2 = rankRepetition.get(1);

                // Warning Integer comparison vs int
                if (Objects.equals(repRank_1.getKey(), repRank_2.getKey())) {
                    handRankResult = Hand.HAND_RANK.DOUBLE_PAIR;
                    highnessResult = String.valueOf(Card.RANKS.charAt(rankInfo.getHighness()));
                } else {
                    handRankResult = Hand.HAND_RANK.FULL_HOUSE;
                    highnessResult = String.valueOf(Card.RANKS.charAt(rankInfo.getHighness()))
                        + ":" + rankInfo.getRankRepetitions().get(rankInfo.getPairIndex()).getValue();
                }
            }
            return new ImmutableKV<Hand.HAND_RANK, HandRankInfo>(handRankResult, rankInfo);
        }

        Hand hand = new Hand(refHand);
        hand.sort(CardPile.SORT_CRITERIA.RANK);

        return new ImmutableKV<Hand.HAND_RANK, HandRankInfo>(Hand.HAND_RANK.CARD_HIGH, new HandRankInfo(null, hand.getCardAt(hand.size() -1).getRankAsInt()));
    }

    /**
     * Method which sorts hand by rank and then checks if Card.RANKS contains handAsString sequence
     * @param refHand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, HandRankInfo> isStraight(@NotNull Hand refHand) {
        Hand hand = new Hand(refHand);
        // TODO: REFACTORISATION with isFlush
        hand.sort(CardPile.SORT_CRITERIA.RANK);

        String handAsString = hand.asString(CardPile.SORT_CRITERIA.RANK, "");

        // Check if handAsString equals any substring or A2345 Straight
        if (Card.RANKS.contains(handAsString) ) {
            Card lastCard = hand.getCardAt(hand.size()  -1);
            HandRankInfo handRankInfo = new HandRankInfo(null, lastCard.getRankAsInt());
            return new ImmutableKV<Boolean, HandRankInfo>(Boolean.TRUE, handRankInfo);
        } else if (handAsString.equals("2345A")) {
            Card lastCard = hand.getCardAt(3);
            HandRankInfo handRankInfo = new HandRankInfo(null, lastCard.getRankAsInt());
            return new ImmutableKV<Boolean, HandRankInfo>(Boolean.TRUE, handRankInfo);
        }

        return new ImmutableKV<Boolean, HandRankInfo>(Boolean.FALSE, new HandRankInfo());
    }

    /**
     * Method which sorts hand by color and then checks if Card.COLORS contains handAsString sequence
     * @param refHand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, HandRankInfo> isFlush(@NotNull Hand refHand) {
        Hand hand = new Hand(refHand);
        hand.sort(CardPile.SORT_CRITERIA.COLOR);

        String handAsString = hand.asString(CardPile.SORT_CRITERIA.COLOR, "");

        if (FLUSH_PATTERN.matcher(handAsString).matches()) {
            hand.sort(CardPile.SORT_CRITERIA.RANK);
            Card lastCard = hand.getCardAt(hand.size()  -1);
            HandRankInfo handRankInfo = new HandRankInfo(null, lastCard.getRankAsInt());
            return new ImmutableKV<Boolean, HandRankInfo>(Boolean.TRUE, handRankInfo);
        }

        return new ImmutableKV<Boolean, HandRankInfo>(Boolean.FALSE, new HandRankInfo());
    }

    /**
     * Checks if straight flush is present in hand and returns HIGHER CARD
     * @param hand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, HandRankInfo> isStraightFlush(Hand hand) {
        Map.Entry<Boolean, HandRankInfo> isStraightCheck = isStraight(hand);
        boolean isStraight = (boolean) isStraightCheck.getKey();
        boolean isFlush =  (boolean) isFlush(hand).getKey();

        if (isStraight && isFlush) {
            HandRankInfo lastCard = isStraightCheck.getValue();
            return new ImmutableKV<Boolean, HandRankInfo>(Boolean.TRUE, lastCard);
        }

        return new ImmutableKV<Boolean, HandRankInfo>(Boolean.FALSE, new HandRankInfo());
    }

    /**
     * Checks if ROYAL flush is present in hand and returns Ace high card
     * @param hand Hand object
     * @return Entry with boolean and highest card
     */
    static private Map.Entry<Boolean, HandRankInfo> isRoyalFlush(Hand hand) {
        Map.Entry<Boolean, HandRankInfo> isStraightFlushCheck = isStraightFlush(hand);

        if (isStraightFlushCheck.getKey() && isStraightFlushCheck.getValue().getHighness() == 12) {
            return new ImmutableKV<Boolean, HandRankInfo>(Boolean.TRUE, isStraightFlushCheck.getValue());
        }

        return new ImmutableKV<Boolean, HandRankInfo>(Boolean.FALSE, new HandRankInfo());
    }

    /**
     * Return filled ArrayList of Map.Entry with { occurrences : rank} key pair
     * @return Map Entry of Card string and his occurrences example:
     * [2: "T", 3: "K"]
     */
    static private HandRankInfo getRankRepetitions(Hand refHand) {
        Hand hand = new Hand(refHand);
        hand.sort(CardPile.SORT_CRITERIA.RANK);

        ArrayList<Map.Entry<Integer, String>> rankRepetitions = new  ArrayList<>();
        String handAsString = hand.asString(CardPile.SORT_CRITERIA.RANK, "");

        for(char rank: Card.RANKS.toCharArray()) {
            int count = (int) handAsString.chars().filter(ch -> ch == rank).count();

            // Means that there is multiple occurrence of current rank
            if (count >= 2) {
                rankRepetitions.add(new ImmutableKV<Integer, String>(count, Character.toString(rank)));

                // Remove current rank from hand String representation bc we just found it
                handAsString = handAsString.replace(Character.toString(rank), "");
            }

        }

        // Works only if there is still something in handAsString (all cases EXCEPT FulL House)
        if (handAsString.length() > 0) {
            return new HandRankInfo(rankRepetitions, Card.RANKS.indexOf(handAsString.charAt(handAsString.length() - 1)));
        }


        int rep_0 = rankRepetitions.get(0).getKey();
        int rep_1 =  rankRepetitions.get(1).getKey();

        int TOK_index = rep_0 > rep_1 ? 0 : 1;
        int Pair_index = rep_0 > rep_1 ? 1 : 0;
        HandRankInfo handRankInfo = new HandRankInfo(rankRepetitions, Card.RANKS.indexOf(rankRepetitions.get(TOK_index).getValue()));
        handRankInfo.setPairIndex(Pair_index);
        return handRankInfo;
    }
}
class HandRankInfo {
    private ArrayList<Map.Entry<Integer, String>> _rankRepetitions =  new ArrayList<>();
    private final int _highness;
    private int _pairIndex;

    HandRankInfo() {
        _highness = -1;
    }

    HandRankInfo(ArrayList<Map.Entry<Integer, String>> rankReps, int highnessVal) {
        if (rankReps != null) {
            _rankRepetitions = rankReps;
        }
        _highness = highnessVal;
    }

    public int getHighness() {
        return _highness;
    }

    public int getPairIndex() {
        return _pairIndex;
    }

    public void setPairIndex(int newIndex) {
        if( _pairIndex != 0 && _pairIndex != 1) {
            return;
        }

        _pairIndex = newIndex;
    }

    public ArrayList<Map.Entry<Integer, String>> getRankRepetitions() {
        return _rankRepetitions;
    }

    public String highnessAsString() {
        if (_highness >= 0 && _highness < Card.RANKS.length()) {
            return String.valueOf(Card.RANKS.charAt(_highness));
        }

        return "NONE";
    }
}