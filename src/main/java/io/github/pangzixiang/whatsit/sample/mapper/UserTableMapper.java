package io.github.pangzixiang.whatsit.sample.mapper;

import io.github.pangzixiang.whatsit.sample.model.UserTable;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.RowMapper;

public class UserTableMapper implements RowMapper<UserTable> {
    @Override
    public UserTable map(Row row) {
        return UserTable
                .builder()
                .userId(row.getString("user_id"))
                .userName(row.getString("user_name"))
                .key(row.getString("key"))
                .createdTimestamp(row.getLocalDateTime("created_timestamp"))
                .build();
    }
}
