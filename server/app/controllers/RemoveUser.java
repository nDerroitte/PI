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
import java.util.List;

import services.MongoInterface;
import services.EncryptionException;
import services.MongoDB;

public class RemoveUser extends Controller {
	
	 private final MongoDatabase database ;

	@Inject
	public RemoveUser  (MongoInterface db){
		this.database = db.get_database();	
	}
	
	public Result remove_user(String a_user,String a_password) throws EncryptionException, UnsupportedEncodingException{
		ArrayList<Byte> a_user_E = MongoDB.aes.encrypt(a_user);
		ArrayList<Byte> a_password_E = MongoDB.aes.encrypt(a_password);
		MongoCollection<Document> users = database.getCollection("users");
		DeleteResult delete_result = users.deleteMany(and(eq("user",a_user_E),eq("password",a_password_E)));
		if (delete_result.getDeletedCount() == 1){
			return ok("user succesfully removed");				
		}
		if(users.find(eq("user",a_user_E)).first() != null){
			return ok("incorrect password");
		}
		return ok("user doesn't exist");
	}
}
