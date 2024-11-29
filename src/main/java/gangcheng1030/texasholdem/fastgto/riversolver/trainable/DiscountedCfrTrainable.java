package gangcheng1030.texasholdem.fastgto.riversolver.trainable;

import com.alibaba.fastjson.JSONObject;
import gangcheng1030.texasholdem.fastgto.riversolver.nodes.ActionNode;
import gangcheng1030.texasholdem.fastgto.riversolver.nodes.GameActions;
import gangcheng1030.texasholdem.fastgto.riversolver.ranges.PrivateCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huangxuefeng on 2019/10/12.
 * trainable by discounted cfr
 */
public class DiscountedCfrTrainable extends Trainable{
    ActionNode action_node;
    PrivateCards[] privateCards;
    int action_number;
    int card_number;
    float[] r_plus = null;
    float alpha = 1.5f;
    float beta = 0.5f;
    float gamma = 2;
    float theta = 0.9f;
    float[][] reach_probs;
    float[] evs;

    public PrivateCards[] getPrivateCards() {
        return privateCards;
    }

    public float[] getR_plus() {
        return r_plus;
    }

    public float[] getR_plus_sum() {
        return r_plus_sum;
    }

    public float[] getCum_r_plus() {
        return cum_r_plus;
    }

    public float[] getCum_r_plus_sum() {
        return cum_r_plus_sum;
    }

    public float[][] getReach_probs() {
        return reach_probs;
    }

    float[] r_plus_sum = null;

    float[] cum_r_plus = null;
    float[] cum_r_plus_sum = null;

    float[] regrets = null;

    public void setReach_probs(float[][] reach_probs) {
        this.reach_probs = reach_probs;
    }

    public float[] getEvs() {
        return evs;
    }

    public void setEvs(float[] evs) {
        this.evs = evs;
    }

    public DiscountedCfrTrainable(ActionNode action_node, PrivateCards[] privateCards){
        this.action_node = action_node;
        this.privateCards = privateCards;
        this.action_number = action_node.getChildrens().size();
        this.card_number = privateCards.length;

        this.r_plus = new float[this.action_number * this.card_number];
        this.r_plus_sum = new float[this.card_number];

        this.cum_r_plus = new float[this.action_number * this.card_number];
        this.cum_r_plus_sum = new float[this.card_number];
    }

    private boolean isAllZeros(float[] input_array){
        for(float i:input_array){
            if (i != 0)return false;
        }
        return true;
    }

    @Override
    public float[] getAverageStrategy() {
        float[] retval = new float[this.action_number * this.card_number];
        if(this.cum_r_plus_sum == null || this.isAllZeros(this.cum_r_plus_sum)){
            Arrays.fill(retval,Float.valueOf(1) / (this.action_number));
        }else {
            for (int action_id = 0; action_id < action_number; action_id++) {
                for (int private_id = 0; private_id < this.card_number; private_id++) {
                    int index = action_id * this.card_number + private_id;
                    if(this.cum_r_plus_sum[private_id] != 0) {
                        retval[index] = this.cum_r_plus[index] / this.cum_r_plus_sum[private_id];
                    }else{
                        retval[index] = Float.valueOf(1) / (this.action_number);
                    }
                }
            }
        }
        //return this.getcurrentStrategy();
        return retval;
    }

    @Override
    public float[] getcurrentStrategy() {
        float[] retval = new float[this.action_number * this.card_number];
        if(this.r_plus_sum == null ){
            Arrays.fill(retval,Float.valueOf(1) / (this.action_number));
        }else {
            for (int action_id = 0; action_id < action_number; action_id++) {
                for (int private_id = 0; private_id < this.card_number; private_id++) {
                    int index = action_id * this.card_number + private_id;
                    if(this.r_plus_sum[private_id] != 0) {
                        retval[index] = Math.max(this.r_plus[index],0) / this.r_plus_sum[private_id];
                    }else{
                        retval[index] = Float.valueOf(1) / (this.action_number);
                    }
                    if(this.r_plus[index] != this.r_plus[index]) throw new RuntimeException();
                }
            }
        }
        return retval;
    }

    @Override
    public void updateRegrets(float[] regrets,int iteration_number, float[] reach_probs) {
        this.regrets = regrets;
        if(regrets.length != this.action_number * this.card_number) throw new RuntimeException("length not match");

        float alpha_coef = (float) Math.pow((double) iteration_number, this.alpha);
        alpha_coef = alpha_coef / (1 + alpha_coef);

        //Arrays.fill(this.r_plus_sum,0);
        Arrays.fill(this.r_plus_sum,0);
        Arrays.fill(this.cum_r_plus_sum,0);
        for (int action_id = 0;action_id < action_number;action_id ++) {
            for(int private_id = 0;private_id < this.card_number;private_id ++){
                int index = action_id * this.card_number + private_id;
                float one_reg = regrets[index];

                // 更新 R+
                this.r_plus[index] = one_reg + this.r_plus[index];
                if(this.r_plus[index] > 0){
                    this.r_plus[index] *= alpha_coef;
                }else{
                    this.r_plus[index] *= beta;
                }

                this.r_plus_sum[private_id] += Math.max(0,this.r_plus[index]);

                // 更新累计策略
                // this.cum_r_plus[index] += this.r_plus[index] * iteration_number;
                // this.cum_r_plus_sum[private_id] += this.cum_r_plus[index];
            }
        }
        float[] current_strategy = this.getcurrentStrategy();
        float strategy_coef = (float)Math.pow(((float)iteration_number / (iteration_number + 1)),gamma);
        for (int action_id = 0;action_id < action_number;action_id ++) {
            for(int private_id = 0;private_id < this.card_number;private_id ++) {
                int index = action_id * this.card_number + private_id;
                this.cum_r_plus[index] *= this.theta;
                this.cum_r_plus[index] += current_strategy[index] * strategy_coef * reach_probs[private_id];
                this.cum_r_plus_sum[private_id] += this.cum_r_plus[index] ;
            }
        }
    }

    @Override
    public JSONObject dumps(boolean with_state) {
        if(with_state) throw new RuntimeException("state storage not implemented");

        JSONObject strategy = new JSONObject();
        float[] average_strategy = this.getAverageStrategy();
        List<GameActions> game_actions = action_node.getActions();
        List<String> actions_str = new ArrayList<>();
        for(GameActions one_action:game_actions) actions_str.add(
                one_action.toString()
        );

        for(int i = 0;i < this.privateCards.length;i ++){
            PrivateCards one_private_card = this.privateCards[i];
            float[] one_strategy = new float[this.action_number];


            for(int j = 0;j < this.action_number;j ++){
                int strategy_index = j * this.privateCards.length + i;
                one_strategy[j] = average_strategy[strategy_index];
            }
            strategy.put(String.format("%s",one_private_card.toString()),
                    one_strategy
                    );
        }

        JSONObject retjson = new JSONObject();
        retjson.put("actions",actions_str);
        retjson.put("strategy",strategy);
        return retjson;
    }
}
