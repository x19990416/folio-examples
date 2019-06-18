/*
 * Copyright (C) 2019 The demo-vertx-web-jdbc Authors
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
package demo.test.vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher extends io.vertx.core.Launcher {

    public static void main(String[] args) {
        System.out.println("start on main");
        (new Launcher()).dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
//      String configFilePath = getClass().getClassLoader().getResource("conf.json").getPath();
//      System.out.println("load conf file before starting vertx path is:"+ configFilePath);        
//      ConfigStoreOptions confStore = new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", configFilePath));
//      ConfigRetriever.create(Vertx.vertx(),new ConfigRetrieverOptions().addStore(confStore));
      

    }

}
