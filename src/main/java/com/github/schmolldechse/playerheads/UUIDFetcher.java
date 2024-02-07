package com.github.schmolldechse.playerheads;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class UUIDFetcher {

    private final HttpClient httpClient;

    public UUIDFetcher() {
        this(HttpClient.newHttpClient());
    }

    public UUIDFetcher(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    public CompletableFuture<Optional<UUID>> lookup(String username) {
        CompletableFuture<HttpResponse<InputStream>> future = this.httpClient.sendAsync(HttpRequest
                .newBuilder(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .header("Accept", "application/json")
                .header("User-Agent", "Playerheads")
                .timeout(Duration.ofSeconds(5))
                .build(), HttpResponse.BodyHandlers.ofInputStream());

        return future.thenApply((HttpResponse<InputStream> response) -> {
            int code = response.statusCode();

            switch (code) {
                case 200 -> {
                    JsonObject jsonObject = read(response);

                    String id = jsonObject.get("id").getAsString();
                    return Optional.of(dashed(id));
                }

                default -> {
                    return handleNotOkay(response);
                }
            }
        });
    }

    public CompletableFuture<Optional<String>> lookup(UUID uniqueId) {
        CompletableFuture<HttpResponse<InputStream>> future = this.httpClient.sendAsync(HttpRequest
                .newBuilder(URI.create("https://sessionserver.mojang.com/session/minecraft/profile" + undashed(uniqueId)))
                .header("Accept", "application/json")
                .header("User-Agent", "Playerheads")
                .timeout(Duration.ofSeconds(5))
                .build(), HttpResponse.BodyHandlers.ofInputStream());

        return future.thenApply((HttpResponse<InputStream> response) -> {
            int code = response.statusCode();

            switch (code) {
                case 200 -> {
                    JsonObject jsonObject = read(response);

                    String username = jsonObject.get("name").getAsString();
                    return Optional.of(username);
                }

                default -> {
                    return handleNotOkay(response);
                }
            }
        });
    }

    private <T> Optional<T> handleNotOkay(HttpResponse<InputStream> response) {
        int code = response.statusCode();

        switch (code) {
            case 204 -> {
                //No content
                return Optional.empty();
            }

            case 400 -> {
                //Bad request
                JsonObject jsonObject = read(response);

                String error = jsonObject.get("error").getAsString();
                String errorMessage = jsonObject.get("errorMessage").getAsString();

                throw new RuntimeException("Bad request sent to Mojang. Received " + error + ": " + errorMessage);
            }

            default -> {
                //Unknown status code
                throw new RuntimeException("Unexpected status code from Mojang: " + response.statusCode());
            }
        }
    }

    public JsonObject read(HttpResponse<InputStream> response) {
        Charset charset = charsetFromHeaders(response.headers());

        try (InputStream inputStream = response.body();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(inputStreamReader, JsonObject.class);

            if (jsonObject != null) return jsonObject;
            else throw new RuntimeException("Expected response to be represented as a json object, but it was null");
        } catch (IOException exception) {
            throw new RuntimeException("Could not read http response body", exception);
        } catch (JsonParseException exception) {
            throw new RuntimeException("Invalid json from api", exception);
        }
    }

    private Charset charsetFromHeaders(HttpHeaders headers) {
        Optional<String> optionalContentType = headers.firstValue("Content-Type");
        if (optionalContentType.isPresent()) {
            String contentType = optionalContentType.get();
            int indexOfSemi = contentType.indexOf(";");

            if (indexOfSemi != -1) {
                String charsetPart = contentType.substring(indexOfSemi + 1).trim();
                String[] charSetKeyAndValue = charsetPart.split("=", 2);

                if (charSetKeyAndValue.length == 2 && "charset".equalsIgnoreCase(charSetKeyAndValue[0])) {
                    String charsetName = charSetKeyAndValue[1];
                    return Charset.forName(charsetName);
                }
            }
        }

        return StandardCharsets.UTF_8;
    }



    private UUID dashed(String id) {
        return UUID.fromString(id.substring(0, 8) + '-' +
                id.substring(8, 12) + '-' +
                id.substring(12, 16) + '-' +
                id.substring(16, 20) + '-' +
                id.substring(20, 32));
    }

    private String undashed(UUID uniqueId) {
        return uniqueId.toString().replace("-", "");
    }
}
