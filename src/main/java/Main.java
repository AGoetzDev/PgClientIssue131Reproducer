import java.io.File;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class Main {

	public static void main(String[] args) {
		Logger LOGGER = LoggerFactory.getLogger(Main.class);
		if(args.length <1){
			LOGGER.error("Path to config file not specified!");
			System.exit(1);
		}
		final Vertx vertx = Vertx.vertx();
		if(new File(args[0]).isFile()){

			Future<Void> startFuture = Future.future();
			startFuture.setHandler(v->{
				if(v.succeeded()){
					LOGGER.info("Startup completed");
					System.exit(0);
				} else {
					LOGGER.error("Startup failed, reason: "+v.cause().getMessage());
					System.exit(1);
				}
			});
			
			Future<JsonObject> readConfigFuture = Future.future();
			vertx.fileSystem().readFile(args[0], result -> {
			    if (result.succeeded()) {
			    	try{
				        readConfigFuture.complete( new JsonObject(result.result()));
			    	} catch (Exception e){
			    		readConfigFuture.fail(e);
			    	}
			    } else {
			    	readConfigFuture.fail(result.cause());
			    }
			});
			readConfigFuture.compose(v->{
				
				
				DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("dbConfig", v));
				vertx.deployVerticle(new ReproducerVerticle(), options, deployResult->{
					if(deployResult.succeeded()){
						startFuture.complete();
					} else {
						startFuture.fail(deployResult.cause());
					}
				});
				
			}, startFuture);
			
		} else {
			LOGGER.error("Invalid DataBaseConfig Path!");
			System.exit(1);
		}

	}

}
