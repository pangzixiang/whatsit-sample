package io.github.pangzixiang.whatsit.sample.dao;

import io.github.pangzixiang.whatsit.sample.mapper.UserTableMapper;
import io.github.pangzixiang.whatsit.sample.model.UserTable;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.vertx.core.Future;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

import static io.github.pangzixiang.whatsit.sample.mapper.ColumnMapper.USER_TABLE_TUPLE_MAPPER;

@Slf4j
public class UserTableDao {

    private static final String SAVE_SQL = """
            insert into USER_TABLE (USER_ID, USER_NAME, KEY, CREATED_TIMESTAMP)
             values (#{USER_ID}, #{USER_NAME}, #{KEY}, #{CREATED_TIMESTAMP})
            """;

    private static final String SELECT_BY_USERNAME_SQL = """
            select * from USER_TABLE where USER_NAME = #{USER_NAME}
            """;

    private static final String UPDATE_KEY_BY_USERNAME_SQL = """
            update USER_TABLE set KEY = #{KEY} where USER_NAME = #{USER_NAME}
            """;

    private final ApplicationContext applicationContext;

    public UserTableDao() {
        this.applicationContext = ApplicationContext.getApplicationContext();
    }


    public Future<UserTable> save(UserTable userTable) {
        return SqlTemplate
                .forUpdate(this.applicationContext.getJdbcPool(), SAVE_SQL)
                .mapFrom(USER_TABLE_TUPLE_MAPPER)
                .execute(userTable)
                .map(userTable)
                .onSuccess(voidSqlResult -> log.info("NEW USER [{}] saved!", userTable.getUserName()))
                .onFailure(throwable -> log.info("FAILED to save new user [{}]", userTable.getUserName(), throwable));
    }

    public Future<UserTable> selectByUsername(String username) {
        return SqlTemplate
                .forQuery(this.applicationContext.getJdbcPool(), SELECT_BY_USERNAME_SQL)
                .mapTo(new UserTableMapper())
                .execute(Collections.singletonMap("USER_NAME", username))
                .map(userTables -> {
                    if (userTables.iterator().hasNext()) {
                        return userTables.iterator().next();
                    } else {
                        return null;
                    }
                })
                .onFailure(throwable -> log.info("FAILED to query user by username [{}]", username, throwable));
    }

    public Future<UserTable> updateKeyByUserName(UserTable userTable) {
        return SqlTemplate
                .forUpdate(this.applicationContext.getJdbcPool(), UPDATE_KEY_BY_USERNAME_SQL)
                .mapFrom(USER_TABLE_TUPLE_MAPPER)
                .execute(userTable)
                .map(userTable)
                .onSuccess(result -> log.info("USER [{}] KEY updated to [{}]", result.getUserName(), result.getKey()))
                .onFailure(throwable -> log.error("Failed to update Key [{}] for user [{}]", userTable.getKey(), userTable.getUserName()));
    }

}
