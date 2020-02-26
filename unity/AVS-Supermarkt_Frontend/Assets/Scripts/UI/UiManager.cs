using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class UiManager : MonoBehaviour {

    public ShoppingPlannerUI plannerPanel;
    public GameObject loadingPanel;
    public GameObject openPlannerPanel;
    public ResultUI resultPanel;
    public TMP_InputField schedulerIpField;
    public StatisticsUI statisticsUI;

    public Customer customer;

    private PathDisplayer pathDisplayer;

    public bool AllowCameraScript { get; private set; }

    private void Awake() {
        schedulerIpField.onSelect.AddListener(delegate { AllowCameraScript = false; });
        schedulerIpField.onDeselect.AddListener(delegate { AllowCameraScript = true; });

        OpenOpenerUi();
    }

    private void Start() {
        pathDisplayer = PathDisplayer.Instance;
    }

    public void StartCalculation() {
        CloseAllUis();
        statisticsUI.ClearStatistics();
        pathDisplayer.ClearAll();

        loadingPanel.GetComponentInChildren<TextMeshProUGUI>().text = "Berechnung läuft...";
        loadingPanel.SetActive(true);

        //Create node list and set the statics for waypoint count
        var nodes = NodeModel.CreateList(plannerPanel.GetShoppingList());
        statisticsUI.UpdateWaypointCnt(nodes.Count);


        Debug.Log("Posting shoppping list...");
        var hostUrl = schedulerIpField.text;
        SchedulerRestClient.Instance.StartCalculationForShoppinglist(nodes, hostUrl, ProcessIntermediateResult, ProcessCalculationResult);
        statisticsUI.ResetAndStartTimer();
    }

    public void CancelCalculationAndVisualization() {
        if(SchedulerRestClient.Instance.CalcuationRunning) SchedulerRestClient.Instance.CancelCalculation(schedulerIpField.text);
        else {
            customer.ResetPosition();
            pathDisplayer.ClearAll();
            OpenOpenerUi();
        }
    }

    private void ProcessCalculationResult(PathResponse result, bool wasCanceled) {
        if(wasCanceled) {
            OpenOpenerUi();
            return;
        }

        if(result == null || result.Items.Count <= 0) {
            Debug.Log($"Got no result to display. Result count was: {result?.Items.Count}");
            OpenOpenerUi();
            return;
        }

        CloseAllUis();
        resultPanel.SetResult(result.Items);
        resultPanel.gameObject.SetActive(true);
       
        statisticsUI.StopTimerAndDisplay();
        statisticsUI.UpdateCalcDistance(result.Distance);
        if(!customer.onlyBeeLine) statisticsUI.UpdateRealDistance(NodeModel.GetVector3List(result.Items));
    }

    private void ProcessIntermediateResult(PathResponse intermediateRes) {
        if(intermediateRes == null || intermediateRes.DemoItems.Count <= 0) return;

        statisticsUI.UpdateCalcDistance(intermediateRes.Distance);
        pathDisplayer.DisplayStraightPath(NodeModel.GetVector3List(intermediateRes.DemoItems));
    }


    public void StartVisualization() {
        CloseAllUis();
        if(resultPanel.ResultNodeList == null || resultPanel.ResultNodeList.Count <= 0) {
            Debug.LogWarning("Result list is null or empty!");
            return;
        }

        loadingPanel.GetComponentInChildren<TextMeshProUGUI>().text = "Einkauf läuft...";
        loadingPanel.SetActive(true);

        //Get the waypoints from the displayer ui
        var waypoints = NodeModel.GetVector3List(resultPanel.ResultNodeList);

        //Display the waypoints beeline before adding the additional points for visualisation puropses
        pathDisplayer.DisplayStraightPath(waypoints);

        //Add the second point of the cashdesk and the entry/exit point to the list
        //Only for visual puropses :)
        waypoints.Add(plannerPanel.GetCheckout().CheckoutPoint);
        waypoints.Add(plannerPanel.GetEntrypoint().CheckoutPoint);

        //Set the waypoints to the customer which walks through the store
        customer.SetWaypoints(waypoints, OpenOpenerUi);

        //Display the all waypoints as the customers real path
        if(!customer.onlyBeeLine) pathDisplayer.DisplayNavPath(waypoints);

        //Display order numbers
        pathDisplayer.DisplayOrderNumbers(waypoints);
    }

    public void OpenPlannerUi() {
        CloseAllUis();
        AllowCameraScript = false;
        plannerPanel.gameObject.SetActive(true);
    }

    public void OpenOpenerUi() { //The sidebar on the right
        CloseAllUis();
        openPlannerPanel.SetActive(true);
    }

    private void CloseAllUis() {
        plannerPanel.gameObject.SetActive(false);
        loadingPanel.SetActive(false);
        openPlannerPanel.SetActive(false);
        resultPanel.gameObject.SetActive(false);

        AllowCameraScript = true;
    }


    public void ToogleDisplayStraightPath(bool check) => pathDisplayer.EnableDisplayStraithPath(check);
    public void ToogleDisplayNavPath(bool check) => pathDisplayer.EnableDisplayNavPath(check);
    public void ToogleDisplayOrderNumbers(bool check) => pathDisplayer.EnableOrderNumbers(check);

}
