using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Random = UnityEngine.Random;

public class ShoppingPlannerUI : MonoBehaviour {

    public ItemButton itemPrefab;
    public RectTransform shopViewPortContent;
    public RectTransform listViewPortContent;

    public Transform shelfParent;
    public Transform entrypointParent;
    public Transform checkoutParent;

    private List<Shelf> shoppingList;
    private ItemButton entrypointBtn;
    private ItemButton checkoutBtn;

    private void Awake() {
        var shelfs = new List<Shelf>(shelfParent.GetComponentsInChildren<Shelf>());
        shelfs.Sort((x, y) => x.productName.CompareTo(y.productName)); //Sort list alphabetical

        //For every shelf add a button in the list planner
        foreach(var shelf in shelfs) {
            var item = Instantiate(itemPrefab);
            item.transform.SetParent(shopViewPortContent);
            item.Setup(this, shelf, item.transform.GetSiblingIndex());
        }

        shoppingList = new List<Shelf>();


        //Add the entrypoint and checkout to the shopping list as not clickable buttons
        entrypointBtn = Instantiate(itemPrefab);
        entrypointBtn.transform.SetParent(listViewPortContent);
        entrypointBtn.Setup(this, GetRandomEntrypoint(), -1);
        entrypointBtn.buttonComponent.interactable = false;

        checkoutBtn = Instantiate(itemPrefab);
        checkoutBtn.transform.SetParent(listViewPortContent);
        checkoutBtn.Setup(this, GetRandomCheckout(), -1);
        checkoutBtn.buttonComponent.interactable = false; ;
    }

    public List<Shelf> GetShoppingList() {
        var list = new List<Shelf>(shoppingList);

        //Add the entrypoint and checkout to the list
        //The buttons are only visable in the list but not added to the internal list
        //This way its easier to handle because otherwise every time when a button gets added
        //to the shopping list, the checkout has to be move to the end of the list.
        list.Insert(0, entrypointBtn.Shelf);
        list.Add(checkoutBtn.Shelf);

        return list;
    }

    public Shelf GetRandomEntrypoint() {
        var entrypoints = entrypointParent.GetComponentsInChildren<Shelf>();
        return entrypoints[Random.Range(0, entrypoints.Length)];
    }

    public Shelf GetRandomCheckout() {
        var checkouts = checkoutParent.GetComponentsInChildren<Shelf>();
        return checkouts[Random.Range(0, checkouts.Length)];
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

        //Set the checkout as the last elements
        checkoutBtn.transform.SetAsLastSibling();
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
