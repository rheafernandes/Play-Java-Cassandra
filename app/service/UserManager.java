package service;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.uuid.Generators;
import models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManager {
    Cluster cluster;
    public void  clusterBuilder(){
        cluster = Cluster.builder()
                .addContactPoint("127.0.0.1")
                .build();
    }
    public User addUser(JsonNode user) {
        User userTest = null;
        UUID uuid = Generators.randomBasedGenerator().generate();
        if(user.has("userName")){
            userTest = new User( user.get("userName").toString(),uuid.toString());
            String query ="INSERT INTO user_data.user (userid,username) VALUES ('"+userTest.getUserId()+"','"+userTest.getUserName()+"')";
            try {
                clusterBuilder();
                Session session = cluster.connect();
                session.execute(query);
            }catch (Exception e){
                e.printStackTrace();
                return new User("Not created","xxx");
            }
        }
        return userTest;
    }

    public List<User> allUsers() {
        String query ="SELECT * from user_data.user";
        List<User> users = new ArrayList<>();
        try {
            clusterBuilder();
            Session session = cluster.connect();
            ResultSet resultSet = session.execute(query);
            if (! resultSet.isExhausted()) {
                List<Row> rows = resultSet.all();
                for (Row row :rows) {
                    System.out.println(row);
                    users.add(new User(row.getString("userid"),row.getString("username").replace("\"","")));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<User>();
        }finally {
            if (cluster != null) {
                cluster.close();
            }
            return users;
        }
    }

    public User getUser(String id){
        User user= null;
        try {
            clusterBuilder();
            Session session = cluster.connect();
            ResultSet resultSet = session.execute("Select * from user_data.user where userid='"+id.replace("\"","")+"'");
            if (! resultSet.isExhausted()) {
                Row row = resultSet.one();
                user = new User(row.getString("userid"),row.getString("username").replace("\"",""));
            }
        }catch (Exception e){
            e.printStackTrace();
            return new User("User not found", "xxx");
        }finally {
            if (cluster != null) {
                cluster.close();
            }
            return user;
        }
    }

    public String deleteUser(String id){
        Boolean response =false;
        try {
            clusterBuilder();
            Session session = cluster.connect();
            ResultSet resultSet = session.execute("delete from user_data.user where userid='"+id.replace("\"","")+"' if exists");
            Row row = resultSet.one();
            response= row.getBool("[applied]");
        }catch (Exception e){
            e.printStackTrace();
            return "Internal Server Error";
        }finally {
            if (cluster != null) {
                cluster.close();
            }
            if(response){
                return "User Deleted";
            }else{
                return "User Not Found";
            }
        }
    }

    public User updateUser(JsonNode incomingNode){
        Boolean response =false;
        String query ="";
        if(incomingNode.has("userId")){
            incomingNode.get("userId").toString();
             query="UPDATE user_data.user SET username = '"+incomingNode.get("userName").toString().replace("\"","")+"'  WHERE userid = '"+incomingNode.get("userId").toString().replace("\"","")+"' IF EXISTS";
            try {
                clusterBuilder();
                Session session = cluster.connect();
                ResultSet resultSet = session.execute(query);
                System.out.println(resultSet);
                Row row = resultSet.one();
                response= row.getBool("[applied]");
            }catch (Exception e){
                e.printStackTrace();
                return new User("Couldn't Internal Server Error","xxx");
            }finally {
                if (cluster != null) {
                    cluster.close();
                }
                if(response){
                    return new User("User Updated","true");
                }else{
                    return new User("User Not Updated","xxx");
                }
            }
        } else {
            return new User("Please provide userId","xxx");
        }
    }

}

