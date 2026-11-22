package com.example.mcardle_daniel_s2375827;

import androidx.lifecycle.ViewModel;
import com.example.mcardle_daniel_s2375827.Situation;
import java.util.ArrayList;

public class SituationViewModel extends ViewModel {
    private ArrayList<Situation> allSituations;
    private ArrayList<Situation> visibleSituations;
    private int currentScreen = 0;
    private int loadingStatusScreen = 0;

    public ArrayList<Situation> getAllSituations() { return allSituations; }
    public void setAllSituations(ArrayList<Situation> s) { allSituations = s; }

    public ArrayList<Situation> getVisibleSituations() { return visibleSituations; }
    public void setVisibleSituations(ArrayList<Situation> s) { visibleSituations = s; }

    public int getCurrentScreen() { return currentScreen; }
    public void setCurrentScreen(int screen) { currentScreen = screen; }
    public int getLoadingStatusScreen() { return loadingStatusScreen; }
    public void setLoadingStatusScreen(int screen) { loadingStatusScreen = screen; }

}