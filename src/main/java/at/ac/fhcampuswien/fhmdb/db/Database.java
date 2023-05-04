package at.ac.fhcampuswien.fhmdb.db;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/*
  TODO: enthält alle notwendigen Attribute um eine Datenbankverbindung/ConnectionSource
   herzustellen (URL, Username und Passwort), sowie die ConnectionSource und das DAO.
*/

public class Database {
    // Konstanten für die Datenbankverbindung
    public static final String DB_URL = "jdbc:h2:file: ./db/Database";
    public static final String username = "admin";
    public static final String password = "123";

    // ConnectionSource-Objekt für die Datenbankverbindung
    private static ConnectionSource connectionSource;

    // DAO-Objekt für die Interaktion mit der WatchlistMovieEntity-Tabelle in der Datenbank
    Dao<WatchlistMovieEntity, Long> dao;

    // Singleton-Instanz der Database-Klasse
    private static Database instance;

    // Privater Konstruktor, der die ConnectionSource und das DAO initialisiert
    private Database() {
        try {
            createConnectionSource();
            dao = DaoManager.createDao(connectionSource, WatchlistMovieEntity.class);
            createTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Gibt die Singleton-Instanz der Database-Klasse zurück
    public static Database getDatabase() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Erstellt die Tabellen in der Datenbank, wenn sie noch nicht existieren
    private static void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, WatchlistMovieEntity.class);
    }

    // Erstellt die ConnectionSource mit der Datenbankverbindung
    private static void createConnectionSource() throws SQLException {
        connectionSource = new JdbcConnectionSource(DB_URL, username, password);
    }
}