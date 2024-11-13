package gangcheng1030.texasholdem.fastgto;

import gangcheng1030.texasholdem.fastgto.core.PreflopTree;
import org.junit.Test;

import java.util.Arrays;

public class PreflopTreeTest {
    @Test
    public void getRaiseRanges() {
        PreflopTree preflopTree = new PreflopTree();
        Double[] res =  preflopTree.getRaiseRanges("LJ", new String[]{});
        System.out.println(Arrays.stream(res).toList());

        res = preflopTree.getRaiseRanges("HJ", new String[]{"LJ2bet"});
        System.out.println(Arrays.stream(res).toList());
    }

    @Test
    public void getCallRanges() {
        PreflopTree preflopTree = new PreflopTree();
        Double[] res =  preflopTree.getCallRanges("LJ", new String[]{});
        System.out.println(Arrays.stream(res).toList());
    }
}
