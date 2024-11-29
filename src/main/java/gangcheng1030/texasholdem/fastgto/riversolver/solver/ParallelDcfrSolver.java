package gangcheng1030.texasholdem.fastgto.riversolver.solver;

import gangcheng1030.texasholdem.fastgto.riversolver.GameTree;

import java.util.Map;

/**
 * Created by huangxuefeng on 2019/10/9.
 * Contains DCFR implemtation
 */
public class ParallelDcfrSolver extends Solver{
    public ParallelDcfrSolver(GameTree tree){
        super(tree);
    }
    @Override
    public void train(Map training_config) {
        Map<String,Object> config_map = training_config;
        Integer iterations = (Integer)config_map.get("iterations");
        if(iterations == null){
            throw new RuntimeException("iteration is null");
        }
    }
}
