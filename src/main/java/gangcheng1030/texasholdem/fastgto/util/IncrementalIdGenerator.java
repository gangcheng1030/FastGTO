package gangcheng1030.texasholdem.fastgto.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IncrementalIdGenerator {
    // 用于生成低位部分的AtomicInteger
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    // 根据当前时间戳和计数器生成ID
    public static long nextId() {
        long timestamp = timeGen();
        return timestamp << 17 | SEQUENCE.getAndIncrement();
    }

    // 高位部分的时间戳生成
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(nextId());
        }
    }
}
