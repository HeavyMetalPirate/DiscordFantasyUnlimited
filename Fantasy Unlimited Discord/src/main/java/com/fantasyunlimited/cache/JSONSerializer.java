package com.fantasyunlimited.cache;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattleParticipant;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JSONSerializer<T> implements Serializer<T> {
	private static final Logger logger = Logger.getLogger(JSONSerializer.class);

	private final Gson gson;
	private final TypeToken<T> token;

	public JSONSerializer(Class<T> clazz) {
		gson = new GsonBuilder()
				.registerTypeAdapter(BattleParticipant.class, new BattleParticipantDeserializer())
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
			binary.flip();
			byte[] data;
			if (binary.hasArray()) {
				data = binary.array();
			} else {
				data = new byte[binary.remaining()];
				binary.get(data);
			}
			String json = new String(data);
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
			binary.flip();
			byte[] data;
			if (binary.hasArray()) {
				data = binary.array();
			} else {
				data = new byte[binary.remaining()];
				binary.get(data);
			}
			String json = new String(data);
			return object.equals((T) gson.fromJson(json, token.getType()));
		} catch (Exception | Error e) {

			logger.error("Error!", e);
			throw new SerializerException("Error", e);
		}
	}

	private class BattleParticipantDeserializer implements JsonDeserializer<BattleParticipant>, JsonSerializer<BattleParticipant> {

		@Override
		public BattleParticipant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonElement element = json.getAsJsonObject().get("discordId");
			if(element == null) {
				//BattleNPC
				return context.deserialize(json, BattleNPC.class);
			}
			else {
				//BattlePlayer
				return context.deserialize(json, BattlePlayer.class);
			}
		}

		@Override
		public JsonElement serialize(BattleParticipant src, Type typeOfSrc, JsonSerializationContext context) {
			if(src instanceof BattleNPC) {
				BattleNPC object = (BattleNPC) src;
				return context.serialize(object, BattleNPC.class);
			}
			else {
				BattlePlayer object = (BattlePlayer) src;
				return context.serialize(object, BattlePlayer.class);
			}
		}
	}
}
