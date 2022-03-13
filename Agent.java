import java.util.Arrays;
import com.supermarket.*;

public class Agent extends SupermarketComponentImpl {
    public Agent() {
	super();
	shouldRunExecutionLoop = true;
	log.info("In Agent constructor.");
    }
	boolean firsttime = false;
	boolean state1 = false;
	boolean upstate = false;
	boolean goright = false;
	boolean goup = false;
	boolean godown = false;
	boolean iniright = false;
	boolean goleft = false;
	boolean moveuntilnocollide = false;
	boolean turnstate = false;
	boolean weststate = false;
	boolean eaststate = false;
	boolean findcart = true;
	int iterator = 0;
	int shelfnumber = -1;
	int aislenumber = 1;
	double xold = 1000; 
    @Override
    protected void executionLoop() {
	// this is called every 100ms
	// put your code in here
	Observation obs = getLastObservation();
	System.out.println("Shoppping list: " + Arrays.toString(obs.players[0].shopping_list));	
	String foody = obs.players[0].shopping_list[0];
	boolean NCR = obs.northOfCartReturn(0);
	boolean SCR = obs.southOfCartReturn(0);
	boolean ACR = obs.atCartReturn(0);
	boolean AH = obs.inAisleHub(0);
	boolean RAH = obs.inRearAisleHub(0);
	boolean hascart = false;
	if(obs.players[0].curr_cart == -1){
		hascart = false;
	}
	else{
		hascart = true;
	}
	double x = obs.players[0].position[0];
	double y = obs.players[0].position[1];
	System.out.println("iterator " + iterator + " shelfnumber " + shelfnumber);
	if(findcart){
		goSouth();
		interactWithObject();
		if(hascart){
			firsttime = true;
			findcart = false;
		}
	}
	if(firsttime){
		int[] shelvestovisit = new int[obs.players[0].shopping_list.length];
		System.out.println("initial state");
		for(int j = 0; j <  shelvestovisit.length;j++){
			for (int i = 0;i < obs.shelves.length;i++){
					if(obs.shelves[i].food.equals(foody)){
						shelvestovisit[j] = i;
				}
				foody = obs.players[0].shopping_list[j];
			}
		}
		Arrays.sort(shelvestovisit);
		System.out.println("Shelves to visit" + Arrays.toString(shelvestovisit));
		shelfnumber = shelvestovisit[iterator];
		if(iterator == 0){
			state1 = true;
			firsttime = false;
		}
		else{
			xold = 1000;
			firsttime = false;
			weststate = true;
		}
	}
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
			goright = false;
			godown = true;
			aislenumber++;
		}
	}
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
	if(goleft){
		System.out.println("going left");
		if(!AH){
			if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
				goleft = false;
				moveuntilnocollide = true;
				xold = x;
			}
			else{
				goWest();
			}
		}
		else{
			goleft = false;
			godown = true;
			aislenumber++;
		}
	}
	if(moveuntilnocollide){
		System.out.println("not colliding looking for " + shelfnumber);
		//Find Player Direction

		if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
			if(obs.players[0].direction==2){
				if(!(x > xold + 1.5)){
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
	if(turnstate){
		System.out.println("grabbing item");
		goNorth();
		interactWithObject();
		iterator++;
		turnstate = false;
		firsttime = true;
	}
	if(weststate){
		System.out.println("checking west");
		goWest();
		interactWithObject();
		toggleShoppingCart();
		weststate = false;
		eaststate = true;
	}
	if(eaststate){
		System.out.println("checking east");
		goEast();	
		if(!hascart){
			interactWithObject();
			toggleShoppingCart();
			goright = true;
		}
		else{
			goleft = true;
		}
		eaststate = false;
	}
	System.out.println("Where am i " + x + " " + y);
	}
}
