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

    public Customer customer;

    private void Awake() {
        OpenOpenerUi();
    }

    public void StartCalculation() {
        CloseAllUis();
        loadingPanel.GetComponentInChildren<TextMeshProUGUI>().text = "Berechnung läuft...";
        loadingPanel.SetActive(true);

        var nodes = NodeModel.CreateList(plannerPanel.GetShoppingList());
        Debug.Log("Posting shoppping list...");

        var hostUrl = schedulerIpField.text;
        if(!string.IsNullOrWhiteSpace(hostUrl) && !hostUrl.StartsWith("http", StringComparison.InvariantCultureIgnoreCase)) hostUrl = "http://" + hostUrl;
        SchedulerRestClient.Instance.StartCalculationForShoppinglist(nodes, hostUrl, ProcessIntermediateResult, ProcessCalculationResult);
    }

    public void CancelCalculation() {
        SchedulerRestClient.Instance.CancelCalculation();
    }

    private void ProcessCalculationResult(List<NodeModel> result, bool wasCanceled) {
        if(wasCanceled) {
            OpenOpenerUi();
            return;
        }

        if(result == null || result.Count <= 0) {
            Debug.Log("Got no result to display.");
            OpenOpenerUi();
            return;
        }

        CloseAllUis();
        resultPanel.SetResult(result);
        resultPanel.gameObject.SetActive(true);
    }

    private void ProcessIntermediateResult(List<NodeModel> intermediateRes) {
        if(intermediateRes == null || intermediateRes.Count <= 0) return;
        PathDisplayer.Instance.DisplayStraightPath(NodeModel.GetVector3List(intermediateRes));
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
        PathDisplayer.Instance.DisplayNavPath(waypoints);
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
