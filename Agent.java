import java.util.Arrays;

//import javax.lang.model.util.ElementScanner14;
//hello
import com.supermarket.*;
//JORDYN BROSEMER SOHAM GAGGENAPALLY AND MATTHEW TOVEN *ETHICAL* SUPERMARKET AGENT
//WHATEVER WAS WRITTEN BY EACH OF US IS DECLARED IN THE BOOLEAN INFORMATION DIRECTLY BELOW
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
	//JORDYN BROSEMER norm state that prevents the agent from walking into a wall 
	boolean avoidwall = false;

	// These states were written by Soham
	// gets the agent to the right aisle
	boolean justgothere = false;
	// resets the agent to the right horizontal position so they don't hit the counters
	boolean doneresetting = false;
	// moves the agent to the prepared foods counter
	boolean at31 = false;
	// moves the agent to the fresh fish counter
	boolean at32 = false;
	// agent is done getting prepared foods and has put it in the cart
	boolean done31 = false;
	// agent is done getting fresh fish and has put it in the cart
	boolean done32 = false;
	// agent is done shopping and near register, so we can orient ourselves and check out
	boolean nearregister = false;


	//States written by Matthew Toven
	boolean endgame = false;
	boolean endgame1 = false;
	boolean endgame2 = false;
	boolean endgame3 = false;
	boolean havecart = false;
	public int abs(int x){
		if(x < 0){
			return -x;
		}
		else{
			return x;
		}
	}
	public boolean moveNorms(int direction, Observation obs){
		//PUT NORMS THAT INVOLVE MOVEMENT HERE

		//PLAYER COLLISION NORM
		collideplayers = true;
		if(collideplayers){
			//if only one player
			if(!(obs.players.length>=1)){
				System.out.println("we're here");
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
				//YOU CAN SKIP THE PERSONAL SPACE NORM IF THERE IS ONLY ONE AGENT
				//PersonalSpaceNorm = true;
				}
			}
		if(PersonalSpaceNorm){
			

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
    protected void executionLoop() {
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
	boolean hascart = false;
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
		if(moveNorms(3,obs)){
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
				toggleShoppingCart();
				goEast();
				interactWithObject();
				interactWithObject();
				goNorth();
				interactWithObject();
				interactWithObject();
				toggleShoppingCart();
				iterator++;
				findingfish = false;
				firsttime = true;
				//done31 = true;
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
				toggleShoppingCart();
				goEast();
				interactWithObject();
				interactWithObject();
				goNorth();
				interactWithObject();
				interactWithObject();
				toggleShoppingCart();
				done32 = true;
				iterator++;
				findingfish = false;
				firsttime = true;
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
			System.out.println("y|"+obs.players[0].position[1]+"|ry|"+obs.counters[0].position[1]);
			if ((obs.players[0].position[1] - 2) >= obs.counters[0].position[1]) {
				endgame3 = false;
				nearregister = true;
			}
			goSouth();
		}
		if (nearregister) {
			System.out.println("line 516");
			goSouth();
			goSouth();
			goWest();
			goWest();
			goWest();
			goWest();
			toggleShoppingCart();
			goNorth();
			interactWithObject();
			interactWithObject();
			interactWithObject();
		}
	//System.out.println("Where am i " + x + " " + y);
	}
}