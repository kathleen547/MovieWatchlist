import java.time.LocalDate;

public class Movie {
    private int id;

    private String title;
    private LocalDate releaseDate;

    int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }

    String getTitle() {
        return title;
    }

    void setTitle(final String title) {
        this.title = title;
    }

    LocalDate getReleaseDate() {
        return releaseDate;
    }

    void setReleaseDate(final LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
