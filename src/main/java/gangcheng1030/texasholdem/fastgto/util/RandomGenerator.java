package gangcheng1030.texasholdem.fastgto.util;

import java.util.Random;

public class RandomGenerator {
    private static final Random random = new Random(System.currentTimeMillis());

    public static int nextInt(int n) {
        return random.nextInt(100);
    }
}
