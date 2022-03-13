# PT AR Game 5 Web Server
## High level solution
* Based on Spring Boot
* Stores data using JsonDB
* Embeds public HTML web page
  * Display public and TV leaderboard
  * Handle player registration
* Exposes API for public and private use cases
  * Public leaderboard data
  * Player registration
  * Provide user data for cabin player selection
  * Provide trivia questions to cabin
  * Store and manage game results

  
  
## Notes
command line param -DshowCaseCategory=Playtech
com.playtech.ptargame5.web.game.GameSessionManager can change the default:
static final String showCaseCategory = System.getProperty("showCaseCategory");
