using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.Networking;
using Random = UnityEngine.Random;

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


    public void StartCalculationForShoppinglist(List<NodeModel> nodes, string hostUrl, Action<List<NodeModel>> intAction, Action<List<NodeModel>> actionOnResult) {
        if(calculationActive) {
            throw new NotImplementedException("Caculation still active. Cannot start a new one!");
        }

        calculationActive = true;
        if(string.IsNullOrWhiteSpace(hostUrl)) StartCoroutine(DoCalculationEmulated(nodes, actionOnResult));
        else StartCoroutine(DoCalculation(nodes, hostUrl, intAction, actionOnResult));
    }


    private IEnumerator DoCalculationEmulated(List<NodeModel> nodes, Action<List<NodeModel>> actionOnResult) {
        Debug.Log("Emulating Scheduler is enabled.");
        yield return new WaitForSeconds(Random.Range(0.5f, 2f));

        Debug.Log("Got result.");
        calculationActive = false;
        actionOnResult(nodes);
    }


    private IEnumerator DoCalculation(List<NodeModel> nodes, string hostUrl, Action<List<NodeModel>> intAction, Action<List<NodeModel>> actionOnResult) {
        var request = CreatePostShoppingListRequest(nodes, hostUrl);
        yield return request.SendWebRequest();

        if(request.responseCode != 200) {
            Debug.LogWarning("Error: " + request.responseCode);
            throw new NotImplementedException("No error handling for given response code while POST shopping list");

        } else {
            Debug.Log("Sending to scheduler successful.");
            yield return QueryCalculationResult(hostUrl, intAction, actionOnResult);
        }
    }

    private IEnumerator QueryCalculationResult(string hostUrl, Action<List<NodeModel>> intAction, Action<List<NodeModel>> actionOnResult) {
        Debug.Log("Checking for result...");
        List<NodeModel> result = null;

        while(result == null) {
            var request = CreateGetCalculatedWaypointsRequest(hostUrl);
            yield return request.SendWebRequest();

            if(!request.isNetworkError && request.responseCode == 200) {
                Debug.Log("Got result.");

                var response = Encoding.UTF8.GetString(request.downloadHandler.data);
                result = JsonHelper.FromJson<NodeModel>(response);

            } else if(request.isNetworkError /*|| request.responseCode == 503*/) {
                Debug.LogWarning($"Cant get result. Network-Error: {request.isNetworkError}, Response-Code: {request.responseCode}");
                break;

            } else {

                yield return HandleIntermediateRequest(hostUrl, intAction);
            }

            yield return new WaitForSeconds(delayBetweenRequests);
        }

        calculationActive = false;
        actionOnResult(result);
    }

    private IEnumerator HandleIntermediateRequest(string hostUrl, Action<List<NodeModel>> intAction) {
        var request = CreateGetIntermediateWaypointsRequest(hostUrl);
        yield return request.SendWebRequest();

        if(!request.isNetworkError && request.responseCode == 200) {
            var response = Encoding.UTF8.GetString(request.downloadHandler.data);
            var result = JsonHelper.FromJson<NodeModel>(response);
            intAction(result);
        }
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

    private static UnityWebRequest CreateGetIntermediateWaypointsRequest(string hostUrl) {
        UnityWebRequest request = UnityWebRequest.Get(hostUrl + "/path"); //TODO Adjust to Scheduler-Endpoint
        request.SetRequestHeader("Accept", "application/json");

        return request;
    }

    private static UnityWebRequest CreateGetCalculatedWaypointsRequest(string hostUrl) {
        UnityWebRequest request = UnityWebRequest.Get(hostUrl + "/path"); //TODO Adjust to Scheduler-Endpoint
        request.SetRequestHeader("Accept", "application/json");

        return request;
    }

}
