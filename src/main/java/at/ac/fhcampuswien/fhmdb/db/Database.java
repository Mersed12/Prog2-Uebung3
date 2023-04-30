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
    public static final String DB_URL = "jdbc:h2:file: ./db/Database";
    public static final String username = "admin";
    public static final String password = "123";

    private static ConnectionSource connectionSource;

    Dao<WatchlistMovieEntity, Long> dao;

    private static Database instance;

    private Database() {
        try {
            createConnectionSource();
            dao = DaoManager.createDao(connectionSource, WatchlistMovieEntity.class);
            createTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Database getDatabase() {
        if(instance == null) {
            instance = new Database();
        }

        return instance;
    }

    private static void createTables() throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, WatchlistMovieEntity.class);
    }

    private static void createConnectionSource() throws SQLException {
        connectionSource = new JdbcConnectionSource(DB_URL, username, password);
    }
}
