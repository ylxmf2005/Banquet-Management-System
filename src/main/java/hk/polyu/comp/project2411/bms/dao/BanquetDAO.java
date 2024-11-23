package hk.polyu.comp.project2411.bms.dao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hk.polyu.comp.project2411.bms.connection.SQLConnection;
import hk.polyu.comp.project2411.bms.model.Banquet;

public class BanquetDAO {
    private SQLConnection sqlConnection;

    public BanquetDAO(SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public boolean deleteBanquet(int BIN) throws SQLException {
        String sql = "DELETE FROM Banquet WHERE BIN=?";
        Object[] params = new Object[] {BIN};
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    private int getNextBIN() throws SQLException {
        String getMaxBinSql = "SELECT MAX(BIN) AS MaxBIN FROM Banquet";
        List<Map<String, Object>> result = sqlConnection.executeQuery(getMaxBinSql);
        int newBIN = 1;
        if (!result.isEmpty() && result.get(0).get("MAXBIN") != null) {
            newBIN = ((Number) result.get(0).get("MAXBIN")).intValue() + 1;
        }
        return newBIN;
    }

    private int insertBanquet(Banquet banquet) throws SQLException {
        String sql = "INSERT INTO Banquet (BIN, Name, DateTime, Address, Location, ContactFirstName, ContactLastName, Available, Quota) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = new Object[] {
            banquet.getBIN(),
            banquet.getName(),
            banquet.getDateTime(),
            banquet.getAddress(),
            banquet.getLocation(),
            banquet.getContactFirstName(),
            banquet.getContactLastName(),
            banquet.getAvailable(),
            banquet.getQuota()
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected;
    }

    public Banquet createBanquet(Banquet banquet) throws SQLException {
        int newBIN = getNextBIN();
        banquet.setBIN(newBIN);
        int rowsAffected = insertBanquet(banquet);
        if (rowsAffected > 0) {
            return banquet;
        } else {
            throw new SQLException("Failed to create the banquet.");
        }
    }

    public boolean updateBanquet(Banquet banquet) throws SQLException {
        String sql = "UPDATE Banquet SET Name=?, DateTime=?, Address=?, Location=?, ContactFirstName=?, ContactLastName=?, Available=?, Quota=? WHERE BIN=?";
        Object[] params = new Object[] {
            banquet.getName(),
            banquet.getDateTime(),
            banquet.getAddress(),
            banquet.getLocation(),
            banquet.getContactFirstName(),
            banquet.getContactLastName(),
            banquet.getAvailable(),
            banquet.getQuota(),
            banquet.getBIN()
        };
        int rowsAffected = sqlConnection.executePreparedUpdate(sql, params);
        return rowsAffected > 0;
    }

    public List<Banquet> getAllBanquets() throws SQLException {
        String sql = "SELECT * FROM Banquet";
        List<Map<String, Object>> result = sqlConnection.executeQuery(sql);
        List<Banquet> banquets = new ArrayList<>();
        if (!result.isEmpty()) {
            for (Map<String, Object> row : result) {
                banquets.add(new Banquet(row));
            }
        }
        else return null;
        return banquets;
    }

    public List<Banquet> getAvailableBanquets() throws SQLException {
        String sql = "SELECT * FROM Banquet WHERE Available = Y";

        List<Map<String, Object>> result = sqlConnection.executeQuery(sql);

        List<Banquet> banquets = new ArrayList<>();

        if (!result.isEmpty()) {
            for (Map<String, Object> row : result) {
                banquets.add(new Banquet(row));
            }
        }
        else return null;
        return banquets;
    }
}
