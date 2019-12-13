using System;
using System.Collections.Generic;
using TMPro;
using UnityEditor;
using UnityEngine;

[ExecuteInEditMode]
public abstract class ShopAsset : NavNode {

    /// <summary>
    /// Name of the shop asset / furniture.
    /// </summary>
    public abstract string AssetName { get; }


    /// <summary>
    /// Returns the point for "accessing" the shelf.
    /// </summary>
    public abstract Vector3 WalkToPoint { get; }



    private TextMeshPro textPlane;

    protected override void Update() {
        base.Update();

#if UNITY_EDITOR
        if(!EditorApplication.isPlaying && transform.hasChanged && textPlane != null) {
            var rect = textPlane.GetComponent<RectTransform>();
            if(transform.localScale.x != 0 && transform.localScale.z != 0) {
                rect.localScale = new Vector3(1 / transform.localScale.x, 1 / transform.localScale.z, 1);
            }
        }
#endif
    }

    public override Vector3 NodeCenter => WalkToPoint;



#if UNITY_EDITOR
    protected virtual void OnDrawGizmos() {
        Gizmos.color = Color.grey;
        Gizmos.DrawSphere(WalkToPoint, .4f);

        if(nextNodes.Count <= 0) {
            Gizmos.color = Color.red;
            Gizmos.DrawSphere(WalkToPoint, .8f);
        }
    }

    protected virtual void OnValidate() {
        if(textPlane == null) textPlane = transform.GetComponentInChildren<TextMeshPro>();
        if(textPlane != null) textPlane.text = AssetName;

        name = $"{GetType().Name} ({AssetName})";

        var bc = GetComponent<BoxCollider>();
        if(bc) {          
            bc.isTrigger = true;           
            bc.center = transform.InverseTransformPoint(WalkToPoint);

            var scale = transform.lossyScale;
            if(scale.x == 0) scale.x = 1;
            if(scale.y == 0) scale.y = 1;
            if(scale.z == 0) scale.z = 1;
            bc.size = new Vector3(.6f / scale.x, .6f / scale.y, .6f / scale.z);
        }
    }
#endif

}

