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
 * Get string like "1,2,3" as long List and save long list as string by join comma.
 * */
@Alias("string2LongList")
public class String2LongListHandler extends BaseTypeHandler<List<Long>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        if (CollectionUtils.isEmpty(parameter)) {
            ps.setString(i, StringUtils.EMPTY);
            return;
        }
        ps.setString(i, StringUtils.join(parameter, StringUtil.COMMA));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {

        return StringUtil.splitAsLongList(rs.getString(columnName));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

        return StringUtil.splitAsLongList(rs.getString(columnIndex));
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {

        return StringUtil.splitAsLongList(cs.getString(columnIndex));
    }
}
