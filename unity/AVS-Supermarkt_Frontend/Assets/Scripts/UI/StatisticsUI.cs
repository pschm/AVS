using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using System;
using UnityEngine.AI;

public class StatisticsUI : MonoBehaviour {

    public TextMeshProUGUI txtWaypointCnt;
    public TextMeshProUGUI txtTime;
    public TextMeshProUGUI txtCalcDistance;
    public TextMeshProUGUI txtRealDistance;

    private bool runTimer;
    private float timer;

    private void Start() {
        ClearStatistics();
    }

    private void Update() {
        if(runTimer) {
            UpdateTime(timer);
            timer += Time.deltaTime;
        }
    }

    public void ClearStatistics() {
        txtWaypointCnt.text = "-";
        txtTime.text = "-";
        txtCalcDistance.text = "-";
        txtRealDistance.text = "-";
    }


    public void UpdateWaypointCnt(int cnt) {
        txtWaypointCnt.text = cnt.ToString();
    }

    public void UpdateTime(float timer) {
        string minutes = Mathf.Floor(timer / 60).ToString("00");
        string seconds = (timer % 60).ToString("00");

        txtTime.text = minutes + ":" + seconds;
    }

    public void ResetAndStartTimer() {
        timer = 0f;
        runTimer = true;
    }

    public void StopTimerAndDisplay() {
        runTimer = false;
        UpdateTime(timer);
    }

    public void UpdateCalcDistance(float distance) {
        txtCalcDistance.text = $"{distance.ToString("F2")}m";
    }

    public void UpdateRealDistance(float distance) {
        txtRealDistance.text = $"{distance.ToString("F2")}m";
    }

    public void UpdateRealDistance(List<Vector3> waypoints) {
        NavMeshPath path = new NavMeshPath();
        float realDistance = 0f;

        //Calculate a path from each self to the next one
        for(int i = 0; i < waypoints.Count - 1; i++) {
            if(NavMesh.CalculatePath(waypoints[i], waypoints[i + 1], NavMesh.AllAreas, path)) {

                for(int j = 0; j < path.corners.Length - 1; j++) {
                    realDistance += Vector3.Distance(path.corners[j], path.corners[j + 1]);
                }

            }
        }

        UpdateRealDistance(realDistance);
    }
}
