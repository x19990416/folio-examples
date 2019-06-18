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
package demo.test.vertx.spring.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import demo.test.vertx.spring.jpa.verticles.HttpServerVerticle;
import demo.test.vertx.spring.jpa.verticles.SpringVerticle;
import io.vertx.core.Vertx;
@EnableJpaRepositories(basePackages = {"demo.test.vertx.spring.jpa.repository"})
@ComponentScan("demo.test.vertx.spring.jpa")
@EntityScan("demo.test.vertx.spring.jpa.entity") 
@SpringBootApplication
public class Application {

  public static void main(String... args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);    
    final Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new HttpServerVerticle(context));
    vertx.deployVerticle(new SpringVerticle(context));
    System.out.println("Deployment done");
  }

}
