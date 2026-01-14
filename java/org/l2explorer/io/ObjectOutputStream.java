package org.l2explorer.io;

import java.io.OutputStream;
import java.nio.charset.Charset;

// MUDANÇA: Agora estende a SUA L2DataOutputStream para ganhar Little-Endian e Charset
public class ObjectOutputStream<T extends Context> extends org.l2explorer.io.L2DataOutputStream implements ObjectOutput<T> {
    private final SerializerFactory<T> serializerFactory;
    private final T context;

    public ObjectOutputStream(OutputStream out, Charset charset, SerializerFactory<T> serializerFactory, T context) {
        this(out, charset, 0, serializerFactory, context);
    }

    public ObjectOutputStream(OutputStream out, Charset charset, int position, SerializerFactory<T> serializerFactory, T context) {
        // Agora o super vai funcionar, pois sua L2DataOutputStream aceita esses parâmetros
        super(out, charset, position);
        this.serializerFactory = serializerFactory;
        this.context = context;
    }

    @Override
    public SerializerFactory<T> getSerializerFactory() {
        return serializerFactory;
    }

    @Override
    public T getContext() {
        return context;
    }
    
    // O método writeObject(Object) já é default na ObjectOutput, 
    // então ele não precisa ser implementado aqui.
}