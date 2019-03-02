package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import java.io.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.List;

import services.MongoInterface;
import services.Journey;
 
public class SignUp extends Controller {
	
	 private final MongoDatabase database ;

	@Inject
	public SignUp  (MongoInterface db){
		this.database = db.get_database();	
	}
	
	public Result sign_up(String a_user, String a_password,String email){
		MongoCollection<Document> users = database.getCollection("users");
		Document registred_user = users.find(eq("user", a_user)).first();
		//sign in the new user.
		String key = UUID.randomUUID().toString();
		if (registred_user == null){
			// write new user in database
			Document new_user = new Document("user", a_user).append("password",a_password).append("email", email).append("key",key).append("journeys",new ArrayList<Document>());
			users.insertOne(new_user);
		response().setCookie(Cookie.builder("user", key).build());
		return ok("user successfully recorded");
 
		}
		return ok("pseudo already used" );	
	}	
}
