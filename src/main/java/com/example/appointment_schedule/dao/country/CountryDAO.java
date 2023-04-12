package com.example.appointment_schedule.dao.country;


import com.example.appointment_schedule.model.Country;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * interface for accessing entities from the Country table in a database.
 * @author Brett Kohler
 */
public interface CountryDAO {
    ObservableList<Country> getAllCountries() throws SQLException;
    Country getCountryByName(String selectedCountryName) throws SQLException;
    Country getCountryById(int countryId) throws SQLException;
    int getCountryAppointmentCount(String country) throws SQLException;
}
