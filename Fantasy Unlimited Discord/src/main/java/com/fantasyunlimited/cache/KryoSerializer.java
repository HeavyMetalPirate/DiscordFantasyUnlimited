package com.fantasyunlimited.cache;

import java.nio.ByteBuffer;

import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.entity.Attributes;

public class KryoSerializer<T> implements Serializer<T> {

	private static final Kryo kryo = new Kryo();

	public KryoSerializer(ClassLoader loader) {
		// no-op
		kryo.register(Attributes.class);
	}

	@Override
	public ByteBuffer serialize(final T object) throws SerializerException {
		try {
			Output output = new Output(4096);
			kryo.writeClassAndObject(output, object);
			return ByteBuffer.wrap(output.getBuffer());
		} catch (Exception e) {
			FantasyUnlimited.getInstance().sendExceptionMessage(e);
			throw new SerializerException("Error!", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
		try {
			Input input = new Input(new ByteBufferInputStream(binary));
			return (T) kryo.readClassAndObject(input);
		} catch (Exception e) {
			FantasyUnlimited.getInstance().sendExceptionMessage(e);
			throw new SerializerException("Error!", e);
		}
	}

	@Override
	public boolean equals(final T object, final ByteBuffer binary) throws ClassNotFoundException, SerializerException {
		try {
			return object.equals(read(binary));
		} catch (Exception e) {
			FantasyUnlimited.getInstance().sendExceptionMessage(e);
			throw new SerializerException("Error!", e);
		}
	}

}