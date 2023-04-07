package com.example.appointment_schedule.dao.firstLevelDivision;


import com.example.appointment_schedule.model.FirstLevelDivision;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface FirstLevelDivisionDAO {
    public ObservableList<FirstLevelDivision> getAllFirstLevelDivisions() throws SQLException;
    public void addFirstLevelDivision(FirstLevelDivision firstLevelDivision);
    public void deleteFirstLevelDivision(FirstLevelDivision firstLevelDivision);
    public void updateFirstLevelDivision(FirstLevelDivision firstLevelDivision);

    public FirstLevelDivision getFirstLevelDivisionById(int divisionId) throws SQLException;

    public FirstLevelDivision getFirstLevelDivisionByName(String name) throws SQLException;
}
