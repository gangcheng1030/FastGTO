package gangcheng1030.texasholdem.fastgto.util;

public class HashUtil {
    public static long get32BitHash(String input) {
        return (input.hashCode() & 0xffffffffL);
    }

    public static void main(String[] args) {
        System.out.println(get32BitHash("2c2d3d") % 1024);
    }
}
