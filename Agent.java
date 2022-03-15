import java.util.Arrays;

import javax.lang.model.util.ElementScanner14;

import com.supermarket.*;

public class Agent extends SupermarketComponentImpl {
    public Agent() {
	super();
	shouldRunExecutionLoop = true;
	log.info("In Agent constructor.");
    }
	//incremental state for every first time after grabbing an ingredient
	boolean firsttime = false;
	//only run this once, get to cart return and grab cart
	boolean findcart = true;
	//after getting cart go to aislehub
	boolean state1 = false;
	//go up until above the top aisle, could change this eventually to set which aisle you go above as the aisle of the shelf
	boolean upstate = false;
	//go right until you (can collide) with the shelf you're supposed to be looking for
	boolean goright = false;
	//go down until you're in the next aisle if you're on the right side, go left else go right
	boolean godown = false;
	//go right until you're actually in the aisle hub rather than above the aisle
	boolean iniright = false;
	//go back left until you're in the left aisle hub so you can go down again and then search the next aisle going right
	boolean goleft = false;
	//while you are capable of colliding with the shelf you need to grab an item from, move to the middle of it. Once you're in the middle enter turn state
	boolean moveuntilnocollide = false;
	//turn and interact with the shelf you're supposed to, check if you have the item and if you dont grab again, increase iterator and re-sort state
	boolean turnstate = false;
	//the cart should only be on the right side so turn east and grab the cart if you dont have one already then go right
	boolean eaststate = false;

	boolean justgothere = false;
	boolean doneresetting = false;
	boolean at31 = false;
	boolean at32 = false;
	boolean done31 = false;
	boolean done32 = false;


	boolean endgame = false;
	boolean endgame1 = false;
	boolean endgame2 = false;
	boolean endgame3 = false;
	boolean havecart = false;
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
	//only run this once, get to cart return and grab cart
	if(findcart){
		//go south and interact until you reach the cart return
		System.out.println("finding cart!");
		goSouth();
		interactWithObject();
		if(hascart){
			//if you have a cart
			firsttime = true;
			findcart = false;
		}
	}
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
				//set the food you're looking for
				foody = obs.players[0].shopping_list[j];
			}
		}
		//shelves 
		Arrays.sort(shelvestovisit);
		System.out.println("Shelves to visit" + Arrays.toString(shelvestovisit));
		if(iterator < shelvestovisit.length){
			System.out.println("SHELVES LENGTH" + shelvestovisit.length);
			shelfnumber = shelvestovisit[iterator];
			if(iterator == 0){
				state1 = true;
				firsttime = false;
			}
			else{
				xold = 1000;
				firsttime = false;
				if(shelfnumber < 30){
					eaststate = true;
				}
				else if ((shelfnumber == 31) || (shelfnumber == 32)) {
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
			System.out.println("TIME TO GO TO CHECKOUT");
			firsttime = false;
			endgame = true;
		}
	}
	//after getting cart go to aislehub
	if(state1){
		System.out.println("Going to hub");
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
			if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
				goright = false;
				moveuntilnocollide = true;
				xold = x;
			}
			else{
				goEast();
			}
		}
		else{
			if(aislenumber == 5){
				goSouth();
				goSouth();
				goSouth();
				goSouth();
				goSouth();
			}
			goright = false;
			goleft = true;
			//aislenumber++;
		}
	}
	//go down until you're in the next aisle if you're on the right side, go left else go right
	if(godown){
		System.out.println("moving down");
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
		System.out.println("going left" + aislenumber);
		if(!AH){
			//if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
			//	goleft = false;
			//	moveuntilnocollide = true;
			//	xold = x;
			//}
			//else{
			goWest();
			goWest();
		}
		else{
			goleft = false;
			godown = true;
			aislenumber++;
		}
	}
	//while you are capable of colliding with the shelf you need to grab an item from, move to the middle of it. Once you're in the middle enter turn state
	if(moveuntilnocollide){
		System.out.println("not colliding looking for " + shelfnumber);
		if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
			if(obs.players[0].direction==2){
				if(!(x > xold + 1)){
					System.out.println("moving until mid");
					goEast();
				}
				else{
					toggleShoppingCart();
					moveuntilnocollide = false;
					turnstate = true;
				}
			}
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
		goNorth();
		interactWithObject();
		System.out.println("Player is holding " + obs.players[0].holding_food);
		if(obs.players[0].holding_food == null){
			goNorth();
			interactWithObject();
		}
		iterator++;
		turnstate = false;
		firsttime = true;
	}
	//the cart should only be on the right side so turn east and grab the cart if you dont have one already then go right
	if(eaststate){
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
		if (shelfnumber == 31) {
			// yeet yourself to the right side
			if (((x+.5) < obs.counters[0].position[0]) && !doneresetting) {
				System.out.println("going east");
				goEast();
			}
			else if ((!((x+.5) < obs.counters[0].position[0])) && !justgothere) {
				System.out.println("line 78");
				goWest();
				goWest();
				goWest();
				goWest();
				justgothere = true;
				doneresetting = true;
			}
			else if ((doneresetting) && (shelfnumber == 31)) {
				
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
		//after last item is picked up put it into cart and take cart
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
			goSouth();
		}
	//System.out.println("Where am i " + x + " " + y);
	}
}