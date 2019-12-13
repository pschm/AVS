using System.Collections;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;

[ExecuteInEditMode]
[RequireComponent(typeof(BoxCollider))]
public class NavNode : MonoBehaviour {

    [Header("NavNode-Settings")]
    [Range(2, 10)] public int rangeRadius = 6;
    public bool alwaysShowGizmos;


    internal List<NavNode> nextNodes = new List<NavNode>();


    protected virtual void Update() {
        if(!EditorApplication.isPlaying && transform.hasChanged) {
            nextNodes = new List<NavNode>();

            Collider[] hits = Physics.OverlapSphere(ColliderCenter, rangeRadius, LayerMask.NameToLayer("Everything"), QueryTriggerInteraction.Collide);
            foreach(var hit in hits) {
                var node = hit.GetComponent<NavNode>();
                if(node != null) {
                    if(node == this) continue;
                    if(this is ShopAsset && node is ShopAsset) continue;

                    nextNodes.Add(node);
                }
            }
        }
    }

    internal virtual Vector3 ColliderCenter => transform.position;

    private void OnValidate() {
        var bc = GetComponent<BoxCollider>();
        bc.size = new Vector3(.1f, .1f, .1f);
        bc.isTrigger = true;
    }

    private void OnDrawGizmos() {
        if(alwaysShowGizmos) Gizmos.DrawSphere(ColliderCenter, .25f);

        foreach(var node in nextNodes) {
            Gizmos.color = Color.magenta;
                      
            if(!node.nextNodes.Contains(this)) {
                Gizmos.color = Color.red;
                Gizmos.DrawCube(ColliderCenter, Vector3.one); 
            }

            if(alwaysShowGizmos) Gizmos.DrawLine(ColliderCenter, node.ColliderCenter);
        }
    }

    private void OnDrawGizmosSelected() {
        Gizmos.color = Color.gray;
        Gizmos.DrawWireSphere(ColliderCenter, rangeRadius);
    }


}
