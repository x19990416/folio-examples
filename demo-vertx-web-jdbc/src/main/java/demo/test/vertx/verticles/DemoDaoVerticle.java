package demo.test.vertx.verticles;

import demo.test.vertx.ErrorCodes;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.UpdateResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoDaoVerticle extends AbstractVerticle {
  private JDBCClient dbClient;

  public void start(Future<Void> startFuture) throws Exception {
    // 创建数据库客户端
    dbClient = JDBCClient.createShared(vertx, config());
    dbClient.getConnection(ar -> {
      if (ar.failed()) {
        System.out.println("regist demo.dao error\t"+ar.failed());
        log.error("Could not open a database connection", ar.cause());
        startFuture.fail(ar.cause());
      } else {
        System.out.println("regist demo.dao success");
        vertx.eventBus().consumer("demo.dao", this::onMessage); // 注册事件监听
        startFuture.complete();
      }
    });
  }

  private void onMessage(Message<JsonObject> message) {
    if (!message.headers().contains("action")) {
      message.fail(ErrorCodes.NO_ACTION_SPECIFIED.code(), ErrorCodes.NO_ACTION_SPECIFIED.msg());
      return;
    }
    String action = message.headers().get("action");
    switch (action) {
      case "updateOrDelete":
        updateOrDelete(message);
        break;
      case "save":
        save(message);
        break;
      default:
        message.fail(ErrorCodes.BAD_ACTION.code(), ErrorCodes.BAD_ACTION.msg() + "-" + action);
    }
  }

  private void updateOrDelete(Message<JsonObject> message) {
    String sql = message.body().getString("sql");
    log.info("sql is :" + sql);
    dbClient.updateWithParams(sql, new JsonArray(), res -> {
      if (res.succeeded()) {
        UpdateResult result = res.result();
        message.reply(new JsonObject().put("count", result.getUpdated()));
      } else {
        log.error(ErrorCodes.DB_ERROR.name(), res.cause().getMessage());
        message.fail(ErrorCodes.DB_ERROR.code(), res.cause().getMessage());
      }
    });
  }

  private void save(Message<JsonObject> message) {
    String sql = message.body().getString("sql");
    log.info("sql is :" + sql);
    dbClient.updateWithParams(sql, new JsonArray(), res -> {
      if (res.succeeded()) {
        UpdateResult result = res.result();
        message
            .reply(new JsonObject().put("count", result.getUpdated()).put("key", result.getKeys()));
      } else {
        log.error(ErrorCodes.DB_ERROR.name(),message.toString(), res.cause().getMessage());
        message.fail(ErrorCodes.DB_ERROR.code(), res.cause().getMessage());
      }
    });
  }
}
