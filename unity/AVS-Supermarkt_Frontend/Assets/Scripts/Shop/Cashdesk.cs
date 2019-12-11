using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class Cashdesk : ShopAsset {

    public string cashdeskName;

    [Header("Offsets")]
    public float offsetZ = 1.5f;
    public float walkOffsetX;
    public float checkoutOffsetX;

    public override string AssetName => cashdeskName;

    public override Vector3 WalkToPoint => transform.position + transform.forward * offsetZ + transform.right * walkOffsetX;


    public Vector3 CheckoutPoint => transform.position + transform.forward * offsetZ + transform.right * checkoutOffsetX;


    protected override void OnDrawGizmosSelected() {
        base.OnDrawGizmosSelected();
        Gizmos.color = Color.yellow;
        Gizmos.DrawSphere(CheckoutPoint, .5f);
    }

    protected override void OnValidate() {
        base.OnValidate();

        var clamp = transform.lossyScale.x / 2f;
        walkOffsetX = Mathf.Clamp(walkOffsetX, -clamp, clamp);
        checkoutOffsetX = Mathf.Clamp(checkoutOffsetX, -clamp, clamp);
    }

}
