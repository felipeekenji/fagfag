import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Dados {
    private static final String ARQUIVO_DADOS_USUARIO = "noticias_do_usuario.json";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void salvarNoticiasUsuario(Noticias noticiasUsuario) {
        try (FileWriter writer = new FileWriter(ARQUIVO_DADOS_USUARIO)) {
            noticiasUsuario.prepararParaSalvar();
            gson.toJson(noticiasUsuario, writer);
            System.out.println("Dados do usuário salvos com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados do usuário: " + e.getMessage());
        }
    }

    public static Noticias carregarNoticiasUsuario() {
        if (!Files.exists(Paths.get(ARQUIVO_DADOS_USUARIO))) {
            return null;
        }
        try (FileReader reader = new FileReader(ARQUIVO_DADOS_USUARIO)) {
            Noticias noticiasUsuario = gson.fromJson(reader, Noticias.class);
            if (noticiasUsuario != null) {
                noticiasUsuario.posCarregamento();
                System.out.println("Dados do usuário carregados.");
            }
            return noticiasUsuario;
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Erro ao carregar dados do usuário: " + e.getMessage());
            return null;
        }
    }

    public static boolean apagarDadosDoUsuario() {
        try {
            return Files.deleteIfExists(Paths.get(ARQUIVO_DADOS_USUARIO));
        } catch (IOException e) {
            System.err.println("Erro ao tentar apagar o arquivo de dados do usuário: " + e.getMessage());
            return false;
        }
    }
}