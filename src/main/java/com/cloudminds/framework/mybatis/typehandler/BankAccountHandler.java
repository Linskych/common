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

        return desensitize(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return desensitize(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        return desensitize(cs.getString(columnIndex));
    }

    /**
     * Split account into 3 parts and replace the part in the middle with stars
     * */
    private String desensitize(String account) {
        if (StringUtils.isEmpty(account)) {
            return account;
        }
        int size = account.length();
        switch (size){
            case 1:
                return "***";
            case 2:
                return "***" + account.substring(1);
            default:
               return account.substring(0, size/3) + "***" + account.substring(size*2/3-1, size-1);
        }
    }

}
