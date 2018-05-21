package reservations;

import java.util.*;

import java.io.*;
import java.sql.*;

public class Database {

    Database() {
        this("data.db");
    }

    Database(String data) {
        connect(data);
        createTable();

    }

    private void connect(String data) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("class not found.");
        }
        try {
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:../%s", data));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Problems with connecting to the database");
        }
    }

    private void createTable() {
        try {
            statement = connection.createStatement();
            String createTable = "create table if not exists User " + "(user varchar(64) not null primary key, "
                    + " password varchar(32) not null);";
            statement.executeUpdate(createTable);

            createTable = "create table if not exists Movies" + "(moviename varchar(32) not null,"
                    + " time time not null," + " primary key(moviename, time));";
            statement.executeUpdate(createTable);

            createTable = "create table if not exists Watch " + "(user varchar(64) not null references User,"
                    + "moviename varchar(32) not null references Movies," + "date date not null,"
                    + "time time not null references Movies," + "allocatSeats varchar(32) not null,"
                    + "primary key(user, moviename, date, time, allocatSeats));";
            statement.executeUpdate(createTable);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("can't create table");
        }
    }

    // movieChoice for distinct, to display right data
    public ResultSet selectQuery(String table, boolean movieChoice) {
        return selectQuery(table, movieChoice, false, null);

    }

    // for time with moviename
    public ResultSet selectQuery(String table, String moviename) {
        return selectQuery(table, false, true, moviename);
    }

    // Overload for select movieTime
    public ResultSet selectQuery(String table, boolean movieChoice, boolean time, String moviename) {
        String query = "";
        // standardQuery
        if (movieChoice == false && time == false && moviename == null) {
            query = String.format("select * from %s;", table);
        }
        // movieChoice
        if (movieChoice == true && time == false && moviename == null) {
            query = String.format("select distinct moviename from %s;", table);
        }
        // time
        else if (time == true && movieChoice == false && moviename != null) {
            query = String.format("select distinct time from %s where moviename = '%s'", table, moviename);
        }
        try {
            ResultSet rs;
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("can't execute query");
            return null;
        }
    }

    // for tables user and Movie
    public boolean insertSimplyTable(String table, String col1, String col2) {
        String insert = "";
        if (table.equals("User")) {
            insert = String.format("insert into User(user, password) " + "values('%s','%s');", col1, col2);

        }
        if (table.equals("Movies")) {
            insert = String.format("insert into Movies(moviename, time) " + "values('%s','%s');", col1, col2);
        }
        try {
            statement = connection.createStatement();

            statement.executeUpdate(insert);
            statement.close();
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    // delete for User and Movies
    public boolean deleteSimplyTable(String table, String col1, String col2) {
        String delete = "";
        if (table.equals("User")) {
            delete = String.format("delete from User where user = '%s' and password = '%s'; ", col1, col2);

        }
        if (table.equals("Movies")) {
            delete = String.format("delete from Movies where moviename = '%s' and time = '%s'; ", col1, col2);
        }
        try {
            statement = connection.createStatement();

            statement.executeUpdate(delete);
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("can't delete");
            return false;
        }
    }

    public boolean deleteWatch(String user, String moviename, String date, String time, String allocatSeats) {
        String delete = String.format(
                "delete from Watch where user = '%s' and moviename = '%s' and date = '%s' and time = '%s' and allocatSeats = '%s'; ",
                user, moviename, date, time, allocatSeats);
        try {
            statement = connection.createStatement();

            statement.executeUpdate(delete);
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("can't delete");
            return false;
        }
    }

    // if admin delete a movie all stored reservations will be remove
    public boolean deleteWatchMovie(String moviename, String time) {
        String delete = String.format("delete from Watch where moviename = '%s' and time = '%s'", moviename, time);
        try {
            statement = connection.createStatement();
            statement.executeUpdate(delete);
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("can't delete");
            return false;
        }
    }

    public boolean insertWatch(String user, String moviename, String date, String time, String allocatedSeats) {

        String insert = String.format("insert into Watch(user, moviename, date, time, allocatSeats) "
                + "values('%s','%s', '%s', '%s', '%s');", user, moviename, date, time, allocatedSeats);
        try {
            statement = connection.createStatement();
            statement.executeUpdate(insert);
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("can't inset your reservation");
            return false;
        }
    }

    public ResultSet getSeatsFromWatch(String moviename, String date, String time) {
        String query = String.format(
                "select allocatSeats,user from Watch where moviename = '%s' and date = '%s' and time = '%s'", moviename,
                date, time);
        try {
            ResultSet rs;
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("can't get seats");
            return null;
        }
    }

    public String loginQuery(String user) {
        try {
            ResultSet rs;
            statement = connection.createStatement();
            rs = statement.executeQuery(String.format("select password from User WHERE user='%s';", user));
            return rs.getString("password");
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("user or pwassword not found");
            // System.exit(0);
            return "failed";
        }
    }

    public ResultSet selectQueryWatch(String user) {
        try {
            ResultSet rs;
            statement = connection.createStatement();
            rs = statement.executeQuery(String.format("select * from Watch where user='%s';", user));
            return rs;
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("user or pwassword not found");
            // System.exit(0);
            return null;
        }
    }

    /*
     * public static void main(String[] args){ Database db = new Database();
     * //db.insertTable("User", "test2", "test200");
     * 
     * ResultSet res = db.selectQuery("User", false); //System.out.println(res);
     * try{ while(res.next()){ String user = res.getString("user"); String password
     * = res.getString("password");
     * 
     * System.out.println("user = " + user); System.out.println("password  = " +
     * password);
     * 
     * } } catch(SQLException e){ e.printStackTrace(); } String rs =
     * db.loginQuery("test2"); System.out.println(rs); }
     */

    private Connection connection = null;
    private Statement statement = null;

}