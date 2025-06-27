import java.util.List;
import com.google.gson.annotations.SerializedName;

public class RespostaAPI {
    @SerializedName("items")
    private List<Noticia> items;
    private int count;
    private int page;
    private int perPage;
    private int totalPages;
    private String nextLink;
    private String previousLink;

    public List<Noticia> getItems() {
        return items;
    }

    public void setItems(List<Noticia> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    public String getPreviousLink() {
        return previousLink;
    }

    public void setPreviousLink(String previousLink) {
        this.previousLink = previousLink;
    }
}