package org.l2explorer.io;

import java.io.IOException;

/**
 * Interface para leitura de objetos complexos (L2/Unreal) usando Serializers.
 * Refatorada para remover UncheckedIOException e tratar Generics corretamente.
 */
public interface ObjectInput<C extends Context> extends L2DataInput {
    
    SerializerFactory<C> getSerializerFactory();

    C getContext();

    default <T> T readObject(Class<T> clazz) throws IOException {
        if (getSerializerFactory() == null) {
            throw new IllegalStateException("SerializerFactory is null");
        }

        Serializer<T, C> serializer = getSerializerFactory().forClass(clazz);
        T obj = serializer.instantiate(this);
        
        if (obj != null) {
            // Buscamos o serializer da classe real (caso seja uma subclasse)
            @SuppressWarnings("unchecked")
            Serializer<T, C> s = (Serializer<T, C>) getSerializerFactory().forClass((Class<T>) obj.getClass());
            s.readObject(obj, this);
        }
        
        return obj;
    }

    /**
     * Fábrica para criar instâncias de ObjectInput.
     * Certifique-se que UnrealObjectInput implementa ObjectInput<C>.
     */
    static <C extends Context> ObjectInput<C> objectInput(org.l2explorer.io.L2DataInput dataInput, SerializerFactory<C> serializerFactory, C context) {
        // Adicionamos explicitamente o parâmetro de tipo <C> na instância
        return new UnrealObjectInput<C>(dataInput, serializerFactory, context);
    }
}