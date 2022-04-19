import java.util.Arrays;

//import javax.lang.model.util.ElementScanner14;
//hello
import com.supermarket.*;
//JORDYN BROSEMER SOHAM GAGGENAPALLY AND MATTHEW TOVEN *ETHICAL* SUPERMARKET AGENT
//WHATEVER WAS WRITTEN BY EACH OF US IS DECLARED IN THE BOOLEAN INFORMATION DIRECTLY BELOW
//THIS CODE CURRENTLY DOES NOT PREVENT THE AGENT FROM PERFOMING UNETHICAL ACITIONS
//FOR NOW IT JUST NOTIFIES WHEN AN ACTION BEING PERFORMED IS UNETHICAL

//LATER WE INTEND ON CHECKING BEFORE ALL MOVES AND INTERACTS IF WE ARE VIOLATING A NORM OR POTENTIALLY GOING TO VIOLATE A NORM
//IF A NORM IS GOING TO BE VIOLATED WE WILL NOT PERFORM THAT ACTION
public class Agent extends SupermarketComponentImpl {
    public Agent() {
	super();
	shouldRunExecutionLoop = true;
	log.info("In Agent constructor.");
    }
	//JORDYN BROSEMER incremental state for every first time after grabbing an ingredient
	boolean firsttime = false;
	//JORDYN BROSEMER only run this once, get to cart return and grab cart
	boolean findcart = true;
	//JORDYN BROSEMER after getting cart go to aislehub
	boolean state1 = false;
	//JORDYN BROSEMER go up until above the top aisle, could change this eventually to set which aisle you go above as the aisle of the shelf
	boolean upstate = false;
	//JORDYN BROSEMER go right until you (can collide) with the shelf you're supposed to be looking for
	boolean goright = false;
	//JORDYN BROSEMER go down until you're in the next aisle if you're on the right side, go left else go right
	boolean godown = false;
	//JORDYN BROSEMER go right until you're actually in the aisle hub rather than above the aisle
	boolean iniright = false;
	//JORDYN BROSEMER go back left until you're in the left aisle hub so you can go down again and then search the next aisle going right
	boolean goleft = false;
	//JORDYN BROSEMER while you are capable of colliding with the shelf you need to grab an item from, move to the middle of it. Once you're in the middle enter turn state
	boolean moveuntilnocollide = false;
	//JORDYN BROSEMER turn and interact with the shelf you're supposed to, check if you have the item and if you dont grab again, increase iterator and re-sort state
	boolean turnstate = false;
	//JORDYN BROSEMER the cart should only be on the right side so turn east and grab the cart if you dont have one already then go right
	boolean eaststate = false;

	boolean movenorms = false;
	boolean interactingnorms = false;
	boolean collideplayers = false;
	boolean PersonalSpaceNorm = false;
	boolean unattendedCartNorm = false;
	boolean OneCartOnlyNorm = false;
	boolean CartTheftNorm = false;
	//JORDYN BROSEMER norm state that prevents the agent from walking into a wall 
	boolean avoidwall = false;

	// These states were written by Soham
	// SOHAM GAGGENAPALLY holds whether or not the agent has food either in cart or in hand, this is used for the ShopliftingNorm
	boolean haspaid = false;
	// SOHAM GAGGENAPALLY by default is true, will turn once the person has paid
	boolean ShopliftingNorm = true;
	// SOHAM GAGGENAPALLY the following three hold whether or not the agent didn't finish/was interrupted during an interaction
	boolean startinteraction = false;
	boolean endinteraction = false;
	boolean brokeinteraction = false;	
	// SOHAM GAGGENAPALLY this will turn true if the agent cancels an interaction in the middle
	boolean InteractionCancellationNorm = false;
	// SOHAM GAGGENAPALLY this is a counter that we need to track time
	int timeatexit = 0;
	// SOHAM GAGGENAPALLY this will turn true if the agent stays in the exit for more than 30 seconds
	boolean BlockingExitNorm = false;
	// SOHAM GAGGENAPALLY this will turn true if the agent tries to exit through the entrance
	boolean EntranceOnlyNorm = false;
	// SOHAM GAGGENAPALLY gets the agent to the right aisle
	boolean justgothere = false;
	// SOHAM GAGGENAPALLY resets the agent to the right horizontal position so they don't hit the counters
	boolean doneresetting = false;
	// SOHAM GAGGENAPALLY moves the agent to the prepared foods counter
	boolean at31 = false;
	// SOHAM GAGGENAPALLY moves the agent to the fresh fish counter
	boolean at32 = false;
	// SOHAM GAGGENAPALLY agent is done getting prepared foods and has put it in the cart
	boolean done31 = false;
	// SOHAM GAGGENAPALLY agent is done getting fresh fish and has put it in the cart
	boolean done32 = false;
	// SOHAM GAGGENAPALLY agent is done shopping and near register, so we can orient ourselves and check out
	boolean nearregister = false;

	boolean hascart = false;
	//States written by Matthew Toven
	boolean endgame = false;
	boolean endgame1 = false;
	boolean endgame2 = false;
	boolean endgame3 = false;
	boolean havecart = false;
	//will be true if agent is holding food and goes to a different shelf
	boolean wrongshelf= false;
	//will be true if agent is about to run into a shelf
	boolean shelfcollide = false;
	//if agent is about to run into cart
	boolean cartcollide= false;
	boolean checkcartcollide= false;
	// if agent is about to run into cart return
	boolean cartreturncollide = false;
	//if agent is about to run into food counter
	boolean countercollide= false;
	// will be true if agent is about to collide into wall
	boolean wallcollide = false;
	boolean registercollide= false;
	public double abs(double x){
		if(x < 0){
			return -x;
		}
		else{
			return x;
		}
	}
	public boolean moveNorms(int direction, Observation obs,double prevposx,double prevposy){
		//PUT NORMS THAT INVOLVE MOVEMENT HERE
		//WALL NORMS MATTHEW TOVEN
		
		if (obs.players[0].position[0] >= 18.5){
			goWest();
		}
		if (obs.players[0].position[1] <= 2.18){
			goSouth();
		}
		if (obs.players[0].position[1] >= 23.6){
			goNorth();
		}
		
		//PLAYER COLLISION NORM
		collideplayers = true;
		if(collideplayers){
			//if only one player
			if(!(obs.players.length>=1)){
				double xpos = obs.players[0].position[0];
				double ypos = obs.players[0].position[1];
				for(int i=1;i<obs.players.length;i++){
						if(obs.defaultCollision(obs.players[i],xpos,ypos)){
							//WILL COLLIDE WITH ANOTHER AGENT
							collideplayers = false;
							return false;
						}
						else if(i==obs.players.length){
							collideplayers = false;
							PersonalSpaceNorm = true;
						}
					}
				}
			else{
				//ONLY ONE AGENT
				System.out.println("theres only one agent");
				collideplayers = false;
				unattendedCartNorm = true;
				//YOU CAN SKIP THE PERSONAL SPACE NORM IF THERE IS ONLY ONE AGENT
				//PersonalSpaceNorm = true;
				}
			}
		if(PersonalSpaceNorm){
			double xpos = obs.players[0].position[0];
			double ypos = obs.players[0].position[1];
			for(int i=1;i<obs.players.length;i++){
				if(abs(xpos-obs.players[i].position[0]) <= 1 ){
					if(abs(ypos-obs.players[i].position[1]) <= 1){
						//PLAYER IS CLOSE TO ANOTHER PLAYER
						System.out.println("Player is too close to another player");
						return false;
					}
				}
				else if(i==obs.players.length-1){
					PersonalSpaceNorm = false;
					unattendedCartNorm = true;
				}
			}
		}
		if(unattendedCartNorm){
			if(obs.players[0].curr_cart != -1){
				unattendedCartNorm = false;
				hascart = false;
			} //IF THE PLAYER CURRENTLY DOESNT HAVE A CART THIS IS ASSUMING THAT THE CART IS OWNED BY PLAYER 0
			else{
				hascart = true;
				double xpos = obs.players[0].position[0];
				double ypos = obs.players[0].position[1];
				if(obs.carts.length > 1){
					for(int i = 0;i<obs.carts.length;i++){
						if(obs.carts[i].owner == 0){
							//OUR PLAYERS CART IS FOUND
							if(abs(prevposx-xpos) >= 2){
								if(abs(prevposy-ypos) >= 2){
									System.out.println("too far away from your cart");
									return false;
								}
							}
						}
						else if(i==obs.players.length-1){
							unattendedCartNorm = false;
						}
					}
				}
				else{
					System.out.println("No carts yet");
				}
			}
		}
		//OBJECT COLLISION NORMS WRITTEN BY MATTHEW TOVEN
		//iterate through shelf numbers
		for (int b = 0;b < obs.shelves.length;b++){
			//if agent is about to collide with a shelf, shelfcollide becomes true
			if (obs.defaultCollision(obs.shelves[b],obs.players[0].position[0],obs.players[0].position[1])){
				shelfcollide= true;
			}
			else{
				shelfcollide= false;
			}
			if (shelfcollide){
				System.out.println("ABOUT TO COLLIDE WITH A SHELF");
			}
		}
		//loop through carts
		for (int c = 0;c < obs.carts.length;c++){
			//if agent is about to collide with a cart, cartcollide becomes true
			if(hascart){
				checkcartcollide = false;
			}
			if(!hascart){
				checkcartcollide = true;
			}
			if (checkcartcollide){
				if (obs.defaultCollision(obs.carts[c],obs.players[0].position[0],obs.players[0].position[1])){
					cartcollide = true;
				}
				if (cartcollide){
					System.out.println("ABOUT TO COLLIDE WITH A CART");
				}
			}

		}
		for (int d = 0;d < obs.counters.length;d++){
			if (obs.defaultCollision(obs.counters[d],obs.players[0].position[0],obs.players[0].position[1])){
				countercollide = true;
		}
		if (countercollide){
			System.out.println("ABOUT TO COLLIDE WITH A COUNTER");
		}
		}
		for (int e = 0;e < obs.registers.length;e++){
			if (obs.defaultCollision(obs.registers[e],obs.players[0].position[0],obs.players[0].position[1])){
				registercollide = true;
		}
		if (registercollide){
			System.out.println("ABOUT TO COLLIDE WITH A REGISTER");
		}
	}
		return true;
	}
	public boolean interactingnorms(Observation obs){
		//PUT NORMS THAT INVOLVE INTERACTION HERE
        //if the food on the shelf doesnt match the food the agent is holding, wrongshelf becomes true   

		if(obs.shelves[shelfnumber].food != obs.players[0].holding_food){
			wrongshelf = true;
			System.out.println("This food does not belong on this shelf");
		}
		OneCartOnlyNorm = true;
		if(OneCartOnlyNorm){
			if(obs.atCartReturn(0)){
				if(obs.carts.length < 1){
					for(int i = 0;i<obs.carts.length;i++){
						if(obs.carts[i].owner == 0){
							System.out.println("User already has a cart");
							OneCartOnlyNorm = false;
							return false;
						}
						else if(i==obs.players.length-1){
							OneCartOnlyNorm = false;
						}
					}
				}
				else{
					System.out.println("No carts yet");
					OneCartOnlyNorm = false;
					CartTheftNorm = true;
				}
			}
		}
		if(CartTheftNorm){
			if(obs.carts.length < 1){
				for(int i = 0;i<obs.carts.length;i++){
					//test if user can interact with a cart
					if(obs.carts[i].canInteract(obs.players[0])){
						//test if the cart that the user can interact with is theirs
						if(!(obs.carts[i].owner == 0)){
							System.out.println("User is trying to take a cart that isnt theirs");
							CartTheftNorm = false;
							return false;
						}
					}
					else if(i==obs.players.length-1){
						CartTheftNorm = false;
					}
				}
			}
			else{
				System.out.println("No carts yet");
				CartTheftNorm = false;
			}
		}
		return true;
	}


	boolean findingfish = false;
	//iterator for the index of the shelvestovisit list
	int iterator = 0;
	//set the shelfnumber to -1 originally and if the shopping list returns no information of shelves
	int shelfnumber = -1;
	//set aisle number to the top aisle
	int aislenumber = 1;
	//xold is a big number, this is the number used for comparing distance when you're moving to middle of shelf
	double xold = 1000;


    @Override
    protected void executionLoop(){
	// this is called every 100ms
	// put your code in here
	Observation obs = getLastObservation();
	//print out shopping list every time through the loop 
	System.out.println("Shoppping list: " + Arrays.toString(obs.players[0].shopping_list));
	//set original foody to the first item in your shopping list
	String foody = obs.players[0].shopping_list[0];
	//north of cart return
	boolean NCR = obs.northOfCartReturn(0);
	//south of cart return
	boolean SCR = obs.southOfCartReturn(0);
	//at cart return
	boolean ACR = obs.atCartReturn(0);
	//aisle hub
	boolean AH = obs.inAisleHub(0);
	//rear aisle hub
	boolean RAH = obs.inRearAisleHub(0);
	//besides counters
	boolean BC;
	//beside counters written, method does not work
	if(obs.players[0].position[0] >= 17.5){
		BC = true;
	}
	else{
		BC = false;
	}
	//check if the agent has a cart
	if(obs.players[0].curr_cart == -1){
		hascart = false;
	}
	else{
		System.out.println("Cart Quantity" + Arrays.toString(obs.carts[0].contents_quant));
		hascart = true;
	}
	//store agents position in x & y
	double x = obs.players[0].position[0];
	double y = obs.players[0].position[1];
	//print out which item # in the list we're looking for and the shelfnumber we're looking for 
	System.out.println("iterator " + iterator + " shelfnumber " + shelfnumber);
	if(interactingnorms){
		//PUT NORMS THAT TEST INTERACTION HERE
	}
	//only run this once, get to cart return and grab cart
	if(findcart){
		//go south and interact until you reach the cart return
		System.out.println("finding cart!");
		if(moveNorms(3,obs,x,y)){
			goSouth();
			interactWithObject();
			if(hascart){
				//if you have a cart
				firsttime = true;
				findcart = false;
			}
		}
	}
	//incremental state for every first time after grabbing a grocery on your list
	if(firsttime){
		//initialize an array of shelves you need to visit of the length of your shopping list
		int[] shelvestovisit = new int[obs.players[0].shopping_list.length];
		System.out.println("initial state");
		//grab all of the shelf numbers and place them into your shelves to visit array
		for(int j = 0; j <  shelvestovisit.length;j++){
			for (int i = 0;i < obs.shelves.length;i++){
					if(obs.shelves[i].food.equals(foody)){
						shelvestovisit[j] = i;
				}
				//if the foody you're looking for is prepared foods set the shelf you need to 31
				if(foody.equals("prepared foods")){
					shelvestovisit[j] = 31;
				}
				//if the foody you're looking for is fresh fish set the shelf you need to 32
				else if(foody.equals("fresh fish")){
					shelvestovisit[j] = 32;
				}
				//set the food you're looking for as the next item in the shopping list
				foody = obs.players[0].shopping_list[j];
			}
		}
		//sort the shelves to visit 1-32
		Arrays.sort(shelvestovisit);
		//print the sorted shelves to visit list
		System.out.println("Shelves to visit" + Arrays.toString(shelvestovisit));
		//if the iterator hasnt reached the end of the shelves list 
		if(iterator < shelvestovisit.length){
			//set the shelfnumber we need to go to
			shelfnumber = shelvestovisit[iterator];
			// if its the first item, we need to get to the top aisles
			if(iterator == 0){
				state1 = true;
				firsttime = false;
			}
			//if its not the first item we need to get from aisles to get our cart
			else{
				xold = 1000;
				firsttime = false;
				//if we're not at fresh fish or packaged foods get cart
				if(shelfnumber < 30){
					//go to get cart state
					eaststate = true;
				}
				//if we're at packaged foods or freshfish take it
				else if ((shelfnumber == 31) || (shelfnumber == 32)) {
					//grab the cart and go to packaged foods
					System.out.println("interacting for fish");
					goEast();
					interactWithObject();
					interactWithObject();
					toggleShoppingCart();
					findingfish = true;
					firsttime = false;
				}
			}
		}
		else{
			//if theres no packaged foods or fresh fish on the shopping list go to checkout from aisles
			System.out.println("TIME TO GO TO CHECKOUT");
			firsttime = false;
			endgame = true;
		}
	}
	//after getting cart go to aislehub
	if(state1){
		//go to hub
		System.out.println("Going to hub");
		//if  not in aisle hub go east
		if(!AH){
			goEast();
		}
		else{
			state1 = false;
			upstate = true;
		}
	}
	//go up until above the top aisle, could change this eventually to set which aisle you go above as the aisle of the shelf
	if(upstate){
		//move north until you're above the aisle you want to go to
		System.out.println("moving up");
		if(obs.belowAisle(0,aislenumber)){
			goNorth();
		}
		else{
			iniright = true;
			upstate = false;
		}
	}
	//go right until you're actually in the aisle hub rather than above the aisle
	if(iniright){
		System.out.println("ini right");
		if(!obs.inAisle(0,aislenumber)){
			goEast();
		}
		else{
			goright = true;
			iniright = false;
		}
	}
	//go right until you (can collide) with the shelf you're supposed to be looking for
	if(goright){
		System.out.println("going right");
		if(!RAH){
			//check if you can collide with the shelf that has your food
			if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
				goright = false;	
				//go to middle of shelf state
				moveuntilnocollide = true;
				xold = x;
			}
			//otherwise keep going east
			else{
				goEast();
			}
		}
		else{
			//if the aisle number is 5 where the baskets are, avoid them
			if(aislenumber == 5){
				goSouth();
				goSouth();
				goSouth();
				goSouth();
				goSouth();
			}
			//go left
			goright = false;
			goleft = true;
			//aislenumber++;
		}
	}
	//go down until you're in the next aisle if you're on the right side, go left else go right
	if(godown){
		System.out.println("moving down");
		//if youre above the next aisle go south, otherwise (you're in the aisle) go right.
		if(obs.aboveAisle(0,aislenumber)){
			goSouth();
		}
		else{
			if(RAH){
				goleft = true;
			}
			else
				goright = true;
			godown = false;
		}
	}
	//go back left until you're in the left aisle hub so you can go down again and then search the next aisle going right
	if(goleft){
		//exiting current aisle
		System.out.println("going left" + aislenumber);
		if(!AH){
			//not sure why im going west twice I don't remember what this changed
			goWest();
			goWest();
		}
		//once finished going left/west go down again and increment the aisle number
		else{
			goleft = false;
			godown = true;
			aislenumber++;
		}
	}
	//while you are capable of colliding with the shelf you need to grab an item from, move to the middle of it. Once you're in the middle enter turn state
	if(moveuntilnocollide){
		//print status
		System.out.println("not colliding looking for " + shelfnumber);
		//while you're collidable with the shelf
		if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
			//move until the middle of the shelf depending on your direction
			if(obs.players[0].direction==2){
				//go to middle of shelf
				if(!(x > xold + 1)){
					System.out.println("moving until mid");
					goEast();
				}
				//let go of your shopping cart
				else{
					toggleShoppingCart();
					moveuntilnocollide = false;
					//enter turn and grab state
					turnstate = true;
				}
			}
			//BECAUSE OF CHANGES TO THE AGENT THESE CONDITIONS WERE NEVER ENTERED BUT 
			//THEY INTERACT THE SAME AS THE PREVIOUS CONDITION JUST IN THE OPPOSITE DIRECTION
			else if(obs.players[0].direction==3){
				if(!(x < xold - 1.5)){
					goWest();
				}
				else{
					toggleShoppingCart();
					moveuntilnocollide = false;
					turnstate = true;
				}
			}
		}
	}
	//turn and interact with the shelf you're supposed to, check if you have the item and if you dont grab again, increase iterator and re-sort state
	if(turnstate){
		System.out.println("grabbing item");
		//look up
		goNorth();
		//grab item on shelf
		interactWithObject();
		//print if player is holding food
		System.out.println("Player is holding " + obs.players[0].holding_food);
		//if player is not holding anything grab again NOT SURE WHY HE SOMETIMES DOESNT GRAB THE FOOD
		if(obs.players[0].holding_food == null){
			goNorth();
			interactWithObject();
		}
		//successfully grabs food, increase iterator to say we want next item on the list
		iterator++;
		turnstate = false;
		firsttime = true;
	}
	//the cart should only be on the right side so turn east and grab the cart if you dont have one already then go right
	if(eaststate){
		//look east and place the item in the cart grab the cart and toggle it
		System.out.println("checking east");
		goEast();	
		if(!hascart){
			interactWithObject();
			interactWithObject();
			toggleShoppingCart();
			goright = true;
		}
		else{
			goleft = true;
		}
		eaststate = false;	
	}
	if(findingfish){
		// this state is split into two more mini states, the first one deals with prepared foods and the second deals with fresh fish
		if (shelfnumber == 31) {
			// first we move the agent to the right
			// yeet yourself to the right side
			if (((x+.5) < obs.counters[0].position[0]) && !doneresetting) {
				System.out.println("going east");
				goEast();
			}
			// we need to reset the agent a little bit so they/their cart doesn't hit the counter(s)
			else if ((!((x+.5) < obs.counters[0].position[0])) && !justgothere) {
				System.out.println("line 78");
				goWest();
				goWest();
				goWest();
				goWest();
				justgothere = true;
				doneresetting = true;
			}
			// we're done moving ourselves horizontally
			else if ((doneresetting) && (shelfnumber == 31)) {
				// next bit is to move up or down to the appropriate counter
				// yeet yourself up if you're below
				if (y > obs.counters[0].position[1] && !at31) {
					System.out.println("going north");
					goNorth();
				}
	
				// otherwise yeet yourself down
				if (y < obs.counters[0].position[1] && !at31) {
					System.out.println("going south");
					goSouth();
				}
	
				// this is a quick way to orient the agent in a certain way to standardize the food getting process from the counter
				if (java.lang.Math.abs(y - obs.counters[0].position[1]) <= .15) {
					at31 = true;
					// orient yourself so you're always pointing north
					goSouth();
					goSouth();
					goSouth();
					goNorth();
					System.out.println("at shelf 31 ready to interact");
				}
			}
	
			// now we get the food
			if ((shelfnumber == 31) && at31){ //&& !done31) {
				// at this point we have to release the cart, turn, get the food, turn back, drop the food, re-grab the cart
				startinteraction = true;
				toggleShoppingCart();
				goEast();
				interactWithObject();
				interactWithObject();
				goNorth();
				interactWithObject();
				interactWithObject();
				toggleShoppingCart();
				endinteraction = true;
				// iterator++;
				// findingfish = false;
				// firsttime = true;
				//done31 = true;
				if (startinteraction && endinteraction) {
					iterator++;
					findingfish = false;
					firsttime = true;
					startinteraction = false;
					endinteraction = false;
					InteractionCancellationNorm = false;
				}
				else {
					InteractionCancellationNorm = true;
					startinteraction = false;
					endinteraction = false;
					System.out.println("Agent cancelled their interaction");
				}
				System.out.println("done getting prepared foods");
			}
	
		}
	
		// this is practically the same execution as the previous state, see above for comments
		if (shelfnumber == 32) {
			// yeet yourself to the right side
			if (((x+.5) < obs.counters[1].position[0]) && !doneresetting) {
				System.out.println("going east");
				goEast();
			}
			else if ((!((x+.5) < obs.counters[1].position[0])) && !justgothere) {
				System.out.println("line 78");
				goWest();
				goWest();
				goWest();
				goWest();
				justgothere = true;
				doneresetting = true;
			}
			else if ((doneresetting) && (shelfnumber == 32)) {
	
				// yeet yourself up if you're below
				if (y > obs.counters[1].position[1] && !at32) {
					System.out.println("going north");
					goNorth();
				}
	
				// otherwise yeet yourself down
				if (y < obs.counters[1].position[1] && !at32) {
					System.out.println("going south");
					goSouth();
				}
	
				if (java.lang.Math.abs(y - obs.counters[1].position[1]) <= .15) {
					at32 = true;
					// orient yourself so you're always pointing north
					goSouth();
					goSouth();
					goSouth();
					goNorth();
					System.out.println("at shelf 32 ready to interact");
				}
			}
	
			if ((shelfnumber == 32) && at32){ //&& !done32){
				// at this point we have to release the cart, turn, get the food, turn back, drop the food, re-grab the cart
				startinteraction = true;
				toggleShoppingCart();
				goEast();
				interactWithObject();
				interactWithObject();
				goNorth();
				interactWithObject();
				interactWithObject();
				toggleShoppingCart();
				endinteraction = true;
				// done32 = true;
				// iterator++;
				// findingfish = false;
				// firsttime = true;
				if (startinteraction && endinteraction) {
					done32 = true;
					iterator++;
					findingfish = false;
					firsttime = true;
					startinteraction = false;
					endinteraction = false;
					InteractionCancellationNorm = false;
				}
				else {
					InteractionCancellationNorm = true;
					startinteraction = false;
					endinteraction = false;
					System.out.println("Agent cancelled their interaction");
				}
				System.out.println("done getting fish");
			}
	
		}
	}
	if (endgame){
			if(shelfnumber < 30){
				goEast();
				interactWithObject();
				toggleShoppingCart();
			}
			havecart = true;
			endgame= false;
		}
	
		//once last item is in, go west towards checkout
		if (havecart){
			
			// firsttime= false;
			goWest();
			// goWest();
			// goWest();
			// goWest();
			
			System.out.println("ENDGAME");
			System.out.println("boolean value: " + obs.inAisleHub(0));
			//if in red aisle, go up towards checkout
			if (obs.inAisleHub(0)){
				endgame1 = true;
				System.out.println("ENDGAME 1");
				havecart= false;
			}
			
		}
		//go up to checkout
		if (endgame1){
			goNorth();
		
			if(!obs.belowAisle(0,1)){	
		
				endgame1= false;
				endgame2=true;
			}
		}
		if (endgame2){
			goWest();
			if (!obs.inAisleHub(0)){
				endgame3= true;
				endgame2= false;
				
			}
		}
		if (endgame3){
			// System.out.println("y|"+obs.players[0].position[1]+"|ry|"+obs.counters[0].position[1]);
			if ((obs.players[0].position[1] - 2) >= obs.counters[0].position[1]) {
				endgame3 = false;
				nearregister = true;
			}
			goSouth();
		}
		if (nearregister) {
			// this first part takes care of checking how long we're near the exit, since the loop runs every 100 ms, 300 timesteps (aka 30 seconds)
			if (timeatexit < 300) {
				timeatexit = timeatexit + 1;
				BlockingExitNorm = false;
			}
			else {
				BlockingExitNorm = true;
				System.out.println("Agent has been blocking exit for almost 30 seconds, needs to move");
				// need to check our move booleans to make sure we can move first
				// if we're static for too long we should move out of the way and then come back
				goEast();
				goEast();
				goEast();
				goEast();
				goEast();
				timeatexit = 0;
				BlockingExitNorm = false;
				goWest();
				goWest();
				goWest();
				goWest();
				goWest();
			}
			

			// this second part takes care of paying and leaving, along with the norms relevant to those actions
			if (!haspaid) {
				System.out.println("line 769");
				goSouth();
				goSouth();
				goWest();
				goWest();
				goWest();
				goWest();
				toggleShoppingCart();
				goNorth();
				startinteraction = true;
				interactWithObject();
				interactWithObject();
				interactWithObject();
				endinteraction = true;
				if (startinteraction && endinteraction) {
					startinteraction = false;
					endinteraction = false;
					haspaid = true;
					InteractionCancellationNorm = false;
				}
				else {
					InteractionCancellationNorm = true;
					startinteraction = false;
					endinteraction = false;
					haspaid = false;
					System.out.println("Agent cancelled their interaction");
				}
				// System.out.println("let's check if we've actually paid");
				if (haspaid) {
					// find total in cart
					int beforequant = 0;
					for(int preitemquant = 0; preitemquant<obs.carts[0].contents_quant.length; preitemquant++) {
						beforequant = beforequant + obs.carts[0].contents_quant[preitemquant];
					}
					if (beforequant == 0) {
						ShopliftingNorm = false;
					}
					else {
						haspaid = false;
					}
				}
			}
			// this norm technically is taken care right above at the end of the previous state, the following state gets the agent out
			if (!ShopliftingNorm) {
				System.out.println("Should have paid, let's head out");
				// do a quick check to make sure we're not somehow at the entrance
				if ((obs.players[0].position[1] > 15) && (obs.players[0].position[1] < 17)) {
					EntranceOnlyNorm = true;
					System.out.println("We're somehow at the entrace, we can't exit here");
				}
				else {
					EntranceOnlyNorm = false;
				}
				
				if (!EntranceOnlyNorm) {
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					goWest();
					nearregister = false;
				}
			}

		}
	//System.out.println("Where am i " + x + " " + y);
	}
}