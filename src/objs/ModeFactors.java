package objs;


public class ModeFactors
{
	protected double x;
	protected double y;
	final static int MODE_ATTACK = 0;
	final static int MODE_DEFEND = 1;

	static int mode;

	static double d = 100.0; 

	static double a = 100.0;



	public static void switchMode(int f) 
	{
		mode = f;
		
		if(mode == 0) 
		{
			a= 100.00;
			d= 10.00;
		}
		else if (mode == 10) {
			a= 10.00;
			d= 100.00;
		}
	}

	public static double update(Enemy enemy, Player player) 
	{
		
		if(enemy.difficulty == 1){// Medium AI 
			
			double relativeX = enemy.getX() - (player.getX() + (player.getWidth() / 2)); 
			// Time factor 
			if (mode == 0)
			{
				d++;
			}
			else if (mode == 1)
			{
				a++;
			}
			//Enemy AI HP factor
			if (enemy.getHp() <= (player.getInitialHP() * 0.10)) {
				d = d +25;
			}
			else if (enemy.getHp() >= (player.getInitialHP() * 0.90)) {
				a = a +55;
			}
			//Player HP factor
			if (player.getHp() <= (player.getInitialHP() * 0.10)) {
				a = a +55;
			}
			else if (player.getHp() >= (player.getInitialHP() * 0.90)) {
				d = d +30;
			}
			// Distance Factor
			if (relativeX < 150) {
				a = a +15;
			}
			//If player is blocking
			if (player.block.isActive() ) {
				a = a+50;
			}
			
		}
		else if (enemy.difficulty== 0) {// easy AI 
			
			double relativeX = enemy.getX() - (player.getX() + (player.getWidth() / 2)); 
			// Time factor 
			
			if (mode == 0)
			{
				d++;
			}
			else if (mode == 0)
			{
				a++;
			}
			//Enemy AI HP factor
			if (enemy.getHp() <= (player.getInitialHP() * 0.10)) {
				d = d +10;
			}
			else if (enemy.getHp() >= (player.getInitialHP() * 0.90)) {
				a = a +10;
			}
			//Player HP factor
			if (player.getHp() <= (player.getInitialHP() * 0.10)) {
				a = a +10;
			}
			else if (player.getHp() >= (player.getInitialHP() * 0.90)) {
				d = d +10;
			}
			// Distance Factor
			if (relativeX < 150) {
				a = a +5;
			}
			//If player is blocking
			if (player.block.isActive() ) {
				a++;
			}
			
		}return a/(a+d);
	}
	


}
