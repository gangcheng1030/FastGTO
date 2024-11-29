package gangcheng1030.texasholdem.fastgto.riversolver.nodes;

/**
 * Created by huangxuefeng on 2019/10/7.
 * This file contains code for game actions.
 */
public class GameActions {
    GameTreeNode.PokerActions action;
    Double amount;

    public GameTreeNode.PokerActions getAction() {
        return action;
    }

    public double getAmount() {
        return amount;
    }

    public GameActions(GameTreeNode.PokerActions action, Double amount) {
        this.action = action;
        if (action == GameTreeNode.PokerActions.RAISE || action == GameTreeNode.PokerActions.BET || action == GameTreeNode.PokerActions.CALL) {
            assert (amount != null);
        } else {
            assert (amount == null || amount == 0.0);
        }
        this.amount = amount;
    }

    @Override
    public String toString() {
        if(amount == null) {
            return this.action.toString();
        }else{
            return this.action.toString() + " " + amount.toString();
        }
    }
}
