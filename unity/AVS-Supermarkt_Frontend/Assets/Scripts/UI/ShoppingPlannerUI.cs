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

    private List<Shelf> shoppingList;


    private void Awake() {
        var shelfs = new List<Shelf>(shelfParent.GetComponentsInChildren<Shelf>());
        shelfs.Sort((x, y) => x.productName.CompareTo(y.productName)); //Sort list alphabetical

        foreach(var shelf in shelfs) {
            var item = Instantiate(itemPrefab);
            item.transform.SetParent(shopViewPortContent);
            item.transform.localScale = Vector3.one;
            item.Setup(this, shelf, item.transform.GetSiblingIndex());
        }

        shoppingList = new List<Shelf>();
    }

    public List<Shelf> GetShoppingList() {
        return new List<Shelf>(shoppingList);
    }

    public void TryTransferItemToOtherList(ItemButton itmBtn, Shelf shelf) {
        if(itmBtn.transform.parent.Equals(shopViewPortContent)) {
            //Debug.Log("Move to shopping list.");
            MoveToShoppingList(itmBtn, shelf);

        } else if(itmBtn.transform.parent.Equals(listViewPortContent)) {
            //Debug.Log("Move out of shopping list.");
            MoveOutOfShoppingList(itmBtn, shelf);

        } else {
            throw new InvalidOperationException();
        }
    }

    public void MoveToShoppingList(ItemButton itmBtn, Shelf shelf) {
        itmBtn.transform.SetParent(listViewPortContent);
        shoppingList.Add(shelf);
    }

    public void MoveOutOfShoppingList(ItemButton itmBtn, Shelf shelf) {
        itmBtn.transform.SetParent(shopViewPortContent);
        itmBtn.transform.SetSiblingIndex(itmBtn.SiblingIndex);
        shoppingList.Remove(shelf);
    }

    public void ResetToShopList(ItemButton itmBtn, Shelf shelf) {
        if(shoppingList.Contains(shelf)) MoveOutOfShoppingList(itmBtn, shelf);
    }

}
