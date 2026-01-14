package org.l2explorer.io;

import java.io.IOException;

public interface ObjectOutput<C extends Context> extends L2DataOutput {
    SerializerFactory<C> getSerializerFactory();

    C getContext();

    /**
     * Método de conveniência (alias) para writeObject.
     * Resolve o erro de "undefined method" no UnrealSerializerFactory.
     */
    default void write(Object object) throws IOException {
        writeObject(object);
    }

    // Mantido conforme sua versão, agora consistente com o método write()
    @SuppressWarnings("unchecked")
    default void writeObject(Object object) throws IOException {
        if (getSerializerFactory() == null)
            throw new IllegalStateException("SerializerFactory is null");

        if (object == null) {
            // Em arquivos Unreal, objetos nulos geralmente são referências vazias (0)
            writeCompactInt(0);
            return;
        }

        Serializer<Object, C> serializer = (Serializer<Object, C>) getSerializerFactory().forClass(object.getClass());
        serializer.writeObject(object, this);
    }

    static <C extends Context> ObjectOutput<C> objectOutput(L2DataOutput dataOutput, SerializerFactory<C> serializerFactory, C context) {
        return new UnrealObjectOutput<>(dataOutput, serializerFactory, context);
    }
}