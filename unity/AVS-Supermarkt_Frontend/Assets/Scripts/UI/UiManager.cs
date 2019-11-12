#define EMULATE_SCHEDULER

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class UiManager : MonoBehaviour {

    public ShoppingPlannerUI plannerPanel;
    public GameObject loadingPanel;
    public GameObject openPlannerPanel;
    public ResultUI resultPanel;

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
        StartCoroutine(HandleCalculation(nodes));
    }

    public void StartVisualization() {
        CloseAllUis();
        if(resultPanel.ResultNodeList == null || resultPanel.ResultNodeList.Count <= 0) {
            Debug.LogWarning("Result list is null or empty!");
            return;
        }

        loadingPanel.GetComponentInChildren<TextMeshProUGUI>().text = "Einkauf läuft...";
        loadingPanel.SetActive(true);

        var points = new List<Vector3>();
        resultPanel.ResultNodeList.ForEach(x => points.Add(new Vector3(x.position.x, 0, x.position.y)));
        customer.SetWaypoints(points, OpenOpenerUi);
    }


#if EMULATE_SCHEDULER
    private IEnumerator HandleCalculation(List<NodeModel> nodes) {
        Debug.Log("Emulating Scheduler is enabled.");
        yield return new WaitForSeconds(Random.Range(2, 6));

        Debug.Log("Got result.");
        ProcessCalculationResult(nodes);
    }

#else
    private IEnumerator HandleCalculation(List<NodeModel> nodes) {
        var responseCode = SchedulerRestClient.PostShoppingList(nodes);
        if(responseCode != 200) {
            Debug.Log("Error: " + responseCode);
            throw new System.InvalidProgramException();

        } else {
            Debug.Log("Sending to scheduler successful.");
            yield return CheckForCalculationResult();
        }        
    }

    private IEnumerator CheckForCalculationResult() {
        Debug.Log("Checking for result...");
        List<NodeModel> result = null;
        while(result == null) {
            yield return new WaitForSeconds(2f);
            result = SchedulerRestClient.GetCalculatedWaypoints();
        }

        Debug.Log("Got result.");
        ProcessCalculationResult(result);
    }

#endif //Emulate Scheduler

    private void ProcessCalculationResult(List<NodeModel> result) {
        CloseAllUis();
        resultPanel.SetResult(result);
        resultPanel.gameObject.SetActive(true);
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
