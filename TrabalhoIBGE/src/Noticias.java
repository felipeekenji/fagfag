import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Noticias implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private List<Noticia> favoritos;
    private List<Noticia> lidas;
    private List<Noticia> paraLerDepois;

    public Noticias(String nome) {
        this.nome = nome;
        this.favoritos = new ArrayList<>();
        this.lidas = new ArrayList<>();
        this.paraLerDepois = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public List<Noticia> getFavoritos() {
        return new ArrayList<>(favoritos);
    }

    public List<Noticia> getLidas() {
        return new ArrayList<>(lidas);
    }

    public List<Noticia> getParaLerDepois() {
        return new ArrayList<>(paraLerDepois);
    }

    public void adicionarFavorito(Noticia noticia) {
        if (!favoritos.contains(noticia)) {
            favoritos.add(noticia);
        }
    }

    public void removerFavorito(Noticia noticia) {
        favoritos.remove(noticia);
    }

    public void marcarComoLida(Noticia noticia) {
        if (!lidas.contains(noticia)) {
            lidas.add(noticia);
        }
    }

    public void removerLida(Noticia noticia) {
        lidas.remove(noticia);
    }

    public void adicionarParaLerDepois(Noticia noticia) {
        if (!paraLerDepois.contains(noticia)) {
            paraLerDepois.add(noticia);
        }
    }

    public void removerParaLerDepois(Noticia noticia) {
        paraLerDepois.remove(noticia);
    }

    public void ordenarFavoritos(Comparator<Noticia> comparador) {
        if (comparador != null) {
            Collections.sort(favoritos, comparador);
        }
    }

    public void ordenarLidas(Comparator<Noticia> comparador) {
        if (comparador != null) {
            Collections.sort(lidas, comparador);
        }
    }

    public void ordenarParaLerDepois(Comparator<Noticia> comparador) {
        if (comparador != null) {
            Collections.sort(paraLerDepois, comparador);
        }
    }

    public void prepararParaSalvar() {
    }

    public void posCarregamento() {
        favoritos.forEach(Noticia::processarDataPublicacao);
        lidas.forEach(Noticia::processarDataPublicacao);
        paraLerDepois.forEach(Noticia::processarDataPublicacao); 
    }
}