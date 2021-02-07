package fr.isen.java2.db.daos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDao {

	/**
	 * @author Michael Selasi Dzamesi
	 *
	 */

	public List<Film> listFilms() {
		List<Film> listOfFilm = new ArrayList<>();
		try(Connection connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db")){
			try(Statement statement = connection.createStatement()) {
				try(ResultSet resultSet = statement.executeQuery("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre")){
					browsingResultSet(listOfFilm, resultSet);
				}
			}
		} catch (SQLException e){ e.printStackTrace(); }

		return listOfFilm;
	}

	public List<Film> listFilmsByGenre(String genreName) {
		List<Film> listOfFilm = new ArrayList<>();
		try(Connection connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db")){
			try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name = ?")){
				statement.setString(1, genreName);
				try(ResultSet resultSet = statement.executeQuery()){
					browsingResultSet(listOfFilm, resultSet);
				}
			}
		} catch (SQLException e){ e.printStackTrace(); }

		return listOfFilm;
	}

	private void browsingResultSet(List<Film> listOfFilm, ResultSet resultSet) throws SQLException {
		while(resultSet.next()){
			Date sqlDate = resultSet.getDate("release_date");
			LocalDate date = sqlDate.toLocalDate();

			Film film = new Film(resultSet.getInt("idfilm"),
								resultSet.getString("title"),
								date,
								new Genre(resultSet.getInt("idgenre"),resultSet.getString("name")),
								resultSet.getInt("duration"),
								resultSet.getString("director"),
								resultSet.getString("summary")
			);
			listOfFilm.add(film);
		}
	}

	public Film addFilm(Film film) {

		try(Connection connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db")){
			try(PreparedStatement statement = connection.prepareStatement("INSERT INTO film(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)")){
				statement.setString(1, film.getTitle());
				statement.setDate(2, Date.valueOf(film.getReleaseDate()));
				statement.setInt(3, film.getGenre().getId());
				statement.setInt(4, film.getDuration());
				statement.setString(5, film.getDirector());
				statement.setString(6, film.getSummary());

				statement.executeUpdate();
				ResultSet ids = statement.getGeneratedKeys();

				if (ids.next()) {
					return new Film (ids.getInt(1), film.getTitle(), film.getReleaseDate(), film.getGenre(),
							film.getDuration(),film.getDirector(), film.getSummary());
				}
			}
		} catch (SQLException e){ e.printStackTrace(); }

		return null;
	}
}
