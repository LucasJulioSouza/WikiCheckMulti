import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WikiCheckerMulti {

    public static void main(String[] args) {
        System.out.println("Executando com threads:");

        List<CompletableFuture<String>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= 50; i++) {
            String wikiPageUrl = "https://en.wikipedia.org/wiki/" + i;
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return verificaPaginaWikiExistente(wikiPageUrl);
                } catch (IOException e) {
                    return wikiPageUrl + " - erro: " + e.getMessage();
                }
            });
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(); // Aguarda todas as tarefas serem concluídas
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime) / 1000.0;

        futures.forEach(future -> System.out.println(future.join()));
        System.out.println("Tempo de execução em paralelo: " + elapsedTime + " segundos");
    }

    public static String verificaPaginaWikiExistente(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        try {
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                return url + " - Existe";
            } else if (responseCode == 404) {
                return url + " - Não existe";
            } else {
                return url + " - desconhecido";
            }
        } finally {
            connection.disconnect();
        }
    }
}
