package demo.test.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

  public void start(Future<Void> startFuture) throws Exception {
    config().getMap().forEach((k,v)->{
      System.out.println(k+"\t"+v);
    });
    
    
    Integer port = config().getInteger("http.server.port");
    System.out.println("listen on port:" + port);
    
    DeploymentOptions options = new DeploymentOptions()
        .setConfig(
            new JsonObject().put("http.server.port", port))
        .setInstances(2);
    vertx.deployVerticle("demo.test.vertx.verticles.HttpServerVerticle", options, Future.future());

    JsonObject jdbcClientConfig = new JsonObject().put("url", config().getString("jdbc.url"))
        .put("driver_class", config().getString("jdbc.driverClassName"))
        .put("user", config().getString("jdbc.username"))
        .put("password", config().getString("jdbc.password"));

    jdbcClientConfig.getMap().forEach((k,v)->{
      System.out.println(k+"\t"+v);
    });
    DeploymentOptions jdbcClientOptions = new DeploymentOptions().setConfig(jdbcClientConfig);
    vertx.deployVerticle("demo.test.vertx.verticles.DemoDaoVerticle", jdbcClientOptions, Future.future());
  }
}
