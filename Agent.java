import java.util.Arrays;
import com.supermarket.*;

public class Agent extends SupermarketComponentImpl {
	/*
	public boolean move(int direction){
		Observation obs = getLastObservation();
		double x = obs.players[0].position[0];
		double y = obs.players[0].position[1];
		if(direction == 0){
			goNorth();
		}
		if(direction == 1){
			goWest();
		}
		if(direction == 2){
			goSouth();
		}
		if(direction == 3){
			goEast();
		}
		Observation obsnew = getLastObservation();
		double xnew = obs.players[0].position[0];

		double ynew = obs.players[0].position[1];
		if(ynew > y){
			if(direction == 2){
				return true;
			}
		}
		else if(ynew<y){
		}
	}
	public void direct(int direction){
		Observation obs = getLastObservation();
	}
	*/

    public Agent() {
	super();
	shouldRunExecutionLoop = true;
	log.info("In Agent constructor.");
    }
	boolean firsttime = true;
	boolean state1 = false;
	boolean upstate = false;
	boolean goright = false;
	boolean goup = false;
	boolean godown = false;
	boolean iniright = false;
	boolean goleft = false;
	String foody = "brie cheese";
	int shelfnumber = -1;
	int aislenumber = 1;
    @Override
    protected void executionLoop() {
	// this is called every 100ms
	// put your code in here
	Observation obs = getLastObservation();
	boolean NCR = obs.northOfCartReturn(0);
	boolean SCR = obs.southOfCartReturn(0);
	boolean ACR = obs.atCartReturn(0);
	boolean AH = obs.inAisleHub(0);
	boolean RAH = obs.inRearAisleHub(0);
	boolean hascart = false;
	double x = obs.players[0].position[0];
	double y = obs.players[0].position[1];
	if(firsttime){
		System.out.println("initial state");
		for (int i = 0;i < obs.shelves.length;i++){
			System.out.println(i + " " + obs.shelves[i].food);
			if(obs.shelves[i].food.equals(foody)){
				shelfnumber = i;
			}
		}
		System.out.println(shelfnumber);
		state1 = true;
		firsttime = false;
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
		if(!obs.inAisle(0,aislenumber)){
			goEast();
		}
		else{
			goup = true;
			iniright = false;
		}
	}
	if(goup){
		if(!obs.defaultCanInteract(obs.shelves[0],obs.players[0])){
			goNorth();
		}
		else{
			goup = false;
			goright = true;
		}
	}
	if(goright){
		if(!RAH){
			if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
				goright = false;
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
		if(!AH){
			if(obs.defaultCollision(obs.shelves[shelfnumber],obs.players[0].position[0],obs.players[0].position[1])){
				goleft = false;
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
	/*
	if(obs.inAisle(0,1)){
		System.out.println("im here 1");
	}
	else if(obs.inAisle(0,2)){
		System.out.println("im here 2");
	}
	else if(obs.inAisle(0,3)){
		System.out.println("im here 3");
	}
	else if(obs.inAisle(0,4)){
		System.out.println("im here 4");
		
	}
	else if(obs.inAisle(0,5)){
		System.out.println("im here 5");
	}
	else{
		goEast();
		goEast();
	}
	*/
	/*
	if(obs.players[0].curr_cart == -1){
		hascart = false;
	}
	else{
		hascart = true;
	}
	System.out.println(hascart);
	if(NCR){
		if(hascart){
			move(3);
		}
		else{
			move(2);
		}
	}
	else if(ACR){
		if(hascart){
			move(3);
		}
		else{
			interactWithObject();
			move(2);
		}
	}
	*/

	System.out.println("Where am i " + x + " " + y);
		//Going South
		//System.out.println("Position2 " + Double.toString(obsnew.players[0].position[1]));

		//if (ynew == y){
		//	move(3);
		//}
	//System.out.println(obs.players.length + " players");
	//System.out.println(obs.carts.length + " carts");
	//System.out.println(obs.shelves.length + " shelves");
	//System.out.println(obs.counters.length + " counters");
	//System.out.println(obs.registers.length + " registers");
	//System.out.println(obs.cartReturns.length + " cartReturns");
	// print out the shopping list
	//System.out.println("Shoppping list: " + Arrays.toString(obs.players[0].shopping_list));
	//if(northOfCartReturn(obs.players[0])){
	//	System.out.println("Where am i ");
	//}
	//System.out.println("Do I have a cart " + obs.players[0].curr_cart);
	//System.out.println("Position " + Double.toString(obs.players[0].position[1]));
	}
}
