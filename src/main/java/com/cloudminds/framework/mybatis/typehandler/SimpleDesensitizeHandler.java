package com.cloudminds.framework.mybatis.typehandler;

import com.cloudminds.framework.utils.encryption.DesensitizeUtil;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Save real account in db, but show secret account with "*"
 */
@Alias("simpleDesensitize")
public class SimpleDesensitizeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        //save origin bank account
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {

        return DesensitizeUtil.simpleDesensitize(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return DesensitizeUtil.simpleDesensitize(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        return DesensitizeUtil.simpleDesensitize(cs.getString(columnIndex));
    }

}
