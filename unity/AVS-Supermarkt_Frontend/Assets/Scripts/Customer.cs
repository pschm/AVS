using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;

[RequireComponent(typeof(NavMeshAgent))]
public class Customer : MonoBehaviour {

    private List<Vector3> waypoints;
    private int waypointIndex;
    private float grabCooldown;

    private NavMeshAgent agent;
    private Vector3 initialPos;
    private Action finishAction;

    private void Awake() {
        agent = GetComponent<NavMeshAgent>();
        agent.isStopped = true;
        initialPos = transform.position;
    }


    private void Update() {
        if(!agent.isStopped && !agent.pathPending && agent.remainingDistance < 0.2f) {

            if(grabCooldown <= 0) {
                GotoNextPoint();
                grabCooldown = 3f;

            } else {
                grabCooldown -= Time.deltaTime;
            }

        }

    }

    private void GotoNextPoint() {
        //Returns if no waypoints have been set up
        if(waypoints == null || waypoints.Count == 0) return;

        //Special Action when the agent reached the final waypoint
        if(waypointIndex >= waypoints.Count) {
            agent.isStopped = true;
            finishAction?.Invoke();
            finishAction = null;
            return;
        }

        //Set the agent to go to the currently selected waypoint.
        agent.destination = waypoints[waypointIndex];

        //Choose the next point in the list as the destination
        waypointIndex++;
    }

    public void SetWaypoints(List<Vector3> waypoints, Action finishAction = null) {
        ResetPosition(waypoints[0]);

        this.waypoints = waypoints;
        waypointIndex = 0;
        agent.isStopped = false;

        this.finishAction = finishAction;

#if UNITY_EDITOR
        UpdateGizmosLines(waypoints);
#endif
    }

    public void ResetPosition() {
        ResetPosition(initialPos);
    }

    public void ResetPosition(Vector3 position) {
        agent.isStopped = true;
        transform.position = position;

#if UNITY_EDITOR
        gizmosLinePoints = new List<Vector3>();
#endif
    }



#if UNITY_EDITOR
    //Used for drawing gizoms so that its not required to re-build the list every draw call
    private List<Vector3> gizmosLinePoints = new List<Vector3>();

    private void UpdateGizmosLines(List<Vector3> waypoints) {
        if(waypoints == null || waypoints.Count <= 0) return;

        gizmosLinePoints = new List<Vector3>();
        NavMeshPath path = new NavMeshPath();

        //Calculate a path from each self to the next one
        for(int i = 0; i < waypoints.Count - 1; i++) {
            if(NavMesh.CalculatePath(waypoints[i], waypoints[i + 1], NavMesh.AllAreas, path)) {

                foreach(var point in path.corners) {
                    gizmosLinePoints.Add(new Vector3(point.x, point.y + 1, point.z));
                }
            }
        }
    }

    private void OnDrawGizmos() {
        var oldColor = Gizmos.color;

        Gizmos.color = Color.cyan;
        for(int i = 0; i < gizmosLinePoints.Count - 1; i++) {
            Gizmos.DrawLine(gizmosLinePoints[i], gizmosLinePoints[i + 1]);
        }

        Gizmos.color = oldColor;
    }

#endif

}
