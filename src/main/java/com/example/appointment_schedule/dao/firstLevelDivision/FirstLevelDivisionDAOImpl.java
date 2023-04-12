package com.example.appointment_schedule.dao.firstLevelDivision;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * implementation for FirstLevelDivisionDAO to retrieve FirstLevelDivision data from a MySQL database
 * @author Brett Kohler
 */
public class FirstLevelDivisionDAOImpl implements FirstLevelDivisionDAO{
    private final ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();

    /**
     * retrieves all FirstLevelDivisions from database
     * @return list of FirstLevelDivision data
     * @throws SQLException thrown by invalid SQL syntax
     */
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

    /**
     * retrieves a FirstLevelDivision with matching id as parameter `divisionId`
     * @param divisionId id to search for
     * @return FirstLevelDivision matching id
     * @throws SQLException thrown by invalid SQL syntax
     */
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

    /**
     * retrieves a FirstLevelDivision with matching name as parameter `name`
     * @param name name for search for
     * @return FirstLevelDivision with matching name
     * @throws SQLException thrown by invalid SQL syntax
     */
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
