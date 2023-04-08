package com.example.appointment_schedule.dao.country;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CountryDAOImpl implements CountryDAO {

    private final ObservableList<Country> countries = FXCollections.observableArrayList();
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

    @Override
    public void addCountry(Country country) {

    }

    @Override
    public void deleteCountry(Country country) {

    }

    @Override
    public void updateCountry(Country country) {

    }

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
