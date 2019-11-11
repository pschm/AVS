using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class NodeModel {

    public string name;
    public Vector2 position;
    
    public static List<NodeModel> CreateList(List<Shelf> shelves) {
        var nodes = new List<NodeModel>();
        shelves.ForEach(x => nodes.Add(new NodeModel() {
            name = x.productName,
            position = new Vector2(x.GetWalkToPoint().x, x.GetWalkToPoint().z)
        }));

        return nodes;
    }

}
