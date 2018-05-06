package com.fantasyunlimited.cache;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONSerializer<T> implements Serializer<T> {
	private static final Logger logger = Logger.getLogger(JSONSerializer.class);

	private final Gson gson;
	private final TypeToken<T> token;

	public JSONSerializer(Class<T> clazz) {
		gson = new GsonBuilder()
				.create();
		token = TypeToken.of(clazz);
	}

	@Override
	public ByteBuffer serialize(T object) throws SerializerException {
		try {
			String json = gson.toJson(object);
			return ByteBuffer.wrap(json.getBytes());
		} catch (Exception | Error e) {
			logger.error("Error!", e);
			throw new SerializerException("Error", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
		try {
			String json = new String(binary.array());

			return (T) gson.fromJson(json, token.getType());
		} catch (Exception | Error e) {
			logger.error("Error!", e);
			throw new SerializerException("Error", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(T object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
		try {
			String json = new String(binary.array());
			return object.equals((T) gson.fromJson(json, token.getType()));
		} catch (Exception | Error e) {

			logger.error("Error!", e);
			throw new SerializerException("Error", e);
		}
	}
}
