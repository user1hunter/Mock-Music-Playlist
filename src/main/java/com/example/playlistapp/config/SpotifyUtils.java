package com.example.playlistapp.config;
import java.net.URI;
import java.net.http.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class SpotifyUtils {

    @Value("${app.spotify.client_id}")
    private String client_id;

    @Value("${app.spotify.client_secret}")
    private String client_secret;

    @Value("${app.spotify.token_url}")
    private String token_url;

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String TOKEN_URL;

    @PostConstruct
    private void init() {
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
        TOKEN_URL = token_url;
    }

    public static String getSpotifyAccessToken() throws Exception {
        String form = "grant_type=client_credentials" +
                "&client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(response.body());
            return json.getString("access_token");
        } else {
            throw new RuntimeException("Failed to get Spotify token: " + response.body());
        }
    }
}
