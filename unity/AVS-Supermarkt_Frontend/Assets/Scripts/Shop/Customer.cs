using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AI;

[RequireComponent(typeof(NavMeshAgent))]
public class Customer : MonoBehaviour {

    public bool onlyBeeLine = true;

    private List<Vector3> waypoints = new List<Vector3>();
    private int waypointIndex;
    private float grabCooldown;

    private NavMeshAgent agent;
    private Vector3? destination = null;
    private Vector3 initialPos;
    private Action finishAction;

    private void Awake() {
        agent = GetComponent<NavMeshAgent>();
        agent.isStopped = true;
        initialPos = transform.position;
    }

    private void Update() {
        if(onlyBeeLine && destination.HasValue && Vector3.Distance(transform.position, destination.Value + Vector3.up) > 0.2f) {
            transform.position = Vector3.MoveTowards(transform.position, destination.Value + Vector3.up, 5f * Time.deltaTime);

            transform.LookAt(destination.Value);
            var rot = transform.rotation.eulerAngles;
            rot.x = 0;
            transform.rotation = Quaternion.Euler(rot);
        }
        

        if( (agent.enabled && !agent.isStopped && !agent.pathPending && agent.remainingDistance < 0.2f) || 
            (onlyBeeLine && destination.HasValue && Vector3.Distance(transform.position, destination.Value + Vector3.up) < 0.2f) ) {

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
            if(agent.enabled) agent.isStopped = true;
            finishAction?.Invoke();
            finishAction = null;
            return;
        }

        //Set the agent to go to the currently selected waypoint.
        destination = waypoints[waypointIndex];
        if(agent.enabled) agent.destination = destination.Value;

        //Choose the next point in the list as the destination
        waypointIndex++;
    }

    public void SetWaypoints(List<Vector3> waypoints, Action finishAction = null) {
        ResetPosition(waypoints[0] + Vector3.up); //Add y-Offset to not end up in the floor

        if(onlyBeeLine) agent.enabled = false;
        else agent.enabled = true;

        this.waypoints = waypoints;
        waypointIndex = 0;
        if(agent.enabled) agent.isStopped = false;

        this.finishAction = finishAction;

        GotoNextPoint(); //Get and set the first waypoint as the destination
    }

    public void ResetPosition() {
        ResetPosition(initialPos);
    }

    public void ResetPosition(Vector3 position) {
        agent.isStopped = true;
        destination = null;
        waypoints = null;
        transform.position = position;
    }



#if UNITY_EDITOR
    private void OnDrawGizmos() {
        var oldColor = Gizmos.color;

        Gizmos.color = Color.magenta;
        foreach(var waypoint in waypoints) {
            Gizmos.DrawSphere(waypoint, .5f);
        }

        Gizmos.color = oldColor;
    }

#endif

}
