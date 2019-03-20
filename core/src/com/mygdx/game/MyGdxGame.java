package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {

	iOSInterface interfaceIOS;
	SpriteBatch batch;
	Texture img;

	public interface iOSInterface {
		boolean isSignedIn();
		void login();
		void loadAchievements();
		void loadLeaderboards();
		void resetAchievements();
		void showAchievements();
		void showLeaderboards();
		void showSingleLeaderboard(String leaderboardID);
		void unlockAchievement(String achievementID);
		void updateLeaderboardScore(String leaderboardID, long score);
		void updateIncrementalAchievement(String achievementID, double percentComplete);
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

		if (!interfaceIOS.isSignedIn()) {
			interfaceIOS.login();
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
