/*
 * Copyright 2021 Srikavin Ramkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.infuzion.chess.web;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.infuzion.chess.ai.AiService;
import me.infuzion.chess.ai.StockfishEngine;
import me.infuzion.chess.clock.ClockService;
import me.infuzion.chess.data.PubSubChannel;
import me.infuzion.chess.data.PubSubChannelPredicate;
import me.infuzion.chess.data.RedisPubSubSource;
import me.infuzion.chess.game.util.ChessUtilities;
import me.infuzion.chess.web.dao.impl.MatchDatabase;
import me.infuzion.chess.web.dao.impl.UserDatabase;
import me.infuzion.chess.web.domain.service.GameService;
import me.infuzion.chess.web.domain.service.TokenService;
import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequestUserParamMapper;
import me.infuzion.chess.web.event.helper.RequireAuthenticationPredicate;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.chess.web.listener.ChessAuthenticationHelper;
import me.infuzion.chess.web.listener.ChessUserAuthenticationListener;
import me.infuzion.chess.web.listener.ChessUserProfileListener;
import me.infuzion.chess.web.listener.game.ChessGameListener;
import me.infuzion.chess.web.listener.game.ChessMoveListener;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.EventManager;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.param.DefaultTypeConverter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Chess implements EventListener {

    public Chess(Server server, String postgresJDBCUri, String redisUri) throws IOException, URISyntaxException {
        EventManager manager = server.getEventManager();

        HikariDataSource ds = getSqlDataSource(postgresJDBCUri);
        JedisPool pool = createJedisPool(redisUri);

        UserDatabase userDatabase = new UserDatabase(ds);
        MatchDatabase matchDatabase = new MatchDatabase(ds);

        RedisPubSubSource source = new RedisPubSubSource(manager, pool, new DefaultTypeConverter(ChessUtilities.gson));

        TokenService tokenService = new TokenService(pool, userDatabase);
        GameService gameService = new GameService(matchDatabase, source);

        manager.registerAnnotation(RequiresAuthentication.class, new RequireAuthenticationPredicate(tokenService));
        manager.registerAnnotation(RequestUser.class, new RequestUserParamMapper(tokenService));
        manager.registerAnnotation(PubSubChannel.class, new PubSubChannelPredicate());

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    pool.getResource().psubscribe(source, "chess*");
                } catch (JedisConnectionException ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.setName("Redis Subscription Thread");
        thread.start();

        ClockService service = new ClockService(pool, source);
        Thread thread1 = new Thread(service::runClearExpiringGames);
        thread1.setName("ClockService - runClearExpiringGames");
        thread1.start();

        Thread thread2 = new Thread(service::runHandleExpiringGames);
        thread2.setName("ClockService - runHandleExpiringGames");
        thread2.start();

        manager.registerListener(service);
        manager.registerListener(new ChessAuthenticationHelper(tokenService));
        manager.registerListener(new ChessUserAuthenticationListener(userDatabase, tokenService));
        manager.registerListener(new ChessMoveListener(gameService, service, manager));
        manager.registerListener(new ChessUserProfileListener(userDatabase));
        manager.registerListener(new ChessGameListener(gameService));

        manager.registerListener(new AiService(gameService, userDatabase, new StockfishEngine(System.getenv("STOCKFISH_PATH"))));

        manager.registerListener(new EventListener() {
            @EventHandler()
            private void cors(PageRequestEvent event) {
                event.setResponseHeader("Access-Control-Allow-Origin", "*");
                event.setResponseHeader("Access-Control-Allow-Headers", "*");
            }
        });

        server.start();
    }

    private static HikariDataSource getSqlDataSource(String url) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);

        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    private static JedisPool createJedisPool(String uri) throws URISyntaxException {
        URI redisURI = new URI(uri);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        return new JedisPool(poolConfig, redisURI);
    }
}
