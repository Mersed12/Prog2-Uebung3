Ziele der Übung

Exercise 3
• 3-Layer Architektur in der Applikation umsetzen (UI-Layer, Business/Logic-Layer und Data- Layer)
• Exception Handling in der FHMDb App einsetzen
o Schreiben eigener Exception Klassen
o Abfangen von möglichen Fehlerfällen und sinnvolle Exception Propagation
• Functional Interfaces und Lambda Expressions

Aufgabenstellung:
Im Zuge der Exercise 3 ist die FHMDb App um einen Data-Layer (Persistenz) mithilfe einer H2 Datenbank Engine sowie der ORMLite Library zu erweitern. Dabei soll es möglich sein, Filme in einer Watchlist hinzuzufügen oder wieder von der Watchlist zu entfernen. Die gespeicherte Watchlist soll auch nach einem Neustart der App abgerufen werden können.
Außerdem soll die FHMDb App um ein umfangreiches Exception Handling erweitert werden. Ziel ist es, dass das Programm im Fehlerfall nicht abstürzt, und passende Meldungen an die Enduser*innen ausgegeben werden.

Rahmenbedingungen (K.O Kriterien):
• Das Projekt muss ein GUI bieten (keine Konsolenapp)
• Das Projekt muss auf Maven basieren
• Das Projekt muss auf GitHub verfügbar sein

Presentation-Layer (2 Punkte)
User*innen sollen die Möglichkeit haben zwischen der Anzeige aller Filme (Home) und der Anzeige der gespeicherten Watchlist zu wechseln. Die technische Umsetzung ist für die Erfüllung dieser Aufgabe irrelevant. Hierfür könnte eine neue JavaFX Scene erstellt und zu dieser navigiert werden. Es kann aber auch der Content des Homescreen neu befüllt werden, oder dergleichen.
Im Home-Screen soll es möglich sein, Einträge zur Watchlist hinzuzufügen (z.B. via Button), wohingegen im Watchlist-Screen ein Button zum Entfernen angezeigt werden soll.

Data-Layer (8 Punkte)
Folgende Klassen/Interfaces sind für den Data-Layer zu implementieren:
Klasse WatchlistMovieEntity: enthält jene Daten der Filme, die in der Datenbank gespeichert werden sollen (siehe Klassendiagramm). Da für das Speichern von Listen (zB directors, writers, mainCast) weitere Tabellen nötig wären, werden diese ausgenommen. Die Genres sollen als String durch „,“ getrennt gespeichert werden.
Klasse Database: enthält alle notwendigen Attribute um eine Datenbankverbindung/ConnectionSource herzustellen (URL, Username und Passwort), sowie die ConnectionSource und das DAO.
Klasse WatchlistRepository: stellt die notwendigen Funktionen der Datenbank bereit, nämlich:
- Auslesen aller WatchlistMovieEntity Einträgen aus der Datenbank
- Hinzufügen eines übergebenen WatchlistMovieEntity Eintrags in die Datenbank, wenn dieser noch nicht existiert
- Löschen eines übergebenen WatchlistMovieEntity Eintrags aus der Datenbank

Business/Logic-Layer (2 Punkte)
Die Controller Klasse(n) fungieren als Schicht zwischen dem UI-Layer und dem Data-Layer. Folglich sind diese nur um jeweils eine Click-Funktion zu erweitern. So soll beim Klick auf „(Add to) Watchlist” ein Film in der Datenbank angelegt, bzw. beim Klick auf „Remove“ wieder aus der Datenbank entfernt werden. Dies soll mithilfe einer Lambda Expression umgesetzt werden, die vom Controller an die MovieCell Klasse übergeben wird. Schreibt dazu ein Functional Interface ClickEventHandler, welches eine Methode void onClick(T t) zur Verfügung stellt.
Auszug MovieCell Klasse:
 
public MovieCell(ClickEventHandler addToWatchlistClicked) { super();
watchlistBtn.setOnMouseClicked(mouseEvent -> { addToWatchlistClicked.onClick(getItem());
});
    // ... rest of code
}
Auszug Controller:
private final ClickEventHandler onAddToWatchlistClicked = (clickedItem) -> {
    // add code to add movie to watchlist here
};
Diskussion: warum bietet sich hierfür eine Lambda Expression an?

Exception Handling (4 Punkte)
Implementiert das Exception Handling in der FHMDb. Exceptions sollen abgefangen werden und Fehlermeldungen an User*innen ausgegeben werden, sofern der Ablauf nicht sinnvoll weitergeführt werden kann. Schreibt dazu 2 Custom-Exception Klassen DatabaseException und MovieApiException. Alle Exceptions des Data-Layer sollen als DatabaseExceptions propagiert und im Controller gehandelt werden. Exceptions der MovieAPI Klasse sollen als MovieApiException propagiert und ebenfalls im Controller gehandelt werden. Im Fehlerfall soll User*innen eine sinnvolle Fehlermeldung ausgegeben werden. Die Fehlermeldung soll im UI und nicht in der Konsole dargestellt werden. Ziel ist ein Programm, das auf keinen Fall abstürzt.

Individuelle Punkte bei Exercise-Abnahme (4 Punkte)
Individuell (je Teammitglied) erreichbare Punkte. Zum Beispiel durch Erklärung der Solution, Beantwortung von Fragen, Contribution etc.
Abgabe
• Link zum Repository auf Moodle abgeben (letzter Commit vor Abgabeende wird bewertet)
• Das Repository muss public sein!
• Ist mind. eines der K.O.-Kriterien nicht erfüllt, wird die Abgabe negativ bewertet
 
