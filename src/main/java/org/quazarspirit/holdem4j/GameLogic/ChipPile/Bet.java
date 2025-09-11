package org.quazarspirit.holdem4j.GameLogic.ChipPile;

import org.quazarspirit.holdem4j.GameLogic.BettingRound;
import org.quazarspirit.holdem4j.GameLogic.Game;
import org.quazarspirit.holdem4j.PlayerLogic.Player.IPlayer;
import org.quazarspirit.holdem4j.RoomLogic.Table.Table;

public class Bet extends ChipCount implements IBet {
    private Bet(Game game, int count) {
        super(game.getUnit());
        set(count);
    }

    public static IBet createBet(int count, IPlayer player, Table table) {
        if (isValid(table.getGame(), count, player, table)) {
            return new Bet(table.getGame(), count);
        }
        return NullBet.getSingleton();
    }

    /**
     * <a href="https://www.pokerlistings.com/texas-holdem-betting-rules">Holdem
     * betting rules</a>
     * 
     * @return Result of validation by rules
     */
    public static boolean isValid(Game game, int count, IPlayer player, Table table) {
        Game.BetStructureEnum betStructure = game.getBetStructure();
        Chip unit = table.getGame().getUnit();
        ChipCount minimumBet = new ChipCount(unit, 2);
        ; // Expressed in Number of SB
        ChipCount maximumBet = minimumBet; // Same as above

        switch (betStructure) {
            case NO_LIMIT -> {
                // Minimum bet for no limit is 1 Big Blind -> 2 Small Blind
                // Because ChipCount is expressed in number of small blind
                // Maximum bet in this case is all-in for specified player
                maximumBet = table.getStack(player);
            }
            case FIXED_LIMIT -> {
                // In fixed limit in addition to blind you got bet
                // small bet is equal to 1 big blind

                // Raising is only increments of last bet, so if first bet is 4$
                // next bet will be 8$ then 12$, ..

                BettingRound.PhaseEnum roundPhase = table.getRound().getPhase();
                if (roundPhase == BettingRound.PhaseEnum.RIVER || roundPhase == BettingRound.PhaseEnum.TURN) {
                    // if roundPhase is flop or before, you can only use small bet
                    // if roundPhase is river or turn, you can only use big bet
                    minimumBet = new ChipCount(unit, 4);
                }

                // TODO: After a certain number of raise pot is capped
                if (count == minimumBet.get()) {
                    // If bet is not equal to minimumBet then bet is not valid
                    // Because in fixed limit a bet is only an increment
                    return true;
                } else {
                    return false;
                }
            }
            case POT_LIMIT -> {
                // TODO: Get pot + actual bets on table
                // Maximum bet is pot and minimum is big blind
                maximumBet = new ChipCount(unit, table.getPot().get());
            }
        }

        // Any bet between min and max bet is legal in pot limit and no limit
        return count >= minimumBet.get() && count <= maximumBet.get();
    }
}
