// para compilar javac -cp "lib\*" src\*.java -d bin
// para rodar java -cp "bin;lib\*" Main

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final APINoticia noticiasAPI = new APINoticia();
    private static final DateTimeFormatter FORMATADOR_DATA_EXIBICAO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    private static Noticias dadosDoUsuario;

    public static void main(String[] args) {
        carregarDadosDoUsuario();
        menuPrincipal();
    }

    private static void carregarDadosDoUsuario() {
        dadosDoUsuario = Dados.carregarNoticiasUsuario();
        if (dadosDoUsuario == null) {
            System.out.println("--- Bem-vindo(a) ao Blog de Notícias do IBGE ---");
            String nome;
            do {
                System.out.print("Parece que é seu primeiro acesso. Digite seu nome/apelido: ");
                nome = scanner.nextLine().trim();
            } while (nome.isEmpty());
            dadosDoUsuario = new Noticias(nome);
        } else {
            System.out.println("Bem-vindo(a) de volta, " + dadosDoUsuario.getNome() + "!");
        }
    }

    private static void menuPrincipal() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Buscar notícias");
            System.out.println("2. Gerenciar minhas listas");
            System.out.println("3. Ordenar uma lista específica");
            System.out.println("4. Sair");

            int opcao = lerOpcaoNumerica("Escolha uma opção: ", 1, 4);

            switch (opcao) {
                case 1 -> buscarNoticias();
                case 2 -> gerenciarListasDoUsuario();
                case 3 -> ordenarListasDoUsuario();
                case 4 -> {
                    Dados.salvarNoticiasUsuario(dadosDoUsuario);
                    System.out.println("Saindo do programa. Seus dados foram salvos.");
                    continuar = false;
                }
            }
        }
    }

    private static void ordenarListasDoUsuario() {
        System.out.println("\n--- Ordenar uma Lista ---");
        System.out.println("1. Favoritos\n2. Lidas\n3. Para Ler Depois");
        int listaEscolha = lerOpcaoNumerica("Qual lista você deseja ordenar? ", 1, 3);

        System.out.println("\n--- Critério de Ordenação ---");
        System.out.println("1. Título (A-Z)\n2. Data (Mais recentes primeiro)\n3. Tipo (A-Z)");
        int criterio = lerOpcaoNumerica("Escolha o critério: ", 1, 3);

        Comparator<Noticia> comparador = null;
        switch (criterio) {
            case 1:
                comparador = Comparator.comparing(Noticia::getTitulo, String.CASE_INSENSITIVE_ORDER);
                break;
            case 2:
                comparador = Comparator.comparing(Noticia::getDataPublicacao, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
                break;
            case 3:
                comparador = Comparator.comparing(Noticia::getTipo, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        switch(listaEscolha) {
            case 1 -> dadosDoUsuario.ordenarFavoritos(comparador);
            case 2 -> dadosDoUsuario.ordenarLidas(comparador);
            case 3 -> dadosDoUsuario.ordenarParaLerDepois(comparador);
        }
        
        System.out.println("Lista ordenada com sucesso!");
    }

    private static void gerenciarListasDoUsuario() {
        System.out.println("\n--- Gerenciar Listas ---");
        System.out.println("1. Favoritos\n2. Lidas\n3. Para Ler Depois");
        int escolha = lerOpcaoNumerica("Qual lista você quer gerenciar? ", 1, 3);

        List<Noticia> listaSelecionada;
        String nomeLista;
        
        if (escolha == 1) {
            listaSelecionada = dadosDoUsuario.getFavoritos();
            nomeLista = "Favoritos";
        } else if (escolha == 2) {
            listaSelecionada = dadosDoUsuario.getLidas();
            nomeLista = "Notícias Lidas";
        } else {
            listaSelecionada = dadosDoUsuario.getParaLerDepois();
            nomeLista = "Para Ler Depois";
        }
        
        System.out.println("\n--- " + nomeLista + " ---");
        if (listaSelecionada.isEmpty()) {
            System.out.println("(Vazio)");
            return;
        }

        for (int i = 0; i < listaSelecionada.size(); i++) {
            System.out.println("[" + i + "] " + listaSelecionada.get(i).getTitulo());
        }
        
        System.out.println("\nO que você deseja fazer?");
        System.out.println("1. Remover notícia da lista");
        System.out.println("2. Abrir link de uma notícia no navegador");
        int acao = lerOpcaoNumerica("Escolha uma ação (ou -1 para voltar): ", -1, 2);
        
        if (acao == 1) { 
            int idxParaRemover = lerOpcaoNumerica("\nDigite o número para remover: ", 0, listaSelecionada.size() - 1);
            Noticia noticiaParaRemover = listaSelecionada.get(idxParaRemover);
            if (escolha == 1) dadosDoUsuario.removerFavorito(noticiaParaRemover);
            else if (escolha == 2) dadosDoUsuario.removerLida(noticiaParaRemover);
            else dadosDoUsuario.removerParaLerDepois(noticiaParaRemover);
            System.out.println("Notícia removida da lista '" + nomeLista + "'.");

        } else if (acao == 2) {
             int idxParaAbrir = lerOpcaoNumerica("\nDigite o número da notícia para abrir o link: ", 0, listaSelecionada.size() - 1);
             Noticia noticiaParaAbrir = listaSelecionada.get(idxParaAbrir);
             abrirLinkNoNavegador(noticiaParaAbrir.getLink());
        }
    }

    private static void abrirLinkNoNavegador(String url) {
        System.out.println("Tentando abrir o link: " + url);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                System.err.println("Erro ao tentar abrir o link no navegador.");
                System.out.println("Por favor, copie o link manualmente e cole no seu navegador.");
            }
        } else {
            System.out.println("Não foi possível abrir o navegador automaticamente.");
            System.out.println("Por favor, copie o link manualmente e cole no seu navegador.");
        }
    }

    private static void buscarNoticias() {
        System.out.print("\nDigite o termo para buscar na API (deixe em branco para todas as notícias): ");
        String termo = scanner.nextLine();
        
        LocalDate dataInicial = null;
        LocalDate dataFinal = null;
        
        System.out.print("Deseja filtrar por data inicial? (S/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
            dataInicial = lerData("Digite a data inicial (DD/MM/AAAA): ");
        }

        System.out.print("Deseja filtrar por data final? (S/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
            dataFinal = lerData("Digite a data final (DD/MM/AAAA): ");
        }

        try {
            List<Noticia> resultados = noticiasAPI.buscarNoticias(termo, dataInicial, dataFinal);
            if (resultados.isEmpty()) {
                System.out.println("Nenhuma notícia encontrada para '" + termo + "' no período especificado.");
                return;
            }
            exibirResultadosBusca(resultados);
            adicionarNoticiaEmLista(resultados);
        } catch (RuntimeException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }

    private static LocalDate lerData(String mensagem) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = null;
        while (data == null) {
            System.out.print(mensagem);
            String dataStr = scanner.nextLine().trim();
            try {
                data = LocalDate.parse(dataStr, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use DD/MM/AAAA.");
            }
        }
        return data;
    }

    private static void exibirResultadosBusca(List<Noticia> resultados) {
        System.out.println("\n--- Resultados da Busca ---");
        for (int i = 0; i < resultados.size(); i++) {
            Noticia n = resultados.get(i);
            System.out.println("\n[" + i + "] " + n.getTitulo());
            System.out.println("  Introdução: " + n.getIntroducao());
            if (n.getDataPublicacao() != null) {
                System.out.println("  Data: " + n.getDataPublicacao().format(FORMATADOR_DATA_EXIBICAO));
            } else {
                System.out.println("  Data: Indisponível");
            }
            System.out.println("  Tipo: " + n.getTipo());
            System.out.println("  Link: " + n.getLink());
        }
    }

    private static void adicionarNoticiaEmLista(List<Noticia> resultados) {
        int escolha = lerOpcaoNumerica("\nDigite o número da notícia para adicionar a uma lista (ou -1 para voltar): ", -1, resultados.size() - 1);
        if (escolha != -1) {
            Noticia noticia = resultados.get(escolha);
            System.out.println("1. Favoritar\n2. Marcar como Lida\n3. Salvar para Ler Depois");
            int acao = lerOpcaoNumerica("Escolha: ", 1, 3);
            switch (acao) {
                case 1 -> dadosDoUsuario.adicionarFavorito(noticia);
                case 2 -> dadosDoUsuario.marcarComoLida(noticia);
                case 3 -> dadosDoUsuario.adicionarParaLerDepois(noticia);
            }
            System.out.println("Notícia adicionada!");
        }
    }

    private static int lerOpcaoNumerica(String mensagem, int min, int max) {
        int opcao;
        while (true) {
            System.out.print(mensagem);
            try {
                opcao = Integer.parseInt(scanner.nextLine());
                if (opcao >= min && opcao <= max) return opcao;
                else System.out.println("Opção inválida. Digite um número entre " + min + " e " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }
}