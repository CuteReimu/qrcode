package net.cutereimu.qrcode;

import java.util.Arrays;

public final class Bitset {
    private int numBits;
    private byte[] bits;

    private Bitset(int numBits, byte[] bits) {
        this.numBits = numBits;
        this.bits = bits;
    }

    public Bitset(boolean... v) {
        bits = new byte[0];
        append(v);
    }

    public Bitset(Bitset obj) {
        numBits = obj.numBits;
        bits = obj.bits;
    }

    public Bitset substr(int start, int end) {
        if (start > end || end > numBits) {
            throw new IllegalArgumentException("Out of range start=%d end=%d numBits=%d".formatted(start, end, numBits));
        }

        Bitset result = new Bitset();
        result.ensureCapacity(end - start);

        for (int i = start; i < end; i++) {
            if (at(i)) {
                result.bits[result.numBits / 8] |= 0x80 >>> (result.numBits % 8);
            }
            result.numBits++;
        }

        return result;
    }

    public static Bitset fromString(String s) {
        Bitset b = new Bitset(0, new byte[0]);

        for (char c : s.toCharArray()) {
            switch (c) {
                case '1':
                    b.append(true);
                    break;
                case '0':
                    b.append(false);
                    break;
                case ' ':
                    break;
                default:
                    throw new RuntimeException("Invalid char %c in NewFromBase2String".formatted(c));
            }
        }

        return b;
    }

    public void append(byte[] data) {
        for (byte d : data) {
            append(d, 8);
        }
    }

    public void append(byte value, int numBits) {
        ensureCapacity(numBits);

        if (numBits > 8) {
            throw new IllegalArgumentException("numBits %d out of range 0-8".formatted(numBits));
        }

        for (int i = numBits - 1; i >= 0; i--) {
            if ((value & (1 << i)) != 0) {
                bits[this.numBits / 8] |= 0x80 >>> (this.numBits % 8);
            }

            this.numBits++;
        }
    }

    public void append(int value, int numBits) {
        ensureCapacity(numBits);

        if (numBits > 32) {
            throw new IllegalArgumentException("numBits %d out of range 0-32".formatted(numBits));
        }

        for (int i = numBits - 1; i >= 0; i--) {
            if ((value & (1 << i)) != 0) {
                bits[this.numBits / 8] |= 0x80 >>> (this.numBits % 8);
            }

            this.numBits++;
        }
    }

    private void ensureCapacity(int numBits) {
        numBits += this.numBits;

        int newNumBits = numBits / 8;
        if (numBits % 8 != 0) {
            newNumBits++;
        }

        if (bits.length >= newNumBits) {
            return;
        }

        bits = Arrays.copyOfRange(bits, 0, bits.length * 3 + newNumBits);
    }

    public void append(Bitset other) {
        ensureCapacity(other.numBits);

        for (int i = 0; i < other.numBits; i++) {
            if (other.at(i)) {
                bits[numBits / 8] |= 0x80 >>> (numBits % 8);
            }
            numBits++;
        }
    }

    public void append(boolean... bits) {
        ensureCapacity(bits.length);

        for (boolean v : bits) {
            if (v) {
                this.bits[numBits / 8] |= 0x80 >>> numBits % 8;
            }
            numBits++;
        }
    }

    public void append(int num, boolean value) {
        for (int i = 0; i < num; i++) {
            append(value);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("numBits=%d, bits=".formatted(numBits));
        for (int i = 0; i < numBits; i++) {
            if ((i % 8) == 0) {
                sb.append(' ');
            }

            if ((bits[i / 8] & (0x80 >>> (i % 8))) != 0) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }

        return sb.toString();
    }

    public int size() {
        return numBits;
    }

    public boolean[] bits() {
        boolean[] result = new boolean[numBits];

        for (int i = 0; i < numBits; i++) {
            result[i] = (bits[i / 8] & (0x80 >>> (i % 8))) != 0;
        }

        return result;
    }

    public boolean at(int index) {
        if (index >= numBits) {
            throw new IllegalArgumentException("Index %d out of range".formatted(index));
        }

        return (bits[index / 8] & (0x80 >>> (index % 8))) != 0;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (!(otherObject instanceof Bitset other)) return false;

        if (numBits != other.numBits)
            return false;

        for (int i = 0; i < numBits / 8; i++) {
            if (bits[i] != other.bits[i]) {
                return false;
            }
        }

        for (int i = 8 * (numBits / 8); i < numBits; i++) {
            byte a = (byte) (bits[i / 8] & (0x80 >>> (i % 8)));
            byte b = (byte) (other.bits[i / 8] & (0x80 >>> (i % 8)));

            if (a != b) {
                return false;
            }
        }

        return true;
    }

    public byte byteAt(int index) {
        if (index < 0 || index >= numBits) {
            throw new IllegalArgumentException("Index %d out of range".formatted(index));
        }

        byte result = 0;

        for (int i = index; i < index + 8 && i < numBits; i++) {
            result <<= 1;
            if (at(i)) {
                result |= 1;
            }
        }

        return result;
    }
}
