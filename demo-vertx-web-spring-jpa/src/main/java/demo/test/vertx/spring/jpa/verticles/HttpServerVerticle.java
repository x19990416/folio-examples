package demo.test.vertx.spring.jpa.verticles;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import com.google.gson.Gson;
import demo.test.vertx.spring.jpa.entity.Test;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Administrator on 2018/3/9.
 */

@Slf4j
public class HttpServerVerticle extends AbstractVerticle {

  private Integer port = 8080;
  
  public HttpServerVerticle (final ApplicationContext context) {
    this.port=Integer.valueOf(context.getEnvironment().getProperty("http.server.port"));
  }
  

  public void start(Future<Void> startFuture) throws Exception {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/").handler(this::indexHandler);
    router.delete().handler(BodyHandler.create());
    router.delete("/").handler(this::deleteHandler);
    router.put().handler(BodyHandler.create());
    router.put("/").handler(this::saveHandler);
    server.requestHandler(router::handle) // 绑定路由
        .listen(port, ar -> { // 监听端口
          if (ar.succeeded()) {
            log.info("HTTP服务器启动，端口号： " + port);            
            startFuture.complete();
          } else {
            log.error("无法启动HTTP服务器", ar.cause());
            startFuture.fail(ar.cause());
          }
        });
  }

  private void indexHandler(RoutingContext context) {
    context.response().putHeader("content-type", "text/html").end("hello world - jpa");
  }

  private void deleteHandler(RoutingContext context) {
    String id = context.getBodyAsJson().getString("id");
    DeliveryOptions options = new DeliveryOptions();
    options.addHeader("action", "delete");
    JsonObject request = new JsonObject();
    request.put("param", id);
    vertx.eventBus().send("demo.spring", request, options, reply -> {
      if (reply.succeeded()) {
          JsonObject body = (JsonObject) reply.result().body();
          Map<String, Object> resultMap = new HashMap<>();
          if (body.containsKey("count") && body.getInteger("count") > 0) {
              resultMap.put("result", "success");
          } else {
              resultMap.put("result", "failed");
          }
          HttpServerResponse response = context.response();
          response.setStatusCode(200);
          JsonObject jsonObject = new JsonObject(resultMap);
          response.putHeader("content-type", "application/json; charset=utf-8").end(jsonObject.toString());
      } else {
          context.fail(reply.cause());
      }
    });
  }

  private void saveHandler(RoutingContext context) {
    String name = context.getBodyAsJson().getString("name");
    
    DeliveryOptions options = new DeliveryOptions();
    options.addHeader("action", "saveOrUpdate");
    
    JsonObject request = new JsonObject();
    request.put("name",name);    
    vertx.eventBus().send("demo.spring", request, options, reply -> {
      if (reply.succeeded()) {
          JsonObject body = (JsonObject) reply.result().body();
          Map<String, Object> resultMap = new HashMap<>();
          if (body.containsKey("result") ) {
              resultMap.put("code", 1);
              resultMap.put("result", "success>>"+body.getLong("result"));
          } else {
              resultMap.put("result", "failed");
          }
          HttpServerResponse response = context.response();
          response.setStatusCode(200);
          JsonObject jsonObject = new JsonObject(resultMap);
          response.putHeader("content-type", "application/json; charset=utf-8").end(jsonObject.toString());
      } else {
          context.fail(reply.cause());
      }
  });
  }
}
