using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UiManager : MonoBehaviour {

    public GameObject plannerPanel;
    public GameObject loadingPanel;
    public GameObject openPlannerPanel;

    private void Awake() {
        OpenOpenerUi();
    }


    public void StartCalculation() {
        CloseAllUis();
        loadingPanel.SetActive(true);
    }

    public void OpenPlannerUi() {
        CloseAllUis();
        plannerPanel.SetActive(true);
    }

    public void OpenOpenerUi() {
        CloseAllUis();
        openPlannerPanel.SetActive(true);
    }

    private void CloseAllUis() {
        plannerPanel.SetActive(false);
        loadingPanel.SetActive(false);
        openPlannerPanel.SetActive(false);
    }

}
