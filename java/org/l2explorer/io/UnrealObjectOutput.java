package org.l2explorer.io;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Implementação concreta para escrita de objetos Unreal/L2.
 */
public class UnrealObjectOutput<C extends Context> implements ObjectOutput<C> {
    private final L2DataOutput dataOutput;
    private final SerializerFactory<C> serializerFactory;
    private final C context;

    public UnrealObjectOutput(L2DataOutput dataOutput, SerializerFactory<C> serializerFactory, C context) {
        this.dataOutput = dataOutput;
        this.serializerFactory = serializerFactory;
        this.context = context;
    }

    @Override
    public SerializerFactory<C> getSerializerFactory() {
        return serializerFactory;
    }

    @Override
    public C getContext() {
        return context;
    }

    @Override
    public Charset getCharset() {
        return dataOutput.getCharset();
    }

    @Override
    public int getPosition() throws IOException {
        return dataOutput.getPosition();
    }

    // --- Repassando métodos para o dataOutput ---

    @Override
    public void writeByte(int v) throws IOException { dataOutput.writeByte(v); }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException { dataOutput.writeBytes(b, off, len); }

    @Override
    public void writeInt(int v) throws IOException { dataOutput.writeInt(v); }

    @Override
    public void writeShort(int v) throws IOException { dataOutput.writeShort(v); }

    @Override
    public void writeLong(long v) throws IOException { dataOutput.writeLong(v); }

    @Override
    public void writeFloat(float v) throws IOException { dataOutput.writeFloat(v); }

    @Override
    public void writeDouble(double v) throws IOException { dataOutput.writeDouble(v); }

    @Override
    public void writeBoolean(boolean v) throws IOException { dataOutput.writeBoolean(v); }

    @Override
    public void writeChar(int v) throws IOException { dataOutput.writeChar(v); }

    @Override
    public void writeUTF(String s) throws IOException { dataOutput.writeUTF(s); }
}