package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import service.UserManager;
import util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserController extends Controller {
    UserManager userManager= new UserManager();
    ObjectMapper mapper = new ObjectMapper();

    public F.Promise<Result> addUser(){
        JsonNode jsonNode = request().body().asJson();
        if (jsonNode == null){
            return F.Promise.<Result>pure(Results.badRequest(Util.createResponse(
                    "Expecting Json data", false)));
        } else{
            User user = userManager.addUser(jsonNode);
            try{
                jsonNode = mapper.readTree("{\"" + user.getUserId() + "\": \"" + user.getUserName().replace("\"","") + "\"}");
                if(user.getUserName()=="Not Created"){
                    return F.Promise.<Result>pure(Results.notFound(Util.createResponse("Couldn't Be Created",false)));
                }
            } catch (IOException e){
                e.printStackTrace();
                return F.Promise.<Result>pure(Results.internalServerError(Util.createResponse("Internal Server Error", false)));
            }
            return F.Promise.<Result>pure(Results.created(jsonNode.toString()));
        }
    }

    public  F.Promise<Result> allUsers(){
        List<User> users = userManager.allUsers();
        List<JsonNode> jsonNodes = new ArrayList<>();
        try {
            for (User user : users) {
                String newString = "{\"" + user.getUserId() + "\": \"" + user.getUserName() + "\"}";
                JsonNode newNode = null;
                newNode = mapper.readTree(newString);
                jsonNodes.add(newNode);
            }
            } catch(IOException e){
                e.printStackTrace();
                return F.Promise.<Result>pure(Results.internalServerError(Util.createResponse("Internal Server Error", false)));
            }
        return F.Promise.<Result>pure(Results.created(jsonNodes.toString()));
    }

    public F.Promise<Result> getUser(String id){
        User user = userManager.getUser(id);
        JsonNode jsonNode =null;
        try{
            jsonNode = mapper.readTree("{\"" + user.getUserId() + "\": \"" + user.getUserName() + "\"}");
        } catch (IOException e){
            e.printStackTrace();
            return F.Promise.<Result>pure(Results.internalServerError(Util.createResponse("Internal Server Error", false)));
        }
        return F.Promise.<Result>pure(Results.created(jsonNode.toString()));

    }

    public F.Promise<Result> deleteUser(String id){
        String response = userManager.deleteUser(id);
        if( response =="User Not Found"){
            return F.Promise.<Result>pure(Results.internalServerError(Util.createResponse(response, false)));
        }else {
            return F.Promise.<Result>pure(Results.ok(Util.createResponse(response, true)));
        }
    }

    public F.Promise<Result> updateUser(){
        JsonNode incomingNode = request().body().asJson();
        User user = null;
        if (incomingNode == null){
            return F.Promise.<Result>pure(Results.badRequest(Util.createResponse(
                    "Expecting Json data", false)));
        } else {
                 user= userManager.updateUser(incomingNode);
                 if(user.getUserId()=="xxx"){
                     return F.Promise.<Result>pure(Results.notFound(Util.createResponse(user.getUserName(), false)));
                 }else{
                     return F.Promise.<Result>pure(Results.ok(Util.createResponse(user.getUserName(), true)));
                 }
        }
    }
}
