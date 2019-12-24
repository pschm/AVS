using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;


public class Shelf : ShopAsset {

    public string productName;


    public override string AssetName => productName;

    //Add an offset because the shelf point itself is inside the shelf.
    public override Vector3 WalkToPoint => transform.position + transform.forward * 1.5f;


}
