package com.example.appointment_schedule.dao.country;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * implementation for CountryDAO to retrieve Country data from a MySQL database
 * @author Brett Kohler
 */
public class CountryDAOImpl implements CountryDAO {

    private final ObservableList<Country> countries = FXCollections.observableArrayList();

    /**
     * retrieve all countries from Country table and place them in an ObservableList to be used
     * in a fxml form
     * @return list of countries
     * @throws SQLException thrown by invalid SQL query
     */
    @Override
    public ObservableList<Country> getAllCountries() throws SQLException {
        String sql = "select * from Countries";
        Query.makeQuery(sql);

        countries.clear();
        Country countryResult;
        ResultSet result = Query.getResult();
        while (result.next()) {
            int id = result.getInt("Country_ID");
            String name = result.getString("Country");
            Timestamp createDate = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            countryResult = new Country(id, name, createDate, createdBy, lastUpdate, lastUpdatedBy);
            countries.add(countryResult);
        }
        return countries;
    }

    /**
     * retrieve the Country with matching name as parameter `selectedCountryName`
     * @param selectedCountryName name to search for Country with matching name
     * @return Country returned
     * @throws SQLException thrown from invalid SQL syntax
     */
    @Override
    public Country getCountryByName(String selectedCountryName) throws SQLException {
        String sql = "select * from Countries where Country = '" + selectedCountryName + "'";
        Query.makeQuery(sql);

        Country countryResult;
        ResultSet result = Query.getResult();
        if (result.next()) {
            int id = result.getInt("Country_ID");
            String name = result.getString("Country");
            Timestamp createDate = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            countryResult = new Country(id, name, createDate, createdBy, lastUpdate, lastUpdatedBy);
            return countryResult;
        }
        return null;
    }

    /**
     * retrieves a Country using the parameter `countryId`
     * @param countryId id to search for
     * @return Country returned
     * @throws SQLException thrown by invalid SQL syntax
     */
    @Override
    public Country getCountryById(int countryId) throws SQLException {
        String sql = "select * from Countries where Country_ID = " + countryId;
        Query.makeQuery(sql);

        Country countryResult;
        ResultSet result = Query.getResult();
        if (result.next()) {
            int id = result.getInt("Country_ID");
            String name = result.getString("Country");
            Timestamp createDate = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            countryResult = new Country(id, name, createDate, createdBy, lastUpdate, lastUpdatedBy);
            return countryResult;
        }
        return null;
    }

    /**
     * gets the number of appointments located in a specified country
     * @param country name of country to search for matching appointments
     * @return number of appointments
     * @throws SQLException thrown by invalid SQL syntax
     */
    @Override
    public int getCountryAppointmentCount(String country) throws SQLException {
        String sql = "select count(co.Country) as count from Countries as co" +
                " inner join First_Level_Divisions as f on f.Country_ID = co.Country_ID" +
                " inner join Customers as cu on cu.Division_ID = f.Division_ID" +
                " inner join Appointments as a on a.Customer_ID = cu.Customer_ID" +
                " where co.Country = '" + country + "'";
        Query.makeQuery(sql);

        ResultSet result = Query.getResult();
        if (result.next()) {
            return result.getInt("count");
        }
        return 0;
    }
}
