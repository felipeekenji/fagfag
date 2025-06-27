import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class APINoticia {
    private static final String BASE_API_URL = "https://servicodados.ibge.gov.br/api/v3/noticias";
    private final HttpClient httpClient;
    private final Gson gson;
    private static final DateTimeFormatter API_DATE_PARAM_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public APINoticia() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Noticia> buscarNoticias(String termoBusca) {
        return buscarNoticias(termoBusca, null, null);
    }

    public List<Noticia> buscarNoticias(String termoBusca, LocalDate dataInicial, LocalDate dataFinal) {
        List<Noticia> noticias = new ArrayList<>();
        StringBuilder urlBuilder = new StringBuilder(BASE_API_URL);
        boolean firstParam = true;

        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            try {
                String termoCodificado = URLEncoder.encode(termoBusca.trim(), StandardCharsets.UTF_8.toString());
                urlBuilder.append("?busca=").append(termoCodificado);
                firstParam = false;
            } catch (java.io.UnsupportedEncodingException e) {
                System.err.println("Erro de busca: " + e.getMessage());
                return noticias;
            }
        }

        if (dataInicial != null) {
            urlBuilder.append(firstParam ? "?" : "&").append("dataInicial=")
                      .append(dataInicial.format(API_DATE_PARAM_FORMATTER));
            firstParam = false;
        }

        if (dataFinal != null) {
            urlBuilder.append(firstParam ? "?" : "&").append("dataFinal=")
                      .append(dataFinal.format(API_DATE_PARAM_FORMATTER));
            firstParam = false;
        }

        String urlCompleta = urlBuilder.toString();
        System.out.println("Buscando: " + urlCompleta);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlCompleta))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                RespostaAPI resposta = gson.fromJson(response.body(), RespostaAPI.class);

                if (resposta != null && resposta.getItems() != null) {
                    for (Noticia n : resposta.getItems()) {
                        n.processarDataPublicacao();
                    }
                    noticias.addAll(resposta.getItems());
                }
            } else {
                System.err.println("Erro ao se conectar na API. Status: " + response.statusCode());
                System.err.println("Resposta: " + response.body());
                throw new RuntimeException("Falha ao buscar notícias da API do IBGE. Status: " + response.statusCode());
            }
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            System.err.println("Erro ao buscar/processar notícias da API: " + e.getMessage());
            throw new RuntimeException("Erro de rede ou processamento de dados da API.", e);
        }
        return noticias;
    }
}