package com.fyts.mail.common.typehandler;

import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.Arrays;

/**
 * @author: 刘志新
 * @email: lzxorz@163.com
 */
@MappedTypes({String[].class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null)
            ps.setNull(i, Types.VARCHAR);
        else {
            JSONArray array = new JSONArray(Arrays.asList(parameter));
            ps.setString(i, array.toString());
        }
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getArray(rs.getString(columnName));
    }


    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getArray(rs.getString(columnIndex));
    }


    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getArray(cs.getString(columnIndex));
    }

    private String[] getArray(String columnValue) {
        if (columnValue == null) return null;
        JSONArray jsonArr = JSONArray.parseArray(columnValue);
        return jsonArr.toArray(new String[jsonArr.size()]);
    }
}