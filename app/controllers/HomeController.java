package controllers;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

public class HomeController extends Controller {

    public F.Promise<Result> index(){
        Cluster cluster=null;
        try{
            cluster = Cluster.builder()
                    .addContactPoint("127.0.0.1")
                    .build();
            Session session = cluster.connect();
            ResultSet resultSet = session.execute("select release_version from system.local");
            Row row = resultSet.one();
            String query="CREATE KEYSPACE IF NOT EXISTS user_data WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};";
            session.execute(query);
            query="Use user_data";
            session.execute(query);
            query= "Create Table IF NOT EXISTS user (userid text Primary Key, username text);";
            session.execute(query);
            return F.Promise.<Result>pure(Results.ok("Cassandra Connected"));
        }catch (Exception e){
            e.printStackTrace();
            return F.Promise.<Result>pure(Results.unauthorized("Cassandra connection failed"));
        }finally {
            if (cluster != null) cluster.close();
        }
    }
}
