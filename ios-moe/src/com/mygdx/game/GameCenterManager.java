package com.mygdx.game;

import java.util.ArrayList;

import apple.foundation.NSDictionary;
import apple.foundation.NSError;
import apple.foundation.NSMutableArray;
import apple.gamekit.GKAchievement;
import apple.gamekit.GKGameCenterViewController;
import apple.gamekit.GKLeaderboard;
import apple.gamekit.GKLocalPlayer;
import apple.gamekit.GKScore;
import apple.uikit.UIViewController;
import apple.uikit.UIWindow;

import static apple.gamekit.enums.GKGameCenterViewControllerState.Achievements;
import static apple.gamekit.enums.GKGameCenterViewControllerState.Leaderboards;

public class GameCenterManager implements MyGdxGame.iOSInterface {

    private static final String GCM_DOMAIN = GameCenterManager.class.getSimpleName();
    private static final long GCM_ERROR_NOT_AUTHENTICATED = -1024;

    private final UIWindow keyWindow;
    private final GameCenterListener listener;

    /** Constructor.
     * @param keyWindow KeyWindow can't be accessed from the Delegate sometimes, so we need to save a reference
     * @param listener - App Class */
    GameCenterManager(UIWindow keyWindow, GameCenterListener listener) {
        this.keyWindow = keyWindow;
        this.listener = listener;
    }

    /** Do the login logic. If the user has never logged, a dialog will be shown. */
    @Override
    public void login() {
        GKLocalPlayer.localPlayer().setAuthenticateHandler((viewController, error) -> {
                    // If the device does not have an authenticated player, show the login dialog
                    if (viewController != null) {
                        keyWindow.rootViewController().presentViewControllerAnimatedCompletion(
                                viewController, true, null);
                    }
                    // If the viewController is null and the player is authenticated, the login is completed
                    else if (GKLocalPlayer.localPlayer().isAuthenticated()) {
                        listener.playerLoginCompleted();
                    }
                    // If the viewController is null and the player is not authenticated the login has failed
                    else {
                        listener.playerLoginFailed(error);
                    }
                }
        );
    }

    /** Report an achievement completed (100 as percentComplete)
     *
     * @param identifier - Achievement ID */
    @Override
    public void unlockAchievement(String identifier) {
        updateIncrementalAchievement(identifier, 100);
    }

    /** Report an achievement with a percentComplete
     *
     * @param percentComplete - Amount complete */
    @Override
    public void updateIncrementalAchievement(String achievementID, double percentComplete) {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            listener.achievementReportFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKAchievement achievement = GKAchievement.alloc().init();
        achievement.setIdentifier(achievementID);
        achievement.setPercentComplete(percentComplete);
        achievement.setShowsCompletionBanner(true);

        // Create an array with the achievement
        NSMutableArray<GKAchievement> achievements = (NSMutableArray<GKAchievement>) NSMutableArray.alloc().init();
        achievements.add(achievement);

        GKAchievement.reportAchievementsWithCompletionHandler(achievements, error -> {
            if (error != null) {
                listener.achievementReportFailed(error);
            } else {
                listener.achievementReportCompleted();
            }
        });
    }

    /** Load all the achievements for the local player */
    @Override
    public void loadAchievements() {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            listener.achievementsLoadFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKAchievement.loadAchievementsWithCompletionHandler((array, error) -> {
            if (error != null) {
                listener.achievementsLoadFailed(error);
            } else {
                ArrayList<GKAchievement> achievements = new ArrayList<>(array);
                listener.achievementsLoadCompleted(achievements);
            }
        });
    }

    /** Reset the achievements progress for the local player.
     * All the entries for the local player are removed from the server.
     * */
    @Override
    public void resetAchievements() {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            listener.achievementsResetFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKAchievement.resetAchievementsWithCompletionHandler(error -> {
            if (error != null) {
                listener.achievementsResetFailed(error);
            } else {
                listener.achievementsResetCompleted();
            }
        });
    }

    /** Report a score to GameCenter
     * @param identifier - Leaderboard ID
     * @param score - Digits to post to leaderboard */
    @Override
    public void updateLeaderboardScore(String identifier, long score) {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            listener.scoreReportFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKScore scoreReporter = GKScore.alloc().init();
        scoreReporter.setValue(score);

        scoreReporter.setLeaderboardIdentifier(identifier);

        NSMutableArray<GKScore> scores = (NSMutableArray<GKScore>) NSMutableArray.alloc().init();
        scores.add(scoreReporter);

        GKScore.reportScoresWithCompletionHandler(scores, error -> {
            if (error != null) {
                listener.scoreReportFailed(error);
            } else {
                listener.scoreReportCompleted();
            }
        });
    }

    /** Load all the Leaderboards for the Game. Warning: If using iOS5 or less the Leaderboard object will only include the
     * Category (identifier) */
    @Override
    public void loadLeaderboards() {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            listener.leaderboardsLoadFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKLeaderboard.loadLeaderboardsWithCompletionHandler((array, error) -> {
            if (error != null) {
                listener.leaderboardsLoadFailed(error);
            } else {
                ArrayList<GKLeaderboard> leaderboards = new ArrayList<>(array);
                listener.leaderboardsLoadCompleted(leaderboards);
            }
        });
    }

    /** Return the id of a leaderboard (category or identifier)
     * @param leaderboard - GKLeaderboard you want identifier of
     * @return Leaderboard ID String*/
    public String getLeaderboardId(GKLeaderboard leaderboard) {
        return leaderboard.identifier();
    }

    /** Shows GameCenter standard interface for Achievements */
    @Override
    public void showAchievements() {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            return;
        }

        GKGameCenterViewController gameCenterView = GKGameCenterViewController.alloc().init();
        gameCenterView.setGameCenterDelegate(gameCenterViewController ->
                dismissViewControllerAndNotifyListener(gameCenterViewController, Achievements));
        gameCenterView.setViewState(Achievements);
        keyWindow.rootViewController()
                .presentViewControllerAnimatedCompletion(gameCenterView, true, null);
    }

    /** Shows GameCenter standard interface for Leaderboards */
    @Override
    public void showLeaderboards() {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            return;
        }

        GKGameCenterViewController gameCenterView = GKGameCenterViewController.alloc().init();
        gameCenterView.setGameCenterDelegate(gameCenterViewController ->
                dismissViewControllerAndNotifyListener(gameCenterViewController, Leaderboards));
        gameCenterView.setViewState(Leaderboards);
        keyWindow.rootViewController()
                .presentViewControllerAnimatedCompletion(gameCenterView, true, null);
    }

    /** Shows GameCenter standard interface for one Leaderboard
     * @param identifier - Leaderboard ID*/
    @Override
    public void showSingleLeaderboard(String identifier) {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.localPlayer().isAuthenticated()) {
            return;
        }

        GKGameCenterViewController gameCenterView = GKGameCenterViewController.alloc().init();
        gameCenterView.setGameCenterDelegate(gameCenterViewController ->
                dismissViewControllerAndNotifyListener(gameCenterViewController, Leaderboards));

        gameCenterView.setViewState(Leaderboards);
        gameCenterView.setLeaderboardIdentifier(identifier);

        keyWindow.rootViewController()
                .presentViewControllerAnimatedCompletion(gameCenterView, true, null);
    }

    @Override
    public boolean isSignedIn() {
        return GKLocalPlayer.localPlayer().isAuthenticated();
    }

    /** Dismiss the {@link UIViewController} and invoke the appropriate callback on the {@link #listener}.
     *
     * @param viewController the {@link UIViewController} to dismiss
     * @param viewControllerState the type of the View Controller being dismissed */
    private void dismissViewControllerAndNotifyListener(UIViewController viewController,
                                                        final long viewControllerState) {
        viewController.dismissViewControllerAnimatedCompletion(true, () -> {
            if (viewControllerState == Achievements) {
                listener.achievementViewDismissed();
            } else if (viewControllerState == Leaderboards) {
                listener.leaderboardViewDismissed();
            }
        });
    }

    /** Generate an {@link NSError} indicating that the local player is unauthenticated.
     *
     * NOT TESTED YET
     *
     * @return {@link NSError} */
    private NSError buildUnauthenticatedPlayerError() {
        NSError info = NSError.alloc().init();
        NSDictionary dictionary = info.userInfo();
        dictionary.setValueForKey("Local player is unauthenticated", "NSLocalizedDescriptionKey");
        info.initWithDomainCodeUserInfo(GCM_DOMAIN, GCM_ERROR_NOT_AUTHENTICATED, dictionary);
        return info;
    }

}