package util;
import java.util.Random;

public final class RandomUtil {
    private static final Random RANDOM = new Random();

    public static int nextInt(int begin, int end) {
        return begin + RANDOM.nextInt(end - begin + 1);
    }
}
