package net.aleric.unisameteo.service;

import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class StorageService {

    @Inject
    RedisClient redisClient;
    @Inject
    ReactiveRedisClient reactiveRedisClient;

    public Optional<String> get(String key) {
        io.vertx.redis.client.Response response = redisClient.get(key);

        if (response == null)
            return Optional.empty();

        return Optional.of(response.toString());
    }

    public void set(String key, String value) {
        redisClient.set(Arrays.asList(key, value));
    }

    public Uni<List<String>> keys() {
        return reactiveRedisClient
                .keys("*")
                .map(response -> {
                    List<String> result = new ArrayList<>();
                    for (Response r : response) {
                        result.add(r.toString());
                    }
                    return result;
                });
    }
}