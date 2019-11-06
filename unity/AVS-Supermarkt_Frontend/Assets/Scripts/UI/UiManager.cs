using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class UiManager : MonoBehaviour {

    public ShoppingPlannerUI plannerPanel;
    public GameObject loadingPanel;
    public GameObject openPlannerPanel;

    public Customer customer;

    private void Awake() {
        OpenOpenerUi();
    }


    public void StartCalculation() {
        CloseAllUis();
        loadingPanel.SetActive(true);

        var points = new List<Vector3>();
        plannerPanel.GetShoppingList().ForEach(x => points.Add(x.transform.position));
        customer.SetWaypoints(points);
    }

    public void OpenPlannerUi() {
        CloseAllUis();
        plannerPanel.gameObject.SetActive(true);
    }

    public void OpenOpenerUi() {
        CloseAllUis();
        openPlannerPanel.SetActive(true);
    }

    private void CloseAllUis() {
        plannerPanel.gameObject.SetActive(false);
        loadingPanel.SetActive(false);
        openPlannerPanel.SetActive(false);
    }

}
