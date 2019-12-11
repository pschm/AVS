using System;
using System.Collections.Generic;
using TMPro;
using UnityEngine;

[ExecuteInEditMode]
public abstract class ShopAsset : MonoBehaviour {

    /// <summary>
    /// Name of the shop asset / furniture.
    /// </summary>
    public abstract string AssetName { get; }


    /// <summary>
    /// Returns the point for "accessing" the shelf.
    /// </summary>
    public abstract Vector3 WalkToPoint { get; }



    private TextMeshPro textPlane;
    
    protected virtual void Update() {
#if UNITY_EDITOR
        if(transform.hasChanged && textPlane != null) {
            var rect = textPlane.GetComponent<RectTransform>();
            if(transform.localScale.x != 0 && transform.localScale.z != 0) {
                rect.localScale = new Vector3(1 / transform.localScale.x, 1 / transform.localScale.z, 1);
            }
        }
#endif
    }


#if UNITY_EDITOR
    protected virtual void OnDrawGizmosSelected() {
        Gizmos.color = Color.grey;
        Gizmos.DrawSphere(WalkToPoint, .5f);
    }

    protected virtual void OnValidate() {
        if(textPlane == null) textPlane = transform.GetComponentInChildren<TextMeshPro>();
        if(textPlane != null) textPlane.text = AssetName;

        name = $"{GetType().Name} ({AssetName})";
    }
#endif

}

