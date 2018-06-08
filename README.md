# Calorie Tracker Android App

# Description:
This is a repo for my calorie tracking android application. The purpose of the
 app is to allow users to track their daily calorie intake as well as track their
 bodyweightweight over time.

# Technology
### Front end:
* XML components to provide the visual components of the user interface.
* SQLite database to provide caching and offline functionality.
* Java to provide functional capabilities such as activity navigation, click events etc.
* Volley library for HTTP requests.

### Back end:
* MySQL database.
* PHP scripts to handle requests.
* Hosted on an AWS EC2 instance.

# Functionality / Usage:
* **Sign up:** Users sign up using an email and password.
* **Login:** Users login also using an email and password.
* **Food diary:** The main activity of the app displays a food diary containing
the food that the user has eaten for that day. Here we display the name, the
protein, carb and fat content of the food as well as the total calories in the
food. Also displayed is the users nutrient goals and the nutrient totals for
the day so far.
* **Goals:** The user is able to manipulate their goals using the Goals
activity. Here simply edit one of the fields and click submit in order to change
 your goals.
* **Progress:** The progress activity simply displays the user's bodyweight
entries to date. By manipulating the bodyweight field in the Goals activity
the user can add a new bodyweight entry. However, this can only be
done once per day.
* **Search Food(online):** When the user goes to the 'Add Food' activity they
can enter a search term into the searchview and search for a food. If there is
a food in the remote database who's title matches the search term then the user
is directed to a search results page  where they can select the food they want.
* **Search Food (offline):** Foods that the user has previously used are stored
for that user in the (local) database. These foods populate a listview in the
'Add Food' activity and as the user adds a search term in this activity the
foods are filtered according to that search term.
* **Voice Search:** If the user wishes to search using voice rather than text
then they can click on the voice icon and speak into the microphone. Anything
they say is then entered into the search bar.
* **Add Food(online):** The user can add food by clicking on a search result
(either from on or offline searches) and then, after specifying the number of
servings, clicking the 'Add Food' button.
* **Add Food(offline):** If the user has no network connection then they can
add food to their diary using foods from the offline search. These foods are
added to the local database and when the user comes back online the changes are
pushed to the remote database.
* **Nutrition Info:** The user can view a piechart that breaks down their
nutrient intake for the day by clicking on the Nutrition activity
* **Create New Food:** If the user finds that there are no matches for a food
they wish to use in the database then they can create a new food using the
'Create Food' activity. Here they simply fill out some fields and click submit
to add the food to the database.
* **Edit Food:** If a user makes a mistake when adding a food to their diary
then they can edit the food by clicking on the entry on the home page of the
app. Here they can change the number of servings of the food and click the
'Edit Food' button to adjust the number of servings.
* **Remove Food:** If the user wishes to remove a food then they can perform the
 same actions as outlined above(to edit a food) and simply adjust the servings to
 zero. The button text will change to 'Remove Food' and when clicked on the food
 will be removed from the diary.

## Other Details:
* This app was developed on android studio version 2.2.3
* This app has been tested on a Samsung galaxy note 2(API level 19), a Samsung
galaxy s7 and on a nexus 5 virtual device(API level 25).
* This app targets a minimum SDK level of 15.
