package net.cutereimu.qrcode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class BitsetTest {
    private static final boolean b0 = false;
    private static final boolean b1 = true;

    @Test
    public void testNewBitset() {
        boolean[][] tests = {
                {},
                {b1},
                {b0},
                {b1, b0},
                {b1, b0, b1},
                {b0, b0, b1}
        };

        for (boolean[] v : tests) {
            Bitset result = new Bitset(v);
            Assertions.assertArrayEquals(result.bits(), v);
        }
    }

    @Test
    public void testAppend() {
        boolean[] randomBooleans = new boolean[128];

        Random rng = ThreadLocalRandom.current();

        for (int i = 0; i < randomBooleans.length; i++) {
            randomBooleans[i] = rng.nextBoolean();
        }

        for (int i = 0; i < randomBooleans.length - 1; i++) {
            Bitset a = new Bitset(Arrays.copyOfRange(randomBooleans, 0, i));
            Bitset b = new Bitset(Arrays.copyOfRange(randomBooleans, i, randomBooleans.length));

            a.append(b);

            Assertions.assertArrayEquals(a.bits(), randomBooleans);
        }
    }

    private static class TestData<T> {
        public Bitset initial;
        public T value;
        public int numBits;
        public Bitset expected;

        public TestData(Bitset initial, T value, int numBits, Bitset expected) {
            this.initial = initial;
            this.value = value;
            this.numBits = numBits;
            this.expected = expected;
        }
    }

    @Test
    public void testAppendByte() {
        List<TestData<Byte>> tests = new ArrayList<>();
        tests.add(new TestData<>(new Bitset(), (byte) 0x01, 1, new Bitset(b1)));
        tests.add(new TestData<>(new Bitset(b1), (byte) 0x01, 1, new Bitset(b1, b1)));
        tests.add(new TestData<>(new Bitset(b0), (byte) 0x01, 1, new Bitset(b0, b1)));
        tests.add(new TestData<>(new Bitset(b1, b0, b1, b0, b1, b0, b1), (byte) 0xAA, 2, new Bitset(b1, b0, b1, b0, b1, b0, b1, b1, b0)));
        tests.add(new TestData<>(new Bitset(b1, b0, b1, b0, b1, b0, b1), (byte) 0xAA, 8, new Bitset(b1, b0, b1, b0, b1, b0, b1, b1, b0, b1, b0, b1, b0, b1, b0)));

        for (TestData<Byte> test : tests) {
            test.initial.append(test.value, test.numBits);
            Assertions.assertArrayEquals(test.initial.bits(), test.expected.bits());
        }
    }

    @Test
    public void testAppendInt() {
        List<TestData<Integer>> tests = new ArrayList<>();
        tests.add(new TestData<>(new Bitset(), 0xAAAAAAAF, 4, new Bitset(b1, b1, b1, b1)));
        tests.add(new TestData<>(new Bitset(), 0xFFFFFFFF, 32,
                new Bitset(b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1, b1)));
        tests.add(new TestData<>(new Bitset(), 0x0, 32,
                new Bitset(b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0, b0)));
        tests.add(new TestData<>(new Bitset(), 0xAAAAAAAA, 32,
                new Bitset(b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0)));
        tests.add(new TestData<>(new Bitset(), 0xAAAAAAAA, 31,
                new Bitset(b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0, b1, b0)));

        for (TestData<Integer> test : tests) {
            test.initial.append(test.value, test.numBits);
            Assertions.assertArrayEquals(test.initial.bits(), test.expected.bits());
        }
    }

    @Test
    public void testAppendBooleans() {
        boolean[] randomBooleans = new boolean[128];

        Random rng = ThreadLocalRandom.current();

        for (int i = 0; i < randomBooleans.length; i++) {
            randomBooleans[i] = rng.nextBoolean();
        }

        for (int i = 0; i < randomBooleans.length - 1; i++) {
            Bitset result = new Bitset(Arrays.copyOfRange(randomBooleans, 0, i));
            result.append(new Bitset(Arrays.copyOfRange(randomBooleans, i, randomBooleans.length)));

            Assertions.assertArrayEquals(result.bits(), randomBooleans);
        }
    }

    @Test
    public void testSize() {
        boolean[] randomBooleans = new boolean[128];

        Random rng = ThreadLocalRandom.current();

        for (int i = 0; i < randomBooleans.length; i++) {
            randomBooleans[i] = rng.nextBoolean();
        }

        for (int i = 0; i < randomBooleans.length - 1; i++) {
            Bitset result = new Bitset(Arrays.copyOfRange(randomBooleans, 0, i));

            Assertions.assertEquals(result.size(), i);
        }
    }

    @Test
    public void testAt() {
        boolean[] test = {b0, b1, b0, b1, b0, b1, b1, b0, b1};

        Bitset bitset = new Bitset(test);
        for (int i = 0; i < test.length; i++) {
            boolean result = bitset.at(i);

            Assertions.assertEquals(result, test[i]);
        }
    }

    @Test
    public void test() {
        Bitset b = new Bitset();
        Assertions.assertArrayEquals(b.bits(), new boolean[0]);

        b.append(b1, b1, b0);
        Assertions.assertArrayEquals(b.bits(), new boolean[]{b1, b1, b0});

        b.append(b1);
        Assertions.assertArrayEquals(b.bits(), new boolean[]{b1, b1, b0, b1});

        b.append((byte) 0x02, 4);
        Assertions.assertArrayEquals(b.bits(), new boolean[]{b1, b1, b0, b1, b0, b0, b1, b0});
    }

    @Test
    public void testByteAt() {
        boolean[] data = {b0, b1, b0, b1, b0, b1, b1, b0, b1};

        Map<Integer, Byte> tests = new HashMap<>();
        tests.put(0, (byte) 0x56);
        tests.put(1, (byte) 0xad);
        tests.put(2, (byte) 0x2d);
        tests.put(5, (byte) 0x0d);
        tests.put(8, (byte) 0x01);

        for (Entry<Integer, Byte> test : tests.entrySet()) {
            Bitset b = new Bitset();
            b.append(data);

            byte result = b.byteAt(test.getKey());

            Assertions.assertEquals(result, test.getValue());
        }
    }

    private static class TestSubstrData {
        public int start;
        public int end;
        public boolean[] expected;

        public TestSubstrData(int start, int end, boolean[] expected) {
            this.start = start;
            this.end = end;
            this.expected = expected;
        }
    }

    @Test
    public void testSubstr() {
        boolean[] data = {b0, b1, b0, b1, b0, b1, b1, b0};

        TestSubstrData[] tests = {
                new TestSubstrData(0, 8, new boolean[]{b0, b1, b0, b1, b0, b1, b1, b0}),
                new TestSubstrData(0, 0, new boolean[0]),
                new TestSubstrData(0, 1, new boolean[]{b0}),
                new TestSubstrData(2, 4, new boolean[]{b0, b1})
        };

        for (TestSubstrData test : tests) {
            Bitset b = new Bitset();
            b.append(data);

            Bitset result = b.substr(test.start, test.end);

            Bitset expected = new Bitset();
            expected.append(test.expected);

            Assertions.assertEquals(result, expected);
        }
    }
}
