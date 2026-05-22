package com.restaurant.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.restaurant.dao.DatabaseManager;

public class Util {

    public static ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
        setParams(ps, params);
        return ps.executeQuery();
    }

    public static int update(String sql, Object... params) throws SQLException {
        PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql);
        setParams(ps, params);
        return ps.executeUpdate();
    }

    public static int insert(String sql, Object... params) throws SQLException {
        PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS);
        setParams(ps, params);
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        return keys.next() ? keys.getInt(1) : -1;
    }

    private static void setParams(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Boolean b)                    ps.setBoolean(i + 1, b);
            else if (params[i] instanceof Integer n)               ps.setInt(i + 1, n);
            else if (params[i] instanceof java.math.BigDecimal bd) ps.setBigDecimal(i + 1, bd);
            else if (params[i] instanceof Timestamp ts)            ps.setTimestamp(i + 1, ts);
            else ps.setString(i + 1, params[i] == null ? null : params[i].toString());
        }
    }
}