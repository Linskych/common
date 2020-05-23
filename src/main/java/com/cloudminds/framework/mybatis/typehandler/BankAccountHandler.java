package com.cloudminds.framework.mybatis.typehandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Save real account in db, but show secret account with "*"
 */
public class BankAccountHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        //save origin bank account
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {

        String account = rs.getString(columnName);
        return StringUtils.isEmpty(account) ? account : desensitize(account,3);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        String account = rs.getString(columnIndex);
        return StringUtils.isEmpty(account) ? account : desensitize(account,3);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        String account = cs.getString(columnIndex);
        return StringUtils.isEmpty(account) ? account : desensitize(account,3);
    }

    /**
     * @param n Split account into n parts and replace the part in the middle with stars
     * */
    private String desensitize(String account, int n) {

        int size = account.length();
        return account.substring(0, size/n) + "***" + account.substring(size*(n-1)/n-1, size-1);
    }

}
