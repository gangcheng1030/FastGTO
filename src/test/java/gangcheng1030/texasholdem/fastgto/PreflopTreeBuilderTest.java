package gangcheng1030.texasholdem.fastgto;

import gangcheng1030.texasholdem.fastgto.core.PreflopTreeBuilder;
import gangcheng1030.texasholdem.fastgto.core.PreflopTreeNode;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class PreflopTreeBuilderTest {
    @Test
    public void buildPreflopTree() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            String path = "PioRanges_nlhe_100bb_2.5x_NL200";
            PreflopTreeNode root = PreflopTreeBuilder.buildPreflopTree(Paths.get(classLoader.getResource(path).toURI()));
            printPreflopTree(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printPreflopTree(PreflopTreeNode root) {
        if (root == null) {
            return;
        }
        System.out.println(root);
        for (PreflopTreeNode node : root.getChildren().values()) {
            printPreflopTree(node);
        }
    }
}
