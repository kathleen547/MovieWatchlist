import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

class Database {

    private static final String DATABASE_URL = "jdbc:postgresql://database";

    Connection connection;

    public Database() {
        try {
            // Load the PostgreSQL JDBC driver
            String user = "xyz";
            String passwd = "***";
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DATABASE_URL,user,passwd);
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed to establish connection with the database");
        }
    }


    public void createTables() {
        String sqlCreateMoviesTbl = "CREATE TABLE IF NOT EXISTS movies "
                + "  (id           serial,"
                + "   title        varchar(256) not null,"
                + "   releaseDate  date not null,"
                + "   primary key (id));";
        String sqlCreateUsersTbl = "CREATE TABLE IF NOT EXISTS users "
                + "  (username      varchar(256),"
                + "   primary key (username));";
        String sqlCreateWatchlistTbl = "CREATE TABLE IF NOT EXISTS watched "
                + "  (userUsername   varchar(256),"
                + "   movie_id       bigint,"
                + "   FOREIGN KEY(userUsername) REFERENCES users(username),"
                + "   FOREIGN KEY(movie_id) REFERENCES movies(id));";

        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlCreateMoviesTbl);
            statement.executeUpdate(sqlCreateUsersTbl);
            statement.executeUpdate(sqlCreateWatchlistTbl);
            statement.close();

        } catch(SQLException e){
            System.out.println("Failed to create tables");
        }
    }


    public List<Movie> getMovies(boolean upcoming){
        System.out.println("Getting movies");
        final String queryAll = "SELECT * FROM movies;";
        final String queryUpcoming = "SELECT * FROM movies WHERE releaseDate > ?;";
        List<Movie> movies = new ArrayList<>();
        try {
            ResultSet rs = null;
            LocalDate today = LocalDate.now();
            Statement statement = connection.createStatement();
            if (!upcoming) {
                rs = statement.executeQuery(queryAll);
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement(queryUpcoming, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setDate(1, Date.valueOf(today));
                rs = preparedStatement.executeQuery();
            }
            while(rs.next()) {
                final int movieId = rs.getInt("id");
                final String movieTitle = rs.getString("title");
                final Date movieDate = rs.getDate("releaseDate");

                Movie movie = new Movie();
                movie.setId(movieId);
                movie.setTitle(movieTitle);
                movie.setReleaseDate(movieDate.toLocalDate());
                movies.add(movie);
            }

            System.out.println("Movies have been selected");

        } catch (SQLException e) {
            System.out.println("Failed to execute the get movies query");
        }
        return movies;
    }


    public void addMovie(String title, LocalDate date){
        System.out.println("Adding the movie");
        long id = 0;
        final String query = "INSERT INTO movies (title, releaseDate) VALUES(?, ?);";
        try{
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setDate(2, java.sql.Date.valueOf(date));
            long affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                }
            }
            System.out.println("Movie added");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to add a movie");
        }
    }


    public void watchMovie(String username, int movieID){
        System.out.println("Adding watched movie");
        final String query = "INSERT INTO watched VALUES(?, ?);";
        try{
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setInt(2, movieID);
            ps.executeUpdate();

        }
        catch (SQLException e){
            System.out.println("Failed to execute adding watched movie query");
        }
    }


    public List<Movie> getWatchedMovies(String username){
        System.out.println("Movies watched by " + username);
        final String query = "SELECT movies.* FROM users JOIN watched ON users.username=watched.userUsername " +
                "                        JOIN movies ON watched.movie_id=movies.id " +
                "                        WHERE users.username = ?;";
        List<Movie> movies = new ArrayList<>();
        try{
            ResultSet rs = null;
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            rs = ps.executeQuery();
            while(rs.next()) {
                final int movieId = rs.getInt("id");
                final String movieTitle = rs.getString("title");
                final Date movieDate = rs.getDate("releaseDate");

                Movie movie = new Movie();
                movie.setId(movieId);
                movie.setTitle(movieTitle);
                movie.setReleaseDate(movieDate.toLocalDate());
                movies.add(movie);
            }

        }
        catch(SQLException e){
            System.out.println("Failed to execute getting watched movies query");
        }
        return movies;
    }


    public void addUser(String username){
        final String query = "INSERT INTO  users (username) VALUES (?)";
        try{
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.executeUpdate();

        }
        catch (SQLException e){
            System.out.println("Failed to execute adding user query");
        }
    }


    public List<Movie> searchMovies(String searchTerm){
        final String query = "SELECT * FROM movies WHERE title LIKE ?;";
        List<Movie> movies = new ArrayList<>();
        try{
            ResultSet rs = null;
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "%" + searchTerm + "%");
            rs = ps.executeQuery();
            while(rs.next()) {

                final int movieId = rs.getInt("id");
                final String movieTitle = rs.getString("title");
                final Date movieDate = rs.getDate("releaseDate");

                Movie movie = new Movie();
                movie.setId(movieId);
                movie.setTitle(movieTitle);
                movie.setReleaseDate(movieDate.toLocalDate());
                movies.add(movie);
            }
        }
        catch(SQLException e){
            System.out.println("Failed to execute searching movies query");
        }
        return movies;
    }
}
