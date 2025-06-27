import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Noticia implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("id")
    private int id;
    @SerializedName("tipo")
    private String tipo;
    @SerializedName("titulo")
    private String titulo;
    @SerializedName("introducao")
    private String introducao;
    @SerializedName("data_publicacao")
    private String dataPublicacaoString;
    private transient LocalDateTime dataPublicacao;
    @SerializedName("produto_id")
    private int produtoId;
    @SerializedName("editorias")
    private String editorias;
    @SerializedName("link")
    private String link;

    @SerializedName("imagens")
    private transient Object imagens;
    @SerializedName("destaque")
    private transient boolean destaque;
    @SerializedName("alt_titulo")
    private transient String altTitulo;
    @SerializedName("url")
    private transient String url;

    private static final DateTimeFormatter API_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Noticia() {
    }

    public void processarDataPublicacao() {
        if (this.dataPublicacaoString != null && !this.dataPublicacaoString.isEmpty()) {
            try {
                this.dataPublicacao = LocalDateTime.parse(this.dataPublicacaoString, API_DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.err.println("Erro ao parsear data: " + this.dataPublicacaoString + " - " + e.getMessage());
                this.dataPublicacao = null;
            }
        }
    }

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getIntroducao() { return introducao; }
    public LocalDateTime getDataPublicacao() { return dataPublicacao; }
    public String getDataPublicacaoString() { return dataPublicacaoString; }
    public int getProdutoId() { return produtoId; }
    public String getEditorias() { return editorias; }
    public String getLink() { return link; }

    public void setId(int id) { this.id = id; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setIntroducao(String introducao) { this.introducao = introducao; }
    public void setDataPublicacaoString(String dataPublicacaoString) {
        this.dataPublicacaoString = dataPublicacaoString;
        processarDataPublicacao();
    }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
    public void setEditorias(String editorias) { this.editorias = editorias; }
    public void setLink(String link) { this.link = link; }

    @Override
    public String toString() {
        return "Noticia{" +
               "id=" + id +
               ", tipo='" + tipo + '\'' +
               ", titulo='" + titulo + '\'' +
               ", introducao='" + introducao + '\'' +
               ", dataPublicacao='" + (dataPublicacao != null ? dataPublicacao.format(API_DATE_FORMATTER) : "Indisponível") + '\'' +
               ", link='" + link + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Noticia noticia = (Noticia) o;
        return id == noticia.id &&
               Objects.equals(titulo, noticia.titulo) &&
               Objects.equals(link, noticia.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, link);
    }
}