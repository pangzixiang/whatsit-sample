package io.github.pangzixiang.whatsit.sample.mapper;

import io.github.pangzixiang.whatsit.sample.model.UserTable;
import io.vertx.sqlclient.templates.TupleMapper;

import java.util.HashMap;
import java.util.Map;

public class ColumnMapper {

    public static final TupleMapper<UserTable> USER_TABLE_TUPLE_MAPPER = TupleMapper.mapper(userTable -> {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userTable.getUserId());
        map.put("USER_NAME", userTable.getUserName());
        map.put("KEY", userTable.getKey());
        map.put("CREATED_TIMESTAMP", userTable.getCreatedTimestamp());
        return map;
    });

}
