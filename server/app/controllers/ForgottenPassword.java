package controllers;

import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
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
import services.MongoInterface;
import services.EncryptionException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import services.MongoDB;

@Singleton
public class ForgottenPassword extends Controller {
	@Inject MailerClient mailerClient;
	
	 private final MongoDatabase database ;

	@Inject
	public ForgottenPassword  (MongoInterface db){
		this.database = db.get_database();	
	}

	// This function is called when the server is ask to give a new password.
	// It sends an email to the address given in argument, if and only if
	// this email and the username corresponds to an existing user in the database.
	public Result forgotten_password(String a_user, String a_email) throws EncryptionException, UnsupportedEncodingException, IOException{
		MongoCollection<Document> users = database.getCollection("users");
		String key = UUID.randomUUID().toString();
	
		ArrayList<Byte> a_user_E = MongoDB.aes.encrypt(a_user);
		ArrayList<Byte> a_email_E = MongoDB.aes.encrypt(a_email);
		UpdateResult updateresult = users.updateOne(eq("user", a_user_E),set("key",key));
		
		if(updateresult.getModifiedCount() == 1) {
			response().setCookie(Cookie.builder("user",key).build());
			if (users.find(and(eq("user", a_user_E), eq("email", a_email_E))).first() != null) {
				ArrayList<Byte> arr = (ArrayList<Byte>)users.find(eq("user", a_user_E)).first().get("password");
				String mdp = MongoDB.aes.decrypt(arr);

				Email email = new Email()
					.setSubject("Demande de récupération du mot de passe")
					.setFrom("Covoituliège <Proj00102018Covoituliege@gmail.com>")
					.addTo(a_email)
					.setBodyText("Votre mot de passe est " + mdp + ".");
				mailerClient.send(email);
			}

			return ok("username OK");
		}
		
		if (users.find(eq("user",a_user_E)).first() == null){
			return ok("user doesn't exist");		
		}

		return ok("username OK");

	}
}










