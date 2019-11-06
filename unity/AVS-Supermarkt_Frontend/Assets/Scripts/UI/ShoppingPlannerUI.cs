using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ShoppingPlannerUI : MonoBehaviour {

    public ItemButton itemPrefab;
    public RectTransform shopViewPortContent;
    public RectTransform listViewPortContent;

    public Transform shelfParent;

    private List<Shelf> shoppingList = new List<Shelf>();


    private void Awake() {
        var shelfs = new List<Shelf>(shelfParent.GetComponentsInChildren<Shelf>());
        shelfs.Sort((x, y) => x.productName.CompareTo(y.productName)); //Sort list alphabetical

        foreach(var shelf in shelfs) {
            var item = Instantiate(itemPrefab);
            item.Setup(this, shelf);
            item.transform.SetParent(shopViewPortContent);
            item.transform.localScale = Vector3.one;
        }
    }

    public void TryTransferItemToOtherList(Transform btnTransform, Shelf shelf) {
        if(btnTransform.parent.Equals(shopViewPortContent)) {
            //Debug.Log("Move to shopping list.");
            MoveToShoppingList(btnTransform, shelf);

        } else if(btnTransform.parent.Equals(listViewPortContent)) {
            //Debug.Log("Move out of shopping list.");
            MoveOutOfShoppingList(btnTransform, shelf);

        } else {
            throw new InvalidOperationException();
        }
    }

    public void MoveToShoppingList(Transform btnTransform, Shelf shelf) {
        btnTransform.SetParent(listViewPortContent);
        shoppingList.Add(shelf);
    }

    public void MoveOutOfShoppingList(Transform btnTransform, Shelf shelf) {
        btnTransform.SetParent(shopViewPortContent);
        shoppingList.Remove(shelf);
    }


}
