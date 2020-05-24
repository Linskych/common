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
 * Get string like "1,2,3" as int List and save int list as string by join comma.
 * */
@Alias("string2IntList")
public class String2IntListHandler extends BaseTypeHandler<List<Integer>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
        if (CollectionUtils.isEmpty(parameter)) {
            ps.setString(i, StringUtils.EMPTY);
            return;
        }
        ps.setString(i, StringUtils.join(parameter, StringUtil.COMMA));
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName) throws SQLException {

        return StringUtil.splitAsIntList(rs.getString(columnName));
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return StringUtil.splitAsIntList(rs.getString(columnIndex));
    }

    @Override
    public List<Integer> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        return StringUtil.splitAsIntList(cs.getString(columnIndex));
    }
}
