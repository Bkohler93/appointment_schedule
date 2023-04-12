package com.example.appointment_schedule.dao.firstLevelDivision;


import com.example.appointment_schedule.model.FirstLevelDivision;
import javafx.collections.ObservableList;

import java.sql.SQLException;


/**
 * interface for accessing entities from the FirstLevelDivision table in a database.
 * @author Brett Kohler
 */
public interface FirstLevelDivisionDAO {
    ObservableList<FirstLevelDivision> getAllFirstLevelDivisions() throws SQLException;
    FirstLevelDivision getFirstLevelDivisionById(int divisionId) throws SQLException;
    FirstLevelDivision getFirstLevelDivisionByName(String name) throws SQLException;
}
