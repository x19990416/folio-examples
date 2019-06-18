package demo.test.vertx.verticles;

import java.util.HashMap;
import java.util.Map;
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

    public void start(Future<Void> startFuture) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.get("/").handler(this::indexHandler);
        router.delete().handler(BodyHandler.create());
        router.delete("/").handler(this::deleteHandler);
        router.put().handler(BodyHandler.create());
        router.put("/").handler(this::saveHandler);
        server.requestHandler(router::handle) //绑定路由
                .listen(config().getInteger("http.server.port"), ar -> {    //监听端口
                    if (ar.succeeded()) {
                        log.info("HTTP服务器启动，端口号： " + config().getInteger("http.server.port"));
                        startFuture.complete();
                    } else {
                        log.error("无法启动HTTP服务器", ar.cause());
                        startFuture.fail(ar.cause());
                    }
                });
    }

    private void indexHandler(RoutingContext context) {
      context.response().putHeader("content-type", "text/html").end("hello world");
    }

    private void deleteHandler(RoutingContext context) {
        String id = context.getBodyAsJson().getString("id");
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", "updateOrDelete");
        JsonObject request = new JsonObject();
        request.put("sql", "delete from test where id="+id);
        vertx.eventBus().send("demo.dao", request, options, reply -> {
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
        JsonObject request = new JsonObject();
        request.put("sql", "insert into test set name='"+name+"'");
        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("action", "save");
        
        vertx.eventBus().send("demo.dao", request, options, reply -> {
            if (reply.succeeded()) {
                JsonObject body = (JsonObject) reply.result().body();
                Map<String, Object> resultMap = new HashMap<>();
                if (body.containsKey("count") && body.getInteger("count") > 0) {
                    resultMap.put("result", "success");
                    if (body.containsKey("key")) {
                        resultMap.put("key", body.getValue("key"));
                    }
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
