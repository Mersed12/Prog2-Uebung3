package at.ac.fhcampuswien.fhmdb.controller;

import at.ac.fhcampuswien.fhmdb.model.Genre;
import at.ac.fhcampuswien.fhmdb.model.Movie;
import at.ac.fhcampuswien.fhmdb.model.WatchlistEntity;
import at.ac.fhcampuswien.fhmdb.provider.Database;
import at.ac.fhcampuswien.fhmdb.provider.movie.MovieAPI;
import at.ac.fhcampuswien.fhmdb.provider.movie.MovieProvider;
import at.ac.fhcampuswien.fhmdb.event.ClickEventHandler;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import java.net.URL;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class HomeController implements Initializable {
    private final static String ASCENDING = "Sort (asc)";
    private final static String DESCENDING = "Sort (desc)";

    @FXML
    public JFXButton searchBtn;

    @FXML
    public TextField searchField;

    @FXML
    public JFXListView<Movie> movieListView;

    @FXML
    public JFXComboBox<Genre> genreComboBox;

    @FXML
    public JFXComboBox<Integer> yearComboBox;

    @FXML
    public JFXComboBox<Double> ratingComboBox;

    @FXML
    public JFXButton sortBtn;

    @FXML
    public JFXButton resetBtn;

    @FXML
    public JFXButton watchlistBtn;

    public List<Movie> allMovies;

    private final MovieProvider movieAPIProvider = (MovieProvider) new MovieAPI();
    private final ObservableList<Movie> observableMovies = FXCollections.observableArrayList();   // automatically updates corresponding UI elements when underlying data changes
    private WatchlistRepository watchlistRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Database database = new Database();
        watchlistRepository = new WatchlistRepository(database.getWatchlistDao());

        allMovies = movieAPIProvider.getMovies();

        observableMovies.addAll(allMovies);         // add API data to observable list

        // initialize UI stuff
        movieListView.setItems(observableMovies);   // set data of observable list to list view
        movieListView.setCellFactory(movieListView -> new MovieCell(onAddToWatchlistClicked, onRemoveFromWatchlistClicked));

        initializeGenreSelector();
        initializeReleaseYearSelector();
        initializeRatingSelector();

        sortBtn.setOnAction(this::toggleSortOrder);
        searchBtn.setOnAction(this::searchAction);
        resetBtn.setOnAction(this::resetAction);
        watchlistBtn.setOnAction(this::toggleWatchlistMode);

        PauseTransition searchDebounce = new PauseTransition(Duration.millis(500));
        searchDebounce.setOnFinished(this::searchAction);
        searchField.textProperty().addListener((observable, old, newVal) -> searchDebounce.playFromStart());
    }

    void toggleSortOrder(ActionEvent actionEvent) {
        if(sortBtn.getText().equals(ASCENDING)) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle));
            sortBtn.setText(DESCENDING);
        } else {
            observableMovies.sort(Comparator.comparing(Movie::getTitle).reversed());
            sortBtn.setText(ASCENDING);
        }
    }

    void searchAction(ActionEvent actionEvent) {
        observableMovies.clear();
        List<Movie> filteredMovies = movieAPIProvider.getMoviesWithQuery(constructQueryMap());
        observableMovies.addAll(filteredMovies);

        movieListView.refresh();
    }

    private Map<String, String> constructQueryMap(){
        Map<String,String> queryMap = new HashMap<>();

        String searchQuery = searchField.getText().toLowerCase();
        queryMap.put("query", searchQuery);

        Genre selectedGenre = genreComboBox.getSelectionModel().getSelectedItem();
        if(selectedGenre != null && !selectedGenre.toString().equals("ALL")) queryMap.put("genre", selectedGenre.toString());

        Integer selectedYear = yearComboBox.getSelectionModel().getSelectedItem();
        if(selectedYear != null && selectedYear > 0) queryMap.put("releaseYear", selectedYear.toString());

        Double selectedRating = ratingComboBox.getSelectionModel().getSelectedItem();
        if(selectedRating != null && selectedRating > 0) queryMap.put("ratingFrom", selectedRating.toString());

        return queryMap;
    }
    public void resetAction(ActionEvent actionEvent) {
        observableMovies.clear();
        observableMovies.addAll(allMovies);

        searchField.clear();
        genreComboBox.getSelectionModel().clearSelection();
        yearComboBox.getSelectionModel().clearSelection();
        ratingComboBox.getSelectionModel().clearSelection();

        movieListView.refresh();
    }

    public void initializeGenreSelector(){
        genreComboBox.setPromptText("Filter by Genre");
        genreComboBox.getItems().addAll( Genre.values() );
    }

    public void initializeReleaseYearSelector(){
        yearComboBox.setPromptText("Filter by Release Year");
        int[] years = IntStream.range( 1970, Year.now().getValue() + 1 ).toArray(); // generate years from 1970 to current year
        yearComboBox.getItems().addAll( Arrays.stream(years).boxed().toList());
    }

    public void initializeRatingSelector(){
        ratingComboBox.setPromptText("Filter by Rating");
        double[] ratings = DoubleStream.iterate( 0.0, d -> d + 0.5 ).limit( 21 ).toArray(); // generate ratings from 0.0 to 10.0 in steps of 0.5
        ratingComboBox.getItems().addAll( Arrays.stream(ratings).boxed().toList());
    }

    // returns the person who appears most often in the most often in the mainCast of the given movies.
    public String getMostPopularActor(List<Movie> movies) {
        return movies.stream()
                .flatMap(movie -> movie.getMainCast().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    // filters on the longest movie title of the passed movies and returns the number of letters in the title.
    public int getLongestMovieTitle(List<Movie> movies) {
        return movies.stream()
                .mapToInt(movie -> movie.getTitle().length())
                .max()
                .orElse(0);
    }

    // returns the number of movies of a given director
    public long countMoviesFrom(List<Movie> movies, String director) {
        return movies.stream()
                .filter(movie -> movie.getDirectors().stream().anyMatch(director::equals))
                .count();
    }

    // returns those movies that were released between two given years.
    public List<Movie> getMoviesBetweenYears(List<Movie> movies, int startYear, int endYear) {
        return movies.stream()
                .filter(movie -> movie.getReleaseYear() >= startYear && movie.getReleaseYear() <= endYear)
                .collect(Collectors.toList());
    }

    private void toggleWatchlistMode( ActionEvent actionEvent ){
        if( watchlistBtn.getText().equals( "Open watchlist" ) ){
            hideTopBar();
            updateView();
            watchlistBtn.setText( "Close watchlist" );
        } else {
            showTopBar();
            observableMovies.clear();
            observableMovies.addAll(allMovies);
            watchlistBtn.setText("Open watchlist");
        }
    }

    private final ClickEventHandler<Movie> onAddToWatchlistClicked = (clickedItem) ->
    {
        WatchlistEntity watchlistEntity = new WatchlistEntity(clickedItem);
        watchlistRepository.addToWatchlist(watchlistEntity);
    };

    private final ClickEventHandler<Movie> onRemoveFromWatchlistClicked = (clickedItem) -> {
        WatchlistEntity watchlistEntity = new WatchlistEntity(clickedItem);
        watchlistRepository.removeFromWatchlist(watchlistEntity);
        updateView();
    };

    private void updateView(){
        List<WatchlistEntity> repositoryList = watchlistRepository.getAll();
        List<Movie> watchlist = convertWatchlistEntitiesToMovies(repositoryList);
        observableMovies.clear();
        observableMovies.addAll(watchlist);
    }

    private List<Movie> convertWatchlistEntitiesToMovies(List<WatchlistEntity> watchlistEntity){
        List<Movie> movieList = new ArrayList<>();

        if( watchlistEntity == null ) return movieList;

        for(WatchlistEntity entity : watchlistEntity){
            Movie movie = new Movie(
                    entity.getApiId(),
                    entity.getTitle(),
                    entity.stringToList(entity.getGenres()),
                    entity.getReleaseYear(),
                    entity.getDescription(),
                    entity.getImgUrl(),
                    entity.getLengthInMinutes(),
                    entity.stringToList(entity.getDirectors()),
                    entity.stringToList(entity.getWriters()),
                    entity.stringToList(entity.getMainCast()),
                    entity.getRating()
            );

            movie.setInWatchlist(true);
            movieList.add(movie);
        }

        return movieList;
    }

    private void hideTopBar(){
        sortBtn.setVisible(false);
        searchField.setVisible(false);
        genreComboBox.setVisible(false);
        yearComboBox.setVisible(false);
        ratingComboBox.setVisible(false);
        searchBtn.setVisible(false);
        resetBtn.setVisible(false);
    }

    private void showTopBar() {
        sortBtn.setVisible(true);
        searchField.setVisible(true);
        genreComboBox.setVisible(true);
        yearComboBox.setVisible(true);
        ratingComboBox.setVisible(true);
        searchBtn.setVisible(true);
        resetBtn.setVisible(true);
    }
}