#define EMULATE_SCHEDULER

using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using System.Text;

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
        var request = SchedulerRestRequests.BuildPostShoppingListRequest(nodes);
        yield return request.SendWebRequest();

        if(request.responseCode != 200) {
            Debug.LogWarning("Error: " + request.responseCode);
            throw new System.NotImplementedException("No error handling for given response code while POST shopping list");

        } else {
            Debug.Log("Sending to scheduler successful.");
            yield return CheckForCalculationResult();
        }        
    }

    private IEnumerator CheckForCalculationResult() {
        Debug.Log("Checking for result...");        
        bool isCalculating = true;
        List<NodeModel> result = null;

        while(result == null && isCalculating) {
            var request = SchedulerRestRequests.BuildGetCalculatedWaypointsRequest();
            yield return new WaitForSeconds(2f);
            yield return request.SendWebRequest();

            if(!request.isNetworkError && request.responseCode == 200) {
                Debug.Log("Got result.");

                var response = Encoding.UTF8.GetString(request.downloadHandler.data);
                result = JsonHelper.FromJson<NodeModel>(response);

            } else if(request.isNetworkError || request.responseCode == 503) {
                Debug.LogWarning($"Cant get result. Network-Error: {request.isNetworkError}, Response-Code: {request.responseCode}");
                isCalculating = false;
            }
        }

        
        ProcessCalculationResult(result);
    }

#endif //Emulate Scheduler

    private void ProcessCalculationResult(List<NodeModel> result) {
        if(result == null) {
            Debug.Log("Got no result to display.");
            OpenOpenerUi();
            return;
        }

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
