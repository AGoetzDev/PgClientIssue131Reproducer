

import io.vertx.core.json.JsonObject;



public class DataBaseConfig {

	private String host;
	private Integer port;
	private String database;
	private String username;
	private String password;
	private int maxSize;
	

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.put("host", this.host);
		json.put("port", this.port);
		json.put("database", this.database);
		json.put("username", this.username);
		json.put("password", this.password);
		json.put("maxSize", this.maxSize);
		return json;
	}
	
	public static DataBaseConfig fromJson(JsonObject json){
		DataBaseConfig config = new DataBaseConfig();

		config.host = json.getString("host", "localhost");
		config.port = json.getInteger("port", 5432);
		config.database = json.getString("database", "reproducerdb");
		config.username = json.getString("username", "postgres");
		config.password = json.getString("password", "root");
		config.maxSize = json.getInteger("maxSize",5);
		return config;
	}

}
