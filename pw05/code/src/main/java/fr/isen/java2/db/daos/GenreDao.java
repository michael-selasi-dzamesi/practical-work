package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {
	/**
	 * @author Michael Selasi Dzamesi
	 *
	 */

	//Connection connection = DataSourceFactory.getDataSource().getConnection()

	public List<Genre> listGenres() {
		List<Genre> listOfGenre = new ArrayList<>();

		try(Connection connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db")){
			try(Statement statement = connection.createStatement()) {
				try(ResultSet resultSet = statement.executeQuery("SELECT * FROM genre")){
					while(resultSet.next()){
						Genre genre = new Genre(resultSet.getInt("idgenre"),resultSet.getString("name"));
						listOfGenre.add(genre);
					}
				}
			}
		} catch (SQLException e){ e.printStackTrace(); }

		return listOfGenre;
	}

	public Genre getGenre(String name) {

		try(Connection connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db")){
			try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")){
				statement.setString(1, name);
				try(ResultSet resultSet = statement.executeQuery()){
					if(resultSet.next()){
						return new Genre(resultSet.getInt("idgenre"),resultSet.getString("name"));
					}
				}
			}
		} catch (SQLException e){ e.printStackTrace(); }

		return null;
	}

	public void addGenre(String name) {

		try(Connection connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db")){
			try(PreparedStatement statement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")){
				statement.setString(1, name);
				statement.executeUpdate();
				ResultSet ids = statement.getGeneratedKeys();
				if (ids.next()) {
					new Genre(ids.getInt(1), name);
				}
			}
		} catch (SQLException e){ e.printStackTrace(); }

	}
}
