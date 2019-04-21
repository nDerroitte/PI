package services;
import java.util.ArrayList;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import java.text.ParseException;


/**
 * User class for the Simple Model algorithm
 */
public class UserSimpleModel
{
    /**
     * The list of unsed journeys where we want to generate habit from
     */
    private ArrayList<Journey> unused_journeys;
    /**
     * User id
     */
    private String user_id;
    /**
     * List of habits of the user
     */
    private ArrayList<Habit> habits;

    /**
     * Constructor of the USM
     * @param user_id string of the user id
     * @param database MongoDB database
     * @throws ParseException if the user doesnt exist u-in the database
     */
    public UserSimpleModel(String user_id, MongoCollection<Document> database) throws ParseException
    {
        this.user_id = user_id;
        this.habits = new ArrayList<>();
        this.unused_journeys = new ArrayList<>();
        
        // Get user journey from database
        Document user = database.find(eq("user", user_id)).first();
        if (user == null ) {
            System.err.println("User: " + user_id + " not in DB");
        }
        else{
            ArrayList<Document> journeys = (ArrayList<Document>)(user.get("journeys"));
            for(Document journey : journeys){
                unused_journeys.add(Journey.fromDoc(journey));
            }
        }
    }

    /**
     * Create habits for the user
     */
    public void createHabits()
    {
        this.habits = CreationHabitSM.createHabitSM(unused_journeys, user_id);
    }
}
