package reservations;

public class Information {

    public Information(String user, String moviename, String date, String time, String seats) {
        this.user = user;
        this.moviename = moviename;
        this.date = date;
        this.time = time;
        this.seats = seats;
    }

    // for tableview (store in observableList)
    public Information(String table, String col1, String col2) {
        if (table.equals("User")) {
            this.user = col1;
            this.password = col2;
        }
        if (table.equals("Movies")) {
            this.moviename = col1;
            this.time = col2;
        }
    }

    public String getUser() {
        return user;
    }

    public String getMoviename() {
        return moviename;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSeats() {
        return seats;
    }

    public String getPassword() {
        return password;
    }

    private String user;
    private String moviename;
    private String date;
    private String time;
    private String seats;
    private String password;
}