
import io.reactiverse.pgclient.Tuple;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;



public class ReproducerVerticle extends AbstractVerticle {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReproducerVerticle.class);
	
	private static final String INSERT = "INSERT INTO reproducer(integerfield) VALUES ($1) RETURNING id";
	private PostgreSQLClientWrapper client;
	
	/*
	 * Tries to insert entries into the database with wrong type to trigger class cast exception
	 * First two inserts are done by obtaining a connection but not closing it. In this case the exception handler
	 * of the connection object is triggered as expected each time and the ClassCastException is thrown. Unexpected in this case is that the 
	 * close handler of the connection object is called, even though connection.close() is not called.
	 * 
	 * The next three inserts are done by obtaining a connection object and calling connection.close() in the handler.
	 * The first insert in this case triggers the exception handler as expected with a ClassCastException, subsequent inserts however
	 * fail with "Connection not open CLOSED" without triggering the exception handler
	 * 
	 */
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		
		client = new PostgreSQLClientWrapper(vertx, DataBaseConfig.fromJson(config().getJsonObject("dbConfig")));
		this.testInsertWithoutClosing().compose(v->{
			this.testInsertWithConnection().setHandler(h->{
				startFuture.complete();
			});
		}, startFuture);

	}
	
	//Deliberately create tuple of wrong type to trigger ClassCastException
	private Future<Void> testInsertWithConnection(){
		Future<Void> result = Future.future();
		client.queryWithConnection(Tuple.of("wrong"), INSERT, 1).setHandler(h->{
			if(h.failed()){
				LOGGER.error("First insert with connection failed: "+h.cause());
			}
			client.queryWithConnection(Tuple.of("wrong"), INSERT, 2).setHandler(h2->{
				if(h2.failed()){
					LOGGER.error("Second insert with connection failed: "+h2.cause());
				}
				client.queryWithConnection(Tuple.of("wrong"), INSERT, 3).setHandler(h3->{
					if(h3.failed()){
						LOGGER.error("Third insert with connection failed: "+h2.cause());
					}
					result.complete();
				});
			});
		});
		return result;
	}
	
	private Future<Void> testInsertWithoutClosing(){
		Future<Void> result = Future.future();
		client.queryWithoutClosing(Tuple.of("wrong"), INSERT, 1).setHandler(h->{
			if(h.failed()){
				LOGGER.error("First insert without closing failed: "+h.cause());
			}
			client.queryWithoutClosing(Tuple.of("wrong"), INSERT, 2).setHandler(h2->{
				if(h2.failed()){
					LOGGER.error("Second insert without closing failed: "+h2.cause());
				}
				result.complete();
			});
		});
		return result;
	}

}
