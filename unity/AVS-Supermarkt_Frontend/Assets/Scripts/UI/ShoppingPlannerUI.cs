using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Random = UnityEngine.Random;

public class ShoppingPlannerUI : MonoBehaviour {

    [Header("UI Stuff")]
    public ItemButton itemPrefab;
    public RectTransform shopViewPortContent;
    public RectTransform listViewPortContent;

    [Header("Transform Parents")]
    public Transform shelfParent;
    public Transform entrypointParent;
    public Transform checkoutParent;

    [Header("Predefined Lists")]
    public List<Shelf> preListSmall;
    public List<Shelf> preListLarge;

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

    private void OnEnable() {
        ResetShopList();
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


    public void TryTransferItemToOtherList(ItemButton itmBtn) {
        if(itmBtn.transform.parent.Equals(shopViewPortContent)) {
            //Debug.Log("Move to shopping list.");
            MoveToShoppingList(itmBtn);

        } else if(itmBtn.transform.parent.Equals(listViewPortContent)) {
            //Debug.Log("Move out of shopping list.");
            MoveOutOfShoppingList(itmBtn);

        } else {
            throw new InvalidOperationException();
        }
    }

    public void MoveToShoppingList(ItemButton itmBtn) {
        itmBtn.transform.SetParent(listViewPortContent);
        shoppingList.Add(itmBtn.Shelf);

        //Set the checkout as the last elements
        checkoutBtn.transform.SetAsLastSibling();
    }

    public void MoveOutOfShoppingList(ItemButton itmBtn) {
        if(!shoppingList.Contains(itmBtn.Shelf)) return;

        itmBtn.transform.SetParent(shopViewPortContent);
        itmBtn.transform.SetSiblingIndex(itmBtn.SiblingIndex);
        shoppingList.Remove(itmBtn.Shelf);
    }


    public void ResetShopList() {
        foreach(var btn in listViewPortContent.GetComponentsInChildren<ItemButton>()) {
            btn.ResetButtonToShopList();
        }
    }
    
    public void LoadSmallPredefinedList() {
        LoadPredefinedList(preListSmall);
    }

    public void LoadLargePredefinedList() {
        LoadPredefinedList(preListLarge);
    }

    private void LoadPredefinedList(List<Shelf> list) {
        if(list == null || list.Count <= 0) {
            Debug.LogWarning("Given predefined list is null or empty.");
            return;
        }

        ResetShopList();
        var btns = new List<ItemButton>(shopViewPortContent.GetComponentsInChildren<ItemButton>());
        foreach(var shelf in list) {
            MoveToShoppingList(btns.Find(x => x.Shelf == shelf));
        }
    }
}
