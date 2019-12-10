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

    private void ProcessCalculationResult(List<NodeModel> result) {
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

        var waypoints = NodeModel.GetVector3List(resultPanel.ResultNodeList);
        customer.SetWaypoints(waypoints, OpenOpenerUi);
        PathDisplayer.Instance.DisplayAllPath(waypoints);
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
