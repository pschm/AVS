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

    private void Awake() {
        OpenOpenerUi();
    }

    public void StartCalculation() {
        CloseAllUis();
        statisticsUI.ClearStatistics();
        PathDisplayer.Instance.ClearAllPath();

        loadingPanel.GetComponentInChildren<TextMeshProUGUI>().text = "Berechnung läuft...";
        loadingPanel.SetActive(true);

        //Create node list and set the statics for waypoint count
        var nodes = NodeModel.CreateList(plannerPanel.GetShoppingList());
        statisticsUI.UpdateWaypointCnt(nodes.Count);


        Debug.Log("Posting shoppping list...");
        var hostUrl = schedulerIpField.text;
        if(!string.IsNullOrWhiteSpace(hostUrl) && !hostUrl.StartsWith("http", StringComparison.InvariantCultureIgnoreCase)) hostUrl = "http://" + hostUrl;

        SchedulerRestClient.Instance.StartCalculationForShoppinglist(nodes, hostUrl, ProcessIntermediateResult, ProcessCalculationResult);
        statisticsUI.ResetAndStartTimer();
    }

    public void CancelCalculation() {
        SchedulerRestClient.Instance.CancelCalculation();
    }

    private void ProcessCalculationResult(PathResponse result, bool wasCanceled) {
        if(wasCanceled) {
            OpenOpenerUi();
            return;
        }

        if(result == null || result.Items.Count <= 0) {
            Debug.Log("Got no result to display.");
            OpenOpenerUi();
            return;
        }

        CloseAllUis();
        resultPanel.SetResult(result.Items);
        resultPanel.gameObject.SetActive(true);
       
        statisticsUI.StopTimerAndDisplay();
        statisticsUI.UpdateCalcDistance(result.distance);
        if(!customer.onlyBeeLine) statisticsUI.UpdateRealDistance(NodeModel.GetVector3List(result.Items));
    }

    private void ProcessIntermediateResult(PathResponse intermediateRes) {
        if(intermediateRes == null || intermediateRes.Items.Count <= 0) return;

        statisticsUI.UpdateCalcDistance(intermediateRes.distance);
        PathDisplayer.Instance.DisplayStraightPath(NodeModel.GetVector3List(intermediateRes.Items));
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
        PathDisplayer.Instance.DisplayStraightPath(waypoints);

        //Add the second point of the cashdesk and the entry/exit point to the list
        //Only for visual puropses :)
        waypoints.Add(plannerPanel.GetCheckout().CheckoutPoint);
        waypoints.Add(plannerPanel.GetEntrypoint().CheckoutPoint);

        //Set the waypoints to the customer which walks through the store
        customer.SetWaypoints(waypoints, OpenOpenerUi);

        //Display the all waypoints as the customers real path
        if(!customer.onlyBeeLine) PathDisplayer.Instance.DisplayNavPath(waypoints);
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
        resultPanel.gameObject.SetActive(false);
    }

}
