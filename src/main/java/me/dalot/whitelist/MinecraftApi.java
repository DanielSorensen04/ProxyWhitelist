package me.dalot.whitelist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.dalot.ProxyWhitelist;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MinecraftApi {

    private final ProxyWhitelist proxyWhitelist;

    public MinecraftApi(ProxyWhitelist proxyWhitelist) {
        this.proxyWhitelist = proxyWhitelist;
    }

    public UUID getUUID(String username) {
        if (proxyWhitelist.getServer().getPlayer(username).isPresent()) {
            return proxyWhitelist.getServer().getPlayer(username).get().getUniqueId();
        }

        JsonObject playerElement = getApiData(username);
        if (playerElement != null) {
            JsonElement playerUUID = playerElement.get("full_uuid");
            if (playerUUID != null && !playerUUID.isJsonNull()) {
                return UUID.fromString(playerUUID.getAsString());
            }
        }

        return null;
    }

    private static JsonObject getApiData(String data) {
        try {
            URL url = new URL("https://minecraftapi.net/api/v1/profile/" + data);
            HttpURLConnection httpurlconnection = (HttpURLConnection) url.openConnection();
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(false);
            httpurlconnection.connect();

            if (httpurlconnection.getResponseCode() / 100 == 2) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);

                return JsonParser.parseString(response.toString()).getAsJsonObject();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
