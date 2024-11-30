package gangcheng1030.texasholdem.fastgto;

import com.github.f4b6a3.uuid.UuidCreator;
import org.junit.Test;

import java.util.UUID;

public class UUIDTest {
    @Test
    public void testUUID() {
        for (int i = 0; i < 100; i++) {
            UUID uuid = UuidCreator.getTimeOrdered();
            System.out.println(uuid.getMostSignificantBits());
        }
    }
}
