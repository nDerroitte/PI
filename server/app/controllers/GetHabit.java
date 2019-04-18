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
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Future;

import services.HabitGenerator;
import services.MongoInterface;

 

@Singleton
public class GetHabit extends Controller {
	
	private final MongoDatabase database;
    private final HabitGenerator hb;

	@Inject
	public GetHabit (MongoInterface db, HabitGenerator habit_generator){
        this.database = db.get_database();
		this.hb = habit_generator;
	}

	public Result get_habit(String a_user,String method) {
		MongoCollection<Document> users = database.getCollection("users");
		if(a_user.equals("all")){
			System.out.println("Computing habit of all user");
			MongoCursor<Document> cursor = users.find().iterator();
			try {
				while (cursor.hasNext()) {
					Document user = cursor.next();						
					hb.submitTask((String) user.get("user"),1);
					System.out.println("User: " + user.get("user") + " is submit");
				}
			} 
			catch(Exception e){
				e.printStackTrace();
			}
			finally {
				cursor.close();
			}
			return ok("computing...");
		}
		else{
			Document user = users.find(and(eq("user", a_user))).first();
			if(user != null) {
				hb.submitTask(a_user,Integer.parseInt(method));
				return ok("computing...");
			}
			return ok("user does not exist");
		}		

	}


}









