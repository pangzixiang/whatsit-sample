package io.github.pangzixiang.whatsit.sample.controller;

import io.github.pangzixiang.whatsit.sample.dao.UserTableDao;
import io.github.pangzixiang.whatsit.sample.model.AddUserRequest;
import io.github.pangzixiang.whatsit.sample.model.UserTable;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestController;
import io.github.pangzixiang.whatsit.vertx.core.annotation.RestEndpoint;
import io.github.pangzixiang.whatsit.vertx.core.constant.HttpRequestMethod;
import io.github.pangzixiang.whatsit.vertx.core.context.ApplicationContext;
import io.github.pangzixiang.whatsit.vertx.core.controller.BaseController;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
public class EchoController extends BaseController {

    private final UserTableDao userTableDao;

    public EchoController(Router router) {
        super(router);
        userTableDao = new UserTableDao();
    }

    @RestEndpoint(path = "/echo", method = HttpRequestMethod.GET)
    public void echo(RoutingContext routingContext) {
        this.sendJsonResponse(routingContext, HttpResponseStatus.OK, "done");
        log.info("done");
    }

    @RestEndpoint(path = "/user/add", method = HttpRequestMethod.POST)
    public void addUser(RoutingContext routingContext) {
        RequestBody requestBody = routingContext.body();
        if (requestBody.isEmpty()) {
            this.sendJsonResponse(routingContext, HttpResponseStatus.BAD_REQUEST, "EMPTY REQUEST BODY");
        } else {
            AddUserRequest addUserRequest = requestBody.asPojo(AddUserRequest.class);
            log.info("Received Request [{}]", addUserRequest);
            UserTable userTable = UserTable
                    .builder()
                    .userId(UUID.randomUUID().toString())
                    .userName(addUserRequest.getUsername())
                    .key(addUserRequest.getKey())
                    .createdTimestamp(LocalDateTime.now())
                    .build();
            userTableDao
                    .save(userTable)
                    .onComplete(userTableAsyncResult -> {
                        if (userTableAsyncResult.succeeded()) {
                            UserTable result = userTableAsyncResult.result();
                            this.sendJsonResponse(routingContext, HttpResponseStatus.OK, result);
                            log.info("Save User [{}] processed!", result.getUserName());
                        } else {
                            this.sendJsonResponse(routingContext, HttpResponseStatus.BAD_REQUEST, userTableAsyncResult.cause().getMessage());
                            log.error("Failed to process save request for user [{}]", userTable.getUserName(), userTableAsyncResult.cause());
                        }
                    });
        }
    }

    @RestEndpoint(path = "/user/update", method = HttpRequestMethod.POST)
    public void updateUserKey(RoutingContext routingContext) {
        RequestBody requestBody = routingContext.body();
        if (requestBody.isEmpty()) {
            this.sendJsonResponse(routingContext, HttpResponseStatus.BAD_REQUEST, "EMPTY REQUEST BODY");
        } else {
            AddUserRequest addUserRequest = requestBody.asPojo(AddUserRequest.class);
            log.info("Received Request [{}]", addUserRequest);
            userTableDao
                    .selectByUsername(addUserRequest.getUsername())
                    .compose(userTable -> {
                        if (userTable != null) {
                            UserTable userTable1 = UserTable
                                    .builder()
                                    .userName(addUserRequest.getUsername())
                                    .key(addUserRequest.getKey())
                                    .build();
                            return userTableDao
                                    .updateKeyByUserName(userTable1);
                        } else {
                            return Future.failedFuture("USER NOT FOUND");
                        }
                    })
                    .onSuccess(userTable -> {
                        this.sendJsonResponse(routingContext, HttpResponseStatus.OK, userTable);
                        log.info("Update request for user [{}] processed", userTable.getUserName());
                    })
                    .onFailure(throwable -> {
                        this.sendJsonResponse(routingContext, HttpResponseStatus.BAD_REQUEST, throwable.getMessage());
                        log.error("Failed to process update request!", throwable);
                    });
        }
    }
}
