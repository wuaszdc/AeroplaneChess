package model;


public class BGMPlayer extends SoundsPlayer implements Runnable{

	public BGMPlayer(GameData gameData,int playTimes,String soundsURL){
		super(gameData,playTimes,soundsURL) ;
	}
}