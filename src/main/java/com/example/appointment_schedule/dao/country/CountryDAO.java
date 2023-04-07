package com.example.appointment_schedule.dao.country;


import com.example.appointment_schedule.model.Country;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface CountryDAO {
    public ObservableList<Country> getAllCountries() throws SQLException;
    public void addCountry(Country country);
    public void deleteCountry(Country country);
    public void updateCountry(Country country);
    public Country getCountryByName(String selectedCountryName) throws SQLException;

    public Country getCountryById(int countryId) throws SQLException;
}
