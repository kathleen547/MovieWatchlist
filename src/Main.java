import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;



public class Main {

    static Database db1 = new Database();
    public static int printMenu(){
        System.out.println("Please select one of the following options: \n" +
                "1) Add new movie. \n" +
                "2) View upcoming movies.\n" +
                "3) View all movies.\n" +
                "4) Watch a movie.\n" +
                "5) View watched movies.\n" +
                "6) Add user to the app.\n" +
                "7) Search for movie.\n" +
                "8) Exit. \n" +
                "Your selection: ");
        Scanner out = new Scanner(System.in);
        int selected = out.nextInt();
        return selected;
    }

    public static void promptAddMovie(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Movie title: ");
        String title;
        String date;
        LocalDate date1;
        if (scanner.hasNextLine()) {
            title = scanner.nextLine();
            System.out.println("Release date (YYYY-mm-dd): ");
            date = scanner.nextLine();
            date1 = LocalDate.parse(date);
            db1.addMovie(title, date1);
        } else {
            System.out.println("No line to read.");
        }
    }


    public static void printMovieList(String heading, List<Movie> movies){
        System.out.println("-- " + heading + " movies --");
        for (Movie movie : movies){
            LocalDate movieDate = movie.getReleaseDate();
            System.out.println(movie.getId()+ ": " + movie.getTitle() + "(on " + movieDate + ")");
            System.out.println();
        }
    }


    public static void promptAddUser(){
        Scanner out = new Scanner(System.in);
        System.out.println("Username: ");
        String username = out.nextLine();
        db1.addUser(username);
    }


    public static void promptWatchMovie(){
        Scanner out = new Scanner(System.in);
        System.out.println("Username: ");
        String username = out.nextLine();
        System.out.println("Movie id: ");
        int id = out.nextInt();
        db1.watchMovie(username, id);
    }


    public static void promptViewWatchedMovies(){
        Scanner out = new Scanner(System.in);
        System.out.println("Username: ");
        String username = out.nextLine();
        List<Movie> movieList = db1.getWatchedMovies(username);
        if(!movieList.isEmpty()){
            printMovieList("Watched: ", movieList);
        }
        else{
            System.out.println("That user has watched no movies yet");
        }
    }


    public static void promptSearchMovies(){
        Scanner out = new Scanner(System.in);
        System.out.println("Enter the partial movie title");
        String partial = out.nextLine();
        List<Movie> movieList2 = db1.searchMovies(partial);
        if(!movieList2.isEmpty()){
            printMovieList("Movies found ", movieList2);
        }
        else{
            System.out.println("Found no movies for that search term");
        }
    }


    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to the Movie Watchlist App");
        System.out.println(System.getProperty("java.class.path"));
        Database database = new Database();
        database.createTables();
        int result = printMenu();
        while(result != 8){
            if(result == 1){
                promptAddMovie();
            }
            else if(result == 2){
                List<Movie> movies = database.getMovies(true);
                printMovieList("Upcoming ", movies);
            }
            else if(result == 3){
                List<Movie> movies = database.getMovies(false);
                printMovieList("All ", movies);
            }
            else if(result== 4){
                promptWatchMovie();
            }
            else if(result == 5){
                promptViewWatchedMovies();
            }
            else if(result == 6){
                promptAddUser();
            }
            else if (result== 7){
                promptSearchMovies();
            }
            else{
                System.out.println("Wrong input, please try again");
            }
            result = printMenu();
        }
        database.connection.close();
    }
}