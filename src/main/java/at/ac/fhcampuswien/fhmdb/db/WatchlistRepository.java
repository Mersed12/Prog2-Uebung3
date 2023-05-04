package at.ac.fhcampuswien.fhmdb.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

/*
  TODO: stellt die notwendigen Funktionen der Datenbank bereit, nämlich:
   - Auslesen aller WatchlistMovieEntity Einträgen aus der Datenbank
   - Hinzufügen eines übergebenen WatchlistMovieEntity Eintrags in die Datenbank, wenn dieser noch nicht existiert
   - Löschen eines übergebenen WatchlistMovieEntity Eintrags aus der Datenbank
*/

public class WatchlistRepository {
    // DAO-Objekt, das für die Interaktion mit der WatchlistMovieEntity-Tabelle in der Datenbank verwendet wird
    private Dao<WatchlistMovieEntity, Long> dao;

    // Konstruktor, der das DAO-Objekt aus der Database-Instanz initialisiert
    public WatchlistRepository() {
        this.dao = Database.getDatabase().dao;
    }

    /**
     * Gibt eine Liste aller WatchlistMovieEntity-Einträge in der Datenbank zurück.
     * @return Liste der WatchlistMovieEntity-Einträge, oder null im Fehlerfall
     */
    public List<WatchlistMovieEntity> getAllWatchlistMovies() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            System.out.println("Error fetching all watchlist movies: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fügt einen übergebenen WatchlistMovieEntity-Eintrag zur Datenbank hinzu, wenn dieser noch nicht vorhanden ist.
     * @param movie Der WatchlistMovieEntity-Eintrag, der zur Datenbank hinzugefügt werden soll
     * @return true, wenn der Film erfolgreich hinzugefügt wurde, false, wenn der Film bereits vorhanden ist oder ein Fehler aufgetreten ist
     */
    public boolean addWatchlistMovie(WatchlistMovieEntity movie) {
        try {
            // Erstellen einer Abfrage, um zu überprüfen, ob der Film bereits in der Datenbank vorhanden ist
            QueryBuilder<WatchlistMovieEntity, Long> queryBuilder = dao.queryBuilder();
            Where<WatchlistMovieEntity, Long> where = queryBuilder.where();
            where.eq("api_id", movie.apiId);
            List<WatchlistMovieEntity> existingMovies = dao.query(queryBuilder.prepare());

            // Fügen Sie den Film nur hinzu, wenn er noch nicht in der Datenbank vorhanden ist
            if (existingMovies.isEmpty()) {
                dao.create(movie);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error adding watchlist movie: " + e.getMessage());
            return false;
        }
    }

    /**
     * Löscht einen übergebenen WatchlistMovieEntity-Eintrag aus der Datenbank.
     * @param movie Der WatchlistMovieEntity-Eintrag, der aus der Datenbank gelöscht werden soll
     * @return true, wenn der Film erfolgreich gelöscht wurde, false, wenn ein Fehler aufgetreten ist
     */
    public boolean deleteWatchlistMovie(WatchlistMovieEntity movie) {
        try {
            return dao.delete(movie) > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting watchlist movie: " + e.getMessage());
            return false;
        }
    }
}