package de.lighti.clipper.gui;

import java.io.DataInput;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Works like DataInputStream but reverses the bits (big/little endian) when reading
 * values
 *
 * @author Tobias Mahlmann
 *
 */
public class LittleEndianDataInputStream extends FilterInputStream implements DataInput {
    private final byte w[];

    public LittleEndianDataInputStream( InputStream in ) {
        super( in );

        w = new byte[8];
    }

    @Override
    public boolean readBoolean() throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }

    @Override
    public byte readByte() throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte) ch;
    }

    /**
     * like DataInputStream.readChar except little endian.
     */
    @Override
    public final char readChar() throws IOException {
        readFully( w, 0, 2 );
        return (char) ((w[1] & 0xff) << 8 | w[0] & 0xff);
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble( readLong() );
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat( readInt() );
    }

    @Override
    public void readFully( byte[] b ) throws IOException {
        readFully( b, 0, b.length );
    }

    @Override
    public void readFully( byte[] b, int off, int len ) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            final int count = in.read( b, off + n, len - n );
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

    /**
     * like DataInputStream.readInt except little endian.
     */
    @Override
    public final int readInt() throws IOException {
        readFully( w, 0, 4 );
        return w[3] << 24 | (w[2] & 0xff) << 16 | (w[1] & 0xff) << 8 | w[0] & 0xff;
    }

    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * like DataInputStream.readLong except little endian.
     */
    @Override
    public final long readLong() throws IOException {
        readFully( w, 0, 8 );
        return (long) w[7] << 56 | (long) (w[6] & 0xff) << 48 | (long) (w[5] & 0xff) << 40 | (long) (w[4] & 0xff) << 32 | (long) (w[3] & 0xff) << 24
                        | (long) (w[2] & 0xff) << 16 | (long) (w[1] & 0xff) << 8 | w[0] & 0xff;
    }

    @Override
    public final short readShort() throws IOException {
        readFully( w, 0, 2 );
        return (short) ((w[1] & 0xff) << 8 | w[0] & 0xff);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        final int ch = in.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        readFully( w, 0, 2 );
        return (w[1] & 0xff) << 8 | w[0] & 0xff;
    }

    @Override
    public String readUTF() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int skipBytes( int n ) throws IOException {
        int total = 0;
        int cur = 0;

        while (total < n && (cur = (int) in.skip( n - total )) > 0) {
            total += cur;
        }

        return total;
    }

}