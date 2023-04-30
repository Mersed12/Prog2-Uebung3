package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.models.Genre;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/*
  TODO: enthält jene Daten der Filme, die in der Datenbank gespeichert werden sollen (siehe Klassendiagramm).
   Da für das Speichern von Listen (zB directors, writers, mainCast) weitere Tabellen nötig wären, werden diese ausgenommen.
   Die Genres sollen als String durch „,“ getrennt gespeichert werden.
*/

@DatabaseTable(tableName = "watchlistMovie")
public class WatchlistMovieEntity {
    @DatabaseField(columnName = "id", generatedId = true)
    long id;

    @DatabaseField(columnName = "api_id")
    String apiId;

    @DatabaseField(columnName = "title")
    String title;

    @DatabaseField(columnName = "description")
    String description;

    @DatabaseField(columnName = "genres")
    String genres;

    @DatabaseField(columnName = "release_year")
    int releaseYear;

    @DatabaseField(columnName = "img_url")
    String imgUrl;

    @DatabaseField(columnName = "length_in_minutes")
    int lengthInMinutes;

    @DatabaseField(columnName = "rating")
    double rating;

    WatchlistMovieEntity () {

    }

    public static String genresToString(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            sb.append(genres.get(i).toString());
            if (i < genres.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}


