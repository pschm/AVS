using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class NodeModel {

    public string id;
    public string name;
    public Vector2 position;

    public NodeModel() { }

    
    public static List<NodeModel> CreateList(List<ShopAsset> shopAssets) {
        var nodes = new List<NodeModel>();
        shopAssets.ForEach(x => nodes.Add(new NodeModel() {
            id = x.gameObject.GetInstanceID().ToString(),
            name = x.AssetName,
            position = new Vector2(x.WalkToPoint.x, x.WalkToPoint.z)
        }));

        return nodes;
    }

    public static List<Vector3> GetVector3List(List<NodeModel> nodes) {
        if(nodes == null) return null;

        var points = new List<Vector3>();
        nodes.ForEach(x => points.Add(new Vector3(x.position.x, 0, x.position.y)));
        return points;
    }

}
