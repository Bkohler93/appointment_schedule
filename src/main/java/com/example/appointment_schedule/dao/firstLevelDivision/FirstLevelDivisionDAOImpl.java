package com.example.appointment_schedule.dao.firstLevelDivision;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FirstLevelDivisionDAOImpl implements FirstLevelDivisionDAO{
    private final ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
    @Override
    public ObservableList<FirstLevelDivision> getAllFirstLevelDivisions() throws SQLException {
        String sql = "select * from First_Level_Divisions";
        Query.makeQuery(sql);

        FirstLevelDivision divisionResult;
        divisions.clear();
        ResultSet result = Query.getResult();
        while (result.next()) {
            int id = result.getInt("Division_ID");
            String division = result.getString("Division");
            Timestamp createDate = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            int countryId = result.getInt("Country_ID");

            divisionResult = new FirstLevelDivision(id, division, createDate, createdBy, lastUpdate, lastUpdatedBy, countryId);
            divisions.add(divisionResult);
        }
        return divisions;
    }

    @Override
    public void addFirstLevelDivision(FirstLevelDivision firstLevelDivision) {

    }

    @Override
    public void deleteFirstLevelDivision(FirstLevelDivision firstLevelDivision) {

    }

    @Override
    public void updateFirstLevelDivision(FirstLevelDivision firstLevelDivision) {

    }

    @Override
    public FirstLevelDivision getFirstLevelDivisionById(int divisionId) throws SQLException {
        String sql = "select * from First_Level_Divisions where Division_ID = " + divisionId;
        Query.makeQuery(sql);

        FirstLevelDivision divisionResult;
        divisions.clear();
        ResultSet result = Query.getResult();
        if (result.next()) {
            int id = result.getInt("Division_ID");
            String division = result.getString("Division");
            Timestamp createDate = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            int countryId = result.getInt("Country_ID");
            divisionResult = new FirstLevelDivision(id, division, createDate, createdBy, lastUpdate, lastUpdatedBy, countryId);
            return divisionResult;
        }
        return null;
    }

    @Override
    public FirstLevelDivision getFirstLevelDivisionByName(String name) throws SQLException {
        String sql = "select * from First_Level_Divisions where Division = '" + name + "'";
        Query.makeQuery(sql);

        FirstLevelDivision divisionResult;
        divisions.clear();
        ResultSet result = Query.getResult();
        if (result.next()) {
            int id = result.getInt("Division_ID");
            String division = result.getString("Division");
            Timestamp createDate = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            int countryId = result.getInt("Country_ID");
            divisionResult = new FirstLevelDivision(id, division, createDate, createdBy, lastUpdate, lastUpdatedBy, countryId);
            return divisionResult;
        }
        return null;
    }
}
