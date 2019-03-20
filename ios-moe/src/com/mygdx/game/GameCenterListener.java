package com.mygdx.game;

import java.util.ArrayList;

import apple.foundation.NSError;
import apple.gamekit.GKAchievement;
import apple.gamekit.GKLeaderboard;

interface GameCenterListener {

    void playerLoginCompleted();

    void playerLoginFailed(NSError error);

    void achievementReportCompleted();

    void achievementReportFailed(NSError error);

    void achievementsLoadCompleted(ArrayList<GKAchievement> achievements);

    void achievementsLoadFailed(NSError error);

    void achievementsResetCompleted();

    void achievementsResetFailed(NSError error);

    void scoreReportCompleted();

    void scoreReportFailed(NSError error);

    void leaderboardsLoadCompleted(ArrayList<GKLeaderboard> scores);

    void leaderboardsLoadFailed(NSError error);

    void leaderboardViewDismissed();

    void achievementViewDismissed();

}
