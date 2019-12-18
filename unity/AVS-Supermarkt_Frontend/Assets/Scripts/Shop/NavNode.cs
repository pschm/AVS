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


    protected List<NavNode> nextNodes;

    private void Awake() {
        UpdateNextNodes();
    }

    protected virtual void Update() {
#if UNITY_EDITOR
        if(!EditorApplication.isPlaying && transform.hasChanged) {
            UpdateNextNodes();                        
        }
#endif
    }

    private void UpdateNextNodes() {
        nextNodes = new List<NavNode>();
        Collider[] hits = Physics.OverlapSphere(NodeCenter, rangeRadius, LayerMask.NameToLayer("Everything"), QueryTriggerInteraction.Collide);

        foreach(var hit in hits) {
            var node = hit.GetComponent<NavNode>();
            if(node != null) {
                if(node == this) continue;
                if(this is ShopAsset && node is ShopAsset) continue;

                nextNodes.Add(node);
            }
        }
    }

    public virtual Vector3 NodeCenter => transform.position;
    
    public List<NavNode> NextNodes => nextNodes;



    private void OnValidate() {
        var bc = GetComponent<BoxCollider>();
        bc.size = new Vector3(.1f, .1f, .1f);
        bc.isTrigger = true;
    }

    private void OnDrawGizmos() {
        if(alwaysShowGizmos) Gizmos.DrawSphere(NodeCenter, .25f);

        foreach(var node in nextNodes) {
            Gizmos.color = Color.magenta;
                      
            if(!node.nextNodes.Contains(this)) {
                Gizmos.color = Color.red;
                Gizmos.DrawCube(NodeCenter, Vector3.one); 
            }

            if(alwaysShowGizmos) Gizmos.DrawLine(NodeCenter, node.NodeCenter);
        }
    }

    private void OnDrawGizmosSelected() {
        Gizmos.color = Color.gray;
        Gizmos.DrawWireSphere(NodeCenter, rangeRadius);
    }


}
