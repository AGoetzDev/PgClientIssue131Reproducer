


import java.time.LocalDateTime;
import java.util.List;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgConnection;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.pgclient.PgRowSet;
import io.reactiverse.pgclient.Row;
import io.reactiverse.pgclient.Tuple;
import io.reactiverse.pgclient.data.Json;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class PostgreSQLClientWrapper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSQLClientWrapper.class);
	private DataBaseConfig dbConfig;
	private Vertx vertx;
	private PgPool client;

	public PostgreSQLClientWrapper(Vertx vertx, DataBaseConfig dbConfig) {
		this.vertx = vertx;
		this.dbConfig = dbConfig;
		this.setupClient();
	}
	
	private void setupClient(){
		if(this.client != null){
			this.client.close();
		}
		PgPoolOptions options = new PgPoolOptions()
				.setPort(dbConfig.getPort())
				.setHost(dbConfig.getHost())
				.setDatabase(dbConfig.getDatabase())
				.setUser(dbConfig.getUsername())
				.setPassword(dbConfig.getPassword())
				.setMaxSize(dbConfig.getMaxSize())
				.setTrustAll(true)
				.setCachePreparedStatements(false);

		this.client = PgClient.pool(vertx, options);
	}
	
	public Future<JsonArray> queryWithConnection(Tuple tuple, String sql, int i){
		Future<JsonArray> future = Future.future();
		LOGGER.info("QUERY WITH CONNECTION: "+i);
		getConnection().compose(connection -> {
			LOGGER.info("QueryWithConnection "+i+": Got Connection");
			connection.closeHandler(c->{
				LOGGER.info("QueryWithConnection "+i+": Closing");
			});
			connection.exceptionHandler(h->{
		    	  LOGGER.error("QueryWithConnection "+i+" Exception: "+h);
		    });
			connection.preparedQuery(sql, tuple, r -> {
					if (r.succeeded()) {
						future.complete(convertRowSet(r.result()));
						
					} else {
						future.fail(r.cause());
					}
					connection.close();
				});
		}, future);
		return future;
	}
	
	public Future<JsonArray> queryWithoutClosing(Tuple tuple, String sql, int i){
		Future<JsonArray> future = Future.future();
		LOGGER.info("QUERY WITHOUT CLOSING: "+i);
		getConnection().compose(connection -> {
			LOGGER.info("QueryWithoutClosing "+i+": Got Connection");
			connection.closeHandler(c->{
				LOGGER.info("QueryWithoutClosing "+i+": Closing");
			});
			connection.exceptionHandler(h->{
		    	  LOGGER.error("QueryWithoutClosing "+i+" Exception: "+h);
		    });
			connection.preparedQuery(sql, tuple, r -> {
					if (r.succeeded()) {
						future.complete(convertRowSet(r.result()));
						
					} else {
						future.fail(r.cause());
					}
					//connection.close();
				});
		}, future);
		return future;
	}

	private JsonArray convertRowSet(PgRowSet rows){
		JsonArray list = new JsonArray();
		List<String> names = rows.columnsNames();
		for(Row row: rows){
			JsonObject json = new JsonObject();
			for(String name: names){
				Object value = row.getValue(name);
				if(value != null){
					if(value instanceof Json){
						json.put(name,value.toString());
					} else if(value instanceof LocalDateTime){
						json.put(name,value.toString());
					} else {
						json.put(name, value);
					}
				}
				
			}
			list.add(json);
		}
		return list;
	}
	
	private Future<PgConnection> getConnection() {
		Future<PgConnection> future = Future.future();
		client.getConnection(h->{
			if(h.succeeded()){
				future.complete(h.result());
			} else {
				//this.setupClient();
				future.fail(h.cause());
			}
		});
		
		return future;
	}

}
