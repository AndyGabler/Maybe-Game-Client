package com.andronikus.gameclient.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Test of {@link ConcurrentInputManager}.
 *
 * @author Andronikus
 */
public class ConcurrentInputManagerTest {

    @Test
    public void test() {
        final ConcurrentInputManager manager = new ConcurrentInputManager();

        manager.addToQueue("input0");
        manager.addToQueue("input1");

        final List<String> codeSet0 = manager.getUnhandledCodes();

        manager.addToQueue("input2");
        manager.addToQueue("input3");
        manager.addToQueue("input4");

        final List<String> codeSet1 = manager.getUnhandledCodes();

        Assertions.assertEquals(2, codeSet0.size());
        Assertions.assertEquals("input0", codeSet0.get(0));
        Assertions.assertEquals("input1", codeSet0.get(1));

        Assertions.assertEquals(3, codeSet1.size());
        Assertions.assertEquals("input2", codeSet1.get(0));
        Assertions.assertEquals("input3", codeSet1.get(1));
        Assertions.assertEquals("input4", codeSet1.get(2));
    }
}
