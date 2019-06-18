/*
 * Copyright (C) 2019 The demo-vertx-web-spring-jpa Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package demo.test.vertx.spring.jpa.verticles;

import org.springframework.context.ApplicationContext;
import demo.test.vertx.spring.jpa.ErrorCodes;
import demo.test.vertx.spring.jpa.entity.Test;
import demo.test.vertx.spring.jpa.service.TestService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringVerticle extends AbstractVerticle {

  public static final String NAME = "spring_verticle";

  private final TestService service;

  public SpringVerticle(final ApplicationContext context) {
    service = (TestService) context.getBean("testService");
    
  }

  private void onMessage(Message<JsonObject> message) {
    if (!message.headers().contains("action")) {
      message.fail(ErrorCodes.NO_ACTION_SPECIFIED.code(), ErrorCodes.NO_ACTION_SPECIFIED.msg());
      return;
    }
    String action = message.headers().get("action");
    switch (action) {
      case "delete":
        delete(message);
        break;
      case "saveOrUpdate":
        save(message);
        break;
      default:
        message.fail(ErrorCodes.BAD_ACTION.code(), ErrorCodes.BAD_ACTION.msg() + "-" + action);
    }
  }


  private void delete(Message<JsonObject> message) {
    Test test = new Test();
    test.setId(message.body().getLong("id"));
    service.delete(test);
    message.reply(new JsonObject().put("result", "delete success " + test));

  }

  private void save(Message<JsonObject> message) {
    
    Test test = new Test();
    test.setName(message.body().getString("name"));
    test = service.save(test);    
    message.reply(new JsonObject().put("result", test.getId()));    
  }


  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start();
    vertx.eventBus().consumer("demo.spring", this::onMessage);
    startFuture.complete();
  }

}
