using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;

public class SchedulerRestClient : MonoBehaviour {

    public float delayBetweenRequests = 2f;

    private bool calculationActive;

    public static SchedulerRestClient Instance { get; private set; }

    void Awake() {
        if(Instance == null) {
            Instance = this;
        } else {
            Destroy(gameObject);
        }
    }


    public void StartCalculationForShoppinglist(List<NodeModel> nodes, string hostUrl, Action<List<NodeModel>> actionOnResult) {
        if(calculationActive) {
            throw new NotImplementedException("Caculation still active. Cannot start a new one!");
        }

        calculationActive = true;
        if(string.IsNullOrWhiteSpace(hostUrl)) StartCoroutine(DoCalculationEmulated(nodes, actionOnResult));
        else StartCoroutine(DoCalculation(nodes, hostUrl, actionOnResult));
    }


    private IEnumerator DoCalculationEmulated(List<NodeModel> nodes, Action<List<NodeModel>> actionOnResult) {
        Debug.Log("Emulating Scheduler is enabled.");
        yield return new WaitForSeconds(UnityEngine.Random.Range(2, 6));

        Debug.Log("Got result.");
        calculationActive = false;
        actionOnResult(nodes);
    }


    private IEnumerator DoCalculation(List<NodeModel> nodes, string hostUrl, Action<List<NodeModel>> actionOnResult) {
        var request = CreatePostShoppingListRequest(nodes, hostUrl);
        yield return request.SendWebRequest();

        if(request.responseCode != 200) {
            Debug.LogWarning("Error: " + request.responseCode);
            throw new System.NotImplementedException("No error handling for given response code while POST shopping list");

        } else {
            Debug.Log("Sending to scheduler successful.");
            yield return QueryCalculationResult(hostUrl, actionOnResult);
        }
    }

    private IEnumerator QueryCalculationResult(string hostUrl, Action<List<NodeModel>> actionOnResult) {
        Debug.Log("Checking for result...");
        bool isCalculating = true;
        List<NodeModel> result = null;

        while(result == null && isCalculating) {
            yield return new WaitForSeconds(delayBetweenRequests);

            var request = CreateGetCalculatedWaypointsRequest(hostUrl);
            yield return request.SendWebRequest();

            if(!request.isNetworkError && request.responseCode == 200) {
                Debug.Log("Got result.");

                var response = Encoding.UTF8.GetString(request.downloadHandler.data);
                result = JsonHelper.FromJson<NodeModel>(response);

            } else if(request.isNetworkError /*|| request.responseCode == 503*/) {
                Debug.LogWarning($"Cant get result. Network-Error: {request.isNetworkError}, Response-Code: {request.responseCode}");
                isCalculating = false;
            }
        }

        calculationActive = false;
        actionOnResult(result);
    }


    private static UnityWebRequest CreatePostShoppingListRequest(List<NodeModel> nodes, string hostUrl) {
        var jsonBody = JsonHelper.ToJson(nodes);

        var request = new UnityWebRequest(hostUrl + "/map", "POST");
        byte[] bodyRaw = Encoding.UTF8.GetBytes(jsonBody);

        request.uploadHandler = new UploadHandlerRaw(bodyRaw);
        request.downloadHandler = new DownloadHandlerBuffer();
        request.SetRequestHeader("Content-Type", "application/json;charset=utf-8");

        return request;
    }

    private static UnityWebRequest CreateGetCalculatedWaypointsRequest(string hostUrl) {
        UnityWebRequest request = UnityWebRequest.Get(hostUrl + "/path");
        request.SetRequestHeader("Accept", "application/json");

        return request;
    }

}
