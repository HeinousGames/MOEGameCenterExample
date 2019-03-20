package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosmoe.IOSApplication;
import com.badlogic.gdx.backends.iosmoe.IOSApplicationConfiguration;
import org.moe.natj.general.Pointer;

import java.util.ArrayList;

import apple.foundation.NSError;
import apple.gamekit.GKAchievement;
import apple.gamekit.GKLeaderboard;
import apple.uikit.c.UIKit;

public class IOSMoeLauncher extends IOSApplication.Delegate implements GameCenterListener {

    private MyGdxGame game;

    protected IOSMoeLauncher(Pointer peer) {
        super(peer);
    }

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.useAccelerometer = false;

        game = new MyGdxGame() {
            @Override
            public void create() {
                game.interfaceIOS = new GameCenterManager(
                        ((IOSApplication) Gdx.app).getUIWindow(), IOSMoeLauncher.this);
                super.create();
            }
        };

        return new IOSApplication(game, config);
    }

    public static void main(String[] argv) {
        UIKit.UIApplicationMain(0, null, null, IOSMoeLauncher.class.getName());
    }

    @Override
    public void playerLoginCompleted() {
        System.out.println("Login Success");
    }

    @Override
    public void playerLoginFailed(NSError error) {
        System.out.println("Login Failed with Error: " + error.toString());
    }

    @Override
    public void achievementReportCompleted() {

    }

    @Override
    public void achievementReportFailed(NSError error) {

    }

    @Override
    public void achievementsLoadCompleted(ArrayList<GKAchievement> achievements) {

    }

    @Override
    public void achievementsLoadFailed(NSError error) {

    }

    @Override
    public void achievementsResetCompleted() {

    }

    @Override
    public void achievementsResetFailed(NSError error) {

    }

    @Override
    public void scoreReportCompleted() {

    }

    @Override
    public void scoreReportFailed(NSError error) {

    }

    @Override
    public void leaderboardsLoadCompleted(ArrayList<GKLeaderboard> scores) {

    }

    @Override
    public void leaderboardsLoadFailed(NSError error) {

    }

    @Override
    public void leaderboardViewDismissed() {

    }

    @Override
    public void achievementViewDismissed() {

    }
}
