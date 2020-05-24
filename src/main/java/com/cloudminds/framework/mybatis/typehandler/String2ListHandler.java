package com.cloudminds.framework.mybatis.typehandler;

import com.cloudminds.framework.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.CollectionUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Get string like "1,2,3" as List and save list as string by join comma.
 * */
@Alias("string2List")
public class String2ListHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        if (CollectionUtils.isEmpty(parameter)) {
            ps.setString(i, StringUtils.EMPTY);
            return;
        }
        ps.setString(i, StringUtils.join(parameter, StringUtil.COMMA));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {

        return StringUtil.splitAsList(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return StringUtil.splitAsList(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        return StringUtil.splitAsList(cs.getString(columnIndex));
    }
}
