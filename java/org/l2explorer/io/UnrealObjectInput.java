package org.l2explorer.io;

import java.io.IOException;
import java.nio.charset.Charset;

public class UnrealObjectInput<C extends Context> implements ObjectInput<C> {
    private final L2DataInput dataInput;
    private final SerializerFactory<C> serializerFactory;
    private final C context;

    public UnrealObjectInput(L2DataInput dataInput, SerializerFactory<C> serializerFactory, C context) {
        this.dataInput = dataInput;
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
    public <T> T readObject(Class<T> clazz) throws IOException {
        try {
            // 1. Criamos a instância da classe desejada (ex: Token)
            T instance = clazz.getDeclaredConstructor().newInstance();
            
            // 2. Buscamos o serializer
            Serializer<T, C> serializer = (Serializer<T, C>) serializerFactory.forClass(clazz);
            
            // 3. O serializer preenche a instância (void)
            serializer.readObject(instance, this);
            
            // 4. Retornamos a instância agora populada
            return instance;
        } catch (Exception e) {
            throw new IOException("Erro ao instanciar " + clazz.getName(), e);
        }
    }
        
    public L2DataInput getDataInput() {
        return dataInput;
    }
    
    public Charset getCharset() {
        return Charset.forName("EUC-KR"); 
    }

    // Importante para tokens de pulo (Jump/Skip)
    public int getPosition() {
        try {
            // Se o seu dataInput for um L2DataInput, ele deve ter o getPosition()
            // Caso contrário, precisaremos de um wrapper que conte os bytes
            return (dataInput instanceof L2DataInput) ? ((L2DataInput) dataInput).getPosition() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // --- DELEGATE PARA DATAINPUT ---
    @Override public void readFully(byte[] b) throws IOException { dataInput.readFully(b); }
    @Override public void readFully(byte[] b, int off, int len) throws IOException { dataInput.readFully(b, off, len); }
    @Override public int skipBytes(int n) throws IOException { return dataInput.skipBytes(n); }
    @Override public boolean readBoolean() throws IOException { return dataInput.readBoolean(); }
    @Override public byte readByte() throws IOException { return dataInput.readByte(); }
    @Override public int readUnsignedByte() throws IOException { return dataInput.readUnsignedByte(); }
    @Override public short readShort() throws IOException { return dataInput.readShort(); }
    @Override public int readUnsignedShort() throws IOException { return dataInput.readUnsignedShort(); }
    @Override public char readChar() throws IOException { return dataInput.readChar(); }
    @Override public int readInt() throws IOException { return dataInput.readInt(); }
    @Override public long readLong() throws IOException { return dataInput.readLong(); }
    @Override public float readFloat() throws IOException { return dataInput.readFloat(); }
    @Override public double readDouble() throws IOException { return dataInput.readDouble(); }
    @Override public String readLine() throws IOException { return dataInput.readLine(); }
    @Override public String readUTF() throws IOException { return dataInput.readUTF(); }
}