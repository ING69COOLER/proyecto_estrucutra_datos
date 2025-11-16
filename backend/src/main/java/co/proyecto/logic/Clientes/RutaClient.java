package co.proyecto.logic.Clientes;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class RutaClient {
    public Double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        RestTemplate rest = new RestTemplate();

        String url = "https://graphhopper.com/api/1/route?point=" +
                lat1 + "," + lon1 +
                "&point=" + lat2 + "," + lon2 +
                "&vehicle=car&points_encoded=false&key=f16e65b0-2427-4afb-b907-2ac2ddbe6ec3";

        JsonNode response = rest.getForObject(url, JsonNode.class);

        return response.get("paths").get(0).get("distance").asDouble();
    }
}
